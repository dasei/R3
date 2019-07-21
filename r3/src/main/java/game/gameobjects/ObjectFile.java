package game.gameobjects;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import r3.main.Main;

public class ObjectFile extends GameObject{
	public ObjectFile(String path)
	{
		super(new double[][][] {}, null);
		loadCoords(true, path);
	}
	public void loadCoords(boolean useColor,String path){
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		try {

//			BufferedReader br = new BufferedReader(new FileReader(new File("E:/Admin/Desktop/Trash/mineways/mcworld3.raw")));
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			double scale = 0.5;
			
			while(br.ready()) {
				 
				String s = br.readLine();
				String[] coordinates = s.split(" ");
				double[][] vertices = new double[4][];
				
				vertices[0] = new double[] {Double.parseDouble(coordinates[0])*scale, Double.parseDouble(coordinates[1])*scale, Double.parseDouble(coordinates[2])*scale}; 
				vertices[1] = new double[] {Double.parseDouble(coordinates[3])*scale, Double.parseDouble(coordinates[4])*scale, Double.parseDouble(coordinates[5])*scale};
				vertices[2] = new double[] {Double.parseDouble(coordinates[6])*scale, Double.parseDouble(coordinates[7])*scale, Double.parseDouble(coordinates[8])*scale};
				double r = Math.random();
				if(r < 0.3){
					vertices[3] = new double[] {useColor ? ((double) Main.storeColor(Color.red.getRGB())) : -1};
				}else if(r < 0.6){
					vertices[3] = new double[] {useColor ? ((double) Main.storeColor(Color.green.getRGB())) : -1};
				}else{
					vertices[3] = new double[] {useColor ? ((double) Main.storeColor(Color.blue.getRGB())) : -1};
				}
				triangles.add(vertices);
			}
			br.close();
			this.setTriangles(triangles.toArray(new double[][][] {}));
	}
		catch(Exception e)
		{
			
		}
	}
}
