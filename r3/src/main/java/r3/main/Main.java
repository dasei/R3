package r3.main;

import java.awt.event.KeyEvent;

import r3.mathstuff.Camera;
import r3.window.Window;

public class Main {
	private static Window window;
	private static Camera camera;
	
	public static final double ROTATION_DIVISOR = 100d;	
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
					
					window.repaintSynchronous(new double[][][] {
						{{10, 10, 10}, {10, 20, 10}, {20, 20, 10}}
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
			Main.getCamera().pos[0]+=0.1;
		} else if(!register[KeyEvent.VK_W] && register[KeyEvent.VK_S]) {
			Main.getCamera().pos[0]-=0.1;
		}
		
		if(register[KeyEvent.VK_A] && !register[KeyEvent.VK_D]) {
			Main.getCamera().pos[1]+=0.1;
		} else if(!register[KeyEvent.VK_A] && register[KeyEvent.VK_D]) {
			Main.getCamera().pos[1]-=0.1;
		}
		
		if(register[KeyEvent.VK_SPACE] && !register[KeyEvent.VK_SHIFT]) {
			Main.getCamera().pos[2]+=0.1;
		} else if(!register[KeyEvent.VK_SPACE] && register[KeyEvent.VK_SHIFT]) {
			Main.getCamera().pos[2]-=0.1;
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
		
		
		System.out.println("alpha: " + camera.alpha + ", beta: " + camera.beta);
		
	}	
	
	public static Camera getCamera(){
		return camera;
	}
	public static Window getWindow(){
		return window;
	}
}
