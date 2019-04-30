package r3.main;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import r3.mathstuff.Camera;
import r3.window.Window;

public class Main {
	private static Window window;
	private static Camera camera;
	
	public static final double ROTATION_DIVISOR = 1000d;	
	public static final int FPS_MAX = 60;
	
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
					System.out.println("-----------------------------");
					window.repaintSynchronous(new double[][][] {
						
					});
					
					try {
						Thread.sleep(100);
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
		camera.alpha += mouseMovement[1]/ROTATION_DIVISOR;
		camera.beta += mouseMovement[0]/ROTATION_DIVISOR;
		
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
		
		System.out.println("alpha: " + camera.alpha + ", beta: " + camera.beta);
		
	}	
	
	public static Camera getCamera(){
		return camera;
	}
	public static Window getWindow(){
		return window;
	}
}
