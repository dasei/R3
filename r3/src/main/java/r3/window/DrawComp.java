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
		//camera.pos = new double[]{4.8, 5.699999999999996, 4.4};
		camera.alpha+=0.01;
		camera.beta+=0.01;
		Mathstuff.calcR3(this.coords, camera.forward, camera.pos, camera.alpha, camera.beta, camera.scaleFactor);
		
		//System.out.println();
		for(int triangleI = 0; triangleI < coords.length; triangleI++){
			g.drawLine(this.getWidth()/2+(int)coords[triangleI][0][1], this.getHeight()/2-(int)coords[triangleI][0][2], this.getWidth()/2+(int)coords[triangleI][1][1], this.getHeight()/2-(int)coords[triangleI][1][2]);
			g.drawLine(this.getWidth()/2+(int)coords[triangleI][2][1], this.getHeight()/2-(int)coords[triangleI][2][2], this.getWidth()/2+(int)coords[triangleI][1][1], this.getHeight()/2-(int)coords[triangleI][1][2]);
			g.drawLine(this.getWidth()/2+(int)coords[triangleI][0][1], this.getHeight()/2-(int)coords[triangleI][0][2], this.getWidth()/2+(int)coords[triangleI][2][1], this.getHeight()/2-(int)coords[triangleI][2][2]);
			//System.out.println(Arrays.toString(coords[triangleI]));
//			for(int triangleI = 0; triangleI < coords.length; triangleI++){
//				
//			}	
		}
		//System.out.println();
		
	}
	
	public void setCoords(double[][][] coords){
		this.coords = coords;
	}
	
}
