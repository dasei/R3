package r3.main;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import r3.mathstuff.Camera;
import r3.window.Window;

public class Main {
	private static Window window;
	private static Camera camera;
	
	public static final double ROTATION_DIVISOR = 500d;	
	public static final int FPS_MAX = 60;
	
	public static double[][][] coordsDraw;
	public static final double[][][] coordsDefault = loadCoords();
	
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
					
					
//					double[][][] coords = new double[coordsDefault.length][3][3];
//					for(int t = 0; t < coords.length; t++) {
//						for(int p = 0; p < 3; p++) {
//							for(int v = 0; v < 3; v++) {
//								coords[t][p][v] = coordsDefault[t][p][v];
//							}
//						}
//					}
					
//					double[][][] coords = new double[][][] {
////						{{-1.10,-0.50,0},{-1.10,0.50,0},{-1.10,0,0.50}}
//						{{-10,-5,0},{-10,5,0},{-10,0,5}}
//					};					
					
					
//					System.out.println("-----------------------------");
					window.getDrawComp().repaint();
					
					try {
						Thread.sleep(1);
					}catch(Exception e) {};
					
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
		} else if(!register[KeyEvent.VK_SPACE] && register[KeyEvent.VK_SHIFT]) {
			Main.getCamera().pos[2]-=1;
		}
		
		
		
		//////MOUSE - ROTATION
		int[] mouseMovement = window.getMouseMovementPixelSinceLastInvoke();
		camera.alpha += -mouseMovement[1]/ROTATION_DIVISOR;
		camera.beta += -mouseMovement[0]/ROTATION_DIVISOR;
		
		if(register[KeyEvent.VK_UP] && !register[KeyEvent.VK_DOWN]) {
			Main.getCamera().alpha+=10/ROTATION_DIVISOR;
		} else if(!register[KeyEvent.VK_UP] && register[KeyEvent.VK_DOWN]) {
			Main.getCamera().alpha-=10/ROTATION_DIVISOR;
		}
		
		if(register[KeyEvent.VK_LEFT] && !register[KeyEvent.VK_RIGHT]) {
			Main.getCamera().beta+=10/ROTATION_DIVISOR;
		} else if(!register[KeyEvent.VK_LEFT] && register[KeyEvent.VK_RIGHT]) {
			Main.getCamera().beta-=10/ROTATION_DIVISOR;
		}
		//Alpha
		camera.forward = new double[]{Math.cos(camera.alpha)*Camera.forwardDEFAULT[0] + Math.sin(camera.alpha)*Camera.forwardDEFAULT[2],Camera.forwardDEFAULT[1],-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2]};
		camera.left    = new double[]{Math.cos(camera.alpha)*Camera.leftDEFAULT[0] + Math.sin(camera.alpha)*Camera.leftDEFAULT[2],Camera.leftDEFAULT[1],-Math.sin(camera.alpha)*Camera.leftDEFAULT[0] + Math.cos(camera.alpha)*Camera.leftDEFAULT[2]};
		//camera.up 	   = new double[]{Math.cos(camera.alpha)*Camera.upDEFAULT[0] + Math.sin(camera.alpha)*Camera.upDEFAULT[2],Camera.upDEFAULT[1],-Math.sin(camera.alpha)*Camera.upDEFAULT[0] + Math.cos(camera.alpha)*Camera.upDEFAULT[2]};
		//System.out.println("Forward:X3: "+(-Math.sin(camera.alpha)*Camera.forwardDEFAULT[0] + Math.cos(camera.alpha)*Camera.forwardDEFAULT[2])+",or: "+camera.forward[2]);
		//Beta
		camera.forward = new double[]{Math.cos(camera.beta)*camera.forward[0]-Math.sin(camera.beta)*camera.forward[1],Math.sin(camera.beta)*camera.forward[0] + Math.cos(-camera.beta)*camera.forward[1],camera.forward[2]};
		camera.left    = new double[]{Math.cos(camera.beta)*camera.left[0]-Math.sin(camera.beta)*camera.left[1],Math.sin(camera.beta)*camera.left[0] + Math.cos(-camera.beta)*camera.left[1],camera.left[2]};
		//camera.up      = new double[]{Math.cos(camera.beta)*camera.up[0]-Math.sin(camera.beta)*camera.up[1],Math.sin(camera.beta)*camera.up[0] + Math.cos(-camera.beta)*camera.up[1],camera.up[2]};
		//System.out.println(Arrays.toString(camera.forward));
		
//		System.out.println("alpha: " + camera.alpha + ", beta: " + camera.beta);
		
	}	
	
	public static Camera getCamera(){
		return camera;
	}
	public static Window getWindow(){
		return window;
	}
	
	public static double[][][] loadCoords(){
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("E:/Bibliotheken/Downloads/Dragon.raw")));
		
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
			coordsDraw = new double[triangles.size()][3][2];
			return triangles.toArray(new double[0][][]);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new double[][][] {};
	}
}
