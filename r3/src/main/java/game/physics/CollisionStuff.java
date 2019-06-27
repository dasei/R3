package game.physics;

import java.util.ArrayList;
import java.util.Arrays;

import game.Game;
import game.gameobjects.GameObject;
import r3.mathstuff.Mathstuff;

public class CollisionStuff {
	public static boolean collides(GameObject gameObject, double[] gameObjectHitboxCenterPosition) {
		//LOOP THROUGH EVERYTHING
		//--krasses cache field of things
		double[] vecAB = new double[3], vecAC = new double[3], vecTriangleNormal = new double[3];
		double[] hitboxCenterGameObject = gameObjectHitboxCenterPosition;		 
		double hitboxGameObjectRadius = gameObject.getHitbox().getRadius();
		double lambdaNormal;
		//--
		
		ArrayList<GameObject> gameObjects = Game.getGame().getGameObjects();
		GameObject gameObjWorld;
		double[][][] gameObjTriangles;
		for(int gameObjectI = gameObjects.size()-1; gameObjectI >= 0; gameObjectI--){			
			gameObjWorld = gameObjects.get(gameObjectI);
			if(gameObject == gameObjWorld)
				continue;
			gameObjTriangles = gameObjWorld.getTrianglesAbsolute();
						
			for(int triangleI = 0; triangleI < gameObjTriangles.length; triangleI++) {		
				
//				System.out.println(triangleI +Arrays.toString(gameObjTriangles[triangleI][0])+Arrays.toString(gameObjTriangles[triangleI][1])+Arrays.toString(gameObjTriangles[triangleI][2]));
				System.out.println(Arrays.toString(hitboxCenterGameObject));
				vecAB[0] = gameObjTriangles[triangleI][1][0] - gameObjTriangles[triangleI][0][0];
				vecAB[1] = gameObjTriangles[triangleI][1][1] - gameObjTriangles[triangleI][0][1];
				vecAB[2] = gameObjTriangles[triangleI][1][2] - gameObjTriangles[triangleI][0][2];
				
				vecAC[0] = gameObjTriangles[triangleI][2][0] - gameObjTriangles[triangleI][0][0];
				vecAC[1] = gameObjTriangles[triangleI][2][1] - gameObjTriangles[triangleI][0][1];
				vecAC[2] = gameObjTriangles[triangleI][2][2] - gameObjTriangles[triangleI][0][2];
				
				vecTriangleNormal[0] = (vecAC[1] * vecAB[2]) - (vecAC[2] * vecAB[1]);
				vecTriangleNormal[1] = (vecAC[2] * vecAB[0]) - (vecAC[0] * vecAB[2]);
				vecTriangleNormal[2] = (vecAC[0] * vecAB[1]) - (vecAC[1] * vecAB[0]);
				Mathstuff.vectorUnify(vecTriangleNormal);
				
				
				lambdaNormal = 
						-(
								((hitboxCenterGameObject[0] - gameObjTriangles[triangleI][0][0]) * (vecTriangleNormal[0]))
							+	((hitboxCenterGameObject[1] - gameObjTriangles[triangleI][0][1]) * (vecTriangleNormal[1]))
							+	((hitboxCenterGameObject[2] - gameObjTriangles[triangleI][0][2]) * (vecTriangleNormal[2]))
						)
						/
						(
								Math.pow(vecTriangleNormal[0], 2)
							+	Math.pow(vecTriangleNormal[1], 2)
							+	Math.pow(vecTriangleNormal[2], 2)
						)
				;
				
				
				if(Math.abs(lambdaNormal) <= hitboxGameObjectRadius) {
 					System.out.println("collision of: " + gameObject.getClass() + " with lambdaNormal: " + lambdaNormal);
					return true;
				}
			}
		}
		return false;
	}
}
