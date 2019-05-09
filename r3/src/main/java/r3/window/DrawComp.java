package r3.window;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;

import r3.main.Main;
import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;

public class DrawComp extends JComponent {
	
	public double[][][] coords  = Main.coords;
	int r=255;
	int g=0;
	int b=0;
	
	private final Font font = new Font("arial", Font.BOLD, 20);
	
	//FPS counting => initialization
	private long timeStartNanos;
	private long timeNow;
	private int cyclesForFPSCalculation = 10;	
	private int cycleCounter = 0;
	private double fpsCurrent = 0;
	
	public void paintComponent(Graphics g) {		
		//TODO
		if(cycleCounter == 0)
			timeStartNanos = System.nanoTime();
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
		g.setFont(font);
		g.drawString("main" + Main.fpsCurrent, screenWidth-100, 25);
		
		//Draw FPS (calc)
		g.drawString("calc" + fpsCurrent, 0, 25);

	}
	
	private void draw3DZBuffered(Graphics g) {
		final Camera camera = Main.getCamera();
		
		//TODO time measurement
		long timeBeginning = System.currentTimeMillis();
		
		
		
		
		//Calculate Buffer
		double[][] buffCache = Mathstuff.calcR3ZBuff(coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
		
		
		
		
		
		
		//TODO time measurement
		System.out.print((System.currentTimeMillis()-timeBeginning) + "\t");
		timeBeginning = System.currentTimeMillis();
		//
		
		//Draw Buffer
		for(int x = 0;x<buffCache.length;x++){
			for(int y = 0;y<buffCache[0].length;y++){
				if(buffCache[x][y]==0)
					continue;
				g.drawRect(x, y, 0, 0);
			}
		}
		
		//TODO time measurement
		System.out.println((System.currentTimeMillis()-timeBeginning));
		
		
		
		//TODO time measurement => FPS DISPLAY
		cycleCounter++;		
		timeNow = System.nanoTime();
		if(timeNow - timeStartNanos > 1000000000) {
			cyclesForFPSCalculation = 1;
			System.out.println("EEEEEEEEEEEEEEEEND");
		}
		if(cycleCounter % cyclesForFPSCalculation == 0) {
			fpsCurrent =
					((int) ((1000000000d*cyclesForFPSCalculation)/(timeNow - timeStartNanos) * 100)) / 100d;
			//set new start time for next cycle
			timeStartNanos = timeNow;
//			System.out.println(( 1000000000d/(timeNow - timeStartNanos)) + ", " + timeNow + "/t" + timeStartNanos);
		}	
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
