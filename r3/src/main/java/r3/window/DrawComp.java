package r3.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;

import game.Game;
import r3.main.Main;
import r3.mathstuff.Camera;
import r3.multithreading.ThreadProcessor;

public class DrawComp extends JComponent {
	
	public static final int BUFFER_DEPTH_CLEAR_VALUE = Integer.MAX_VALUE;
	
	public static double ANTIALIAZING_RADIUS = 1;
	
	public static int fps;
	
	public static int countfps;
	
	public double[][][] coords  = Main.coords;
	int r=255;
	int g=0;
	int b=0;
	
	private final Font font = new Font("arial", Font.BOLD, 20);
	
	//FPS counting => initialization
//	private long timeStartNanos;
//	private long timeNow;
//	private int cyclesForFPSCalculation = 10;	
//	private int cycleCounter = 0;
//	private double fpsCurrent = 0;
	
	public void paintComponent(Graphics g) {	
		
		if(ThreadProcessor.threadRegister == null)
			return;
		
		//TODO
//		if(cycleCounter == 0)
//			timeStartNanos = System.nanoTime();
		
		
//		Main.processInputs();
		
//		g2 = (Graphics2D) gOld;			
//		if(r > 0 && b == 0){
//			r--;
//		    g++;
//		}
//		else if(g > 0 && r == 0){
//			g--;
//			b++;
//		}
//		else if(b > 0 && g == 0){
//			r++;
//			b--;
//		}
//		g2.setColor(new Color(r,g,b));
		

		
		draw3DZBuffered(g);
		
		
		
		
		//Draw Crosshair
		int screenWidth = this.getWidth();
		int screenHeight = this.getHeight();
		g.drawLine(screenWidth/2-20, screenHeight/2, screenWidth/2+20, screenHeight/2);
		g.drawLine(screenWidth/2, screenHeight/2-20, screenWidth/2, screenHeight/2+20);
		
				
		//Draw FPS (main Loop, Inputs)
//		g.setColor(Color.green);
		g.setFont(font);
//		g.drawString("main" + Main.fpsCurrent, screenWidth-100, 25);
		
		//Draw FPS (calc)
//		g.drawString("calc" + fpsCurrent, 0, 25);
		
		
		ThreadProcessor.onThreadFinish(true);

	}
	
//	private int counter = 0;
	
	private void draw3DZBuffered(Graphics g) {
		countfps++;
//		final Camera camera = Main.getCamera();
		
		//TODO time measurement
//		long timeBeginning = System.currentTimeMillis();
		
		
//		System.out.println(Main.ThreadProcessor.th);
		
		//Calculate Buffer
//		double[][] buffCache = new Mathstuff(true).calcR3ZBuff(coords, camera, 0, Main.coords.length);
		double[][][] buffCache = ThreadProcessor.getBufferToDraw();
		
//		System.out.println("--------------------------------");
//		Main.cycleCounterDebug++;
		
	

		
		//TODO time measurement
//		System.out.print((System.currentTimeMillis()-timeBeginning) + "\t");
//		timeBeginning = System.currentTimeMillis();
		//
		if(buffCache == null)
			return;
		
		//Draw Buffer
		Color color;
		if(Main.lowMode > 0) {
			for(int x = 0; x < buffCache.length; x++){
				for(int y = 0; y < buffCache[0].length; y++){
					if(buffCache[x][y][0] == BUFFER_DEPTH_CLEAR_VALUE && !Game.ANTIALIAZING)
						continue;
					
					if(!Game.ANTIALIAZING) {
						g.setColor(Main.getColorAt((int) buffCache[x][y][1]));						
					} else {
						color = getAntialiazingColor(buffCache, x, y);
						if(color == null)
							continue;
						g.setColor(color);
					}
					
	//				g.drawRect(x, y, 1, 0);                                                 
	//				g.drawLine(x, y, x-1, y-1);
					g.drawRect(x, y, 1,0);					
					
				}
			}
		} else {
			for(int x = 0;x<buffCache.length;x++){
				for(int y = 0;y<buffCache[0].length;y++){
					if(buffCache[x][y][0] == BUFFER_DEPTH_CLEAR_VALUE && !Game.ANTIALIAZING)
						continue;
					
					if(!Game.ANTIALIAZING) {
						g.setColor(Main.getColorAt((int) buffCache[x][y][1]));
					} else {
						color = getAntialiazingColor(buffCache, x, y);
						if(color == null)
							continue;
						g.setColor(color);
					}
					
	//				g.drawRect(x, y, 1, 0);                                                 
					g.drawLine(x, y, x, y);
//					g.fillRect(x, y, Main.lowMode,Main.lowMode);
				}
			}
		}
		g.setColor(Color.BLACK);
		g.drawString("X1: "+Camera.pos[0], 1, 10);
		g.drawString("X2: "+Camera.pos[1], 1, 25);
		g.drawString("X3: "+Camera.pos[2], 1, 40);
		g.drawString(fps+"", 1250, 12);
	}
	
	private final float[] cacheColorSum = new float[3];
	private final float[] cacheColorSum2 = new float[3];
	private Color getAntialiazingColor(double[][][] buffer, int x, int y) {
		if(ANTIALIAZING_RADIUS < 0) {
			return Main.getColorAt((int) buffer[x][y][1]);
		}
		
		//clear caches
		cacheColorSum[0] = 0;
		cacheColorSum[1] = 0;
		cacheColorSum[2] = 0;
		
		cacheColorSum2[0] = 0;
		cacheColorSum2[1] = 0;
		cacheColorSum2[2] = 0;
		
		int colorAmount = 0;
		
		//loop through the destinated rectangle
		for(int xI = (int) (x-ANTIALIAZING_RADIUS); xI < (int) (x+ANTIALIAZING_RADIUS+1); xI++) {
			if(xI < 0 || xI >= buffer.length)
				continue;
			
			for(int yI = (int) (y-ANTIALIAZING_RADIUS); yI < (int) (y+ANTIALIAZING_RADIUS+1); yI++) {
				if(yI < 0 || yI >= buffer[0].length)
					continue;
				
				if(buffer[xI][yI][1] == -1)
					continue;
				
				
				Main.getColorAt((int) buffer[xI][yI][1]).getColorComponents(cacheColorSum2);
				cacheColorSum[0] += cacheColorSum2[0];
				cacheColorSum[1] += cacheColorSum2[1];
				cacheColorSum[2] += cacheColorSum2[2];
				
				colorAmount++;
			}	
		}
		
		if(colorAmount == 0)
			return null;
		else
			return new Color(cacheColorSum[0] / colorAmount, cacheColorSum[1] / colorAmount, cacheColorSum[2] / colorAmount);
	}
	
	private void drawMesh(Graphics g, int[][][] frameBuffer, int screenCenterX, int screenCenterY) {		
		for(int triangleI = 0; triangleI < frameBuffer.length; triangleI++){
			//0 -> 1
			g.drawLine(frameBuffer[triangleI][0][0], frameBuffer[triangleI][0][1], frameBuffer[triangleI][1][0], frameBuffer[triangleI][1][1]);
			//1 -> 2
			g.drawLine(frameBuffer[triangleI][1][0], frameBuffer[triangleI][1][1], frameBuffer[triangleI][2][0], frameBuffer[triangleI][2][1]);
			//2 -> 0
			g.drawLine(frameBuffer[triangleI][2][0], frameBuffer[triangleI][2][1], frameBuffer[triangleI][0][0], frameBuffer[triangleI][0][1]);
			
//			g.drawLine(screenCenterX+(int)coords[triangleI][1][1], screenCenterY-(int)coords[triangleI][1][2], screenCenterX+(int)coords[triangleI][2][1], screenCenterY-(int)coords[triangleI][2][2]);
//			g.drawLine(screenCenterX+(int)coords[triangleI][2][1], screenCenterY-(int)coords[triangleI][2][2], screenCenterX+(int)coords[triangleI][0][1], screenCenterY-(int)coords[triangleI][0][2]);
		
//			System.out.println("Drawn triangle [" + triangleI + "]: "
//					+ Arrays.toString(coords[triangleI][0])
//					+ Arrays.toString(coords[triangleI][1])
//					+ Arrays.toString(coords[triangleI][2])
//			);
		}
	}
	
//	public void setCoords(double[][][] coords){
//		this.coords = coords;
//	}
	
	
	
}
