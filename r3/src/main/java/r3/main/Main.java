package r3.main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import game.Game;
import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.multithreading.ThreadProcessor;
import r3.window.DrawComp;
import r3.window.Window;

public class Main {
	public static boolean WORKING_WITH_GAMEOBJECTS;
	
	private static Camera camera = new Camera();
	public static double[] cameraPosOnIterationStart = new double[]{0,0,0};
	public static double[] cameraForwardOnIterationStart = new double[]{0,0,0};
	private final static Window window = new Window();
	
	
	public static final int FPS_MAX = 144;
	public static int fpsCurrent = 0;
//	public static int[][][] coordsDraw;
//	public static boolean[] fixedColor;
	public static final ArrayList<Color> colors = new ArrayList<Color>();
	public static double[][][] coords = Mathstuff.optimizeCoordinates(loadCoords(true));
	
	public static int lowMode = 0; 
	
	public static void main(String[] args) {		
		WORKING_WITH_GAMEOBJECTS = false;

		window.init();
		
		Thread dt = new Thread()
		{
			public void run()
			{
				while(true)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DrawComp.fps = DrawComp.countfps;
					DrawComp.countfps=0;
				}
			}
		};
		dt.start();
		
		ThreadProcessor.startMultithreadingRaw(Main.coords.length, 6);
	}
	
//	private static void startLoopThread() {
//		(new Thread() {
//			public void run() {		
//				super.run();
//				
//				System.out.println("started main loop");
//				
//				
//				//FPS counting => initialization
//				long timeStartNanos = System.nanoTime();
//				long timeNow;
//				final int cyclesForFPSCalculation = 10;
//				
//				int cycleCounter = 0;
//				while(true) {
//					
//					//process all pending inputs
//					processInputs();
//					
//					//Mathstuff.calcR3(Main.coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
//					
////					long timeBeginning = System.nanoTime();
//					window.getDrawComp().repaint();
//
////					fps++;
////					long timeEnd = System.nanoTime();
//					//System.out.println("Time: " + (timeEnd-timeBeginning));
//					try {
//						Thread.sleep(10);
//					}catch(Exception e) {};
//					
//					
////					coordsDefault = new double[][][] {
//////						{{-1.10,-0.50,0},{-1.10,0.50,0},{-1.10,0,0.50}}
////						{{-10,-5,0},{-10,5,0},{-10,0,5}}
////					};
//					
//					
//					//FPS counting NEEDS TO BE AT THE VERY END OF MAIN LOOP
//					cycleCounter++;
//					if(cycleCounter % cyclesForFPSCalculation == 0) {
//						timeNow = System.nanoTime();
//						fpsCurrent = (int) ((1000000000d*cyclesForFPSCalculation)/(timeNow - timeStartNanos));
//						//set new start time for next cycle
//						timeStartNanos = timeNow;
////						System.out.println(( 1000000000d/(timeNow - timeStartNanos)) + ", " + timeNow + "/t" + timeStartNanos);
//					}					
//				}
//				
//			}
//		}).start();
//	}
	
	
	public static final double[] gravityForce = new double[] {0,0,-9.81};
	private static double[][] currentlyClosestTriangle;
	private static double currentlyClosestTriangleColorOriginal;
	private static double editColor = (double) storeColor(Color.red.getRGB());
	public static final double MOVEMENT_SPEED_PER_SECOND = 10;
	public static final double ROTATION_SPEED_PER_SECOND = Math.toRadians(45); //radians	
	public static final double ROTATION_AMOUNT_PER_MOUSEMOVEMENT_PIXEL = Math.toRadians(0.25); //radians
	private static long processInputsTimeLastNanos = System.nanoTime();
	private static Mathstuff mathstuff;
	public static void processInputs() {
		if(mathstuff == null)
			mathstuff = new Mathstuff(false);
		
		//sync camera and player
		if(WORKING_WITH_GAMEOBJECTS) {
			Game.getGame().syncCameraWithPlayer();
		}
		
		
		//calculate delta time
		long timeNowNanos = System.nanoTime();
		double deltaTimeSeconds = (timeNowNanos - processInputsTimeLastNanos)/1000000000d;
		processInputsTimeLastNanos = timeNowNanos;
		
		//////KEYBOARD - MOVEMENT
		boolean[] register = window.getKeyRegister();
		
		if(register[KeyEvent.VK_K]) {
			for(ThreadProcessor thread : ThreadProcessor.threadRegister) {				
				System.out.print(thread.getGameObjects().size() + ", ");
			}
			System.out.println();
		}
		

		if(!WORKING_WITH_GAMEOBJECTS) {		
			double movementDelta = MOVEMENT_SPEED_PER_SECOND * deltaTimeSeconds;
			
			if(register[KeyEvent.VK_W] ^ register[KeyEvent.VK_S]) {
				//normalize subvector of components x1 and x2 => divide x1 or x2 by pythagoras of x1 and x2
				double normalizationFactor = (1/Math.sqrt((camera.forward[0]*camera.forward[0])+(camera.forward[1]*camera.forward[1])))
						//and for performance reasons, multiply it with the movementDelta, if we're at it
						* movementDelta;
				
				if(register[KeyEvent.VK_W]) {
					Main.getCamera().pos[0]+=camera.forward[0]*normalizationFactor;
					Main.getCamera().pos[1]+=camera.forward[1]*normalizationFactor;
				} else {
					Main.getCamera().pos[0]-=camera.forward[0]*normalizationFactor;
					Main.getCamera().pos[1]-=camera.forward[1]*normalizationFactor;
				}
			}
			
			if(register[KeyEvent.VK_A] && !register[KeyEvent.VK_D]) {
				Main.getCamera().pos[0]+=Main.getCamera().left[0]*movementDelta;
				Main.getCamera().pos[1]+=Main.getCamera().left[1]*movementDelta;
			} else if(!register[KeyEvent.VK_A] && register[KeyEvent.VK_D]) {
				Main.getCamera().pos[0]-=Main.getCamera().left[0]*movementDelta;
				Main.getCamera().pos[1]-=Main.getCamera().left[1]*movementDelta;
			}
			
			if(register[KeyEvent.VK_SPACE] && !(register[KeyEvent.VK_SHIFT]||register[KeyEvent.VK_E])) {
				Main.getCamera().pos[2]+=movementDelta;
			} else if(!register[KeyEvent.VK_SPACE] && (register[KeyEvent.VK_SHIFT]||register[KeyEvent.VK_E])) {
				Main.getCamera().pos[2]-=movementDelta;
			}
			
		}
		
//		if(register[KeyEvent.VK_ENTER])
//		{
//			if(indexSelected!=-1)
//			{
//				coords[indexSelected][3][0] = editColor;
//				fixedColor[indexSelected] = true;
//			}
//			indexSelected = -1;
//		}
		
//		if(register[KeyEvent.VK_BACK_SPACE]) {
//			coords[0][2][1] -= 0.25;
//			convertTriangles();
//		} else if(register[KeyEvent.VK_ENTER]) {
//			coords[0][2][1] += 0.25;
//			convertTriangles();
//		} else if(register[KeyEvent.VK_T]) {
//			convertTriangles();
//		}
//		
//		System.out.println("---");
//		System.out.println(Arrays.toString(coords[0][0]));
//		System.out.println(Arrays.toString(coords[0][1]));
//		System.out.println(Arrays.toString(coords[0][2]));
//		System.out.println("---");
		
		//////MOUSE - ROTATION		
		boolean forwardVectorChanged = false; //used to check if a recalculation of the forward vector is neccessary
		if(register[KeyEvent.VK_UP] && !register[KeyEvent.VK_DOWN]) {
			camera.alpha += ROTATION_SPEED_PER_SECOND*deltaTimeSeconds;
			forwardVectorChanged = true;
			
		} else if(!register[KeyEvent.VK_UP] && register[KeyEvent.VK_DOWN]) {
			camera.alpha -= ROTATION_SPEED_PER_SECOND*deltaTimeSeconds;
			forwardVectorChanged = true;
		}
		
		if(register[KeyEvent.VK_LEFT] && !register[KeyEvent.VK_RIGHT]) {
			camera.beta += ROTATION_SPEED_PER_SECOND*deltaTimeSeconds;
			forwardVectorChanged = true;
			
		} else if(!register[KeyEvent.VK_LEFT] && register[KeyEvent.VK_RIGHT]) {
			camera.beta -= ROTATION_SPEED_PER_SECOND*deltaTimeSeconds;
			forwardVectorChanged = true;
		}
		
		
		int[] mouseMovement = window.getMouseMovementPixelSinceLastInvoke();
		if(mouseMovement[0]!=0||mouseMovement[1]!=0) {
			camera.alpha += -mouseMovement[1]*ROTATION_AMOUNT_PER_MOUSEMOVEMENT_PIXEL;
			camera.beta += -mouseMovement[0]*ROTATION_AMOUNT_PER_MOUSEMOVEMENT_PIXEL;
			forwardVectorChanged = true;
		}
		
		
		//if the forward vector changed due to rotation of the camera, rangecheck alpha and recalculate the forward vector 
		if(forwardVectorChanged) {
		
			////alpha range check (vertical view angle) => sodass man sich nicht sein Genack brechen kann ;D		
			double alphaRange = Math.toRadians(89);
			camera.alpha = camera.alpha > alphaRange ? alphaRange : (
					camera.alpha < -alphaRange ? -alphaRange : camera.alpha
			);
						
			////recalculate forward vector			
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};		
		}
		
		if((mouseMovement[0]!=0||mouseMovement[1]!=0)&&false) {
			
			double[][] closestTriangle = null;
			if(WORKING_WITH_GAMEOBJECTS)
				closestTriangle = mathstuff.getClosestTriangleGameObjects(Main.camera);
			else
				closestTriangle = mathstuff.getClosestTriangleRaw(Main.camera);
			
			if(closestTriangle != currentlyClosestTriangle) {
//				System.out.println("setting");
				if(closestTriangle != null)
					currentlyClosestTriangleColorOriginal = closestTriangle[3][0];
					closestTriangle[3][0] = editColor;
				if(currentlyClosestTriangle != null)
//					currentlyClosestTriangle[3][0] = currentlyClosestTriangleColorOriginal;
				currentlyClosestTriangle = closestTriangle;
			}
		}
	}	
	
	public static Camera getCamera(){
		return camera;
	}
	public static Window getWindow(){
		return window;
	}
//	public static void convertTriangles() {
//		double[] ab0;	// vector ab, unit vector		
//		double lambda;	// ab0 * lambda gives the point, on which the point of C sits in a 90° angle on
//		
//		double abLength;
//		
//		double[] resortCache;
//		
//		for(int triangleI = 0;triangleI < coords.length;triangleI++) {
//			
//			//calculate AB (unit)
//			ab0 = new double[] {coords[triangleI][1][0]-coords[triangleI][0][0],coords[triangleI][1][1]-coords[triangleI][0][1],coords[triangleI][1][2]-coords[triangleI][0][2]};			
//			abLength = Mathstuff.vectorUnify(ab0);
//			
//			//Vektor AC
////			ac0 = Mathstuff.vectorUnify(new double[] {coords[triangleI][2][0]-coords[triangleI][0][0],coords[triangleI][2][1]-coords[triangleI][0][1],coords[triangleI][2][2]-coords[triangleI][0][2]}, false);
////			ac0 = new double[] {coords[x][2][0]-coords[x][0][0],coords[x][2][1]-coords[x][0][1],coords[x][2][2]-coords[x][0][2]};
////			double acLength = Mathstuff.length(ac0);
////			ac0 = new double[] {ac0[0]/acLength,ac0[1]/acLength,ac0[2]/acLength};
//			//Vektor BC
////			bc = new double[]{coords[x][2][0]-coords[x][1][0],coords[x][2][1]-coords[x][1][1],coords[x][2][2]-coords[x][1][2]};
////			bcLength = Mathstuff.length(bc);
//			lambda = 
//			(ab0[0]*(coords[triangleI][2][0]-coords[triangleI][0][0])+ab0[1]*(coords[triangleI][2][1]-coords[triangleI][0][1])+ab0[2]*(coords[triangleI][2][2]-coords[triangleI][0][2]))
//							/
//			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
//			
//			if(lambda < 0) {
//				
//				resortCache = coords[triangleI][0];
//				coords[triangleI][0] = coords[triangleI][2];
//				coords[triangleI][2] = resortCache;
//				
//			} else if(lambda > abLength) {
//				resortCache = coords[triangleI][2];
//				coords[triangleI][2] = coords[triangleI][1];
//				coords[triangleI][1] = resortCache;
//			}			
//		}
//	}
	public static double[][][] loadCoords(boolean useColor){
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		try {

//			BufferedReader br = new BufferedReader(new FileReader(new File("E:/Admin/Desktop/Trash/mineways/mcworld3.raw")));
			BufferedReader br = new BufferedReader(new FileReader(new File("res/dragon.raw")));
			int scale = 10;
			
			while(br.ready()) {
				 
				String s = br.readLine();
				String[] coordinates = s.split(" ");
				double[][] vertices = new double[4][];
				
				vertices[0] = new double[] {Double.parseDouble(coordinates[0])*scale, Double.parseDouble(coordinates[1])*scale, Double.parseDouble(coordinates[2])*scale}; 
				vertices[1] = new double[] {Double.parseDouble(coordinates[3])*scale, Double.parseDouble(coordinates[4])*scale, Double.parseDouble(coordinates[5])*scale};
				vertices[2] = new double[] {Double.parseDouble(coordinates[6])*scale, Double.parseDouble(coordinates[7])*scale, Double.parseDouble(coordinates[8])*scale};
				
				triangles.add(vertices);
			}
			br.close();
			
			double[][][] cacheTriangles = triangles.toArray(new double[0][][]);
			for(int i = 0;i < triangles.size();i++){
				double r = Math.random();
				if(r < 0.3){
					cacheTriangles[i][3] = new double[] {useColor ? ((double) storeColor(Color.red.getRGB())) : -1};
				}else if(r < 0.6){
					cacheTriangles[i][3] = new double[] {useColor ? ((double) storeColor(Color.green.getRGB())) : -1};
				}else{
					cacheTriangles[i][3] = new double[] {useColor ? ((double) storeColor(Color.blue.getRGB())) : -1};
				}
//				cacheTriangles[i][3] = new double[] {-1};
			}
			return cacheTriangles;
		}catch(Exception e) {
			e.printStackTrace();
		}

		return new double[][][] {};
	}
	
	
	
	/**
	 * stores a color in the array, if it is not yet contained
	 */
	public static int storeColor(int rgb){
		//check if color is already contained in colors ArrayList
		for(int i = 0; i < colors.size(); i++) {
			if(colors.get(i).getRGB() == rgb)
				return i;
		}
		
		Color colorNew = new Color(rgb);
		colors.add(colorNew);
		return colors.size()-1;
	}
	
	public static Color getColorAt(int index){
		if(index < 0)
			return Color.WHITE;
		return colors.get(index);
	}
}
