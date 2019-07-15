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
	
	public static void main(String[] args) {
		Main.WORKING_WITH_GAMEOBJECTS = true;
		
		Main.getWindow().init(); Main.getWindow().setTitle(Main.getWindow().getTitle() + "GAME");	
		
		getGame();
	}
	
	public static Game getGame(){
		if(game == null)
			game = new Game();
		return game;
	}
	
	
	
	
	
	public static final int FPS = 60;
	public static final boolean fps_cap = true;
	
	private ArrayList<GameObject> gameObjects;
	
	
	public Game() {
		this.gameObjects = new ArrayList<GameObject>();
		
		
		//--gameObjects		
		ArrayList<GameObject> gameObjectsStart = new ArrayList<GameObject>();
		
		
//		gameObjectsStart.add(new GameObject(FileLoader.loadTrianglesFromFile(new File("res/Dragon.raw")), null));
//		gameObjectsStart.add(
//				new GameObject(
//						new double[][][] {
//							{
////								{-50, -50, -100},
////								{-50, 50, -100},
////								{50, -50, -100}
//								{-0.5, -0.5, 0},
//								{-0.5, 0.5, 0},
//								{0.5, -0.5, 0}
//								
//							}
//						},
//				null
//		));
		
		this.player = new Player();
		gameObjectsStart.add(player);
		
		gameObjectsStart.add(new Floor(0, 0, 0, Main.storeColor(Color.green.getRGB())));
		
//		GameObject cube = Mathstuff.generateCube(new double[] {0, 0, 2}, 1, Main.storeColor(Color.blue.getRGB()));
//		cube.setSpeedPerSecond(new double[] {0,0,-0.1});
//		gameObjectsStart.add(cube);
		
//		double z = 0;
//		GameObject testObj = new GameObject(
//				new double[][][] {
//						{
//								{0, 0, z},
//								{10, 0, z},
//								{0, -10, z},
//								{Main.storeColor(Color.yellow.getRGB())}
//						}
//					},
//				new Hitbox(0.1));
////		testObj.setSpeedPerSecond(new double[] {0,0,-0.1});
//		gameObjectsStart.add(testObj);
		
		
		ThreadProcessor.startMultithreadingGame(new ArrayList<GameObject>(), 4);
		
		ThreadProcessor.addGameObjects(gameObjectsStart, true);
		for(GameObject obj : gameObjectsStart)
			addGameObject(obj);
		//--
		
		startGameLoop();
	}
	
	public void addGameObject(GameObject gameObject) {
		this.gameObjects.add(gameObject);
	}
	
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
