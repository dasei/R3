package game;

import java.awt.Color;
import java.util.ArrayList;

import game.gameobjects.Floor;
import game.gameobjects.GameObject;
import game.gameobjects.Player;
import r3.main.Main;
import r3.mathstuff.Mathstuff;
import r3.multithreading.ThreadProcessor;

public class Game {
	
	private static Game game;
	
	private Player player;
	
	public static boolean GRAVITY = true;
	public static boolean SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN = false;
	public static boolean ANTIALIAZING = true;
	
	public static void main(String[] args) {
		Main.WORKING_WITH_GAMEOBJECTS = true;
		
		Main.getWindow().init(); 
		Main.getWindow().setTitle(Main.getWindow().getTitle() + "GAME");	
		
		getGame();
	}
	
	public static Game getGame(){
		if(game == null)
			game = new Game();
		return game;
	}
	
	
	
	
	
	public static final int FPS = 60;
	public static final boolean fps_cap = true;
	
	private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	
	
	public Game() {
		//PLAYER (without it, camera is not really movable, if the program was started via the main method in class Game)
		this.player = new Player();
		this.gameObjects.add(player);
		
		//FLOOR
		this.gameObjects.add(new Floor(0, 0, 0, Main.storeColor(Color.green.getRGB())));
		
		//CUBE
		GameObject cube = Mathstuff.generateCube(new double[] {0, 0, 2}, 1, Main.storeColor(Color.blue.getRGB()));
		cube.setSpeedPerSecond(new double[] {0,0,-0.1});
		this.gameObjects.add(cube);
		
		
//		//DRAGON
//		this.addGameObject(
//				FileLoader.colorize(
//						FileLoader.loadTrianglesFromFile(new File("res/dragon.raw"), true, 1),
//						Color.red, Color.blue, Color.pink
//				)
//		);
		
		
		
		
		//------------------
		//##################
		//------------------
		ThreadProcessor.startMultithreadingGame(this.gameObjects, 4, true);
		startGameLoop();
	}
	
	public void addGameObject(double[][][] triangles) {
		this.gameObjects.add(new GameObject(triangles, null));
	}
	
//	public void addGameObject(GameObject gameObject) {
//		this.gameObjects.add(gameObject);
//	}
	
	private void startGameLoop() {
		(new Thread() {
			public void run() {				
				
				
				double deltaTimeSeconds = 0;
				
				long iterationStart, duration;
				while(true) {
					iterationStart = System.currentTimeMillis();					
					///--- LOOP
					
					for(GameObject gameObject : gameObjects) {
						gameObject.move(deltaTimeSeconds);
						if(GRAVITY)
							gameObject.induceGravityToSpeeds(deltaTimeSeconds);
					}
					
					for(GameObject gameObject : gameObjects) {
						gameObject.updatePosition(deltaTimeSeconds);
					}
					
					
					///--- LOOP
					duration = System.currentTimeMillis() - iterationStart;
					
					if(fps_cap && duration < 1000 / FPS) {						
						try {
							Thread.sleep((1000 / FPS) - (duration));
						} catch(Exception e) {}
					}					
					
					deltaTimeSeconds = (System.currentTimeMillis() - iterationStart) / 1000d;
				}				
			}
		}).start();
	}
	
	public ArrayList<GameObject> getGameObjects() {
		return this.gameObjects;
	}
	
	public void syncCameraWithPlayer() {
		if(this.player == null)
			return;
		
		Main.getCamera().setPos(player.getPos());
	}
}
