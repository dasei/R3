/**
 * 
 */
package game.gameobjects;

import java.util.ArrayList;

import r3.main.Main;

public class Floor extends GameObject {
	/**
	 * @param colorID colorID as returned by {@link Main#storeColor(int)}
	 */
	public Floor(double x1Center, double x2Center, double x3Center, double colorID) {
		super(new double[][][] {}, null);
		
		boolean[][] matrix = new boolean[][] {
			{false,	false,	false,	false,	true},
			{false,	false,	true,	true,	true},
			{false,	false,	true,	false,	false},
			{false,	false,	true,	false,	false},
			{false,	false,	false,	false,	false}
		};
//		boolean[][] matrix = new boolean[][] {
//			{false,	false,	false,	false,	false},
//			{false,	false,	false,	false,	false},
//			{false,	false,	true,	false,	false},
//			{false,	false,	false,	false,	false},
//			{false,	false,	false,	false,	false}
//		};
		
		double squareSize = 1;
		ArrayList<double[][]> triangles = new ArrayList<double[][]>();
		for(int x = 0; x < matrix.length; x++) {
			for(int y = 0; y < matrix[x].length; y++) {
				if(!matrix[x][y])
					continue;
				
//				add the square
				triangles.add(
						new double[][] {
							{((x-(matrix.length/2d))*squareSize) + x1Center, ((y-(matrix[x].length/2d))*squareSize) + x2Center, x3Center},
							{((x-(matrix.length/2d))*squareSize) + x1Center, ((y-(matrix[x].length/2d)+1)*squareSize) + x2Center, x3Center},
							{((x-(matrix.length/2d)+1)*squareSize) + x1Center, ((y-(matrix[x].length/2d))*squareSize) + x2Center, x3Center},
							{colorID}
						}
				);				
				triangles.add(
						new double[][] {
							{((x-(matrix.length/2d)+1)*squareSize) + x1Center, ((y-(matrix[x].length/2d)+1)*squareSize) + x2Center, x3Center},
							{((x-(matrix.length/2d))*squareSize) + x1Center, ((y-(matrix[x].length/2d)+1)*squareSize) + x2Center, x3Center},
							{((x-(matrix.length/2d)+1)*squareSize) + x1Center, ((y-(matrix[x].length/2d))*squareSize) + x2Center, x3Center},
							{colorID}
						}
				);
			}	
		}
		this.setTriangles(triangles.toArray(new double[][][] {}));
	}
}
