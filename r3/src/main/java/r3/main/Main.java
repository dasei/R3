package r3.main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.multithreading.ThreadProcessor;
import r3.window.Window;

public class Main {
	private final static Camera camera = new Camera();
	private final static Window window = new Window();;
	
	
	public static final int FPS_MAX = 60;
	public static int fpsCurrent = 0;
	public static int[][][] coordsDraw;
	public static final ArrayList<Color> colors = new ArrayList<Color>();
	public static double[][][] coords = loadCoords(true);
	
	public static void main(String[] args) {
		
////		System.out.println(Arrays.toString(Mathstuff.getInstance().vectorUnify(new double[] {0.5,0.5,0.5}, false)));
		
		
//		try {
//			Thread.sleep(5000);
//		} catch(Exception e){}
//		
//		ThreadProcessor.awakeAll();
//		
//		try {
//			Thread.sleep(5000);
//		} catch(Exception e){}
//		
//		ThreadProcessor.awakeAll();
//		
//		System.exit(0);
		
		
		
		
		
		
		window.init();
		
		convertTriangles();
//		startLoopThread();
		

		ThreadProcessor.startMultithreading(Main.coords.length, 8);
	}
	
	private static void startLoopThread() {
		(new Thread() {
			public void run() {		
				super.run();
				
				System.out.println("started main loop");
				
				
				//FPS counting => initialization
				long timeStartNanos = System.nanoTime();
				long timeNow;
				final int cyclesForFPSCalculation = 10;
				
				int cycleCounter = 0;
				while(true) {
					
					//process all pending inputs
					processInputs();
					
					//Mathstuff.calcR3(Main.coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
					
//					long timeBeginning = System.nanoTime();
					window.getDrawComp().repaint();

//					fps++;
//					long timeEnd = System.nanoTime();
					//System.out.println("Time: " + (timeEnd-timeBeginning));
					try {
						Thread.sleep(10);
					}catch(Exception e) {};
					
					
//					coordsDefault = new double[][][] {
////						{{-1.10,-0.50,0},{-1.10,0.50,0},{-1.10,0,0.50}}
//						{{-10,-5,0},{-10,5,0},{-10,0,5}}
//					};
					
					
					//FPS counting NEEDS TO BE AT THE VERY END OF MAIN LOOP
					cycleCounter++;
					if(cycleCounter % cyclesForFPSCalculation == 0) {
						timeNow = System.nanoTime();
						fpsCurrent = (int) ((1000000000d*cyclesForFPSCalculation)/(timeNow - timeStartNanos));
						//set new start time for next cycle
						timeStartNanos = timeNow;
//						System.out.println(( 1000000000d/(timeNow - timeStartNanos)) + ", " + timeNow + "/t" + timeStartNanos);
					}					
				}
				
			}
		}).start();
	}
	private static int indexSelected = -1;
	private static double editColor = (double) storeColor(Color.red.getRGB());
	private static final double MOVEMENT_SPEED_PER_SECOND = 10;
	public static final double ROTATION_SPEED_PER_SECOND = Math.toRadians(45); //radians	
	public static final double ROTATION_AMOUNT_PER_MOUSEMOVEMENT_PIXEL = Math.toRadians(0.25); //radians
	private static long processInputsTimeLastNanos = System.nanoTime();
	public static void processInputs() {
		
		//calculate delta time
		long timeNowNanos = System.nanoTime();
		double deltaTimeSeconds = (timeNowNanos - processInputsTimeLastNanos)/1000000000d;
		processInputsTimeLastNanos = timeNowNanos;
		
//		System.out.println(Arrays.toString(Main.getCamera().pos));
		
		//////KEYBOARD - MOVEMENT
		boolean[] register = window.getKeyRegister();
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
		if(register[KeyEvent.VK_ENTER])
		{
			if(indexSelected!=-1)
			coords[indexSelected][3][0] = editColor;
			indexSelected = -1;
		}
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
		if((mouseMovement[0]!=0||mouseMovement[1]!=0))
		{
			double lambdaCB;
			double lambdaAP;
			double lambdaP;
			double[] camPos = camera.pos;
			double[] forward = camera.forward;
			double[] vectorAC = new double[3];
			double[] vectorAB = new double[3];
			double[] vectorCB = new double[3];
			double[] vectorAP = new double[3];
			double[] pointP = new double[3];
			
			double lastDistance = Double.MAX_VALUE;
			int lastIndex = -1;
			for(int i = 0; i<coords.length;i++)
			{
				vectorAC[0] = coords[i][2][0] - coords[i][0][0];
				vectorAC[1] = coords[i][2][1] - coords[i][0][1];
				vectorAC[2] = coords[i][2][2] - coords[i][0][2];
				
				vectorAB[0] = coords[i][1][0] - coords[i][0][0];
				vectorAB[1] = coords[i][1][1] - coords[i][0][1];
				vectorAB[2] = coords[i][1][2] - coords[i][0][2];
				
				vectorCB[0] = coords[i][1][0] - coords[i][2][0];
				vectorCB[1] = coords[i][1][1] - coords[i][2][1];
				vectorCB[2] = coords[i][1][2] - coords[i][2][2];
				
				lambdaP =
				-((camPos[0]-coords[i][0][0])*(vectorAC[1]*vectorAB[2]-vectorAC[2]*vectorAB[1])+(camPos[1]-coords[i][0][1])*(vectorAC[2]*vectorAB[0]-vectorAC[0]*vectorAB[2])+(camPos[2]-coords[i][0][2])*(vectorAC[0]*vectorAB[1]-vectorAC[1]*vectorAB[0]))
						/
				(forward[0]*(vectorAC[1]*vectorAB[2]-vectorAC[2]*vectorAB[1]) + forward[1]*(vectorAC[2]*vectorAB[0]-vectorAC[0]*vectorAB[2]) + forward[2]*(vectorAC[0]*vectorAB[1]-vectorAC[1]*vectorAB[0]));
				
				if(lambdaP<0)
				{
					continue;
				}
				
				pointP[0] = lambdaP * forward[0] + camPos[0]; 
				pointP[1] = lambdaP * forward[1] + camPos[1];
				pointP[2] = lambdaP * forward[2] + camPos[2];
				
				vectorAP[0] = pointP[0] - coords[i][0][0];
				vectorAP[1] = pointP[1] - coords[i][0][1];
				vectorAP[2] = pointP[2] - coords[i][0][2];
				
				lambdaCB = 
				((-vectorAC[0]*vectorAP[1])+(vectorAC[1]*vectorAP[0]))
						/
				((vectorCB[0]*vectorAP[1])+(-vectorCB[1]*vectorAP[0]));
				
				lambdaAP = 
				(lambdaCB*vectorCB[2]+vectorAC[2])
						/
				(vectorAP[2]);
				if(lambdaAP>=1&&lambdaCB<=1&&lambdaCB>=0)
				{
					double distance = Mathstuff.calcR3Depth(pointP, camPos);
					if(distance<lastDistance)
					{
						lastDistance=distance;
						lastIndex = i;
					}
				}
			}
			if(lastIndex!=-1)
			{
				if(indexSelected!=-1)
					coords[indexSelected ][3][0] = -1;
				indexSelected = lastIndex;
				if(coords[lastIndex][3][0]==-1)
				coords[lastIndex][3][0] = editColor;
			}
			else
			{
				if(indexSelected!=-1)
				{
					coords[indexSelected][3][0] = -1;
					indexSelected = -1;
				}
			}
		}
//		System.out.println("alpha: " + camera.alpha + ", beta: " + camera.beta);
	}	
	
	public static Camera getCamera(){
		return camera;
	}
	public static Window getWindow(){
		return window;
	}
	public static void convertTriangles() {
//		if(1==1)
//		return;
//		System.out.println("CONVERTING TRIANGLES");
		double[] ab0;	// vector ab, unit vector		
		double lambda;	// ab0 * lambda gives the point, on which the point of C sits in a 90° angle on
		
		double[] ac0;	// vector ac
		
		
		double abLength;
		
//		double[] bc;
//		double[] o;
//		double lambda2End = 0;
//		double lambda3 = 0;
//		double lengthB = 0;
//		double bcLength = 0;
//		double lengthMiddle = 0;
//		double oLength = 0;
		
//		Mathstuff mathstuff = new Mathstuff(false);
		
		double[] resortCache;
		
		for(int triangleI = 0;triangleI < coords.length;triangleI++) {
			
			//calculate AB (unit)
			ab0 = new double[] {coords[triangleI][1][0]-coords[triangleI][0][0],coords[triangleI][1][1]-coords[triangleI][0][1],coords[triangleI][1][2]-coords[triangleI][0][2]};			
			abLength = Mathstuff.vectorUnify(ab0);		
//			ab0 = new double[] {coords[x][1][0]-coords[x][0][0],coords[x][1][1]-coords[x][0][1],coords[x][1][2]-coords[x][0][2]};
//			double abLength = Mathstuff.length(ab0);			
//			ab0 = new double[] {ab0[0]/abLength,ab0[1]/abLength,ab0[2]/abLength};
			
			//Vektor AC
			ac0 = Mathstuff.vectorUnify(new double[] {coords[triangleI][2][0]-coords[triangleI][0][0],coords[triangleI][2][1]-coords[triangleI][0][1],coords[triangleI][2][2]-coords[triangleI][0][2]}, false);
//			ac0 = new double[] {coords[x][2][0]-coords[x][0][0],coords[x][2][1]-coords[x][0][1],coords[x][2][2]-coords[x][0][2]};
//			double acLength = Mathstuff.length(ac0);
//			ac0 = new double[] {ac0[0]/acLength,ac0[1]/acLength,ac0[2]/acLength};
			//Vektor BC
//			bc = new double[]{coords[x][2][0]-coords[x][1][0],coords[x][2][1]-coords[x][1][1],coords[x][2][2]-coords[x][1][2]};
//			bcLength = Mathstuff.length(bc);
			lambda = 
			(ab0[0]*(coords[triangleI][2][0]-coords[triangleI][0][0])+ab0[1]*(coords[triangleI][2][1]-coords[triangleI][0][1])+ab0[2]*(coords[triangleI][2][2]-coords[triangleI][0][2]))
							/
			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
			
			if(lambda<0) {
//				System.out.println("Lambda before(<):" + lambda);
//				resortCache = coords[x][0];
//				coords[x][0] = coords[x][2];
//				coords[x][2] = coords[x][1];
//				coords[x][1] = resortCache;
				
				resortCache = coords[triangleI][0];
				coords[triangleI][0] = coords[triangleI][2];
				coords[triangleI][2] = resortCache;
				
			} else if(lambda > abLength) {
//				System.out.println("Lambda before(>):" + lambda);
				resortCache = coords[triangleI][2];
				coords[triangleI][2] = coords[triangleI][1];
				coords[triangleI][1] = resortCache;
			}
			
//			lambda = 
//	    	(ab0[0]*(coords[x][2][0]-coords[x][0][0])+ab0[1]*(coords[x][2][1]-coords[x][0][1])+ab0[2]*(coords[x][2][2]-coords[x][0][2]))
//							/
//			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
//			System.out.println("Lambda after:" + lambda);
		}
	}
	public static double[][][] loadCoords(boolean useColor){
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		try {

			BufferedReader br = new BufferedReader(new FileReader(new File("res/dragon.raw")));

		
			int scale = 10;
			
			Color color;
			while(br.ready()) {
				 
				String s = br.readLine();
				String[] coordinates = s.split(" ");
				double[][] vertices = new double[4][];
				
				vertices[0] = new double[] {Double.parseDouble(coordinates[0])*scale, Double.parseDouble(coordinates[1])*scale, Double.parseDouble(coordinates[2])*scale}; 
				vertices[1] = new double[] {Double.parseDouble(coordinates[3])*scale, Double.parseDouble(coordinates[4])*scale, Double.parseDouble(coordinates[5])*scale};
				vertices[2] = new double[] {Double.parseDouble(coordinates[6])*scale, Double.parseDouble(coordinates[7])*scale, Double.parseDouble(coordinates[8])*scale};
				
				//store color in forth index
				double r = Math.random();
				if(r < 0.3){
//					vertices[3] = new double[] {useColor ? ((double) storeColor(Color.red.getRGB())) : -1};
//					vertices[3] = new double[] {useColor ? ((double) storeColor(Color.PINK.getRGB())) : -1};
				}else if(r < 0.6){
//					vertices[3] = new double[] {useColor ? ((double) storeColor(Color.green.getRGB())) : -1};
//					vertices[3] = new double[] {useColor ? ((double) storeColor(Color.BLUE.getRGB())) : -1};
				}else{
//					vertices[3] = new double[] {useColor ? ((double) storeColor(Color.blue.getRGB())) : -1};
//					vertices[3] = new double[] {-1};
//					vertices[3] = new double[] {useColor ? ((double) storeColor(Color.green.getRGB())) : -1};
				}
				vertices[3] = new double[] {-1};
				
//				vertices[3] = new double[] {(double) Color.PINK.getRGB()};
					
				//System.out.println(vertices[3][0]);
//				new Color(Color.red.getRGB())
//				System.out.println((int) ((double) Color.red.getRGB()) + ", " + Color.red.getRGB());
				
//				try{
//					Thread.sleep(1000);
//				}catch(Exception e){}
				
					
				triangles.add(vertices);
			}
			br.close();
			coordsDraw = new int[triangles.size()][3][2];
			return triangles.toArray(new double[0][][]);		
		}catch(Exception e) {
			e.printStackTrace();
		}

		return new double[][][] {};
/////////////////////////////////////////////////////		
//		return new double[][][] {
//			//{{-10,-5,5},{-10,5,5},{-15,0,-5}},
//			{{0,0,0},{0,5,0},{0,5,10}}
//		};
/////////////////////////////////////////////////////
//		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
//		Random random = new Random();
//		for(int i = 0;i<1;i++)
//		{
//			triangles.add(
//					new double[][] {
//						{random.nextInt(1000),random.nextInt(1000),random.nextInt(1000)},
//						{random.nextInt(1000),random.nextInt(1000),random.nextInt(1000)},
//						{random.nextInt(1000),random.nextInt(1000),random.nextInt(1000)}
//					}
//					);
//		}
//		coordsDraw = new int[triangles.size()][3][2];
//		return triangles.toArray(new double[0][][]);
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
		return colors.get(index);
	}
//	private static final Color colorDefault = Color.black;
//	public static Color getColorWith(int ) {
//		for(Color c : colors) {
//			if(c.getRGB() == rgb) {
//				return c;
//			}
//		}
//		return colorDefault;
//	}
}
