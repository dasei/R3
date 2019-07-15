package game.gameobjects;

import java.awt.event.KeyEvent;

import game.physics.Hitbox;
import r3.main.Main;
import r3.mathstuff.Camera;

public class Player extends GameObject {
	
	public static final double MOVEMENT_SPEED_ACCELERATION = 10;
	
	public Player() {
		super(new double[] {2, 0, 2}, new double[][][] {}, new Hitbox(0.2d), true);
//		Main.setCamera(new Camera());
	}
	
	public void move(double deltaTimeSeconds) {
		
		boolean[] register = Main.getWindow().getKeyRegister();
		Camera camera = Main.getCamera();
		
		double movementDelta = MOVEMENT_SPEED_ACCELERATION * deltaTimeSeconds;
		
		//update camera position
		if(register[KeyEvent.VK_W] ^ register[KeyEvent.VK_S]) {
			//normalize subvector of components x1 and x2 => divide x1 or x2 by pythagoras of x1 and x2
			double normalizationFactor = (1/Math.sqrt((camera.forward[0]*camera.forward[0])+(camera.forward[1]*camera.forward[1])))
					//and for performance reasons, multiply it with the movementDelta, if we're at it
					* movementDelta;
			
			if(register[KeyEvent.VK_W]) {
				this.speedPerSec[0]+=camera.forward[0]*normalizationFactor;
				this.speedPerSec[1]+=camera.forward[1]*normalizationFactor;
			} else {
				this.speedPerSec[0]-=camera.forward[0]*normalizationFactor;
				this.speedPerSec[1]-=camera.forward[1]*normalizationFactor;
			}
		}
		
		if(register[KeyEvent.VK_A] && !register[KeyEvent.VK_D]) {
			this.speedPerSec[0]+=Main.getCamera().left[0]*movementDelta;
			this.speedPerSec[1]+=Main.getCamera().left[1]*movementDelta;
		} else if(!register[KeyEvent.VK_A] && register[KeyEvent.VK_D]) {
			this.speedPerSec[0]-=Main.getCamera().left[0]*movementDelta;
			this.speedPerSec[1]-=Main.getCamera().left[1]*movementDelta;
		}
		
		if(register[KeyEvent.VK_SPACE] && !(register[KeyEvent.VK_SHIFT]||register[KeyEvent.VK_E])) {
			this.speedPerSec[2]+=movementDelta;
		} else if(!register[KeyEvent.VK_SPACE] && (register[KeyEvent.VK_SHIFT]||register[KeyEvent.VK_E])) {
			this.speedPerSec[2]-=movementDelta;
		}
		
		
		
//		Main.getCamera().setPos(this.pos);
		
	}
	
//	public Camera getCamera(){
//		return this.camera;
//	}
}
