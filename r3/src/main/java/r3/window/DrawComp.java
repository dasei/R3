package r3.window;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import r3.main.Main;
import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;

public class DrawComp extends JComponent {
	
	public double[][][] coords  = Main.coordsDefault;
	Graphics2D g;
	public DrawComp() {
//		this.setIgnoreRepaint(true);
	}
	
	public void paintComponent(Graphics gOld) {		
		g = (Graphics2D) gOld;
		
	//	g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		Camera camera = Main.getCamera();


		Mathstuff.calcR3(this.coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
		int[][][] coordsDrawCache = Main.coordsDraw;
		for(int triangleI = 0; triangleI < coordsDrawCache.length; triangleI++){
			//0 -> 1
			g.drawLine(coordsDrawCache[triangleI][0][0], coordsDrawCache[triangleI][0][1], coordsDrawCache[triangleI][1][0], coordsDrawCache[triangleI][1][1]);
			//1 -> 2
			g.drawLine(coordsDrawCache[triangleI][1][0], coordsDrawCache[triangleI][1][1], coordsDrawCache[triangleI][2][0], coordsDrawCache[triangleI][2][1]);
			//2 -> 0
			g.drawLine(coordsDrawCache[triangleI][2][0], coordsDrawCache[triangleI][2][1], coordsDrawCache[triangleI][0][0], coordsDrawCache[triangleI][0][1]);
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
