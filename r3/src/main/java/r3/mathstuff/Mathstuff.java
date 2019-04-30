package r3.mathstuff;

public class Mathstuff {
	//Das übergebene Array: double[amount][3(Punkte von einem Dreieck)][3(Komponenten je Punkt)]

	public static double[][][] calcR3(double[][][] coords, double[] forward, double[] camPos,double alpha, double beta,double factor)	//f:forward vector; a:position of camera, alpha:rotation x2, beta:rotation x3
	{
		double[] z = new double[] {forward[0] + camPos[0],forward[1] + camPos[1],forward[2] + camPos[2]};			//z:"angriffspunkt ebene"
		for(int x = 0;x<coords.length;x++)
		{
			for(int y = 0;y<3;y++)
			{
				double[] b = new double[] {coords[x][y][0]-camPos[0],coords[x][y][1]-camPos[1],coords[x][y][2]-camPos[2]}; //b:vector camera to point
				double bLength = length(b);
				b = new double[] {b[0]/bLength,b[1]/bLength,b[2]/bLength};	//b0
				double lambda = -
				(forward[0]*(camPos[0]-z[0])+forward[1]*(camPos[1]-z[1])+forward[2]*(camPos[2]-z[2]))
						/
				(forward[0]*b[0]+forward[1]*b[1]+forward[2]*b[2]);
				//System.out.println("b;X1: "+b[0]+", bX2: "+b[1]+", bX3: "+b[2]+", lambda: "+lambda);
				if(lambda < 0)
				{
					coords[x][y][0] = 0;	
					coords[x][y][1] = 0;
					coords[x][y][2] = 0;
					continue;
				}
				else
				{
					double[] vecCamPosS = new double[] {lambda * b[0],lambda * b[1],lambda * b[2]};	//jetzt:vektor kamera->schnittpunkt
										
					double[] vecCamPosSX2 = new double[] {Math.cos(-alpha)*vecCamPosS[0] + Math.sin(-alpha)*vecCamPosS[2],vecCamPosS[1],-Math.sin(-alpha)*vecCamPosS[0] + Math.cos(-alpha)*vecCamPosS[2]};
					
					coords[x][y][0]	= (Math.cos(-beta)*vecCamPosSX2[0] - Math.sin(-beta)*vecCamPosSX2[1])*factor;
					coords[x][y][1] = (Math.sin(-beta)*vecCamPosSX2[0] + Math.cos(-beta)*vecCamPosSX2[1])*factor;
			        coords[x][y][2] = (vecCamPosSX2[2])*factor;
//						System.out.println(coords[x][y][1]);
//						System.out.println(coords[x][y][2]);
					//zurückdrehen, jetzt kann x1 ignoriert werden
				}
			}
		}
		return coords;
	}
	public static int[] calcR3Point(double[] coords, double[] forward, double[] camPos,double alpha, double beta,double length,double factor)	//f:forward vector; a:position of camera, alpha:rotation x2, beta:rotation x3, factor: gibt LE -> Pixel Verhältnis an, length gibt die Länge von b an(und speichert es rein)
	{
		//System.out.println("Gegeben: X1"+coords[0]+",X2"+coords[1]+",X3"+coords[2]+",forwardX1"+forward[0]+",camPosX1"+camPos[0]);
		double[] z = new double[] {forward[0] + camPos[0],forward[1] + camPos[1],forward[2] + camPos[2]};			//z:"angriffspunkt ebene"
		double[] b = new double[] {coords[0]-camPos[0],coords[1]-camPos[1],coords[2]-camPos[2]}; //b:vector camera to point
		double bLength = length(b);
		length = bLength;
		//System.out.println("Length of b: "+bLength);
		b = new double[] {b[0]/bLength,b[1]/bLength,b[2]/bLength};
		double lambda = -
		(forward[0]*(camPos[0]-z[0])+forward[1]*(camPos[1]-z[1])+forward[2]*(camPos[2]-z[2]))
				/
		(forward[0]*b[0]+forward[1]*b[1]+forward[2]*b[2]);
		//System.out.println("Lambda: "+lambda);
		if(lambda < 0)
		{
//				coords[0] = 0;	
//				coords[1] = -1;
//				coords[2] = -1;
			length = 0;
			return new int[] {0,-1,-1};
		}
		else
		{
			double[] vecCamPosS = new double[] {lambda * b[0],lambda * b[1],lambda * b[2]};	//jetzt:vektor kamera->schnittpunkt
			//System.out.println("Vec Cam Pos SX1:"+vecCamPosS[0]+"Vec Cam Pos SX2:"+vecCamPosS[1]+"Vec Cam Pos SX2:"+vecCamPosS[2]);	
			double[] vecCamPosSX2 = new double[] {Math.cos(-alpha)*vecCamPosS[0] + Math.sin(-alpha)*vecCamPosS[2],vecCamPosS[1],-Math.sin(-alpha)*vecCamPosS[0] + Math.cos(-alpha)*vecCamPosS[2]};
		//	System.out.println("Vec Cam Pos SX2 : X1:"+vecCamPosSX2[0]+"Vec Cam Pos SX2:"+vecCamPosSX2[1]+"Vec Cam Pos SX2:"+vecCamPosSX2[2]);
			//return new int[] {(int)((Math.cos(-beta)*vecCamPosSX2[0] - Math.sin(-beta)*vecCamPosSX2[1])*factor),(int)((Math.sin(-beta)*vecCamPosSX2[0] + Math.cos(-beta)*vecCamPosSX2[1])*factor),(int)((vecCamPosSX2[2])*factor)};
			length = bLength;
			return new int[] {0,(int)((Math.sin(-beta)*vecCamPosSX2[0] + Math.cos(-beta)*vecCamPosSX2[1])*factor),(int)((vecCamPosSX2[2])*factor)};
//				coords[0]	= Math.cos(-beta)*vecCamPosSX2[0] - Math.sin(-beta)*vecCamPosSX2[1];
//				coords[1] = Math.sin(-beta)*vecCamPosSX2[0] + Math.cos(-beta)*vecCamPosSX2[1];
//				coords[2] = vecCamPosSX2[2];
					
			//zurückdrehen, jetzt kann x1 ignoriert werden
		}
			
		
	}
	public static double[][] calcR3ZBuff(double[][][] coords, double[] forward, double[] camPos,double alpha, double beta,double factor)
	{
		//System.out.println("Start");
		double[][] Buffer2D = new double[1280][720];
		double[] ab;
		double[] ac;
		double[] bc;
		double lambda;
		double[] o;
		//System.out.println(coords.length);
		for(int x = 0;x<coords.length;x++)
		{
			//a = [0], b = [1], c = [2]
			//Vektor AB
			ab = new double[] {coords[x][1][0]-coords[x][0][0],coords[x][1][1]-coords[x][0][1],coords[x][1][2]-coords[x][0][2]};
			double abLength = length(ab);
			ab = new double[] {ab[0]/abLength,ab[1]/abLength,ab[2]/abLength};
			//Vektor AC
			ac = new double[] {coords[x][2][0]-coords[x][0][0],coords[x][2][1]-coords[x][0][1],coords[x][2][2]-coords[x][0][2]};
			double acLength = length(ac);
			ac = new double[] {ac[0]/acLength,ac[1]/acLength,ac[2]/acLength};
			//Vektor BC
			bc = new double[]{coords[x][2][0]-coords[x][1][0],coords[x][2][1]-coords[x][1][1],coords[x][2][2]-coords[x][1][2]};
			double bcLength = length(bc);
			bc = new double[] {bc[0]/bcLength,bc[1]/bcLength,bc[2]/bcLength};
			//lambda gibt den Faktor der Geraden a + lambda * ab an bei dem der Vektor o senkrecht auf ab steht o = x -> c
			//Faktor lambda gibt die Stelle auf der Geraden AB*lambda + A an, bei der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
			lambda = -
			(ab[0]*(coords[x][2][0]-coords[x][0][0])+ab[1]*(coords[x][2][1]-coords[x][0][1])+ab[0]*(coords[x][2][2]-coords[x][0][2]))
							/
			(ab[0]*ab[0]+ab[1]*ab[1]+ab[2]*ab[2]);
			//Vektor O 
			o  = new double[] {coords[x][2][0]-(coords[x][0][0]+ab[0]*lambda),coords[x][2][1]-(coords[x][0][1]+ab[1]*lambda),coords[x][2][2]-(coords[x][0][2]+ab[2]*lambda)};
			double oLength = length(o);
			o = new double[] {o[0]/oLength,o[1]/oLength,o[2]/oLength};
			
			//acB:Zeigt an, ob der Schnittpunkt o-ac (true) oder o-bc(false) gebildet wird
			boolean acB = true;
			//Lambda1:Stelle auf der Gerade AB*lambda1 + A
			double lambda2End = 0;
			double lambda3 = 0;
			double lengthB = 0;
			for(double lambda1 = 0;lambda1<=abLength;lambda1++)
			{
				//System.out.println("Lambda1: "+lambda1);
				//lambda2End gibt an, wo sich o*lambda2 und AC schneiden 
				if(acB)
				{
					lambda2End = 
					(lambda1*ab[0]*ac[1]-lambda1*ab[1]*ac[0])
							/
					(o[1]*ac[0]-o[0]*ac[1]);
					lambda3=
					(lambda2End*o[2]+lambda1*ab[2])
							/
					(ac[2]);
					if(lambda3>acLength)
						acB = false;
				}
				//System.out.println("Lambda2End: " + lambda2End);
				if(acB)
				{
					//lambda2 gibt die Stelle auf der Geraden o*lambda2 an
					for(double lambda2 = 0;lambda2<=lambda2End;lambda2++)
					{
						//Gefundener Punkt
						//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]};
						//System.out.println("ForwardX1:"+forward[0]);
						int[] R3Point = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
						//System.out.println("Bekommen X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						//System.out.println("X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						if(R3Point[1]>=0&&R3Point[2]>=0&&R3Point[1]<1280&&R3Point[2]<720&&(Buffer2D[R3Point[1]][R3Point[2]] > lengthB||Buffer2D[R3Point[1]][R3Point[2]]==0))
						{
							Buffer2D[R3Point[1]][R3Point[2]] = lengthB;
							//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						}
						if(lambda2+1 > lambda2End)
						{
							//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab[2]};
							int[] R3PointEnd = calcR3Point(new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
							//System.out.println("Bekommen X:"+(int)R3PointEnd[1]+" ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" - Final");
							if(R3PointEnd[1]>=0&&R3PointEnd[2]>=0&&R3PointEnd[1]<1280&&R3PointEnd[2]<720&&(Buffer2D[R3PointEnd[1]][R3PointEnd[2]] > lengthB||Buffer2D[R3PointEnd[1]][R3PointEnd[2]]==0))
							{
								Buffer2D[R3PointEnd[1]][R3PointEnd[2]] = lengthB;
								//System.out.println("X:"+(int)R3PointEnd[1]+" ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" - Final");
							}
							break;
						}
					}
					if(lambda1+1 > abLength)
					{
						
						double lambda2End2 = -
						(abLength*ab[0]*ac[1]-abLength*ab[1]*ac[0])
										/
						(o[1]*ac[0]-o[0]*ac[1]);
						for(double lambda2 = 0;lambda2<=lambda2End2;lambda2++)
						{
							//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2*o[2] + coords[x][0][2] + abLength*ab[2]};
							int[] R3Point = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2*o[2] + coords[x][0][2] + abLength*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
							//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
							if(R3Point[1]>=0&&R3Point[2]>=0&&R3Point[1]<1280&&R3Point[2]<720&&(Buffer2D[R3Point[1]][R3Point[2]] > lengthB||Buffer2D[R3Point[1]][R3Point[2]]==0))
							{
								Buffer2D[R3Point[1]][R3Point[2]] = lengthB;
								//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
							}
							if(lambda2+1 > lambda2End2)
							{
								//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + abLength*ab[0],lambda2End*o[1] + coords[x][0][1] + abLength*ab[1],lambda2End*o[2] + coords[x][0][2] + abLength*ab[2]};
								int[] R3PointEnd = calcR3Point(new double[] {lambda2End2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2End2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2End2*o[2] + coords[x][0][2] + abLength*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
								//System.out.println("Bekommen X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
								if(R3PointEnd[1]>=0&&R3PointEnd[2]>=0&&R3PointEnd[1]<1280&&R3PointEnd[2]<720&&(Buffer2D[R3PointEnd[1]][R3PointEnd[2]] > lengthB||Buffer2D[R3PointEnd[1]][R3PointEnd[2]]==0))
								{
									Buffer2D[R3PointEnd[1]][R3PointEnd[2]] = lengthB;
									//System.out.println("X:"+(int)R3PointEnd[1]+" ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" - Final");
								}
								break;
							}
						}
						break;
					}
				}
				else
				{
					//Jetzt wird mit der Geraden BC*lambda3+B gerechnet
					lambda2End=
					(lambda1*ab[0]*bc[1]-lambda1*ab[1]*bc[0])
							/
					(o[1]*bc[0]-o[0]*bc[1]);
					for(double lambda2 = 0;lambda2<=lambda2End;lambda2++)
					{
						//Gefundener Punkt
						//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]};
						//System.out.println("ForwardX1:"+forward[0]);
						int[] R3Point = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
						//System.out.println("Bekommen X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						//System.out.println("X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						if(R3Point[1]>=0&&R3Point[2]>=0&&R3Point[1]<1280&&R3Point[2]<720&&(Buffer2D[R3Point[1]][R3Point[2]] > lengthB||Buffer2D[R3Point[1]][R3Point[2]]==0))
						{
							Buffer2D[R3Point[1]][R3Point[2]] = lengthB;
							//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						}
						if(lambda2+1 > lambda2End)
						{
							//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab[2]};
							int[] R3PointEnd = calcR3Point(new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
							//System.out.println("Bekommen X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
							if(R3PointEnd[1]>=0&&R3PointEnd[2]>=0&&R3PointEnd[1]<1280&&R3PointEnd[2]<720&&(Buffer2D[R3PointEnd[1]][R3PointEnd[2]] > lengthB||Buffer2D[R3PointEnd[1]][R3PointEnd[2]]==0))
							{
								Buffer2D[R3PointEnd[1]][R3PointEnd[2]] = lengthB;
								//System.out.println("X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
							}
							break;
						}
					}
					if(lambda1+1 > abLength)
					{
						
						double lambda2End2 = -
						(abLength*ab[0]*bc[1]-abLength*ab[1]*bc[0])
										/
						(o[1]*bc[0]-o[0]*bc[1]);
						for(double lambda2 = 0;lambda2<=lambda2End2;lambda2++)
						{
							//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2*o[2] + coords[x][0][2] + abLength*ab[2]};
							int[] R3Point = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2*o[2] + coords[x][0][2] + abLength*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
							//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
							if(R3Point[1]>=0&&R3Point[2]>=0&&R3Point[1]<1280&&R3Point[2]<720&&(Buffer2D[R3Point[1]][R3Point[2]] > lengthB||Buffer2D[R3Point[1]][R3Point[2]]==0))
							{
								Buffer2D[R3Point[1]][R3Point[2]] = lengthB;
								//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
							}
							if(lambda2+1 > lambda2End2)
							{
								//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + abLength*ab[0],lambda2End*o[1] + coords[x][0][1] + abLength*ab[1],lambda2End*o[2] + coords[x][0][2] + abLength*ab[2]};
								int[] R3PointEnd = calcR3Point(new double[] {lambda2End2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2End2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2End2*o[2] + coords[x][0][2] + abLength*ab[2]},forward,camPos,alpha,beta,lengthB,factor);
								//System.out.println("Bekommen X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
								if(R3PointEnd[1]>=0&&R3PointEnd[2]>=0&&R3PointEnd[1]<1280&&R3PointEnd[2]<720&&(Buffer2D[R3PointEnd[1]][R3PointEnd[2]] > lengthB||Buffer2D[R3PointEnd[1]][R3PointEnd[2]]==0))
								{
									Buffer2D[R3PointEnd[1]][R3PointEnd[2]] = lengthB;
									//System.out.println("X:"+(int)R3PointEnd[1]+" ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" - Final");
									}
									break;
								}
							}
							break;
						}
					}
				}
				
			}
			return Buffer2D;
		}
		private static double length(double[] vectorR3)
		{
			return Math.sqrt(vectorR3[0]*vectorR3[0]+vectorR3[1]*vectorR3[1]+vectorR3[2]*vectorR3[2]);
		}
}
