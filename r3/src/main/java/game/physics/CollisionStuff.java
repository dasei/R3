package game.physics;

import java.util.ArrayList;
import java.util.Arrays;

import game.Game;
import game.gameobjects.GameObject;
import r3.mathstuff.Mathstuff;

public class CollisionStuff {
	public static boolean collides(GameObject gameObject, double[] gameObjectHitboxCenterPosition, double[] pos) {
		//LOOP THROUGH EVERYTHING
		//--krasses cache field of things
		double[] vecAB = new double[3], vecAC = new double[3],vecCB = new double[3], vecTriangleNormal = new double[3],vecMiddleIntercept = new double[3],vecInterceptMiddleUnified = new double[3],pointIntercept = new double[3],pointInterceptHitbox = new double[3], vecOldPosNewPos = new double[3],pointP = new double[3];
		double[] vectorAP = new double[3];
		double[] hitboxCenterGameObject = gameObjectHitboxCenterPosition;		 
		double hitboxGameObjectRadius = gameObject.getHitbox().getRadius();
		double lambdaNormal,lambdaCB,lambdaAP,a,b,c,lambdaAB,lambdaAB1,lambdaAB2,discriminantCache,lambdaP;
		//--
		
		ArrayList<GameObject> gameObjects = Game.getGame().getGameObjects();
		GameObject gameObjWorld;
		double[][][] gameObjTriangles;
		for(int gameObjectI = gameObjects.size()-1; gameObjectI >= 0; gameObjectI--){			
			gameObjWorld = gameObjects.get(gameObjectI);
			if(gameObject == gameObjWorld||gameObjWorld.isRemoved())
				continue;
			if((gameObjWorld.getClass().getName().equals("game.gameobjects.GameObject")&&!gameObject.getClass().getName().equals("game.gameobjects.GameObject"))||(!gameObjWorld.getClass().getName().equals("game.gameobjects.GameObject")&&gameObject.getClass().getName().equals("game.gameobjects.GameObject")))
			{
				continue;
			}
			gameObjTriangles = gameObjWorld.getTrianglesAbsolute();
						
			for(int triangleI = 0; triangleI < gameObjTriangles.length; triangleI++) {		
				
//				System.out.println(triangleI +Arrays.toString(gameObjTriangles[triangleI][0])+Arrays.toString(gameObjTriangles[triangleI][1])+Arrays.toString(gameObjTriangles[triangleI][2]));
//				System.out.println(Arrays.toString(hitboxCenterGameObject));
			//Calculate needed vectors
				vecAB[0] = gameObjTriangles[triangleI][1][0] - gameObjTriangles[triangleI][0][0];
				vecAB[1] = gameObjTriangles[triangleI][1][1] - gameObjTriangles[triangleI][0][1];
				vecAB[2] = gameObjTriangles[triangleI][1][2] - gameObjTriangles[triangleI][0][2];
				
				vecAC[0] = gameObjTriangles[triangleI][2][0] - gameObjTriangles[triangleI][0][0];
				vecAC[1] = gameObjTriangles[triangleI][2][1] - gameObjTriangles[triangleI][0][1];
				vecAC[2] = gameObjTriangles[triangleI][2][2] - gameObjTriangles[triangleI][0][2];
				
				vecCB[0] = gameObjTriangles[triangleI][1][0] - gameObjTriangles[triangleI][2][0];
				vecCB[1] = gameObjTriangles[triangleI][1][1] - gameObjTriangles[triangleI][2][1];
				vecCB[2] = gameObjTriangles[triangleI][1][2] - gameObjTriangles[triangleI][2][2];
				
				vecTriangleNormal[0] = (vecAC[1] * vecAB[2]) - (vecAC[2] * vecAB[1]);
				vecTriangleNormal[1] = (vecAC[2] * vecAB[0]) - (vecAC[0] * vecAB[2]);
				vecTriangleNormal[2] = (vecAC[0] * vecAB[1]) - (vecAC[1] * vecAB[0]);
				Mathstuff.vectorUnify(vecTriangleNormal);
//				pointMiddle[0]=(gameObjTriangles[triangleI][0][0] + gameObjTriangles[triangleI][1][0] + gameObjTriangles[triangleI][2][0]) / 3;
//				pointMiddle[0]=(gameObjTriangles[triangleI][0][1] + gameObjTriangles[triangleI][1][1] + gameObjTriangles[triangleI][2][1]) / 3;
//				pointMiddle[0]=(gameObjTriangles[triangleI][0][2] + gameObjTriangles[triangleI][1][2] + gameObjTriangles[triangleI][2][2]) / 3;
				
				vecOldPosNewPos[0] = gameObjectHitboxCenterPosition[0] - pos[0];
				vecOldPosNewPos[1] = gameObjectHitboxCenterPosition[1] - pos[1];
				vecOldPosNewPos[2] = gameObjectHitboxCenterPosition[2] - pos[2];
				
				lambdaP =
				-((pos[0]-gameObjTriangles[triangleI][0][0])*vecTriangleNormal[0]+(pos[1]-gameObjTriangles[triangleI][0][1])*vecTriangleNormal[1]+(pos[2]-gameObjTriangles[triangleI][0][2])*vecTriangleNormal[2])
							/
	 			(vecOldPosNewPos[0]*vecTriangleNormal[0] + vecOldPosNewPos[1]*vecTriangleNormal[0] + vecOldPosNewPos[2]*vecTriangleNormal[0]);
						
				if(lambdaP>=0&&lambdaP<=1)
				{
						
						
					pointP[0] = lambdaP * vecOldPosNewPos[0] + pos[0]; 
					pointP[1] = lambdaP * vecOldPosNewPos[1] + pos[1];
					pointP[2] = lambdaP * vecOldPosNewPos[2] + pos[2];
					
					vectorAP[0] = pointP[0] - gameObjTriangles[triangleI][0][0];
					vectorAP[1] = pointP[1] - gameObjTriangles[triangleI][0][1];
					vectorAP[2] = pointP[2] - gameObjTriangles[triangleI][0][2];
					
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
//					System.out.println(lambdaCB+">=0,<=1");
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
//					System.out.println(lambdaAP+">=1");
					if(lambdaAP>=1&&lambdaCB<=1&&lambdaCB>=0)
					{
						System.out.println("lambdaAP: "+lambdaAP+", lambdaCB: "+lambdaCB);
						if(gameObject.isDamageAffected())
						{
							gameObject.remove(true);
						}
						if(gameObjWorld.isDamageAffected())
						{
							gameObjWorld.remove(true);
						}
						return true;
					}
				}
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
				
			//Point where hitbox touches plane(Ebene) of triangle
				pointIntercept[0] = hitboxCenterGameObject[0] + (vecTriangleNormal[0] * lambdaNormal);
				pointIntercept[1] = hitboxCenterGameObject[1] + (vecTriangleNormal[1] * lambdaNormal);
				pointIntercept[2] = hitboxCenterGameObject[2] + (vecTriangleNormal[2] * lambdaNormal);
				
				if(Mathstuff.isInsideOfTriangle(pointIntercept, gameObjTriangles[triangleI]))
				{
					if(gameObject.isDamageAffected())
					{
						gameObject.remove(true);
					}
					if(gameObjWorld.isDamageAffected())
					{
						gameObjWorld.remove(true);
					}
					return true;
				}
		//Check if and where VectorAB collides with hitboxCenterGameObject
				a = (Math.pow((vecAB[0]), 2)+Math.pow((vecAB[1]), 2)+Math.pow((vecAB[2]), 2));
				b = (2*(gameObjTriangles[triangleI][0][0]-hitboxCenterGameObject[0])*vecAB[0])+(2*(gameObjTriangles[triangleI][0][1]-hitboxCenterGameObject[1])*vecAB[1])+(2*(gameObjTriangles[triangleI][0][2]-hitboxCenterGameObject[2])*vecAB[2]);
				c = Math.pow((gameObjTriangles[triangleI][0][0]-hitboxCenterGameObject[0]), 2)+Math.pow((gameObjTriangles[triangleI][0][1]-hitboxCenterGameObject[1]), 2)+Math.pow((gameObjTriangles[triangleI][0][2]-hitboxCenterGameObject[2]), 2)-Math.pow(hitboxGameObjectRadius, 2);
			 
				if((discriminantCache = Math.pow(b, 2)-(4*a*c))>=0)
				{
					lambdaAB1 =
							(-b + Math.sqrt(Math.pow(b, 2)-(4*a*c)))
							/
							(2*a);
					lambdaAB2 =
							(-b - Math.sqrt(Math.pow(b, 2)-(4*a*c)))
							/
							(2*a);
					if(Math.abs(lambdaAB1) < Math.abs(lambdaAB2))
					{
						if(lambdaAB1>=0&&lambdaAB1<=Mathstuff.length(vecAB))
						{
							if(gameObject.isDamageAffected())
							{
								gameObject.remove(true);
							}
							if(gameObjWorld.isDamageAffected())
							{
								gameObjWorld.remove(true);
							}
							return true;
						}
					}
					else
					{
						if(lambdaAB2>=0&&lambdaAB2<=Mathstuff.length(vecAB))
						{
							if(gameObject.isDamageAffected())
							{
								gameObject.remove(true);
							}
							if(gameObjWorld.isDamageAffected())
							{
								gameObjWorld.remove(true);
							}
							return true;
						}
					}
				}
		//Check if and where VectorAC collides with hitboxCenterGameObject		
				a = (Math.pow((vecAC[0]), 2)+Math.pow((vecAC[1]), 2)+Math.pow((vecAC[2]), 2));
				b = (2*(gameObjTriangles[triangleI][0][0]-hitboxCenterGameObject[0])*vecAC[0])+(2*(gameObjTriangles[triangleI][0][1]-hitboxCenterGameObject[1])*vecAC[1])+(2*(gameObjTriangles[triangleI][0][2]-hitboxCenterGameObject[2])*vecAC[2]);
				c = Math.pow((gameObjTriangles[triangleI][0][0]-hitboxCenterGameObject[0]), 2)+Math.pow((gameObjTriangles[triangleI][0][1]-hitboxCenterGameObject[1]), 2)+Math.pow((gameObjTriangles[triangleI][0][2]-hitboxCenterGameObject[2]), 2)-Math.pow(hitboxGameObjectRadius, 2);
			 
				if((discriminantCache = Math.pow(b, 2)-(4*a*c))>=0)
				{
					lambdaAB1 =
							(-b + Math.sqrt(Math.pow(b, 2)-(4*a*c)))
							/
							(2*a);
					lambdaAB2 =
							(-b - Math.sqrt(Math.pow(b, 2)-(4*a*c)))
							/
							(2*a);
					if(Math.abs(lambdaAB1) < Math.abs(lambdaAB2))
					{
						if(lambdaAB1>=0&&lambdaAB1<=Mathstuff.length(vecAB))
						{
							if(gameObject.isDamageAffected())
							{
								gameObject.remove(true);
							}
							if(gameObjWorld.isDamageAffected())
							{
								gameObjWorld.remove(true);
							}
							return true;
						}
					}
					else
					{
						if(lambdaAB2>=0&&lambdaAB2<=Mathstuff.length(vecAB))
						{
							if(gameObject.isDamageAffected())
							{
								gameObject.remove(true);
							}
							if(gameObjWorld.isDamageAffected())
							{
								gameObjWorld.remove(true);
							}
							return true;
						}
					}
				}
		//Check if and where VectorCB collides with hitboxCenterGameObject		
				a = (Math.pow((vecCB[0]), 2)+Math.pow((vecCB[1]), 2)+Math.pow((vecCB[2]), 2));
				b = (2*(gameObjTriangles[triangleI][0][0]-hitboxCenterGameObject[0])*vecCB[0])+(2*(gameObjTriangles[triangleI][0][1]-hitboxCenterGameObject[1])*vecCB[1])+(2*(gameObjTriangles[triangleI][0][2]-hitboxCenterGameObject[2])*vecCB[2]);
				c = Math.pow((gameObjTriangles[triangleI][0][0]-hitboxCenterGameObject[0]), 2)+Math.pow((gameObjTriangles[triangleI][0][1]-hitboxCenterGameObject[1]), 2)+Math.pow((gameObjTriangles[triangleI][0][2]-hitboxCenterGameObject[2]), 2)-Math.pow(hitboxGameObjectRadius, 2);
			 
				if((discriminantCache = Math.pow(b, 2)-(4*a*c))>=0)
				{
					lambdaAB1 =
							(-b + Math.sqrt(Math.pow(b, 2)-(4*a*c)))
							/
							(2*a);
					lambdaAB2 =
							(-b - Math.sqrt(Math.pow(b, 2)-(4*a*c)))
							/
							(2*a);
					if(Math.abs(lambdaAB1) < Math.abs(lambdaAB2))
					{
						if(lambdaAB1>=0&&lambdaAB1<=Mathstuff.length(vecAB))
						{
							if(gameObject.isDamageAffected())
							{
								gameObject.remove(true);
							}
							if(gameObjWorld.isDamageAffected())
							{
								gameObjWorld.remove(true);
							}
							return true;
						}
					}
					else
					{
						if(lambdaAB2>=0&&lambdaAB2<=Mathstuff.length(vecAB))
						{
							if(gameObject.isDamageAffected())
							{
								gameObject.remove(true);
							}
							if(gameObjWorld.isDamageAffected())
							{
								gameObjWorld.remove(true);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
