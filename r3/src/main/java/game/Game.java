package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import game.gameobjects.Floor;
import game.gameobjects.GameObject;
import game.gameobjects.Player;
import r3.main.Main;
import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.multithreading.ThreadProcessor;
import r3.window.DrawComp;
import r3.window.Window;

public class Game {
	
	private static Game game;
	
	private Player player;
	
	public static boolean GRAVITY = true;
	public static boolean SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN = true;
	public static boolean ANTIALIAZING = false;
	
	public static void main(String[] args) {
		Main.WORKING_WITH_GAMEOBJECTS = true;
		
		Main.getWindow().init(); 
		Main.getWindow().setTitle(Main.getWindow().getTitle() + "GAME");	
		Thread dt = new Thread()
		{
			public void run()
			{
				while(true)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DrawComp.fps = DrawComp.countfps;
					DrawComp.countfps=0;
				}
			}
		};
		dt.start();
		getGame();
	}
	
	public static Game getGame(){
		if(game == null)
			game = new Game();
		return game;
	}
	
	
	
	
	
	public static final int FPS = 60;
	public static final boolean fps_cap = true;
	
	public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	public ArrayList<GameObject> gameObjectsCache = new ArrayList<GameObject>();
	
	public Game() {
		//PLAYER (without it, camera is not really movable, if the program was started via the main method in class Game)
		this.player = new Player();
		this.gameObjects.add(player);
		
		//FLOOR
		this.gameObjects.add(new Floor(0, 0, 0, Main.storeColor(Color.green.getRGB())));
//		this.gameObjects.add(new Floor(0, 0, 0, -1));
		
		//CUBE
		GameObject cube = Mathstuff.generateCube(new double[] {0, 0, 5}, 1, Main.storeColor(Color.blue.getRGB()),true);
		cube.setSpeedPerSecond(new double[] {0,0,-0.1});
//		this.gameObjects.add(cube);
		
		
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
	public void addGameObject(GameObject gameObject)
	{
		this.gameObjects.add(gameObject);
	}
//	public void addGameObject(GameObject gameObject) {
//		this.gameObjects.add(gameObject);
//	}
	private Random random = new Random();
	public static boolean modification = false;
	public static boolean machineGun = false;
	private void startGameLoop() {
		(new Thread() {
			public void run() {				
				
				
				double deltaTimeSeconds = 0;
				
				long iterationStart, duration;
				while(true) {
					iterationStart = System.currentTimeMillis();					
					///--- LOOP
//					System.out.println(gameObjects.size());
					if(random.nextInt(400)==1)
					{
						GameObject cube = Mathstuff.generateCube(new double[] {random.nextInt(10),random.nextInt(10),5}, 1, -1,true);
						cube.setSpeedPerSecond(new double[] {0,0,-0.1});
						addGameObject(cube);
						gameObjectsCache.add(cube);
						ThreadProcessor.addGameObjects(gameObjectsCache, true);
						gameObjectsCache.clear();
					}
					if(machineGun)
					{
						Window.playSound1();
						GameObject cube = Mathstuff.generateCube(new double[] {Camera.forward[0]+Camera.pos[0],Camera.forward[1]+Camera.pos[1],Camera.forward[2]+Camera.pos[2]}, 0.1, -1,true );
						cube.setSpeedPerSecond(new double[] {Camera.forward[0]*100,Camera.forward[1]*100,Camera.forward[2]*100});
						addGameObject(cube);
						gameObjectsCache.add(cube);
						ThreadProcessor.addGameObjects(gameObjectsCache, true);
						gameObjectsCache.clear();
					}
//					if(gameObjectsToRemove.size()>0)
//					{
//						for(GameObject gameObject : gameObjectsToRemove) {
//							gameObjects.remove(gameObject);
//						}
//					}
					modification = true;
					for(GameObject gameObject : gameObjects) {
						gameObject.move(deltaTimeSeconds);
						if(GRAVITY)
							gameObject.induceGravityToSpeeds(deltaTimeSeconds);
					}
					
					for(GameObject gameObject : gameObjects) {
						gameObject.updatePosition(deltaTimeSeconds);
					}
					modification = false;
					
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
		
		Main.getCamera().setPos(new double[]{player.getPos()[0],player.getPos()[1],player.getPos()[2]+0.1});
	}
}
