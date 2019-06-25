package game.gameobjects;

import game.physics.CollisionStuff;
import game.physics.Hitbox;

public class GameObject {
	
	private double[][][] triangles;
//	private int[][][] trianglesDrawCoordinates2D; //needed? => check drawCoords2D int[][][] in main
	
	private Hitbox hitbox;
	
	protected double[] pos = new double[3];
	
	private double[] speedPerSec = new double[3];	
	
	public GameObject(double[][][] triangles, Hitbox hitbox) {
		this.triangles = triangles;
//		this.trianglesDrawCoordinates2D = new int[this.triangles.length][3][2];
		this.hitbox = hitbox;
	}

	private final double[] cachePosAfterMovement = new double[3];
	public final void updatePosition(double deltaTimeSeconds) {
		cachePosAfterMovement[0] = pos[0] + (speedPerSec[0] * deltaTimeSeconds);
		cachePosAfterMovement[1] = pos[1] + (speedPerSec[1] * deltaTimeSeconds);
		cachePosAfterMovement[2] = pos[2] + (speedPerSec[2] * deltaTimeSeconds);
		if(CollisionStuff.collides(this, cachePosAfterMovement)){
			
			return;
		}
		this.pos[0] = cachePosAfterMovement[0];
		this.pos[1] = cachePosAfterMovement[1];
		this.pos[2] = cachePosAfterMovement[2];
	}
	
	public void move() {}
	
	protected void setTriangles(double[][][] triangles) {
		this.triangles = triangles;
	}
	
	public double[][][] getTriangles() {
		return this.triangles;
	}
	
	public Hitbox getHitbox(){
		return this.hitbox;
	}
	
	public double[] getHitboxCenterAbsolute() { //TODO use hitbox offset
		return this.pos;
	}
	
//	public int[][][] getTrianglesDrawCoordinates2D() {
//		return this.trianglesDrawCoordinates2D;
//	}
}
