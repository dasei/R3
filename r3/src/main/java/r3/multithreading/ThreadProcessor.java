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
	public static void startMultithreadingGame(ArrayList<GameObject> gameObjects, int threadsAmount) {
		int width = Main.getWindow().getDrawComp().getWidth();
		int height = Main.getWindow().getDrawComp().getHeight();
		bufferCalculation = clearBuffer(new double[width][height][2]);
		bufferToDraw = clearBuffer(new double[width][height][2]);
		bufferToClear = new double[width][height][2];
		
		ThreadProcessor.threadRegister = new ThreadProcessor[threadsAmount]; //TODO test this monstrum
		
		for(int i = 0; i < threadRegister.length; i++) {
			threadRegister[i] = new ThreadProcessor(i);
		}
		
		addGameObjects(gameObjects);	
		
		for(ThreadProcessor thread : ThreadProcessor.threadRegister) {
			thread.start();
		}
		
		startDrawerThread();
		
		startClearThread();
	}
	
	/**
	 * this adds passed GameObjects to the Threads equally. Their order can be quite random so please let these methods do everything for you
	 */
	public static void addGameObjects(ArrayList<GameObject> gameObjectsNew) {
		
		//count all currently active GameObjects		
		int gameObjectsAmountCurrent = 0;
		for(ThreadProcessor thread : threadRegister)
			gameObjectsAmountCurrent += thread.getGameObjects().size();
		
		// the idea is, to take the amount of currently active GameObject plus the new ones and divide that number be the number of threads.
		// every thread is then supposed to get this amount of GameObjects at maximum
		int gameObjectsPerThread = 1 + ((gameObjectsAmountCurrent + gameObjectsNew.size()) / threadRegister.length);
		
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
	
	
	
	
	
	
	private static Object threadLock = new Object();
	
	private int triangleOffset;
	private int triangleAmount;
	private int threadIndex;
	
	private boolean useGameObjectsNotRaw;
	private ArrayList<GameObject> gameObjects;
	
	private Mathstuff mathstuff;
//	private double[][] bufferDepth;
	
	private boolean readyToBeMerged = false;
	
	public ThreadProcessor(int threadIndex, int triangleOffset, int triangleAmount) {			
		this.useGameObjectsNotRaw = false;
		
		this.triangleAmount = triangleAmount;
		this.triangleOffset = triangleOffset;
		this.threadIndex = threadIndex;
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
		
//		double[][] bufferDepth;
		
//		System.out.println("Thread " + threadIndex + " is now waiting to get notified for main calculation cycle");
//		waitOnLock();
//		System.out.println("Thread " + threadIndex + " was notified!");
		
		//main loop
		while(true) {
			//wait for 'calculation notification'				
				
			
			//main calculation
//			this.bufferVersion = Main.cycleCounterDebug;
			mathstuff.calcR3ZBuff(Main.coords, Main.getCamera(), triangleOffset, triangleAmount, false); //TODO if it uses GameObjects, actually calculate them!
			
			//merging of buffers (will wait)
//			mergeBuffersQueu();
			
//			System.out.println("Thread " + threadIndex + " is now waiting to get notified restarting the calculation loop");
//			
//			System.out.println("Thread " + threadIndex + " was notified!");
			
			onThreadFinish(false);
			
		}
	}
	
	public ArrayList<GameObject> getGameObjects(){
		return this.gameObjects;
	}
	
//	public boolean readyToBeMerged() {
//		return this.readyToBeMerged;
//	}
	
//	public void setReadyToBeMerged(boolean readyToBeMerged) {
//		this.finishedAndNotMerged = readyToBeMerged;
//	}
	
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
	
	
	///////////// MERGING BUFFERS		

	public static ThreadProcessor[] threadRegister;
	private static int threadsFinishedAmount = 0;
	private static double[][][] bufferDepthCompleted;
	
	public int bufferVersion = 0;
	
	/**
	 * this method merges the current threads buffer with another threads buffer, that has finished calculating
	 */
//	@Deprecated
//	private void mergeBuffersQueu() {
//		
//		//get thread to merge with
//		int threadToMergeWith;
//		
//		synchronized(threadLock) {
//			
//			//search for other mergeable (finished) thread
//			for(threadToMergeWith = 0; threadToMergeWith < ThreadProcessor.threadRegister.length; threadToMergeWith++) {
//				if(threadToMergeWith != this.threadIndex && threadRegister[threadToMergeWith].readyToBeMerged) {
//					break;
//				}
//			}
//			
//			
//			////check if THIS IS THE LAST THREAD
//			if(threadsFinishedAmount == ThreadProcessor.threadRegister.length-1) {
//				threadsFinishedAmount = 0;
//				
//				
//				//there is still one thread that can be merged into the current one
//				if(threadToMergeWith < ThreadProcessor.threadRegister.length) {
//					mergeBuffers(threadToMergeWith);
//					ThreadProcessor.threadRegister[threadToMergeWith].readyToBeMerged = false;
//				}
//				
//				bufferDepthCompleted = this.bufferDepth;
//				
//				
//				Main.getWindow().getDrawComp().repaint();
//				
////				Main.processInputs();
//				
//				try {
//					threadLock.wait();
//				}catch(Exception exc) {
//					exc.printStackTrace();
//				}
//				
//				return;
//			}
//			
//			//if no other thread is ready to merge, just 'wait' until the next calculation cycle
//			if(threadToMergeWith >= ThreadProcessor.threadRegister.length) {
//				//no finished thread exists
//				threadsFinishedAmount++;
//				this.readyToBeMerged = true;
//				
//				//this thread is now completely 'inactive', until something invokes notify All to restart calculation
//				try {
//					threadLock.wait();
//				} catch (InterruptedException exc) {
//					exc.printStackTrace();
//				}
//				return;
//			}
//			
//			//prevent any other thread from trying to merge any of the two threads
//			this.readyToBeMerged = false;
//			ThreadProcessor.threadRegister[threadToMergeWith].readyToBeMerged = false;
//		}
//		
//		
////		mergeBuffers(threadToMergeWith);
//		
////		threadsFinishedAmount++;
//		
//		//recall this function to either make this thread available for merging by other threads or to initiate a new merge, done by this thread
//		mergeBuffersQueu();
//	}
	
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
				
				//restart all threads
				threadLock.notifyAll();
				
				return;
			}
			
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
	
//	public static double[][] getBufferDepthCompleted() {
//		return bufferDepthCompleted != null ? bufferDepthCompleted : new double[0][0];
//	}
	
	public static double[][][] getBufferToCalculateOn() {
		return bufferCalculation;
	}
	
	public static double[][][] getBufferToDraw() {
		return bufferToDraw;
	}
	
	
//	/**
//	 * the actual process of merging two buffers
//	 */
//	private void mergeBuffers(int threadToMergeWith) {
//		
////		System.out.println("Thread " + this.threadIndex + " is actually merging Thread " + threadToMergeWith + " into itself");
//		
//		//actual merging process			
//		//NOTE: the two buffers are merged into the current thread's buffer
//		double[][] bufferDepthOther = ThreadProcessor.threadRegister[threadToMergeWith].bufferDepth;
//		for(int x = 0; x < this.bufferDepth.length; x++) {
//			for(int y = 0; y < this.bufferDepth[0].length; y++) {
////				this.bufferDepth[x][y] = bufferDepthOther[x][y] > this.bufferDepth[x][y] ? this.bufferDepth[x][y] : bufferDepthOther[x][y];
//				this.bufferDepth[x][y] = bufferDepthOther[x][y] > 0 || this.bufferDepth[x][y] > 0 ? 1 : 0;
//			}
//		}
//	}
	
//	/**
//	 * gives index of thread that can be merged with current thread and <br>
//	 * prevents other threads from merging with this and the returned thread
//	 */
//	private int getThreadToMergeWith(int ownThreadIndex) {
//	}
}
