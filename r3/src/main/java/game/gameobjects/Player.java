package game.gameobjects;

import game.physics.Hitbox;
import r3.main.Main;
import r3.mathstuff.Camera;

public class Player extends GameObject {
	
	public Player() {
		super(new double[][][] {}, new Hitbox(1d));
		Main.setCamera(new Camera());
	}
	
	public void move() {
		boolean[] keyregister = Main.getWindow().getKeyRegister();
		
		//update camera position
		
	}
	
	public Camera getCamera(){
		return this.camera;
	}
}
