package r3.multithreading;

import java.util.ArrayList;

import game.gameobjects.GameObject;
import r3.main.Main;
import r3.mathstuff.Mathstuff;
import r3.window.DrawComp;

public class ThreadProcessor extends Thread {
	
	public static double[][][] bufferCalculation;
	public static double[][][] bufferToDraw;
	public static double[][][] bufferToClear;
	
	public static void startMultithreadingRaw(int trianglesAmount, int threadsAmount) {
		if(trianglesAmount < threadsAmount)
			threadsAmount = trianglesAmount;
		
		int width = Main.getWindow().getDrawComp().getWidth();
		int height = Main.getWindow().getDrawComp().getHeight();
		bufferCalculation = clearBuffer(new double[width][height][2]);
		bufferToDraw = clearBuffer(new double[width][height][2]);
		bufferToClear = new double[width][height][2];
		
		ThreadProcessor.threadRegister = new ThreadProcessor[threadsAmount];
		
		
		int triangleOffset = 0;
		int trianglesPerThread = trianglesAmount / threadsAmount;
		for(int threadI = 0; threadI < threadsAmount; threadI++) {
			if(threadI < threadsAmount - 1) {
				//not the last thread to be created
				ThreadProcessor.threadRegister[threadI] = new ThreadProcessor(threadI, triangleOffset, trianglesPerThread);
				triangleOffset += trianglesPerThread; 
			} else {
				//the last thread gets all remaining triangles (=> rounding errors in other threads)
				ThreadProcessor.threadRegister[threadI] = new ThreadProcessor(threadI, triangleOffset, trianglesAmount - triangleOffset);
			}
		}
		
		for(ThreadProcessor thread : ThreadProcessor.threadRegister) {
			thread.start();
		}
		
		startDrawerThread();
		
		startClearThread();
	}
	
	/**
	 * <p>initializes multithreading for the Game, using GameObjects instead of the raw double[][][] list of triangles in class Main</p>
	 * <p>NOTE: the number of threads actually started equals the passed value of threadsAmount, hence while in the beginning only few objects are present
	 * in the Game, over time more can be added easily without the need to start a new Thread</p>
	 */
	public static void startMultithreadingGame(ArrayList<GameObject> gameObjects, int threadsAmount, boolean optimizeTrianglesOnStart) {		
		int width = Main.getWindow().getDrawComp().getWidth();
		int height = Main.getWindow().getDrawComp().getHeight();
		bufferCalculation = clearBuffer(new double[width][height][2]);
		bufferToDraw = clearBuffer(new double[width][height][2]);
		bufferToClear = new double[width][height][2];
		
		ThreadProcessor.threadRegister = new ThreadProcessor[threadsAmount];
		
		for(int i = 0; i < threadRegister.length; i++) {
			threadRegister[i] = new ThreadProcessor(i);
		}
		
		addGameObjects(gameObjects, optimizeTrianglesOnStart);	
		
		for(ThreadProcessor thread : ThreadProcessor.threadRegister) {
			thread.start();
		}
		
		startDrawerThread();
		
		startClearThread();
	}
	
	/**
	 * this adds passed GameObjects to the Threads equally. Their order can be quite random so please let these methods do everything for you
	 */
	static int gameObjectsAmountCurrent = 0;
	public static void addGameObjects(ArrayList<GameObject> gameObjectsNew, boolean optimizeTriangles) {
		if(optimizeTriangles)
			for(GameObject gameObject : gameObjectsNew)
				Mathstuff.optimizeCoordinates(gameObject.getTriangles());
		
		
		//count all currently active GameObjects		
		
		for(ThreadProcessor thread : threadRegister)
			gameObjectsAmountCurrent += thread.getGameObjects().size();
		
		// the idea is, to take the amount of currently active GameObject plus the new ones and divide that number be the number of threads.
		// every thread is then supposed to get this amount of GameObjects at maximum
		int gameObjectsPerThread = 1 + ((gameObjectsAmountCurrent + gameObjectsNew.size() - 1) / threadRegister.length);
		
		int threadSizeInitial;
		int amountOfNewGameObjectsAdded = 0, amountOfNewGameObjects = gameObjectsNew.size();
		for(int threadI = 0; threadI < threadRegister.length && amountOfNewGameObjectsAdded < amountOfNewGameObjects; threadI++) {
			threadSizeInitial = threadRegister[threadI].getGameObjects().size();
			//add the amount of gameObjects to this Thread, that is need to reach the gameObjectsPerThread amount
			for(int gameObjectI = 0; gameObjectI < gameObjectsPerThread - threadSizeInitial && amountOfNewGameObjectsAdded < amountOfNewGameObjects; gameObjectI++) {
				threadRegister[threadI].getGameObjects().add(gameObjectsNew.get(amountOfNewGameObjectsAdded));
				amountOfNewGameObjectsAdded++;
			}
		}
	}
	public static int amountObjects()
	{
		return gameObjectsAmountCurrent;
	}
	
	private static ArrayList<GameObject> gameObjectsToAddOnNextCycle = new ArrayList<GameObject>();
	public static void addGameObjectOnNextCycle(GameObject gameObject) {
		gameObjectsToAddOnNextCycle.add(gameObject);
	}
	
	
	
	private static Object threadLock = new Object();
	
	private int triangleOffset;
	private int triangleAmount;
	
	private boolean useGameObjectsNotRaw;
	private ArrayList<GameObject> gameObjects;
	
	private Mathstuff mathstuff;
//	private double[][] bufferDepth;
	
	public ThreadProcessor(int threadIndex, int triangleOffset, int triangleAmount) {
		this.useGameObjectsNotRaw = false;
		
		this.triangleAmount = triangleAmount;
		this.triangleOffset = triangleOffset;
	}
	
	/*
	 * public Constructor for ThreadProcessor. This will use GameObjects instead of the raw double[][][] triangle list in Main
	 */
	public ThreadProcessor(int threadIndex) {
		this.useGameObjectsNotRaw = true;
		
		this.gameObjects = new ArrayList<GameObject>();
	}
	
	public void run() {
		this.mathstuff = new Mathstuff(false);
		
		//main loop
		while(true) {
//			System.out.println("-----");
//			System.out.println(gameObjects.size());
			
			if(this.useGameObjectsNotRaw)
				mathstuff.calcR3ZBuff2DRasterization(this.gameObjects, Main.getCamera(), false);
			else
				mathstuff.calcR3ZBuff2DRasterization(Main.coords, Main.getCamera(), triangleOffset, triangleAmount, false);
			
			
			onThreadFinish(false);
			
		}
	}
	
	public ArrayList<GameObject> getGameObjects(){
		return this.gameObjects;
	}	
	
	///////////////// WAIT & NOTIFY		
	
	public static void awakeAll(){
		synchronized(threadLock) {
			threadLock.notifyAll();
		}
	}
	
	public static void waitOnLock() {
		synchronized(threadLock) {
			try {
				threadLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Object getThreadLock() {
		return threadLock;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////// Different threads

	public static ThreadProcessor[] threadRegister;
	private static int threadsFinishedAmount = 0;
	
	/**
	 * should be called if a calculation, drawing or clearing thread finishes
	 */
	public static void onThreadFinish(boolean isDrawingThread) {
		
		
		//get thread to merge with
		synchronized(threadLock) {
			threadsFinishedAmount++;
			
			////check if THIS IS THE LAST THREAD
			if(threadsFinishedAmount == ThreadProcessor.threadRegister.length + 2) {
				threadsFinishedAmount = 0;
				
				// now a completely new cycle of se program will stard
				// lets hope it doesn´t crash boy, hai sou da 
				
				//shift buffers
				double[][][] bufferCache = bufferToClear;
				bufferToClear = bufferToDraw;
				bufferToDraw = bufferCalculation;
				bufferCalculation = bufferCache;
				
				Main.processInputs();
				
				Main.cameraPosOnIterationStart = new double[] {Main.getCamera().pos[0], Main.getCamera().pos[1], Main.getCamera().pos[2]};
				Main.cameraForwardOnIterationStart = new double[] {Main.getCamera().forward[0], Main.getCamera().forward[1], Main.getCamera().forward[2]};
				
				//add game objects, that were added to the cache during the last cycle
				if(gameObjectsToAddOnNextCycle.size() > 0) {
					addGameObjects(gameObjectsToAddOnNextCycle, true);
					gameObjectsToAddOnNextCycle.clear();
				}
				
				//restart all threads
				threadLock.notifyAll();
				
				return;
			}
			
			//let the drawing(AWT) thread continue, to not threadlock the AWT Thread
			if(!isDrawingThread){
				try{
					threadLock.wait();
				}catch(Exception e){}
			}
		}
	}
	
	private static void startDrawerThread(){
		(new Thread(){
			public void run(){
				while(true) {
					
					synchronized(threadLock){						
						Main.getWindow().getDrawComp().repaint();
						
						//DONT CALL ONTRHEADFINISH HERE!
						//onThreadFinish() will be called when the paintComponent(Graphics g) method in DrawComp (the AWT) Thread finishes
						//if onThreadFinished() was called here, the engine would think it's finished drawing, while it was only called to repaint()
						try{
							threadLock.wait();
						}catch(Exception e){}
					}
				}
			}
		}).start();
	}
	
	private static void startClearThread() {
		(new Thread(){
			public void run(){
				while(true) {
					
					//clear buffer
					clearBuffer(bufferToClear);
					
					onThreadFinish(false);
				}
			}
		}).start();
	}
	
	private static double[][][] clearBuffer(double[][][] buffer) {
		for(int x = 0; x < buffer.length; x++){
			for(int y = 0; y < buffer[0].length; y++){
				buffer[x][y][0] = DrawComp.BUFFER_DEPTH_CLEAR_VALUE;
				buffer[x][y][1] = -1;
			}	
		}
		return buffer;
	}
	
	public static double[][][] getBufferToCalculateOn() {
		return bufferCalculation;
	}
	
	public static double[][][] getBufferToDraw() {
		return bufferToDraw;
	}
}
