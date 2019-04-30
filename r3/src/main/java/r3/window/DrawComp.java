package r3.window;

import java.awt.Graphics;
import java.util.Arrays;

import javax.swing.JComponent;

import r3.main.Main;
import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;

public class DrawComp extends JComponent {
	
	public double[][][] coords;
	
	public void paintComponent(Graphics g) {
		
		Camera camera = Main.getCamera();
		
		
		Mathstuff.calcR3(this.coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
		
		System.out.println();
		for(int triangleI = 0; triangleI < coords.length; triangleI++){
			g.drawLine((int)coords[triangleI][0][1], (int)coords[triangleI][0][2], (int)coords[triangleI][1][1], (int)coords[triangleI][1][2]);
			g.drawLine((int)coords[triangleI][2][1], (int)coords[triangleI][2][2], (int)coords[triangleI][1][1], (int)coords[triangleI][1][2]);
			g.drawLine((int)coords[triangleI][0][1], (int)coords[triangleI][0][2], (int)coords[triangleI][2][1], (int)coords[triangleI][2][2]);
			System.out.println(Arrays.toString(coords[triangleI]));
//			for(int triangleI = 0; triangleI < coords.length; triangleI++){
//				
//			}	
		}
		System.out.println();

		
	}
	
	public void setCoords(double[][][] coords){
		this.coords = coords;
	}
	
}
