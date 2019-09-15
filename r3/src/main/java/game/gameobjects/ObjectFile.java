package game.gameobjects;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import r3.main.Main;

public class ObjectFile extends GameObject{
	public ObjectFile(String path, boolean json)
	{
		super(new double[][][] {}, null);
		if(!json)
		{
			loadCoordsRaw(true, path);
		}
		else
		{
			loadCoordsJson(true, path);
		}
	}
	public void loadCoordsRaw(boolean useColor,String path){
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
	public void loadCoordsJson(boolean useJsonColor, String path)
	{
		JSONParser jsonParser = new JSONParser();
//		ArrayList<double[]> verticesList = new ArrayList<double[]>();
		
		ArrayList<double[][]> trianglesList = new ArrayList<double[][]>();
		
		
		
		try {

//			BufferedReader br = new BufferedReader(new FileReader(new File("E:/Admin/Desktop/Trash/mineways/mcworld3.raw")));
			FileReader fileReader = new FileReader(path);
			double scale = 0.4;
			double x3scale = 1;
			JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
			
			JSONArray verticesJson = (JSONArray) jsonObject.get("vertices");
			
			JSONArray triangles = (JSONArray) jsonObject.get("triangles");
			
			
			
			for(int i = 0;i<triangles.size();i++) {
				 
				JSONObject trianglesObject = (JSONObject) triangles.get(i);
				
				JSONArray verticesArray = (JSONArray) trianglesObject.get("vertices");				
				JSONArray colorArray = (JSONArray) trianglesObject.get("color");
				
				JSONArray verticesArray0 = (JSONArray) verticesJson.get(Integer.parseInt(verticesArray.get(0).toString()));
				JSONArray verticesArray1 = (JSONArray) verticesJson.get(Integer.parseInt(verticesArray.get(1).toString()));
				JSONArray verticesArray2 = (JSONArray) verticesJson.get(Integer.parseInt(verticesArray.get(2).toString()));
				//System.out.println(verticesArray2.get(0)+","+verticesArray2.get(1));
				//System.out.println(colorArray.get(0)+","+colorArray.get(1)+","+colorArray.get(2));
//				String[] coordinates = s.split("whatever");//fill this
				double[][] vertices = new double[4][];
//				
				vertices[0] = new double[] {(Integer.parseInt(verticesArray0.get(0).toString()))*scale, (Integer.parseInt(verticesArray0.get(1).toString()))*scale, (Integer.parseInt(verticesArray0.get(2).toString()))*scale*x3scale}; 
				vertices[1] = new double[] {(Integer.parseInt(verticesArray1.get(0).toString()))*scale, (Integer.parseInt(verticesArray1.get(1).toString()))*scale, (Integer.parseInt(verticesArray1.get(2).toString()))*scale*x3scale};
				vertices[2] = new double[] {(Integer.parseInt(verticesArray2.get(0).toString()))*scale, (Integer.parseInt(verticesArray2.get(1).toString()))*scale, (Integer.parseInt(verticesArray2.get(2).toString()))*scale*x3scale};
				vertices[3] = new double[] {useJsonColor ? ((double) Main.storeColor(new Color(Integer.parseInt(colorArray.get(0).toString()),Integer.parseInt(colorArray.get(1).toString()) ,Integer.parseInt(colorArray.get(2).toString())).getRGB())) : -1}; //fill this
//				
				trianglesList.add(vertices);
			}
			this.setTriangles(trianglesList.toArray(new double[][][] {}));
	}
		catch(Exception e)
		{
			
		}
	}
}
