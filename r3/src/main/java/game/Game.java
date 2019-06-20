package game;

import java.util.ArrayList;

import game.gameobjects.GameObject;
import r3.main.Main;
import r3.multithreading.ThreadProcessor;

public class Game {
	
	public static void main(String[] args) {
		
		Main.getWindow().init();		
		
		new Game();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final int FPS = 60;
	public static final boolean fps_cap = true;
	
	private ArrayList<GameObject> gameObjects;
	
	
	public Game() {
		this.gameObjects = new ArrayList<GameObject>();
		
		ThreadProcessor.startMultithreadingGame(new ArrayList<GameObject>(), 4);
		
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
}
