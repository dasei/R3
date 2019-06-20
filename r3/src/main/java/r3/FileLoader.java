package r3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileLoader {
	public static final double DEFAULT_COORDINATE_UPSCALE = 10;
	public static double[][][] loadTrianglesFromFile(File fileTriangles) {
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
				
				vertices[0] = new double[] {Double.parseDouble(coordinates[0])*DEFAULT_COORDINATE_UPSCALE, Double.parseDouble(coordinates[1])*DEFAULT_COORDINATE_UPSCALE, Double.parseDouble(coordinates[2])*DEFAULT_COORDINATE_UPSCALE}; 
				vertices[1] = new double[] {Double.parseDouble(coordinates[3])*DEFAULT_COORDINATE_UPSCALE, Double.parseDouble(coordinates[4])*DEFAULT_COORDINATE_UPSCALE, Double.parseDouble(coordinates[5])*DEFAULT_COORDINATE_UPSCALE};
				vertices[2] = new double[] {Double.parseDouble(coordinates[6])*DEFAULT_COORDINATE_UPSCALE, Double.parseDouble(coordinates[7])*DEFAULT_COORDINATE_UPSCALE, Double.parseDouble(coordinates[8])*DEFAULT_COORDINATE_UPSCALE};
				
				vertices[3] = new double[] {-1}; //TODO read color from file
				
				triangles.add(vertices);
				
			}
			br.close();
			return triangles.toArray(new double[0][][]);		
		}catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
