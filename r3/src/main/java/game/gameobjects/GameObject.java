package game.gameobjects;

import java.util.Arrays;

import javax.swing.plaf.synth.SynthSpinnerUI;

import game.physics.CollisionStuff;
import game.physics.Hitbox;
import r3.main.Main;
import r3.window.Window;

public class GameObject {
	
	/**
	 * the triangles of this GameObject relative to the GameObject 
	 */
	private double[][][] trianglesRelative;
	
	private double[][][] trianglesAbsolute;
	
//	private int[][][] trianglesDrawCoordinates2D; //needed? => check drawCoords2D int[][][] in main
	
	protected Hitbox hitbox;
	
	protected double[] pos = new double[3];
	
	protected double[] speedPerSec = new double[3];	
	
	private boolean gravityAffected = false; 
	
	private boolean damageAffected = false;
	
	private boolean visible = true;
	
	public GameObject(double[][][] triangles, Hitbox hitbox) {
		this.trianglesRelative = triangles;
		this.hitbox = hitbox;
	}
	
	public GameObject(double[] pos, double[][][] triangles, Hitbox hitbox, boolean gravityAffected, boolean damageAffected) {
		this(triangles, hitbox);
		this.pos = pos;
		this.gravityAffected = gravityAffected;
		this.damageAffected = damageAffected;
	}

	protected final double[] cachePosAfterMovement = new double[3];
	protected final double[] cachePosAfterMovementCache = new double[3];
	public void updatePosition(double deltaTimeSeconds) {
		if(deltaTimeSeconds == 0  || (this.speedPerSec[0] == 0 && this.speedPerSec[1] == 0 && this.speedPerSec[2] == 0))
			return;
		
		this.speedPerSec[0] *= 0.88;
		this.speedPerSec[1] *= 0.88;
		this.speedPerSec[2] *= 0.88;
		
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
		if(!CollisionStuff.collides(this, cachePosAfterMovementCache))
			this.pos[0] = cachePosAfterMovement[0];
		
		cachePosAfterMovementCache[1] = cachePosAfterMovement[1];
		if(!CollisionStuff.collides(this, cachePosAfterMovementCache))
				this.pos[1] = cachePosAfterMovement[1];				
		
		cachePosAfterMovementCache[2] = cachePosAfterMovement[2];		
		if(!CollisionStuff.collides(this, cachePosAfterMovementCache))
				this.pos[2] = cachePosAfterMovement[2];
	}
	
	public void move(double deltaTimeSeconds) {}
	
	public void induceGravityToSpeeds(double deltaTimeSeconds) {
		if(!this.gravityAffected)
			return;
		this.speedPerSec[0] += deltaTimeSeconds * Main.gravityForce[0];
		this.speedPerSec[1] += deltaTimeSeconds * Main.gravityForce[1];
		this.speedPerSec[2] += deltaTimeSeconds * Main.gravityForce[2];
	}
	
	protected void setTriangles(double[][][] triangles) {
		this.trianglesRelative = triangles;
	}
	
	public double[][][] getTriangles() {
		return this.trianglesRelative;
	}
	
	public double[][][] getTrianglesAbsolute() {
		if(trianglesRelative.length == 0){
			if(trianglesAbsolute == null)
				trianglesAbsolute = new double[0][0][0];
			return trianglesAbsolute;
		}
		
		if(
				trianglesAbsolute == null
			||	trianglesAbsolute.length != trianglesRelative.length
			||	trianglesAbsolute[0].length != trianglesRelative[0].length
			||	trianglesAbsolute[0][0].length != trianglesRelative[0][0].length
		) {
			trianglesAbsolute = new double[trianglesRelative.length][trianglesRelative[0].length][trianglesRelative[0][0].length];
		}
		
		for(int triangleI = 0; triangleI < trianglesRelative.length; triangleI++) {
			trianglesAbsolute[triangleI][0][0] = trianglesRelative[triangleI][0][0] + this.pos[0];			
			trianglesAbsolute[triangleI][0][1] = trianglesRelative[triangleI][0][1] + this.pos[1];
			trianglesAbsolute[triangleI][0][2] = trianglesRelative[triangleI][0][2] + this.pos[2];
			
			trianglesAbsolute[triangleI][1][0] = trianglesRelative[triangleI][1][0] + this.pos[0];
			trianglesAbsolute[triangleI][1][1] = trianglesRelative[triangleI][1][1] + this.pos[1];
			trianglesAbsolute[triangleI][1][2] = trianglesRelative[triangleI][1][2] + this.pos[2];
			
			trianglesAbsolute[triangleI][2][0] = trianglesRelative[triangleI][2][0] + this.pos[0];
			trianglesAbsolute[triangleI][2][1] = trianglesRelative[triangleI][2][1] + this.pos[1];
			trianglesAbsolute[triangleI][2][2] = trianglesRelative[triangleI][2][2] + this.pos[2];
			
			if(trianglesRelative[triangleI].length > 3)
				trianglesAbsolute[triangleI][3][0] = trianglesRelative[triangleI][3][0];
		}
		return trianglesAbsolute;
	}
	
	public Hitbox getHitbox(){
		return this.hitbox;
	}
	
	public double[] getHitboxCenterAbsolute() { //TODO use hitbox offset
		return this.pos;
	}
	
	public double[] getPos(){
		return this.pos;
	}
	
	public void setSpeedPerSecond(double[] speedPerSecond) {
		if(speedPerSecond == null)
			return;
		this.speedPerSec = speedPerSecond;
	}
	
	public boolean isAffectedByGravity() {
		return this.gravityAffected;
	}
	public boolean isDamageAffected() {
		return this.damageAffected;
	}
	public boolean isRemoved()
	{
		return !this.visible;
	}
	public void remove(boolean sound)
	{
		if(sound)
		Window.playSound2();
		
		this.visible = false;
	}
//	public int[][][] getTrianglesDrawCoordinates2D() {
//		return this.trianglesDrawCoordinates2D;
//	}
}
