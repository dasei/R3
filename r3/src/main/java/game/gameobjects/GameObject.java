package game.gameobjects;

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
	
	public final void updatePosition(double deltaTimeSeconds) {
		this.pos[0] += speedPerSec[0];
		this.pos[1] += speedPerSec[1];
		this.pos[2] += speedPerSec[2];
	}
	
	public void move() {}
	
	protected void setTriangles(double[][][] triangles) {
		this.triangles = triangles;
	}
	
	public double[][][] getTriangles() {
		return this.triangles;
	}
	
//	public int[][][] getTrianglesDrawCoordinates2D() {
//		return this.trianglesDrawCoordinates2D;
//	}
}
