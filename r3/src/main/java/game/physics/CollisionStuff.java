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
		double[] vecAB = new double[3], vecAC = new double[3],vecCB = new double[3], vecTriangleNormal = new double[3],vecInterceptMiddle = new double[3],vecInterceptMiddleUnified = new double[3],pointIntercept = new double[3],pointMiddle = new double[3],pointInterceptHitbox = new double[3];
		double[] vectorAP = new double[3];
		double[] hitboxCenterGameObject = gameObjectHitboxCenterPosition;		 
		double hitboxGameObjectRadius = gameObject.getHitbox().getRadius();
		double lambdaNormal,lambdaCB,lambdaAP;
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
				pointMiddle[0]=(gameObjTriangles[triangleI][0][0] + gameObjTriangles[triangleI][1][0] + gameObjTriangles[triangleI][2][0]) / 3;
				pointMiddle[0]=(gameObjTriangles[triangleI][0][1] + gameObjTriangles[triangleI][1][1] + gameObjTriangles[triangleI][2][1]) / 3;
				pointMiddle[0]=(gameObjTriangles[triangleI][0][2] + gameObjTriangles[triangleI][1][2] + gameObjTriangles[triangleI][2][2]) / 3;
				
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
				if(Math.abs(lambdaNormal) > hitboxGameObjectRadius) {
					continue;
				}
				
				vecCB[0] = gameObjTriangles[triangleI][1][0] - gameObjTriangles[triangleI][2][0];
				vecCB[1] = gameObjTriangles[triangleI][1][1] - gameObjTriangles[triangleI][2][1];
				vecCB[2] = gameObjTriangles[triangleI][1][2] - gameObjTriangles[triangleI][2][2];
				
				pointIntercept[0] = hitboxCenterGameObject[0] + (vecTriangleNormal[0] * lambdaNormal);
				pointIntercept[1] = hitboxCenterGameObject[1] + (vecTriangleNormal[1] * lambdaNormal);
				pointIntercept[2] = hitboxCenterGameObject[2] + (vecTriangleNormal[2] * lambdaNormal);
				
				vecInterceptMiddle[0] = pointIntercept[0] - pointMiddle[0];
				vecInterceptMiddle[1] = pointIntercept[1] - pointMiddle[1];
				vecInterceptMiddle[2] = pointIntercept[2] - pointMiddle[2];
				
				vecInterceptMiddleUnified = Mathstuff.vectorUnify(vecInterceptMiddle, true);
				
				pointInterceptHitbox[0] = vecInterceptMiddle[0] - (vecInterceptMiddleUnified[0] * hitboxGameObjectRadius);
				pointInterceptHitbox[1] = vecInterceptMiddle[1] - (vecInterceptMiddleUnified[1] * hitboxGameObjectRadius);
				pointInterceptHitbox[2] = vecInterceptMiddle[2] - (vecInterceptMiddleUnified[2] * hitboxGameObjectRadius);
				
				vectorAP[0] = pointInterceptHitbox[0] - gameObjTriangles[triangleI][0][0];
				vectorAP[1] = pointInterceptHitbox[1] - gameObjTriangles[triangleI][0][1];
				vectorAP[2] = pointInterceptHitbox[2] - gameObjTriangles[triangleI][0][2];
				
				lambdaCB = 
				((-vecAC[0]*vectorAP[1])+(vecAC[1]*vectorAP[0]))
						/
				((vecCB[0]*vectorAP[1])+(-vecCB[1]*vectorAP[0]));
				if(Double.isNaN(lambdaCB))
				{
					lambdaCB = 
					((-vecAC[0]*vectorAP[2])+(vecAC[2]*vectorAP[0]))
							/
					((vecCB[0]*vectorAP[2])+(-vecCB[2]*vectorAP[0]));
					if(Double.isNaN(lambdaCB))
					{
						lambdaCB = 
						((-vecAC[1]*vectorAP[2])+(vecAC[2]*vectorAP[1]))
								/
						((vecCB[1]*vectorAP[2])+(-vecCB[2]*vectorAP[1]));
					}
				}
//				System.out.println(lambdaCB+">=0,<=1");
				lambdaAP = 
				(lambdaCB*vecCB[2]+vecAC[2])
						/
				(vectorAP[2]);
				if(Double.isNaN(lambdaAP))
				{
					lambdaAP = 
					(lambdaCB*vecCB[1]+vecAC[1])
							/
					(vectorAP[1]);
					if(Double.isNaN(lambdaAP))
					{
						lambdaAP = 
						(lambdaCB*vecCB[0]+vecAC[0])
								/
						(vectorAP[0]);
					}
				}
//				System.out.println(lambdaAP+">=1");
				if(lambdaAP>=1&&lambdaCB<=1&&lambdaCB>=0)
				{
					return true;
				}
//				System.out.println("-------------------------");
//				System.out.println(Arrays.toString(gameObject.getPos()));
//				System.out.println(Arrays.toString(gameObject.getHitboxCenterAbsolute()));
//				System.out.println(Arrays.toString(hitboxCenterGameObject));
//				System.out.println("-------------------------");
				
				
//				if(Math.abs(lambdaNormal) <= hitboxGameObjectRadius) {
////					System.out.println("collision of: " + gameObject.getClass() + " with lambdaNormal: " + lambdaNormal);
//					return true;
//				}

			}
		}
		return false;
	}
}
