package game.gameobjects;

import game.physics.Hitbox;
import r3.main.Main;

public class Player extends GameObject {
	
	public Player() {
		super(new double[][][] {}, new Hitbox(1d));
	}
	
	public void move() {
		this.pos = Main.getCamera().pos;
	}
}
