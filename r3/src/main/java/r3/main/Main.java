package r3.main;

import r3.mathstuff.Camera;
import r3.mathstuff.Mathstuff;
import r3.window.Window;

public class Main {
	private static Window window;
	private static Mathstuff mathstuff;
	private static Camera camera;
	
	public static void main(String[] args) {
		camera = new Camera();
		mathstuff = new Mathstuff();
		window = new Window();
		
		double[][][] coords = new double[][][] {
			{{5, 5, 5}, {5, 10, 5}, {5, 5, 10}}
		};
		
		window.repaint(coords);
				
	}
	
	public static Camera getCamera(){
		return camera;
	}
}
