package r3.multithreading;

import r3.main.Main;
import r3.mathstuff.Mathstuff;

public class ThreadProcessor extends Thread {
	
	public static void startMultithreading(int trianglesAmount, int threadsAmount) {
		if(trianglesAmount < threadsAmount)
			threadsAmount = trianglesAmount;
		
		
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
	}
	
	
	
	
	
	
	
	
	
	
	private static Object threadLock = new Object();
	
	private final int triangleOffset;
	private final int triangleAmount;
	private final int threadIndex;
	
	private Mathstuff mathstuff;
	private double[][] bufferDepth;
	
	private boolean readyToBeMerged = false;
	
	public ThreadProcessor(int threadIndex, int triangleOffset, int triangleAmount) {			
		this.triangleAmount = triangleAmount;
		this.triangleOffset = triangleOffset;
		this.threadIndex = threadIndex;
		
		//TODO reverse this
//		this.triangleOffset = 0;
//		this.triangleAmount = Main.coords.length;
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
			this.bufferDepth = mathstuff.calcR3ZBuff(Main.coords, Main.getCamera(), triangleOffset, triangleAmount);
			
			//merging of buffers (will wait)
			mergeBuffersQueu();
			
//			System.out.println("Thread " + threadIndex + " is now waiting to get notified restarting the calculation loop");
//			
//			System.out.println("Thread " + threadIndex + " was notified!");
			
			
		}
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
	private static double[][] bufferDepthCompleted;
	
	public int bufferVersion = 0;
	
	/**
	 * this method merges the current threads buffer with another threads buffer, that has finished calculating
	 */
	private void mergeBuffersQueu() {
		
		//get thread to merge with
		int threadToMergeWith;
		
		synchronized(threadLock) {
			
			//search for other mergeable (finished) thread
			for(threadToMergeWith = 0; threadToMergeWith < ThreadProcessor.threadRegister.length; threadToMergeWith++) {
				if(threadToMergeWith != this.threadIndex && threadRegister[threadToMergeWith].readyToBeMerged) {
					break;
				}
			}
			
			
			////check if THIS IS THE LAST THREAD
			if(threadsFinishedAmount == ThreadProcessor.threadRegister.length-1) {
				threadsFinishedAmount = 0;
				
				
				//there is still one thread that can be merged into the current one
				if(threadToMergeWith < ThreadProcessor.threadRegister.length) {
					mergeBuffers(threadToMergeWith);
					ThreadProcessor.threadRegister[threadToMergeWith].readyToBeMerged = false;
				}
				
				bufferDepthCompleted = this.bufferDepth;
				
				
				Main.getWindow().getDrawComp().repaint();
				
//				Main.processInputs();
				
				try {
					threadLock.wait();
				}catch(Exception exc) {
					exc.printStackTrace();
				}
				
				return;
			}
			
			//if no other thread is ready to merge, just 'wait' until the next calculation cycle
			if(threadToMergeWith >= ThreadProcessor.threadRegister.length) {
				//no finished thread exists
				threadsFinishedAmount++;
				this.readyToBeMerged = true;
				
				//this thread is now completely 'inactive', until something invokes notify All to restart calculation
				try {
					threadLock.wait();
				} catch (InterruptedException exc) {
					exc.printStackTrace();
				}
				return;
			}
			
			//prevent any other thread from trying to merge any of the two threads
			this.readyToBeMerged = false;
			ThreadProcessor.threadRegister[threadToMergeWith].readyToBeMerged = false;
		}
		
		
		mergeBuffers(threadToMergeWith);
		
//		threadsFinishedAmount++;
		
		//recall this function to either make this thread available for merging by other threads or to initiate a new merge, done by this thread
		mergeBuffersQueu();
	}
	
	public static double[][] getBufferDepthCompleted() {
		return bufferDepthCompleted != null ? bufferDepthCompleted : new double[0][0];
	}
	
	/**
	 * the actual process of merging two buffers
	 */
	private void mergeBuffers(int threadToMergeWith) {
		
//		System.out.println("Thread " + this.threadIndex + " is actually merging Thread " + threadToMergeWith + " into itself");
		
		//actual merging process			
		//NOTE: the two buffers are merged into the current thread's buffer
		double[][] bufferDepthOther = ThreadProcessor.threadRegister[threadToMergeWith].bufferDepth;
		for(int x = 0; x < this.bufferDepth.length; x++) {
			for(int y = 0; y < this.bufferDepth[0].length; y++) {
//				this.bufferDepth[x][y] = bufferDepthOther[x][y] > this.bufferDepth[x][y] ? this.bufferDepth[x][y] : bufferDepthOther[x][y];
				this.bufferDepth[x][y] = bufferDepthOther[x][y] > 0 || this.bufferDepth[x][y] > 0 ? 1 : 0;
			}
		}
	}
	
//	/**
//	 * gives index of thread that can be merged with current thread and <br>
//	 * prevents other threads from merging with this and the returned thread
//	 */
//	private int getThreadToMergeWith(int ownThreadIndex) {
//	}
}
