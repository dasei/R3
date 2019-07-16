package r3;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import r3.main.Main;
import r3.mathstuff.Mathstuff;

public class FileLoader {
	
	public static double[][][] loadTrianglesFromFile(File fileTriangles, boolean optimizeTriangles) {
		return loadTrianglesFromFile(fileTriangles, optimizeTriangles, 1);
	}
	
	public static double[][][] loadTrianglesFromFile(File fileTriangles, boolean optimizeTriangles, double coordinateUpscale) {
		if(!fileTriangles.exists()) {
			System.err.println("specified file at '" + fileTriangles.getAbsolutePath() + "' does not exist");
			return null;
		}
		
		if(!fileTriangles.isFile()) {
			System.err.println("specified file at '" + fileTriangles.getAbsolutePath() + "' is not a file");
			return null;
		}
		
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		try {

			BufferedReader br = new BufferedReader(new FileReader(fileTriangles));

			while(br.ready()) {
				 
				String s = br.readLine();
				String[] coordinates = s.split(" ");
				double[][] vertices = new double[4][];
				
				vertices[0] = new double[] {Double.parseDouble(coordinates[0])*coordinateUpscale, Double.parseDouble(coordinates[1])*coordinateUpscale, Double.parseDouble(coordinates[2])*coordinateUpscale}; 
				vertices[1] = new double[] {Double.parseDouble(coordinates[3])*coordinateUpscale, Double.parseDouble(coordinates[4])*coordinateUpscale, Double.parseDouble(coordinates[5])*coordinateUpscale};
				vertices[2] = new double[] {Double.parseDouble(coordinates[6])*coordinateUpscale, Double.parseDouble(coordinates[7])*coordinateUpscale, Double.parseDouble(coordinates[8])*coordinateUpscale};
				
				vertices[3] = new double[] {-1};
				
				triangles.add(vertices);
			}
			
			br.close();
			if(optimizeTriangles)
				return Mathstuff.optimizeCoordinates(triangles.toArray(new double[0][][]));
			else
				return triangles.toArray(new double[0][][]);		
		}catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static double[][][] colorize(double[][][] triangles, Color... colors) {
		if(colors.length == 0) {
			colors = new Color[] {
					Color.red,
					Color.green,
					Color.blue
			};
		}
		
		for(int i = 0;i < triangles.length; i++){
			triangles[i][3][0] = Main.storeColor(
					colors[(int) (colors.length * Math.random())].getRGB()
			);			
		}
		
		return triangles;
	}
}
