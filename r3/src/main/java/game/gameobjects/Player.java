package game.gameobjects;

import java.awt.event.KeyEvent;

import game.physics.CollisionStuff;
import game.physics.Hitbox;
import r3.main.Main;
import r3.mathstuff.Camera;

public class Player extends GameObject {
	
	public static final double MOVEMENT_SPEED_ACCELERATION = 20;
	
	public Player() {
		super(new double[] {2, 0, 2}, new double[][][] {}, new Hitbox(0.1d), true, false);
//		Main.setCamera(new Camera());
	}
	
	public void move(double deltaTimeSeconds) {
		
		boolean[] register = Main.getWindow().getKeyRegister();
		Camera camera = Main.getCamera();
		
		double movementDelta = MOVEMENT_SPEED_ACCELERATION * deltaTimeSeconds;
		double movementDeltaX3;
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
			movementDeltaX3 = movementDelta*2;
			this.speedPerSec[2]+=movementDeltaX3;
		} else if(!register[KeyEvent.VK_SPACE] && (register[KeyEvent.VK_SHIFT]||register[KeyEvent.VK_E])) {
			this.speedPerSec[2]-=movementDelta;
		}
		
		
		
//		Main.getCamera().setPos(this.pos);
		
	}
	public void updatePosition(double deltaTimeSeconds) {
		if(deltaTimeSeconds == 0  || (this.speedPerSec[0] == 0 && this.speedPerSec[1] == 0 && this.speedPerSec[2] == 0))
			return;
		
		this.speedPerSec[0] *= 0.88;
		this.speedPerSec[1] *= 0.88;
		this.speedPerSec[2] *= 1;
		
		cachePosAfterMovement[0] = pos[0] + (speedPerSec[0] * deltaTimeSeconds);
		cachePosAfterMovement[1] = pos[1] + (speedPerSec[1] * deltaTimeSeconds);
		cachePosAfterMovement[2] = pos[2] + (speedPerSec[2] * deltaTimeSeconds);
		if(cachePosAfterMovement[2]<-20)
		{
			this.remove(false);
		}
		if(this.hitbox == null) {
			this.pos[0] = cachePosAfterMovement[0];
			this.pos[1] = cachePosAfterMovement[1];
			this.pos[2] = cachePosAfterMovement[2];
			return;
		}
		
		cachePosAfterMovementCache[0] = cachePosAfterMovement[0];
		cachePosAfterMovementCache[1] = pos[1];
		cachePosAfterMovementCache[2] = pos[2];		
		if(!CollisionStuff.collides(this, cachePosAfterMovementCache,pos))
			this.pos[0] = cachePosAfterMovement[0];
		
		cachePosAfterMovementCache[1] = cachePosAfterMovement[1];
		if(!CollisionStuff.collides(this, cachePosAfterMovementCache,pos))
				this.pos[1] = cachePosAfterMovement[1];				
		
		cachePosAfterMovementCache[2] = cachePosAfterMovement[2];		
		if(!CollisionStuff.collides(this, cachePosAfterMovementCache,pos))
				this.pos[2] = cachePosAfterMovement[2];
	}
//	public Camera getCamera(){
//		return this.camera;
//	}
}
