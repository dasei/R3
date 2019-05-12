package r3.mathstuff;

import r3.main.Main;

public class Mathstuff {
	//Das übergebene Array: double[amount][3(Punkte von einem Dreieck)][3(Komponenten je Punkt)]
	private static double fov = Main.getCamera().fov;
	private static double fovFactor = 0.5 * (1/Math.tan(Math.toRadians(fov/2)));
	private static int screenX = Main.getWindow().getDrawComp().getWidth();
	private static int screenY = Main.getWindow().getDrawComp().getHeight();
	
	private static int screenCenterX = screenX/2;
	private static int screenCenterY = screenY/2;
	public static void calcR3(double[][][] coords, double[] forward, double[] camPos,double alpha, double beta,double factor)	//f:forward vector; a:position of camera, alpha:rotation x2, beta:rotation x3
	{

		double fov = Main.getCamera().fov;
		double fovFactor = 0.5 * (1/Math.tan(Math.toRadians(fov/2)));
		int screenWidth = Main.getWindow().getDrawComp().getWidth();
		int screenHeight = Main.getWindow().getDrawComp().getHeight();

		

		int screenCenterX = screenWidth/2;
		int screenCenterY = screenHeight/2;
		
		//System.out.println(Arrays.toString(forward));
		int screenSizeMinimum = Math.min(screenWidth, screenHeight);
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
				
				if(lambda < 0) {
					for(int j = 0;j<3;j++)
					{
						Main.coordsDraw[x][j][0] = 0;
						Main.coordsDraw[x][j][1] = 0;
					}
					break;
				}
		        double[] vecCamPosS = new double[] {lambda * b[0],lambda * b[1],lambda * b[2]};	//jetzt:vektor kamera->schnittpunkt
				double[] vecCamPosSX3 = new double[] {(Math.cos(-beta)*vecCamPosS[0] - Math.sin(-beta)*vecCamPosS[1]), Math.sin(-beta)*vecCamPosS[0] + Math.cos(-beta)*vecCamPosS[1], (vecCamPosS[2])};
				Main.coordsDraw[x][y] = new int[] {screenCenterX+(int)((vecCamPosSX3[1]*factor)*screenSizeMinimum*fovFactor), screenCenterY-(int)(((-Math.sin(-alpha)*vecCamPosSX3[0] + Math.cos(-alpha)*vecCamPosSX3[2])*factor)*screenSizeMinimum*fovFactor)};
				//System.out.println("b;X1: "+b[0]+", bX2: "+b[1]+", bX3: "+b[2]+", lambda: "+lambda);
		        //zurückdrehen, jetzt kann x1 ignoriert werden
				
			}
		}
		//return coords;
	}
	
	private static double[] z;
	private static double[] b;
	private static double lambda;
	private static double[] vecCamPosS;
	private static double[] vecCamPosSX3;
	
	private static double screenSizeMinimum;
	/**
	 * f:forward vector; a:position of camera, alpha:rotation x2, beta:rotation x3, factor: gibt LE -> Pixel Verhältnis an, length gibt die Länge von b an(und speichert es rein)	 * 
	 */
	public static double calcR3Point(double[] coords, double[] forward, double[] camPos,double alpha, double beta,double factor){	
	
		//System.out.println("-----------------1");
		//System.out.println("Gegeben: X1"+coords[0]+",X2"+coords[1]+",X3"+coords[2]+",forwardX1"+forward[0]+",camPosX1"+camPos[0]);
		z = new double[] {forward[0] + camPos[0],forward[1] + camPos[1],forward[2] + camPos[2]};			//z:"angriffspunkt ebene"
		b = new double[] {coords[0]-camPos[0],coords[1]-camPos[1],coords[2]-camPos[2]}; //b:vector camera to point
		double bLength = length(b);
		//System.out.println("Length of b: "+bLength);
		b = new double[] {b[0]/bLength,b[1]/bLength,b[2]/bLength};
		lambda = -
		(forward[0]*(camPos[0]-z[0])+forward[1]*(camPos[1]-z[1])+forward[2]*(camPos[2]-z[2]))
				/
		(forward[0]*b[0]+forward[1]*b[1]+forward[2]*b[2]);
		//System.out.println("Lambda: "+lambda);
		if(lambda < 0)
		{
//				coords[0] = 0;	
//				coords[1] = -1;
//				coords[2] = -1;
			
			coordsINTCache = new int[] {0,0,0};
			return 0;
		}
		else
		{
			vecCamPosS = new double[] {lambda * b[0],lambda * b[1],lambda * b[2]};	//jetzt:vektor kamera->schnittpunkt
			//System.out.println("Vec Cam Pos SX1:"+vecCamPosS[0]+"Vec Cam Pos SX2:"+vecCamPosS[1]+"Vec Cam Pos SX2:"+vecCamPosS[2]);	
			vecCamPosSX3 = new double[] {(Math.cos(-beta)*vecCamPosS[0] - Math.sin(-beta)*vecCamPosS[1]), Math.sin(-beta)*vecCamPosS[0] + Math.cos(-beta)*vecCamPosS[1], (vecCamPosS[2])};
		//	System.out.println("Vec Cam Pos SX2 : X1:"+vecCamPosSX2[0]+"Vec Cam Pos SX2:"+vecCamPosSX2[1]+"Vec Cam Pos SX2:"+vecCamPosSX2[2]);
			//return new int[] {(int)((Math.cos(-beta)*vecCamPosSX2[0] - Math.sin(-beta)*vecCamPosSX2[1])*factor),(int)((Math.sin(-beta)*vecCamPosSX2[0] + Math.cos(-beta)*vecCamPosSX2[1])*factor),(int)((vecCamPosSX2[2])*factor)};
			//System.out.println("-----------------2");
			//System.out.println(Arrays.toString(new int[] {0,screenCenterX+(int)((vecCamPosSX3[1]*factor)*screenX*fovFactor), screenCenterY-(int)(((-Math.sin(-alpha)*vecCamPosSX3[0] + Math.cos(-alpha)*vecCamPosSX3[2])*factor)*screenY*fovFactor)}));
			coordsINTCache = new int[] {0,screenCenterX+(int)((vecCamPosSX3[1]*factor)*screenSizeMinimum*fovFactor), screenCenterY-(int)(((-Math.sin(-alpha)*vecCamPosSX3[0] + Math.cos(-alpha)*vecCamPosSX3[2])*factor)*screenSizeMinimum*fovFactor)};
		    //System.out.println(Arrays.toString(coordsINTCache));
			
			return bLength;
//				coords[0]	= Math.cos(-beta)*vecCamPosSX2[0] - Math.sin(-beta)*vecCamPosSX2[1];
//				coords[1] = Math.sin(-beta)*vecCamPosSX2[0] + Math.cos(-beta)*vecCamPosSX2[1];
//				coords[2] = vecCamPosSX2[2];
					
			//zurückdrehen, jetzt kann x1 ignoriert werden
		}
			
		
	}
	public double[] calcMiddle(double[][] pointsABC)
	{
		
		return new double[]{(pointsABC[0][0]+pointsABC[1][0]+pointsABC[2][0])/3,(pointsABC[0][1]+pointsABC[1][1]+pointsABC[2][1])/3,(pointsABC[0][2]+pointsABC[1][2]+pointsABC[2][2])/3};
	}
	private static int[] coordsINTCache;
	public static double[][] calcR3ZBuff(double[][][] coords, double[] forward, double[] camPos,double alpha, double beta,double factor)
	{
		long pointsAmount = 0;
//		System.out.println(Arrays.toString(forward));
//		System.out.println(Arrays.toString(camPos));
		//System.out.println("Start");
		fov = Main.getCamera().fov;
		fovFactor = 0.5 * (1/Math.tan(Math.toRadians(fov/2)));
		screenX = Main.getWindow().getDrawComp().getWidth();
		screenY = Main.getWindow().getDrawComp().getHeight();
		
		screenCenterX = screenX/2;
		screenCenterY = screenY/2;
		
		screenSizeMinimum = Math.min(screenX, screenY);
		double[][] Buffer2D = new double[Main.getWindow().getDrawComp().getWidth()][Main.getWindow().getDrawComp().getHeight()];
		double[] ab0;
		double[] ac;
		double[] bc;
		double lambda;
		double[] o;
		
		double[] middle;
		//System.out.println(coords.length);
		boolean acB = true;
		//Lambda1:Stelle auf der Gerade AB*lambda1 + A
		double lambda2End = 0;
		double lengthB = 0;
//		double bcLength = 0;
		double lengthMiddle = 0;
		double oLength = 0;
		
		double precision = 0.1;
		//System.out.println("start");
		for(int x = 0;x<coords.length;x++)
		{
			//a = [0], b = [1], c = [2]
			//Vektor AB
			acB = true;
			ab0 = new double[] {coords[x][1][0]-coords[x][0][0],coords[x][1][1]-coords[x][0][1],coords[x][1][2]-coords[x][0][2]};
			double abLength = length(ab0);
			double[] ab = ab0;
			ab0 = new double[] {ab0[0]/abLength,ab0[1]/abLength,ab0[2]/abLength};
			//Vektor AC
			ac = new double[] {coords[x][2][0]-coords[x][0][0],coords[x][2][1]-coords[x][0][1],coords[x][2][2]-coords[x][0][2]};
			double acLength = length(ac);
			ac = new double[] {ac[0]/acLength,ac[1]/acLength,ac[2]/acLength};
			//Vektor BC
			bc = new double[]{coords[x][2][0]-coords[x][1][0],coords[x][2][1]-coords[x][1][1],coords[x][2][2]-coords[x][1][2]};
//			bcLength = length(bc);
			middle = new double[]{(coords[x][0][0]+coords[x][1][0]+coords[x][2][0])/3,(coords[x][0][1]+coords[x][1][1]+coords[x][2][1])/3,(coords[x][0][2]+coords[x][1][2]+coords[x][2][2])/3};
			lengthMiddle = calcR3Point(middle,forward,camPos,alpha,beta,factor);
			if(lengthMiddle!=0)
			{
				
//				double precisionOld = 0.00138*lengthMiddle+0.001;
				
//				precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
			//	System.out.println("--PrecisionOld: "+precisionOld+", PrecisionNew: "+precision+"--");
				
				//System.out.println(precision);
				precision = 0.001;

			}
			else
			{
				continue;
			}
			//System.out.println(Arrays.toString(middle));
			//bc = new double[] {bc[0]/bcLength,bc[1]/bcLength,bc[2]/bcLength};
			//lambda gibt den Faktor der Geraden a + lambda * ab an bei dem der Vektor o senkrecht auf ab steht o = x -> c
			//Faktor lambda gibt die Stelle auf der Geraden AB*lambda + A an, bei der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
			lambda = 
			(ab0[0]*(coords[x][2][0]-coords[x][0][0])+ab0[1]*(coords[x][2][1]-coords[x][0][1])+ab0[2]*(coords[x][2][2]-coords[x][0][2]))
							/
			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
//			if(lambda==0)
//			if(lambda < 0 || lambda > abLength)
//			{
//				System.out.println("lambda: "+lambda+", A: "+Arrays.toString(coords[x][0])+", B: "+Arrays.toString(coords[x][1])+", C: "+Arrays.toString(coords[x][2]));
//
//			}
			//Vektor O 
			o  = new double[] {coords[x][2][0]-(coords[x][0][0]+ab0[0]*lambda),coords[x][2][1]-(coords[x][0][1]+ab0[1]*lambda),coords[x][2][2]-(coords[x][0][2]+ab0[2]*lambda)};
			oLength = length(o);
			o = new double[] {o[0]/oLength,o[1]/oLength,o[2]/oLength};
			
			//acB:Zeigt an, ob der Schnittpunkt o-ac (true) oder o-bc(false) gebildet wird
			
			for(double lambda1 = 0;lambda1<=abLength;lambda1+=precision)
			{
				//System.out.println("Lambda1: "+lambda1);
				//lambda2End gibt an, wo sich o*lambda2 und AC schneiden 
				//System.out.println("lambda1: "+lambda1+acB);
				if(acB)
				{
					if((o[1]*ac[0]-o[0]*ac[1])!=0)
					{
						lambda2End = 
						(lambda1*ab0[1]*ac[0]-lambda1*ab0[0]*ac[1])
								/
						(o[0]*ac[1]-o[1]*ac[0]);
//						lambda3=
//						(lambda1*ab0[0]+lambda2End*o[0])
//								/
//						(ac[0]);
					}
					else if((o[0]*ac[2]-o[2]*ac[0])!=0)
					{
						lambda2End = 
						(lambda1*ab0[2]*ac[0]-lambda1*ab0[0]*ac[2])
								/
						(o[0]*ac[2]-o[2]*ac[0]);
//						lambda3=
//						(lambda1*ab0[0]+lambda2End*o[0])
//								/
//						(ac[0]);
					}
					else
					{
						lambda2End = 0;
					}
//					System.out.println("lambda1.1: "+lambda1);
					if(lambda1>lambda)
//						System.out.println("lambda1.2: "+lambda1);
						acB = false;
					//System.out.println("false");
				}
				//System.out.println("Lambda2End: " + lambda2End);
				if(acB)
				{
					//System.out.println(lambda2End);
					if(Double.isInfinite(lambda2End))
					{
						//System.out.println("Lambda1: "+lambda1+", Lambda2: "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B: "+Arrays.toString(coords[x][1])+", C: "+Arrays.toString(coords[x][2]));
						break;
					}
					//lambda2 gibt die Stelle auf der Geraden o*lambda2 an
					for(double lambda2 = 0;lambda2<=lambda2End;lambda2+=precision)
					{
						//System.out.println(Arrays.toString(new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]}));
						//Gefundener Punkt
						//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]};
						//System.out.println("ForwardX1:"+forward[0]);
						lengthB = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab0[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab0[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab0[2]},forward,camPos,alpha,beta,factor);
						//System.out.println("Bekommen X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						//System.out.println("X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
						{
							Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
							pointsAmount++;
							//System.out.println(Arrays.toString(coordsINTCache)+", length: "+lengthB);
								//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						}
						if(lambda2+precision > lambda2End&&lambda2End!=lambda2)
						{
							//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab[2]};
							lengthB = calcR3Point(new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab0[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab0[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab0[2]},forward,camPos,alpha,beta,factor);
							//System.out.println("Bekommen X:"+(int)R3PointEnd[1]+" ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" - Final");
							if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
							{
								Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
								pointsAmount++;
								//System.out.println("X:"+(int)R3PointEnd[1]+" ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" - Final");
							}
							break;
						}
					}
					if(lambda1+precision > abLength)
					{
						
						lambda2End = 
						(lambda1*ab0[1]*ac[0]-lambda1*ab0[0]*ac[1])
								/
						(o[0]*ac[1]-o[1]*ac[0]);
						//System.out.println(lambda2End);
						if(Double.isInfinite(lambda2End))
						{
							//System.out.println("Lambda1: "+lambda1+", Lambda2: "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B: "+Arrays.toString(coords[x][1])+", C: "+Arrays.toString(coords[x][2]));
							break;
						}
						for(double lambda2 = 0;lambda2<=lambda2End;lambda2+=precision)
						{
							lengthB = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab0[0],lambda2*o[1] + coords[x][0][1] + abLength*ab0[1],lambda2*o[2] + coords[x][0][2] + abLength*ab0[2]},forward,camPos,alpha,beta,factor);
							if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
							{
								Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
								pointsAmount++;
								//System.out.println(Arrays.toString(coordsINTCache)+", length: "+lengthB);
								//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
							}
							if(lambda2+precision > lambda2End&lambda2End!=lambda2)
							{
								//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + abLength*ab[0],lambda2End*o[1] + coords[x][0][1] + abLength*ab[1],lambda2End*o[2] + coords[x][0][2] + abLength*ab[2]};
								lengthB = calcR3Point(new double[] {lambda2End*o[0] + coords[x][0][0] + abLength*ab0[0],lambda2End*o[1] + coords[x][0][1] + abLength*ab0[1],lambda2End*o[2] + coords[x][0][2] + abLength*ab0[2]},forward,camPos,alpha,beta,factor);
								//System.out.println("Bekommen X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
								if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
								{
									Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
									pointsAmount++;
									//System.out.println(Arrays.toString(coordsINTCache)+", length: "+lengthB);
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
					lambda2End =
					(lambda1*ab0[0]*bc[1]-lambda1*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
							/
					(o[1]*bc[0]-o[0]*bc[1]);
					//System.out.println(lambda2End);
					if(Double.isInfinite(lambda2End))
					{
						//System.out.println("Lambda1: "+lambda1+", Lambda2: "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B: "+Arrays.toString(coords[x][1])+", C: "+Arrays.toString(coords[x][2]));
						break;
					}
					for(double lambda2 = 0;lambda2<=lambda2End;lambda2+=precision)
					{
						//Gefundener Punkt
						//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab[2]};
						//System.out.println("ForwardX1:"+forward[0]);
						lengthB = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + lambda1*ab0[0],lambda2*o[1] + coords[x][0][1] + lambda1*ab0[1],lambda2*o[2] + coords[x][0][2] + lambda1*ab0[2]},forward,camPos,alpha,beta,factor);
						//System.out.println("Bekommen X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						//System.out.println("X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
						if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
						{
							Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
							pointsAmount++;
							//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
						}
						if(lambda2+precision > lambda2End)
						{
							//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab[2]};
							lengthB = calcR3Point(new double[] {lambda2End*o[0] + coords[x][0][0] + lambda1*ab0[0],lambda2End*o[1] + coords[x][0][1] + lambda1*ab0[1],lambda2End*o[2] + coords[x][0][2] + lambda1*ab0[2]},forward,camPos,alpha,beta,factor);
							//System.out.println("Bekommen X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
							if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
							{
								Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
								pointsAmount++;
								//System.out.println(Arrays.toString(coordsINTCache)+", length: "+lengthB);
								//System.out.println("X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
							}
							break;
						}
					}
					if(lambda1+precision > abLength)
					{
						
						lambda2End = 
						(abLength*ab0[0]*bc[1]-abLength*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
								/
						(o[1]*bc[0]-o[0]*bc[1]);
						//System.out.println(lambda2End);
						if(Double.isInfinite(lambda2End))
						{
							//System.out.println("Lambda1: "+lambda1+", Lambda2: "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B: "+Arrays.toString(coords[x][1])+", C: "+Arrays.toString(coords[x][2]));
							break;
						}
						for(double lambda2 = 0;lambda2<=lambda2End;lambda2+=precision)
						{
							//double[] point = new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab[0],lambda2*o[1] + coords[x][0][1] + abLength*ab[1],lambda2*o[2] + coords[x][0][2] + abLength*ab[2]};
							lengthB = calcR3Point(new double[] {lambda2*o[0] + coords[x][0][0] + abLength*ab0[0],lambda2*o[1] + coords[x][0][1] + abLength*ab0[1],lambda2*o[2] + coords[x][0][2] + abLength*ab0[2]},forward,camPos,alpha,beta,factor);
							//System.out.println("Bekommen X:"+(int)R3Point[1]+" ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
							if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
							{
								Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
								pointsAmount++;
								//System.out.println(Arrays.toString(coordsINTCache)+", length: "+lengthB);
								//System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
							}
							if(lambda2+precision > lambda2End)
							{
								//double[] pointEnd = new double[] {lambda2End*o[0] + coords[x][0][0] + abLength*ab[0],lambda2End*o[1] + coords[x][0][1] + abLength*ab[1],lambda2End*o[2] + coords[x][0][2] + abLength*ab[2]};
								lengthB = calcR3Point(new double[] {lambda2End*o[0] + coords[x][0][0] + abLength*ab0[0],lambda2End*o[1] + coords[x][0][1] + abLength*ab0[1],lambda2End*o[2] + coords[x][0][2] + abLength*ab0[2]},forward,camPos,alpha,beta,factor);
								//System.out.println("Bekommen X:"+R3PointEnd[1]+" ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
								if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenX&&coordsINTCache[2]<screenY&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]] > lengthB||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
								{
									Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = lengthB;
									pointsAmount++;
									//System.out.println(Arrays.toString(coordsINTCache)+", length: "+lengthB);
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
		System.out.println("pointsAmount: "+pointsAmount);
		//System.out.println("stop");
			return Buffer2D;
		}
		public static double length(double[] vectorR3)
		{
			return Math.sqrt(vectorR3[0]*vectorR3[0]+vectorR3[1]*vectorR3[1]+vectorR3[2]*vectorR3[2]);
		}
}
