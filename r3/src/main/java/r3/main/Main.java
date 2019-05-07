package r3.main;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.window.Window;

public class Main {
	private static Window window;
	private static Camera camera;
	
	public static final double ROTATION_DIVISOR = 500d;	
	public static final int FPS_MAX = 60;
	public static int fps = 0;
	public static int lastFps = 0;
	public static int[][][] coordsDraw;
	public static double[][][] coords = loadCoords();
	
	public static void main(String[] args) {
		camera = new Camera();
		
		window = new Window();
		convertTriangles();
		startLoop();		
	}
	
	private static void startLoop() {
		(new Thread() {
			public void run() {				
				super.run();
				
				System.out.println("started main loop");
				while(true) {
					processInputs();
					
					//Mathstuff.calcR3(Main.coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
					
					long timeBeginning = System.nanoTime();
					window.getDrawComp().repaint();

					fps++;
					long timeEnd = System.nanoTime();
					//System.out.println("Time: " + (timeEnd-timeBeginning));
					try {
						Thread.sleep(10);
					}catch(Exception e) {};				

//					coordsDefault = new double[][][] {
////						{{-1.10,-0.50,0},{-1.10,0.50,0},{-1.10,0,0.50}}
//						{{-10,-5,0},{-10,5,0},{-10,0,5}}
//					};				
				}
				
			}
		}).start();
		(new Thread() {
			public void run() {				
				super.run();
				
				
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.out.println(fps);
					lastFps = fps;
					fps=0;
				}
				
			}
		}).start();
	}
	
	private static final double movementSpeedPerSecond = 0.05;
	private static long processInputsTimeLastNanos = System.nanoTime();
	private static void processInputs() {
		
		//calculate delta time
		long timeNowNanos = System.nanoTime();
		double deltaTimeSeconds = (timeNowNanos - processInputsTimeLastNanos)/1000000d;
		processInputsTimeLastNanos = timeNowNanos;
		
//		System.out.println(Arrays.toString(Main.getCamera().pos));
		
		//////KEYBOARD - MOVEMENT
		boolean[] register = window.getKeyRegister();
		double movementDelta = movementSpeedPerSecond * deltaTimeSeconds;
		
		if(register[KeyEvent.VK_W] && !register[KeyEvent.VK_S]) {
			Main.getCamera().pos[0]+=Main.getCamera().forward[0]*movementDelta;
			Main.getCamera().pos[1]+=Main.getCamera().forward[1]*movementDelta;
		} else if(!register[KeyEvent.VK_W] && register[KeyEvent.VK_S]) {
			Main.getCamera().pos[0]-=Main.getCamera().forward[0]*movementDelta;
			Main.getCamera().pos[1]-=Main.getCamera().forward[1]*movementDelta;
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
		
		
		
		//////MOUSE - ROTATION
		if(register[KeyEvent.VK_UP] && !register[KeyEvent.VK_DOWN]) {
			Main.getCamera().alpha = Math.min(Main.getCamera().alpha + (10/ROTATION_DIVISOR), (89d/180d)*Math.PI);
			
			//max value
			
			
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
		} else if(!register[KeyEvent.VK_UP] && register[KeyEvent.VK_DOWN]) {
//			Main.getCamera().alpha-=10/ROTATION_DIVISOR;
			Main.getCamera().alpha = Math.max(Main.getCamera().alpha - (10/ROTATION_DIVISOR), (-89d/180d)*Math.PI);
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
		}
		
		if(register[KeyEvent.VK_LEFT] && !register[KeyEvent.VK_RIGHT]) {
			Main.getCamera().beta+=10/ROTATION_DIVISOR;
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
		} else if(!register[KeyEvent.VK_LEFT] && register[KeyEvent.VK_RIGHT]) {
			Main.getCamera().beta-=10/ROTATION_DIVISOR;
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
		}
		int[] mouseMovement = window.getMouseMovementPixelSinceLastInvoke();
		if(mouseMovement[0]!=0||mouseMovement[1]!=0)
		{
			//System.out.println(Arrays.toString(mouseMovement));
			camera.alpha += -mouseMovement[1]/ROTATION_DIVISOR;
			camera.beta += -mouseMovement[0]/ROTATION_DIVISOR;
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
			
			
		}
//		System.out.println("alpha: " + camera.alpha + ", beta: " + camera.beta);
		
	}	
	
	public static Camera getCamera(){
		return camera;
	}
	public static Window getWindow(){
		return window;
	}
	public static void convertTriangles()
	{
		double[] ab0;
		double[] ac;
		double[] bc;
		double lambda;
		double[] o;
		double lambda2End = 0;
		double lambda3 = 0;
		double lengthB = 0;
		double bcLength = 0;
		double lengthMiddle = 0;
		double oLength = 0;
		for(int x = 0;x<coords.length;x++)
		{
			ab0 = new double[] {coords[x][1][0]-coords[x][0][0],coords[x][1][1]-coords[x][0][1],coords[x][1][2]-coords[x][0][2]};
			double abLength = Mathstuff.length(ab0);
			double[] ab = ab0;
			ab0 = new double[] {ab0[0]/abLength,ab0[1]/abLength,ab0[2]/abLength};
			//Vektor AC
			ac = new double[] {coords[x][2][0]-coords[x][0][0],coords[x][2][1]-coords[x][0][1],coords[x][2][2]-coords[x][0][2]};
			double acLength = Mathstuff.length(ac);
			ac = new double[] {ac[0]/acLength,ac[1]/acLength,ac[2]/acLength};
			//Vektor BC
			bc = new double[]{coords[x][2][0]-coords[x][1][0],coords[x][2][1]-coords[x][1][1],coords[x][2][2]-coords[x][1][2]};
			bcLength = Mathstuff.length(bc);
			lambda = 
			(ab0[0]*(coords[x][2][0]-coords[x][0][0])+ab0[1]*(coords[x][2][1]-coords[x][0][1])+ab0[2]*(coords[x][2][2]-coords[x][0][2]))
							/
			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
			if(lambda<0)
			{
				System.out.println("Lambda before(<):" + lambda);
				double[] aCache = coords[x][0];
				coords[x][0] = coords[x][2];
				coords[x][2] = coords[x][1];
				coords[x][1] = aCache;
			}
			else if(lambda > abLength)
			{
				System.out.println("Lambda before(>):" + lambda);
				double[] cCache = coords[x][2];
				coords[x][2] = coords[x][1];
				coords[x][1] = cCache;
			}
			lambda = 
	    	(ab0[0]*(coords[x][2][0]-coords[x][0][0])+ab0[1]*(coords[x][2][1]-coords[x][0][1])+ab0[2]*(coords[x][2][2]-coords[x][0][2]))
							/
			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
			System.out.println("Lambda after:" + lambda);
		}
	}
	public static double[][][] loadCoords(){
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("res/Dragon.raw")));

		
			int scale = 10;
			
			while(br.ready()) {
				 
				String s = br.readLine();
				String[] coordinates = s.split(" ");
				triangles.add(
						new double[][] {
							{Double.parseDouble(coordinates[0])*scale,Double.parseDouble(coordinates[1])*scale,Double.parseDouble(coordinates[2])*scale},
							{Double.parseDouble(coordinates[3])*scale,Double.parseDouble(coordinates[4])*scale,Double.parseDouble(coordinates[5])*scale},
							{Double.parseDouble(coordinates[6])*scale,Double.parseDouble(coordinates[7])*scale,Double.parseDouble(coordinates[8])*scale}
						}
				);
				
//				p.addVertex(new Point3D(Double.parseDouble(coordinates[0])*scale,Double.parseDouble(coordinates[1])*scale, Double.parseDouble(coordinates[2])*scale));
//				p.addVertex(new Point3D(Double.parseDouble(coordinates[3])*scale,Double.parseDouble(coordinates[4])*scale, Double.parseDouble(coordinates[5])*scale));
//				p.addVertex(new Point3D(Double.parseDouble(coordinates[6])*scale,Double.parseDouble(coordinates[7])*scale, Double.parseDouble(coordinates[8])*scale));
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
//		{{-10,-5,5},{-10,5,5},{-15,0,-5}}};
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
}
