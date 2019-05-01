package r3.main;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.window.Window;

public class Main {
	private static Window window;
	private static Camera camera;
	
	public static final double ROTATION_DIVISOR = 500d;	
	public static final int FPS_MAX = 60;
	public static int fps = 0;
	public static int[][][] coordsDraw;
	public static double[][][] coords = loadCoords();
	
	public static void main(String[] args) {
		camera = new Camera();
		
		window = new Window();
		
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
						Thread.sleep(100000000);
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
						Thread.sleep(1000000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(fps);
					fps=0;
				}
				
			}
		}).start();
	}
	
	private static void processInputs() {
		
		//////KEYBOARD - MOVEMENT
		boolean[] register = window.getKeyRegister();
		
		if(register[KeyEvent.VK_W] && !register[KeyEvent.VK_S]) {
			Main.getCamera().pos[0]+=Main.getCamera().forward[0];
			Main.getCamera().pos[1]+=Main.getCamera().forward[1];
			Main.getCamera().pos[2]+=Main.getCamera().forward[2];
		} else if(!register[KeyEvent.VK_W] && register[KeyEvent.VK_S]) {
			Main.getCamera().pos[0]-=Main.getCamera().forward[0];
			Main.getCamera().pos[1]-=Main.getCamera().forward[1];
			Main.getCamera().pos[2]-=Main.getCamera().forward[2];
		}
		
		if(register[KeyEvent.VK_A] && !register[KeyEvent.VK_D]) {
			Main.getCamera().pos[0]+=Main.getCamera().left[0];
			Main.getCamera().pos[1]+=Main.getCamera().left[1];
			Main.getCamera().pos[2]+=Main.getCamera().left[2];
		} else if(!register[KeyEvent.VK_A] && register[KeyEvent.VK_D]) {
			Main.getCamera().pos[0]-=Main.getCamera().left[0];
			Main.getCamera().pos[1]-=Main.getCamera().left[1];
			Main.getCamera().pos[2]-=Main.getCamera().left[2];
		}
		
		if(register[KeyEvent.VK_SPACE] && !register[KeyEvent.VK_SHIFT]) {
			Main.getCamera().pos[2]+=1;
		} else if(!register[KeyEvent.VK_SPACE] && (register[KeyEvent.VK_SHIFT]||register[KeyEvent.VK_E])) {
			Main.getCamera().pos[2]-=1;
		}
		
		
		
		//////MOUSE - ROTATION
		if(register[KeyEvent.VK_UP] && !register[KeyEvent.VK_DOWN]) {
			Main.getCamera().alpha+=10/ROTATION_DIVISOR;
			//Alpha
			camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
			camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
			//Beta
			camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
			camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
		} else if(!register[KeyEvent.VK_UP] && register[KeyEvent.VK_DOWN]) {
			Main.getCamera().alpha-=10/ROTATION_DIVISOR;
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
			System.out.println(Arrays.toString(mouseMovement));
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
	
	public static double[][][] loadCoords(){
//		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(new File("E:/Bibliotheken/Downloads/Dragon.raw")));
////			BufferedReader br = new BufferedReader(new FileReader(new File("H://testactive.raw")));
//		
//			int scale = 10;
//			
//			while(br.ready()) {
//				 
//				String s = br.readLine();
//				String[] coordinates = s.split(" ");
//				triangles.add(
//						new double[][] {
//							{Double.parseDouble(coordinates[0])*scale,Double.parseDouble(coordinates[1])*scale,Double.parseDouble(coordinates[2])*scale},
//							{Double.parseDouble(coordinates[3])*scale,Double.parseDouble(coordinates[4])*scale,Double.parseDouble(coordinates[5])*scale},
//							{Double.parseDouble(coordinates[6])*scale,Double.parseDouble(coordinates[7])*scale,Double.parseDouble(coordinates[8])*scale}
//						}
//				);
//				
////				p.addVertex(new Point3D(Double.parseDouble(coordinates[0])*scale,Double.parseDouble(coordinates[1])*scale, Double.parseDouble(coordinates[2])*scale));
////				p.addVertex(new Point3D(Double.parseDouble(coordinates[3])*scale,Double.parseDouble(coordinates[4])*scale, Double.parseDouble(coordinates[5])*scale));
////				p.addVertex(new Point3D(Double.parseDouble(coordinates[6])*scale,Double.parseDouble(coordinates[7])*scale, Double.parseDouble(coordinates[8])*scale));
//			}
//			br.close();
//			coordsDraw = new int[triangles.size()][3][2];
//			return triangles.toArray(new double[0][][]);
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return new double[][][] {};
		return new double[][][] {
		//{{-1.10,-0.50,0},{-1.10,0.50,0},{-1.10,0,0.50}}
		{{-10,-5,5},{-10,5,5},{-10,5,-5}}};
	}
}
