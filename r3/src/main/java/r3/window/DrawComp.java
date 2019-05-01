package r3.window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import r3.main.Main;

public class DrawComp extends JComponent {
	
	public double[][][] coords  = Main.coords;
	public Graphics2D g2;
	boolean running;
	int r=255;
	int g=0;
	int b=0;
	public DrawComp() {
	//		this.setIgnoreRepaint(true);
	}
	
	public void paintComponent(Graphics gOld) {		
		g2 = (Graphics2D) gOld;			
		if(r > 0 && b == 0){
			r--;
		    g++;
		}
		else if(g > 0 && r == 0){
			g--;
			b++;
		}
		else if(b > 0 && g == 0){
			r++;
			b--;
		}
		g2.setColor(new Color(r,g,b));
		
						//Main.getWindow().getDrawComp().repaint();
				
		
	//	g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
//		Camera camera = Main.getCamera();


		
		int[][][] coordsDrawCache = Main.coordsDraw;
		for(int triangleI = 0; triangleI < coordsDrawCache.length; triangleI++){
			//0 -> 1
			g2.drawLine(coordsDrawCache[triangleI][0][0], coordsDrawCache[triangleI][0][1], coordsDrawCache[triangleI][1][0], coordsDrawCache[triangleI][1][1]);
			//1 -> 2
			g2.drawLine(coordsDrawCache[triangleI][1][0], coordsDrawCache[triangleI][1][1], coordsDrawCache[triangleI][2][0], coordsDrawCache[triangleI][2][1]);
			//2 -> 0
			g2.drawLine(coordsDrawCache[triangleI][2][0], coordsDrawCache[triangleI][2][1], coordsDrawCache[triangleI][0][0], coordsDrawCache[triangleI][0][1]);
//			g.drawLine(screenCenterX+(int)coords[triangleI][1][1], screenCenterY-(int)coords[triangleI][1][2], screenCenterX+(int)coords[triangleI][2][1], screenCenterY-(int)coords[triangleI][2][2]);
//			g.drawLine(screenCenterX+(int)coords[triangleI][2][1], screenCenterY-(int)coords[triangleI][2][2], screenCenterX+(int)coords[triangleI][0][1], screenCenterY-(int)coords[triangleI][0][2]);
			
//			System.out.println("Drawn triangle [" + triangleI + "]: "
//					+ Arrays.toString(coords[triangleI][0])
//					+ Arrays.toString(coords[triangleI][1])
//					+ Arrays.toString(coords[triangleI][2])
//			);
		}
		
	}
	
	public void setCoords(double[][][] coords){
		this.coords = coords;
	}
	
}
