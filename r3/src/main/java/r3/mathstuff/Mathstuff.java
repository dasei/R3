package r3.mathstuff;

import java.util.ArrayList;
import java.util.Arrays;

import game.gameobjects.Floor;
import game.gameobjects.GameObject;
import game.physics.Hitbox;
import r3.main.Main;
import r3.multithreading.ThreadProcessor;

public class Mathstuff {

	public static final int[] ARRAY_INT_EMPTY_2 = new int[] { 0, 0 };
	public static final int[] ARRAY_INT_EMPTY_3 = new int[] { 0, 0, 0 };

	public Mathstuff(boolean initializeValues) {
		if (initializeValues)
			this.initValues();
	}

	private boolean valuesInitialized = false;

	/**
	 * initializes values that will <b>SUPPOSEDLY NOT CHANGE</b> doing runtime
	 */
	public void initValues() {
		// set flag
		valuesInitialized = true;

		fov = Main.getCamera().fov;
		fovFactor = 0.5 * (1 / Math.tan(Math.toRadians(fov / 2)));
	}

	/**
	 * updates values stored as attributes in this class for convenience
	 */
	public void updateValues() {
		if (!valuesInitialized)
			this.initValues();

		screenWidth = Main.getWindow().getDrawComp().getWidth();
		screenHeight = Main.getWindow().getDrawComp().getHeight();

		screenCenterX = screenWidth / 2;
		screenCenterY = screenHeight / 2;

		screenSizeMinimum = Math.min(screenWidth, screenHeight);
	}

	// Das übergebene Array: double[amount][3(Punkte von einem
	// Dreieck)][3(Komponenten je Punkt)]
	private double fov;
	private double fovFactor;

	private int screenWidth, screenHeight;
	private int screenCenterX, screenCenterY;
	private int screenSizeMinimum;
	
//	//TODO folgende Methode iszt auskommentiert, da sie die Sachen nur in das int[][][] 2d in Main geschrieben hat und dieses Attribut beseitigt werden soll :3
//	public void calcR3(double[][][] coords, double[] forward, double[] camPos, double alpha, double beta,
//			double factor) { // f:forward vector; a:position of camera,
//								// alpha:rotation x2, beta:rotation x3
//		this.updateValues();
//
//		// double fov = Main.getCamera().fov;
//		// double fovFactor = 0.5 * (1/Math.tan(Math.toRadians(fov/2)));
//		int screenWidth = Main.getWindow().getDrawComp().getWidth();
//		int screenHeight = Main.getWindow().getDrawComp().getHeight();
//
//		int screenCenterX = screenWidth / 2;
//		int screenCenterY = screenHeight / 2;
//
//		// System.out.println(Arrays.toString(forward));
//		int screenSizeMinimum = Math.min(screenWidth, screenHeight);
//		double[] z = new double[] { forward[0] + camPos[0], forward[1] + camPos[1], forward[2] + camPos[2] }; // z:"angriffspunkt
//																												// ebene"
//		for (int x = 0; x < coords.length; x++) {
//			for (int y = 0; y < 3; y++) {
//				double[] b = new double[] { coords[x][y][0] - camPos[0], coords[x][y][1] - camPos[1],
//						coords[x][y][2] - camPos[2] }; // b:vector camera to
//														// point
//				double bLength = length(b);
//				b = new double[] { b[0] / bLength, b[1] / bLength, b[2] / bLength }; // b0
//
//				double lambda = -(forward[0] * (camPos[0] - z[0]) + forward[1] * (camPos[1] - z[1])
//						+ forward[2] * (camPos[2] - z[2]))
//						/ (forward[0] * b[0] + forward[1] * b[1] + forward[2] * b[2]);
//
//				if (lambda < 0) {
//					for (int j = 0; j < 3; j++) {
//						Main.coordsDraw[x][j][0] = 0;
//						Main.coordsDraw[x][j][1] = 0;
//					}
//					break;
//				}
//				double[] vecCamPosS = new double[] { lambda * b[0], lambda * b[1], lambda * b[2] }; // jetzt:vektor
//																									// kamera->schnittpunkt
//				double[] vecCamPosSX3 = new double[] {
//						(Math.cos(-beta) * vecCamPosS[0] - Math.sin(-beta) * vecCamPosS[1]),
//						Math.sin(-beta) * vecCamPosS[0] + Math.cos(-beta) * vecCamPosS[1], (vecCamPosS[2]) };
//				Main.coordsDraw[x][y] = new int[] {
//						screenCenterX + (int) ((vecCamPosSX3[1] * factor) * screenSizeMinimum * fovFactor),
//						screenCenterY
//								- (int) (((-Math.sin(-alpha) * vecCamPosSX3[0] + Math.cos(-alpha) * vecCamPosSX3[2])
//										* factor) * screenSizeMinimum * fovFactor) };
//				// System.out.println("b;X1: "+b[0]+", bX2: "+b[1]+", bX3:
//				// "+b[2]+", lambda: "+lambda);
//				// zurückdrehen, jetzt kann x1 ignoriert werden
//
//			}
//		}
//		// return coords;
//	}

	private double[] calcR3DepthVectorCache;

	public double calcR3Depth(double[] point, double[] cameraPos) {
		calcR3DepthVectorCache = new double[] { point[0] - cameraPos[0], point[1] - cameraPos[1],
				point[2] - cameraPos[2] };
		return Math.sqrt((calcR3DepthVectorCache[0] * calcR3DepthVectorCache[0])
				+ (calcR3DepthVectorCache[1] * calcR3DepthVectorCache[1])
				+ (calcR3DepthVectorCache[2] * calcR3DepthVectorCache[2]));
	}

	private double[] cacheAnkerEbene;
	private double[] cacheVectorCamToPoint0;
	private double cacheVectorCamToPointLength;
	private double lambdaCamToPointEbenenSchnittpunkt;
	private double[] vecCamToEbenenSchnittpunkt = new double[3];
	private double[] vecCamToEbenenSchnittpunktX3 = new double[3];
	/**
	 * this is a cache of length two, which holds the most recent calculated
	 * point coordinates(onscreen pixels) calculated in calcR3Point
	 */
	// private int[] coordsINTCache;
	/**
	 * returns depth of point if rotated back to the camera's original forward
	 * direction f:forward vector; a:position of camera, alpha:rotation x2,
	 * beta:rotation x3, factor: gibt LE -> Pixel Verhältnis an, length gibt die
	 * Länge von b an(und speichert es rein) * <br>
	 * <br>
	 * needs to be private, because its values need to be updated in every
	 * frame. This should be done in other methods from this class invoking this
	 * method
	 */

	// private double calcR3Point(double[] coords, double[] forward, double[]
	// camPos,double alpha, double beta, double factor){
	//
	// //System.out.println("Gegeben:
	// X1"+coords[0]+",X2"+coords[1]+",X3"+coords[2]+",forwardX1"+forward[0]+",camPosX1"+camPos[0]);
	// cacheAnkerEbene = new double[] {forward[0] + camPos[0],forward[1] +
	// camPos[1],forward[2] + camPos[2]}; //z:"angriffspunkt ebene"
	//
	// cacheVectorCamToPoint0 = new double[]
	// {coords[0]-camPos[0],coords[1]-camPos[1],coords[2]-camPos[2]}; //b:vector
	// camera to point
	// cacheVectorCamToPointLength =
	// Mathstuff.vectorUnify(cacheVectorCamToPoint0);
	//
	// lambdaCamToPointEbenenSchnittpunkt = -
	// (forward[0]*(camPos[0]-cacheAnkerEbene[0])+forward[1]*(camPos[1]-cacheAnkerEbene[1])+forward[2]*(camPos[2]-cacheAnkerEbene[2]))
	// /
	// (forward[0]*cacheVectorCamToPoint0[0]+forward[1]*cacheVectorCamToPoint0[1]+forward[2]*cacheVectorCamToPoint0[2]);
	// //System.out.println("Lambda: "+lambda);
	// if(lambdaCamToPointEbenenSchnittpunkt < 0) {
	// coordsINTCache = ARRAY_INT_EMPTY_3;
	// return 0;
	// } else {
	// //get vector from camera to Schnittpunkt auf Ebene
	// vecCamToEbenenSchnittpunkt = new double[]
	// {lambdaCamToPointEbenenSchnittpunkt *
	// cacheVectorCamToPoint0[0],lambdaCamToPointEbenenSchnittpunkt *
	// cacheVectorCamToPoint0[1],lambdaCamToPointEbenenSchnittpunkt *
	// cacheVectorCamToPoint0[2]}; //jetzt:vektor kamera->schnittpunkt
	// //rotate that back around x3 axis
	// vecCamToEbenenSchnittpunktX3 = new double[]
	// {(Math.cos(-beta)*vecCamToEbenenSchnittpunkt[0] -
	// Math.sin(-beta)*vecCamToEbenenSchnittpunkt[1]),
	// Math.sin(-beta)*vecCamToEbenenSchnittpunkt[0] +
	// Math.cos(-beta)*vecCamToEbenenSchnittpunkt[1],
	// (vecCamToEbenenSchnittpunkt[2])};
	// //rotate that back around x2 axis
	// coordsINTCache = new int[] {0, screenCenterX +
	// (int)((vecCamToEbenenSchnittpunktX3[1]*factor)*screenSizeMinimum*fovFactor),
	// screenCenterY-(int)(((-Math.sin(-alpha)*vecCamToEbenenSchnittpunktX3[0] +
	// Math.cos(-alpha)*vecCamToEbenenSchnittpunktX3[2])*factor)*screenSizeMinimum*fovFactor)};
	//
	// //return depth / distance of point from camera
	// return cacheVectorCamToPointLength;
	// }
	// }
	/**
	 * returns depth of point if rotated back to the camera's original forward
	 * direction f:forward vector; a:position of camera, alpha:rotation x2,
	 * beta:rotation x3, factor: gibt LE -> Pixel Verhältnis an, length gibt die
	 * Länge von b an(und speichert es rein) * <br>
	 * <br>
	 * needs to be private, because its values need to be updated in every
	 * frame. This should be done in other methods from this class invoking this
	 * method
	 */
	private double calcR3Point(double[] coords, int[] coordsIntCache, double[] forward, double[] camPos, double alpha,
			double beta, double factor) {

		// System.out.println("Gegeben:
		// X1"+coords[0]+",X2"+coords[1]+",X3"+coords[2]+",forwardX1"+forward[0]+",camPosX1"+camPos[0]);
		cacheAnkerEbene = new double[] { forward[0] + camPos[0], forward[1] + camPos[1], forward[2] + camPos[2] }; // z:"angriffspunkt
																													// ebene"

		cacheVectorCamToPoint0 = new double[] { coords[0] - camPos[0], coords[1] - camPos[1], coords[2] - camPos[2] }; // b:vector
																														// camera
																														// to
																														// point
		cacheVectorCamToPointLength = Mathstuff.vectorUnify(cacheVectorCamToPoint0);

		lambdaCamToPointEbenenSchnittpunkt = -(forward[0] * (camPos[0] - cacheAnkerEbene[0])
				+ forward[1] * (camPos[1] - cacheAnkerEbene[1]) + forward[2] * (camPos[2] - cacheAnkerEbene[2]))
				/ (forward[0] * cacheVectorCamToPoint0[0] + forward[1] * cacheVectorCamToPoint0[1]
						+ forward[2] * cacheVectorCamToPoint0[2]);
		// System.out.println("Lambda: "+lambda);
		if (lambdaCamToPointEbenenSchnittpunkt < 0) {
			coordsIntCache[0] = -1;
			coordsIntCache[1] = -1;
			// coordsINTCache = ARRAY_INT_EMPTY_3;
			return 0;
		} else {
			// get vector from camera to Schnittpunkt auf Ebene
			// vecCamToEbenenSchnittpunkt = new double[]
			// {lambdaCamToPointEbenenSchnittpunkt *
			// cacheVectorCamToPoint0[0],lambdaCamToPointEbenenSchnittpunkt *
			// cacheVectorCamToPoint0[1],lambdaCamToPointEbenenSchnittpunkt *
			// cacheVectorCamToPoint0[2]}; //jetzt:vektor kamera->schnittpunkt
			vecCamToEbenenSchnittpunkt[0] = lambdaCamToPointEbenenSchnittpunkt * cacheVectorCamToPoint0[0];
			vecCamToEbenenSchnittpunkt[1] = lambdaCamToPointEbenenSchnittpunkt * cacheVectorCamToPoint0[1];
			vecCamToEbenenSchnittpunkt[2] = lambdaCamToPointEbenenSchnittpunkt * cacheVectorCamToPoint0[2];

			// rotate that back around x3 axis
			vecCamToEbenenSchnittpunktX3[0] = (Math.cos(-beta) * vecCamToEbenenSchnittpunkt[0]
					- Math.sin(-beta) * vecCamToEbenenSchnittpunkt[1]);
			vecCamToEbenenSchnittpunktX3[1] = Math.sin(-beta) * vecCamToEbenenSchnittpunkt[0]
					+ Math.cos(-beta) * vecCamToEbenenSchnittpunkt[1];
			vecCamToEbenenSchnittpunktX3[2] = vecCamToEbenenSchnittpunkt[2];

			// vecCamToEbenenSchnittpunktX3 = new double[]
			// {(Math.cos(-beta)*vecCamToEbenenSchnittpunkt[0] -
			// Math.sin(-beta)*vecCamToEbenenSchnittpunkt[1]),
			// Math.sin(-beta)*vecCamToEbenenSchnittpunkt[0] +
			// Math.cos(-beta)*vecCamToEbenenSchnittpunkt[1],
			// (vecCamToEbenenSchnittpunkt[2])};
			// rotate that back around x2 axis
			coordsIntCache[0] = screenCenterX
					+ (int) (vecCamToEbenenSchnittpunktX3[1] * factor * screenSizeMinimum * fovFactor);
			coordsIntCache[1] = screenCenterY - (int) (((-Math.sin(-alpha) * vecCamToEbenenSchnittpunktX3[0]
					+ Math.cos(-alpha) * vecCamToEbenenSchnittpunktX3[2]) * factor) * screenSizeMinimum * fovFactor);
					// coordsINTCache = new int[] {0, screenCenterX +
					// (int)((vecCamToEbenenSchnittpunktX3[1]*factor)*screenSizeMinimum*fovFactor),
					// screenCenterY-(int)(((-Math.sin(-alpha)*vecCamToEbenenSchnittpunktX3[0]
					// +
					// Math.cos(-alpha)*vecCamToEbenenSchnittpunktX3[2])*factor)*screenSizeMinimum*fovFactor)};

			// return depth / distance of point from camera
			return cacheVectorCamToPointLength;
		}
	}

	public double[][][] calcR3ZBuff(double[][][] coords, Camera camera, int triangleOffset, int triangleAmount,
			boolean createNewBuffer) {
		this.updateValues();

		// Extract constants from camera
		double[] forward = camera.forward;
		double alpha = camera.alpha;
		double beta = camera.beta;
		double factor = camera.scaleFactor;
		double[] camPos = camera.pos;

		// System.out.println(Arrays.toString(forward));

		double[][][] bufferDepth;
		if (createNewBuffer)
			bufferDepth = new double[screenWidth][screenHeight][2];
		else
			bufferDepth = ThreadProcessor.getBufferToCalculateOn();

		double[] ab0;
		double[] ac;
		double[] bc;
		double lambdaAB;
		double[] o;

		double[] middle;
		// System.out.println(coords.length);
		boolean collidingWithAC = true;
		// Lambda1:Stelle auf der Gerade AB*lambda1 + A
		double lambda2Max = 0;
		// double lambda3 = 0;
		double depth = 0;
		// double bcLength = 0;
		double lengthMiddle = 0;
		double oLength = 0;

		int[] coordsIntCache = new int[2];

		double precision = 0.1;
		// System.out.println("start");
		double cacheLambda2Divisor;
		// System.out.println("c " + coords.length);
		for (int triangleI = 0; triangleI < coords.length; triangleI++) {

			// check if no color is given
			if (coords[triangleI][3][0] == -1) {
//				throw new RuntimeException("Diiga");
				calcR3Mesh(bufferDepth, coords[triangleI], forward, camPos, alpha, beta, factor);
				continue;
			}

			// System.out.println("t " + triangleI);
			// a = [0], b = [1], c = [2]
			// Vektor AB
			ab0 = new double[] { coords[triangleI][1][0] - coords[triangleI][0][0],
					coords[triangleI][1][1] - coords[triangleI][0][1],
					coords[triangleI][1][2] - coords[triangleI][0][2] };
			double abLength = length(ab0);
			double[] ab = ab0;
			ab0 = new double[] { ab0[0] / abLength, ab0[1] / abLength, ab0[2] / abLength };
			// Vektor AC
			ac = new double[] { coords[triangleI][2][0] - coords[triangleI][0][0],
					coords[triangleI][2][1] - coords[triangleI][0][1],
					coords[triangleI][2][2] - coords[triangleI][0][2] };
			double acLength = length(ac);
			ac = new double[] { ac[0] / acLength, ac[1] / acLength, ac[2] / acLength };

			// Vektor BC
			bc = new double[] { coords[triangleI][2][0] - coords[triangleI][1][0],
					coords[triangleI][2][1] - coords[triangleI][1][1],
					coords[triangleI][2][2] - coords[triangleI][1][2] };
			// bcLength = length(bc);
			middle = new double[] { (coords[triangleI][0][0] + coords[triangleI][1][0] + coords[triangleI][2][0]) / 3,
					(coords[triangleI][0][1] + coords[triangleI][1][1] + coords[triangleI][2][1]) / 3,
					(coords[triangleI][0][2] + coords[triangleI][1][2] + coords[triangleI][2][2]) / 3 };

			lengthMiddle = calcR3Point(middle, coordsIntCache, forward, camPos, alpha, beta, factor); // dont
																										// calculate
																										// entire
																										// point
																										// with
																										// everything,
																										// but
																										// only
																										// its
																										// depth
			// lengthMiddle = calcR3Depth(middle, camPos); //TODO validate
			// TODO change please
			if (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
					|| coordsIntCache[1] > screenHeight)
				continue;

			if (lengthMiddle == 0)
				continue;

			// precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
			// precision = 0.0058*lengthMiddle+0.001;
			precision = 0.00138 * lengthMiddle + 0.001;
			// precision = 1;

			// lambdaAB gibt die Stelle auf der Geraden AB*lambda + A an, bei
			// der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
			lambdaAB = (ab0[0] * (coords[triangleI][2][0] - coords[triangleI][0][0])
					+ ab0[1] * (coords[triangleI][2][1] - coords[triangleI][0][1])
					+ ab0[2] * (coords[triangleI][2][2] - coords[triangleI][0][2]))
					/ (ab0[0] * ab0[0] + ab0[1] * ab0[1] + ab0[2] * ab0[2]);
			// check boundaries of lambdaAB
			if (lambdaAB > abLength || lambdaAB < 0) {
				// System.err.println("This model contains a invalid triangle
				// (lambdaAB > abLength). Its getting skipped ");
				// System.out.println("---");
				// System.out.println(Arrays.toString(coords[triangleI][0]));
				// System.out.println(Arrays.toString(coords[triangleI][1]));
				// System.out.println(Arrays.toString(coords[triangleI][2]));
				// System.out.println("---");
				continue;
			}

			// Vektor O (Einheitsvektor) (steht senkrecht auf AB und geht durch
			// (bzw. bis) C)
			o = new double[] { coords[triangleI][2][0] - (coords[triangleI][0][0] + ab0[0] * lambdaAB),
					coords[triangleI][2][1] - (coords[triangleI][0][1] + ab0[1] * lambdaAB),
					coords[triangleI][2][2] - (coords[triangleI][0][2] + ab0[2] * lambdaAB) };
			oLength = vectorUnify(o);

			collidingWithAC = true;
			for (double lambdaABCrawler = 0; lambdaABCrawler < abLength + precision; lambdaABCrawler += precision) {

				// check if lambdaVertical should be calculated by collision
				// with BC
				if (collidingWithAC && lambdaABCrawler > lambdaAB)
					collidingWithAC = false;

				//////////// 1. CALCULATE lambda2Max

				// distinguish between the two different calculation methods for
				// lambda2Max, depending on if its a collision with AC or CB
				if (collidingWithAC) {

					// FORMULA:
					// lambdaVertical =
					// (lambdaABCrawler*ab0[1]*ac[0]-lambdaABCrawler*ab0[0]*ac[1])
					// /
					// (o[0]*ac[1]-o[1]*ac[0]);

					// TODO NOTE: this should be cleaned up, as triangles are
					// now reprocessed in the main loop
					// 1.: calculate divisor and check if it is 0. If so,
					// calculate it with two other points. If it is again 0, set
					// lambdaVertical to 0
					cacheLambda2Divisor = (o[0] * ac[1] - o[1] * ac[0]);
					if (cacheLambda2Divisor == 0) {
						cacheLambda2Divisor = (o[0] * ac[2] - o[2] * ac[0]);
						if (cacheLambda2Divisor == 0) {
							cacheLambda2Divisor = (o[1] * ac[2] - o[2] * ac[1]);
							if (cacheLambda2Divisor == 0) {
								// System.err.println("WARNING: This model
								// contains an invalid triangle (at calculating
								// R3zBUFF) " + triangleI + ", " +
								// lambdaABCrawler);
								// System.out.println("---");
								// System.out.println(Arrays.toString(coords[triangleI][0]));
								// System.out.println(Arrays.toString(coords[triangleI][1]));
								// System.out.println(Arrays.toString(coords[triangleI][2]));
								// System.out.println("---");
								break; // this tringle is weird (e.g. just a
										// line) and can't be rendered
							} else {
								lambda2Max = (lambdaABCrawler * ab0[2] * ac[1] - lambdaABCrawler * ab0[1] * ac[2])
										/ cacheLambda2Divisor;
							}
						} else {
							lambda2Max = (lambdaABCrawler * ab0[2] * ac[0] - lambdaABCrawler * ab0[0] * ac[2])
									/ cacheLambda2Divisor;
						}
					} else {
						lambda2Max = (lambdaABCrawler * ab0[1] * ac[0] - lambdaABCrawler * ab0[0] * ac[1])
								/ cacheLambda2Divisor;
					}

				} else {

					cacheLambda2Divisor = (o[1] * bc[0] - o[0] * bc[1]);
					if (cacheLambda2Divisor == 0) {
						cacheLambda2Divisor = (o[2] * bc[0] - o[0] * bc[2]);
						// System.err.println(cacheLambda2Divisor);
						if (cacheLambda2Divisor == 0) {
							// System.err.println("0");
							// cacheLambda2Divisor = (o[1]*bc[2]-o[2]*bc[1]);
							// if(cacheLambda2Divisor == 0) {
							// System.out.println("WARNING: This model contains
							// an invalid triangle (at calculating R3zBUFF) " +
							// triangleI + ", " + lambdaABCrawler);
							// System.out.println("---");
							// System.out.println(Arrays.toString(coords[triangleI][0]));
							// System.out.println(Arrays.toString(coords[triangleI][1]));
							// System.out.println(Arrays.toString(coords[triangleI][2]));
							// System.out.println("---");
							break;
							// break; //this tringle is weird (e.g. just a line)
							// and can't be rendered
							// }else {
							// lambda2Max =
							// (lambdaABCrawler*ab0[2]*bc[1]-lambdaABCrawler*ab0[1]*bc[2]-ab[2]*bc[1]+ab[1]*bc[2])
							// / cacheLambda2Divisor;
							// }
						} else {
							lambda2Max = (lambdaABCrawler * ab0[0] * bc[2] - lambdaABCrawler * ab0[2] * bc[0]
									- ab[0] * bc[2] + ab[2] * bc[0]) / cacheLambda2Divisor;
						}
					} else {
						lambda2Max = (lambdaABCrawler * ab0[0] * bc[1] - lambdaABCrawler * ab0[1] * bc[0]
								- ab[0] * bc[1] + ab[1] * bc[0]) / cacheLambda2Divisor;
					}

					// System.out.println("\t\t\t\t\t\t\t " + lambda2Max);

					// System.out.println(lambda2End);
					if (!Double.isFinite(lambda2Max) || lambda2Max > oLength) {

						// System.out.println("---");
						// System.err.println("AY " + lambda2Max);
						// System.out.println(Arrays.toString(coords[triangleI][0]));
						// System.out.println(Arrays.toString(coords[triangleI][1]));
						// System.out.println(Arrays.toString(coords[triangleI][2]));
						// System.out.println("---");

						// System.out.println("INFINIIIIIIITE");
						// System.out.println("Lambda1: "+lambda1+", Lambda2:
						// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+",
						// B: "+Arrays.toString(coords[x][1])+", C:
						// "+Arrays.toString(coords[x][2]));
						// System.err.println("WHELP " + lambda2Max);
						break;
					}

				}

				//////////// 2. CRAWL THROUGH lambda2
				for (double lambda2 = 0; lambda2 < lambda2Max + precision; lambda2 += precision) {
					// calculate the point (=> 2D into coordsIntCache) and
					// return its depth
					depth = calcR3Point(
							new double[] { lambda2 * o[0] + coords[triangleI][0][0] + lambdaABCrawler * ab0[0],
									lambda2 * o[1] + coords[triangleI][0][1] + lambdaABCrawler * ab0[1],
									lambda2 * o[2] + coords[triangleI][0][2] + lambdaABCrawler * ab0[2] },
							coordsIntCache, forward, camPos, alpha, beta, factor);

					// check if the 2d point is in the screens boundaries and if
					// its depth is smaller that the one noted in the buffer
					if (coordsIntCache[0] > 0 && coordsIntCache[1] > 0 && coordsIntCache[0] < screenWidth
							&& coordsIntCache[1] < screenHeight
							&& (bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] > depth
									|| bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] == 0)) {
						bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] = depth;

						// set color
						bufferDepth[coordsIntCache[0]][coordsIntCache[1]][1] = (int) coords[triangleI][3][0];
					}
				}

				// System.out.println("ja");

				// //do the same one last time
				// //double[] pointEnd = new double[] {lambda2End*o[0] +
				// coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] +
				// coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] +
				// coords[x][0][2] + lambda1*ab[2]};
				// depth = calcR3Point(new double[] {lambda2Max*o[0] +
				// coords[triangleI][0][0] +
				// lambdaABCrawler*ab0[0],lambda2Max*o[1] +
				// coords[triangleI][0][1] +
				// lambdaABCrawler*ab0[1],lambda2Max*o[2] +
				// coords[triangleI][0][2] +
				// lambdaABCrawler*ab0[2]},forward,camPos,alpha,beta,factor);
				// //System.out.println("Bekommen X:"+(int)R3PointEnd[1]+"
				// ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" -
				// Final");
				// if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				//
				// }
				// break;
				// }

				// TODO is the following if complex still needed? => bc the
				// coordinates are already manipulated to prevent
				// (lambdaABCrawler+precision > abLength) from happening
				// this ensures the last pixel (at lambaAB, lambda2 = 0) is
				// drawn. not needed bc. for loop makes sure of that
				// if(lambdaABCrawler+precision > abLength)
				// {
				//
				//// System.out.println("YES THERE IS");
				//
				// lambda2Max =
				// (lambdaABCrawler*ab0[1]*ac[0]-lambdaABCrawler*ab0[0]*ac[1])
				// /
				// (o[0]*ac[1]-o[1]*ac[0]);
				// //System.out.println(lambda2End);
				// if(Double.isInfinite(lambda2Max))
				// {
				// //System.out.println("Lambda1: "+lambda1+", Lambda2:
				// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B:
				// "+Arrays.toString(coords[x][1])+", C:
				// "+Arrays.toString(coords[x][2]));
				// break;
				// }
				// for(double lambda2 =
				// 0;lambda2<=lambda2Max;lambda2+=precision)
				// {
				// depth = calcR3Point(new double[] {lambda2*o[0] +
				// coords[triangleI][0][0] + abLength*ab0[0],lambda2*o[1] +
				// coords[triangleI][0][1] + abLength*ab0[1],lambda2*o[2] +
				// coords[triangleI][0][2] +
				// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
				// if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				// //System.out.println(Arrays.toString(coordsINTCache)+",
				// length: "+lengthB);
				// //System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+"
				// ;Deep:"+R3Point[3]);
				// }
				// if(lambda2+precision > lambda2Max&lambda2Max!=lambda2)
				// {
				// //double[] pointEnd = new double[] {lambda2End*o[0] +
				// coords[x][0][0] + abLength*ab[0],lambda2End*o[1] +
				// coords[x][0][1] + abLength*ab[1],lambda2End*o[2] +
				// coords[x][0][2] + abLength*ab[2]};
				// depth = calcR3Point(new double[] {lambda2Max*o[0] +
				// coords[triangleI][0][0] + abLength*ab0[0],lambda2Max*o[1] +
				// coords[triangleI][0][1] + abLength*ab0[1],lambda2Max*o[2] +
				// coords[triangleI][0][2] +
				// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
				// //System.out.println("Bekommen X:"+R3PointEnd[1]+"
				// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
				// if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				// //System.out.println(Arrays.toString(coordsINTCache)+",
				// length: "+lengthB);
				// //System.out.println("X:"+(int)R3PointEnd[1]+"
				// ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" -
				// Final");
				// }
				// break;
				// }
				// }
				// break;
				// }
				// } else {
				// System.out.println("WE ARE NOW ON THE RIGHT SIDE");
				// lambda2Max =
				// (lambdaABCrawler*ab0[0]*bc[1]-lambdaABCrawler*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
				// /
				// (o[1]*bc[0]-o[0]*bc[1]);

				// if(lambda2Max > 100)
				// break;

				// cacheLambda2Divisor = (o[1]*bc[0]-o[0]*bc[1]);
				// if(cacheLambda2Divisor == 0) {
				// cacheLambda2Divisor = (o[2]*bc[0]-o[0]*bc[2]);
				//// System.err.println(cacheLambda2Divisor);
				// if(cacheLambda2Divisor == 0) {
				//// System.err.println("0");
				//// cacheLambda2Divisor = (o[1]*bc[2]-o[2]*bc[1]);
				//// if(cacheLambda2Divisor == 0) {
				//// System.out.println("WARNING: This model contains an invalid
				// triangle (at calculating R3zBUFF) " + triangleI + ", " +
				// lambdaABCrawler);
				//// System.out.println("---");
				//// System.out.println(Arrays.toString(coords[triangleI][0]));
				//// System.out.println(Arrays.toString(coords[triangleI][1]));
				//// System.out.println(Arrays.toString(coords[triangleI][2]));
				//// System.out.println("---");
				// break;
				//// break; //this tringle is weird (e.g. just a line) and can't
				// be rendered
				//// }else {
				//// lambda2Max =
				// (lambdaABCrawler*ab0[2]*bc[1]-lambdaABCrawler*ab0[1]*bc[2]-ab[2]*bc[1]+ab[1]*bc[2])
				// / cacheLambda2Divisor;
				//// }
				// }else {
				// lambda2Max =
				// (lambdaABCrawler*ab0[0]*bc[2]-lambdaABCrawler*ab0[2]*bc[0]-ab[0]*bc[2]+ab[2]*bc[0])
				// / cacheLambda2Divisor;
				// }
				// } else {
				// lambda2Max =
				// (lambdaABCrawler*ab0[0]*bc[1]-lambdaABCrawler*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
				// / cacheLambda2Divisor;
				// }
				//
				//// System.out.println("\t\t\t\t\t\t\t " + lambda2Max);
				//
				// //System.out.println(lambda2End);
				// if(!Double.isFinite(lambda2Max) || lambda2Max > oLength) {
				//
				//
				//// System.out.println("---");
				//// System.err.println("AY " + lambda2Max);
				//// System.out.println(Arrays.toString(coords[triangleI][0]));
				//// System.out.println(Arrays.toString(coords[triangleI][1]));
				//// System.out.println(Arrays.toString(coords[triangleI][2]));
				//// System.out.println("---");
				//
				//// System.out.println("INFINIIIIIIITE");
				// //System.out.println("Lambda1: "+lambda1+", Lambda2:
				// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B:
				// "+Arrays.toString(coords[x][1])+", C:
				// "+Arrays.toString(coords[x][2]));
				//// System.err.println("WHELP " + lambda2Max);
				// break;
				// }

				// for(double lambda2 = 0; lambda2 < lambda2Max + precision;
				// lambda2 += precision) {
				//// System.out.println("hmh " + lambda2 + ", lambdaMax: " +
				// lambda2Max);
				// //Gefundener Punkt
				// //double[] point = new double[] {lambda2*o[0] +
				// coords[x][0][0] + lambda1*ab[0],lambda2*o[1] +
				// coords[x][0][1] + lambda1*ab[1],lambda2*o[2] +
				// coords[x][0][2] + lambda1*ab[2]};
				// //System.out.println("ForwardX1:"+forward[0]);
				// depth = calcR3Point(new double[] {lambda2*o[0] +
				// coords[triangleI][0][0] + lambdaABCrawler*ab0[0],lambda2*o[1]
				// + coords[triangleI][0][1] +
				// lambdaABCrawler*ab0[1],lambda2*o[2] + coords[triangleI][0][2]
				// + lambdaABCrawler*ab0[2]},forward,camPos,alpha,beta,factor);
				// //System.out.println("Bekommen X:"+R3Point[1]+"
				// ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
				// //System.out.println("Bekommen X:"+(int)R3Point[1]+"
				// ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
				// //System.out.println("X:"+(int)R3Point[1]+"
				// ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
				// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				// //System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+"
				// ;Deep:"+R3Point[3]);
				// }
				// if(lambda2+precision > lambda2Max)
				// {
				// //double[] pointEnd = new double[] {lambda2End*o[0] +
				// coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] +
				// coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] +
				// coords[x][0][2] + lambda1*ab[2]};
				// depth = calcR3Point(new double[] {lambda2Max*o[0] +
				// coords[triangleI][0][0] +
				// lambdaABCrawler*ab0[0],lambda2Max*o[1] +
				// coords[triangleI][0][1] +
				// lambdaABCrawler*ab0[1],lambda2Max*o[2] +
				// coords[triangleI][0][2] +
				// lambdaABCrawler*ab0[2]},forward,camPos,alpha,beta,factor);
				// //System.out.println("Bekommen X:"+R3PointEnd[1]+"
				// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
				// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				// //System.out.println(Arrays.toString(coordsINTCache)+",
				// length: "+lengthB);
				// //System.out.println("X:"+R3PointEnd[1]+"
				// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
				// }
				// break;
				// }
				// }

				// TODO is the following if complex still needed? => bc the
				// coordinates are already manipulated to prevent
				// (lambdaABCrawler+precision > abLength) from happening
				// if(lambdaABCrawler+precision > abLength)
				// {
				//
				// lambda2Max =
				// (abLength*ab0[0]*bc[1]-abLength*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
				// /
				// (o[1]*bc[0]-o[0]*bc[1]);
				// //System.out.println(lambda2End);
				// if(Double.isInfinite(lambda2Max))
				// {
				// //System.out.println("Lambda1: "+lambda1+", Lambda2:
				// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B:
				// "+Arrays.toString(coords[x][1])+", C:
				// "+Arrays.toString(coords[x][2]));
				// break;
				// }
				// for(double lambda2 =
				// 0;lambda2<=lambda2Max;lambda2+=precision)
				// {
				// //double[] point = new double[] {lambda2*o[0] +
				// coords[x][0][0] + abLength*ab[0],lambda2*o[1] +
				// coords[x][0][1] + abLength*ab[1],lambda2*o[2] +
				// coords[x][0][2] + abLength*ab[2]};
				// depth = calcR3Point(new double[] {lambda2*o[0] +
				// coords[triangleI][0][0] + abLength*ab0[0],lambda2*o[1] +
				// coords[triangleI][0][1] + abLength*ab0[1],lambda2*o[2] +
				// coords[triangleI][0][2] +
				// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
				// //System.out.println("Bekommen X:"+(int)R3Point[1]+"
				// ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
				// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				// //System.out.println(Arrays.toString(coordsINTCache)+",
				// length: "+lengthB);
				// //System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+"
				// ;Deep:"+R3Point[3]);
				// }
				// if(lambda2+precision > lambda2Max)
				// {
				// //double[] pointEnd = new double[] {lambda2End*o[0] +
				// coords[x][0][0] + abLength*ab[0],lambda2End*o[1] +
				// coords[x][0][1] + abLength*ab[1],lambda2End*o[2] +
				// coords[x][0][2] + abLength*ab[2]};
				// depth = calcR3Point(new double[] {lambda2Max*o[0] +
				// coords[triangleI][0][0] + abLength*ab0[0],lambda2Max*o[1] +
				// coords[triangleI][0][1] + abLength*ab0[1],lambda2Max*o[2] +
				// coords[triangleI][0][2] +
				// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
				// //System.out.println("Bekommen X:"+R3PointEnd[1]+"
				// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
				// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
				// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
				// {
				// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
				// //System.out.println(Arrays.toString(coordsINTCache)+",
				// length: "+lengthB);
				// //System.out.println("X:"+(int)R3PointEnd[1]+"
				// ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" -
				// Final");
				// }
				// break;
				// }
				// }
				// break;
				// }

			}

		}
		// System.out.println("stop");
		return bufferDepth;
	}
	
	public double[][][] calcR3ZBuff(ArrayList<GameObject> gameObjects, Camera camera, boolean createNewBuffer) {
		this.updateValues();

		// Extract constants from camera
		double[] forward = camera.forward;
		double alpha = camera.alpha;
		double beta = camera.beta;
		double factor = camera.scaleFactor;
		double[] camPos = camera.pos;
		
		// System.out.println(Arrays.toString(forward));

		double[][][] bufferDepth;
		if (createNewBuffer)
			bufferDepth = new double[screenWidth][screenHeight][2];
		else
			bufferDepth = ThreadProcessor.getBufferToCalculateOn();

		double[] ab0;
		double[] ac;
		double[] bc;
		double lambdaAB;
		double[] o;

		double[] middle;
		// System.out.println(coords.length);
		boolean collidingWithAC = true;
		// Lambda1:Stelle auf der Gerade AB*lambda1 + A
		double lambda2Max = 0;
		// double lambda3 = 0;
		double depth = 0;
		// double bcLength = 0;
		double lengthMiddle = 0;
		double oLength = 0;

		int[] coordsIntCache = new int[2];

		double precision = 0.1;
		// System.out.println("start");
		double cacheLambda2Divisor;
		// System.out.println("c " + coords.length);
		
		GameObject gameObject;
		double[][][] coords;
		for(int gameObjectI = gameObjects.size()-1; gameObjectI >= 0; gameObjectI--) {
			gameObject = gameObjects.get(gameObjectI);
			if(gameObject == null)
				continue;
			coords = gameObject.getTrianglesAbsolute();
			
//			if(!(gameObject instanceof Floor)) {
//				System.out.println("------");
//				System.out.println(Arrays.toString(coords[0][0]));
//				System.out.println("------");
//			}
			
			
			for (int triangleI = 0; triangleI < coords.length; triangleI++) {
	
				// check if no color is given
				if (coords[triangleI].length < 4 || coords[triangleI][3][0] == -1) {
	//				throw new RuntimeException("Diiga");
					calcR3Mesh(bufferDepth, coords[triangleI], forward, camPos, alpha, beta, factor);
					continue;
				}
				// System.out.println("t " + triangleI);
				// a = [0], b = [1], c = [2]
				// Vektor AB
				ab0 = new double[] { coords[triangleI][1][0] - coords[triangleI][0][0],
						coords[triangleI][1][1] - coords[triangleI][0][1],
						coords[triangleI][1][2] - coords[triangleI][0][2] };
				double abLength = length(ab0);
				double[] ab = ab0;
				ab0 = new double[] { ab0[0] / abLength, ab0[1] / abLength, ab0[2] / abLength };
				// Vektor AC
				ac = new double[] { coords[triangleI][2][0] - coords[triangleI][0][0],
						coords[triangleI][2][1] - coords[triangleI][0][1],
						coords[triangleI][2][2] - coords[triangleI][0][2] };
				double acLength = length(ac);
				ac = new double[] { ac[0] / acLength, ac[1] / acLength, ac[2] / acLength };
	
				// Vektor BC
				bc = new double[] { coords[triangleI][2][0] - coords[triangleI][1][0],
						coords[triangleI][2][1] - coords[triangleI][1][1],
						coords[triangleI][2][2] - coords[triangleI][1][2] };
				// bcLength = length(bc);
				middle = new double[] { (coords[triangleI][0][0] + coords[triangleI][1][0] + coords[triangleI][2][0]) / 3,
						(coords[triangleI][0][1] + coords[triangleI][1][1] + coords[triangleI][2][1]) / 3,
						(coords[triangleI][0][2] + coords[triangleI][1][2] + coords[triangleI][2][2]) / 3 };
	
				lengthMiddle = calcR3Point(middle, coordsIntCache, forward, camPos, alpha, beta, factor); // dont
																											// calculate
																											// entire
																											// point
																											// with
																											// everything,
																											// but
																											// only
																											// its
																											// depth
				// lengthMiddle = calcR3Depth(middle, camPos); //TODO validate
				// TODO change please
				if (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
						|| coordsIntCache[1] > screenHeight)
					continue;
	
				if (lengthMiddle == 0)
					continue;
	
				// precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
				// precision = 0.0058*lengthMiddle+0.001;
				precision = 0.00138 * lengthMiddle + 0.001;
				// precision = 1;
	
				// lambdaAB gibt die Stelle auf der Geraden AB*lambda + A an, bei
				// der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
				lambdaAB = (ab0[0] * (coords[triangleI][2][0] - coords[triangleI][0][0])
						+ ab0[1] * (coords[triangleI][2][1] - coords[triangleI][0][1])
						+ ab0[2] * (coords[triangleI][2][2] - coords[triangleI][0][2]))
						/ (ab0[0] * ab0[0] + ab0[1] * ab0[1] + ab0[2] * ab0[2]);
				// check boundaries of lambdaAB
				if (lambdaAB > abLength || lambdaAB < 0) {
					// System.err.println("This model contains a invalid triangle
					// (lambdaAB > abLength). Its getting skipped ");
					// System.out.println("---");
					// System.out.println(Arrays.toString(coords[triangleI][0]));
					// System.out.println(Arrays.toString(coords[triangleI][1]));
					// System.out.println(Arrays.toString(coords[triangleI][2]));
					// System.out.println("---");
					continue;
				}
	
				// Vektor O (Einheitsvektor) (steht senkrecht auf AB und geht durch
				// (bzw. bis) C)
				o = new double[] { coords[triangleI][2][0] - (coords[triangleI][0][0] + ab0[0] * lambdaAB),
						coords[triangleI][2][1] - (coords[triangleI][0][1] + ab0[1] * lambdaAB),
						coords[triangleI][2][2] - (coords[triangleI][0][2] + ab0[2] * lambdaAB) };
				oLength = vectorUnify(o);
	
				collidingWithAC = true;
				for (double lambdaABCrawler = 0; lambdaABCrawler < abLength + precision; lambdaABCrawler += precision) {
	
					// check if lambdaVertical should be calculated by collision
					// with BC
					if (collidingWithAC && lambdaABCrawler > lambdaAB)
						collidingWithAC = false;
	
					//////////// 1. CALCULATE lambda2Max
	
					// distinguish between the two different calculation methods for
					// lambda2Max, depending on if its a collision with AC or CB
					if (collidingWithAC) {
	
						// FORMULA:
						// lambdaVertical =
						// (lambdaABCrawler*ab0[1]*ac[0]-lambdaABCrawler*ab0[0]*ac[1])
						// /
						// (o[0]*ac[1]-o[1]*ac[0]);
	
						// TODO NOTE: this should be cleaned up, as triangles are
						// now reprocessed in the main loop
						// 1.: calculate divisor and check if it is 0. If so,
						// calculate it with two other points. If it is again 0, set
						// lambdaVertical to 0
						cacheLambda2Divisor = (o[0] * ac[1] - o[1] * ac[0]);
						if (cacheLambda2Divisor == 0) {
							cacheLambda2Divisor = (o[0] * ac[2] - o[2] * ac[0]);
							if (cacheLambda2Divisor == 0) {
								cacheLambda2Divisor = (o[1] * ac[2] - o[2] * ac[1]);
								if (cacheLambda2Divisor == 0) {
									// System.err.println("WARNING: This model
									// contains an invalid triangle (at calculating
									// R3zBUFF) " + triangleI + ", " +
									// lambdaABCrawler);
									// System.out.println("---");
									// System.out.println(Arrays.toString(coords[triangleI][0]));
									// System.out.println(Arrays.toString(coords[triangleI][1]));
									// System.out.println(Arrays.toString(coords[triangleI][2]));
									// System.out.println("---");
//									break; // this tringle is weird (e.g. just a
											// line) and can't be rendered
								} else {
									lambda2Max = (lambdaABCrawler * ab0[2] * ac[1] - lambdaABCrawler * ab0[1] * ac[2])
											/ cacheLambda2Divisor;
								}
							} else {
								lambda2Max = (lambdaABCrawler * ab0[2] * ac[0] - lambdaABCrawler * ab0[0] * ac[2])
										/ cacheLambda2Divisor;
							}
						} else {
							lambda2Max = (lambdaABCrawler * ab0[1] * ac[0] - lambdaABCrawler * ab0[0] * ac[1])
									/ cacheLambda2Divisor;
						}
	
					} else {
	
						cacheLambda2Divisor = (o[1] * bc[0] - o[0] * bc[1]);
						if (cacheLambda2Divisor == 0) {
							cacheLambda2Divisor = (o[2] * bc[0] - o[0] * bc[2]);
							if (cacheLambda2Divisor == 0) {
									cacheLambda2Divisor = (o[2] * bc[1] - o[1] * bc[2]);	
									if (cacheLambda2Divisor == 0) {
								 System.err.println("0");
								// cacheLambda2Divisor = (o[1]*bc[2]-o[2]*bc[1]);
								// if(cacheLambda2Divisor == 0) {
								// System.out.println("WARNING: This model contains
								// an invalid triangle (at calculating R3zBUFF) " +
								// triangleI + ", " + lambdaABCrawler);
								// System.out.println("---");
								// System.out.println(Arrays.toString(coords[triangleI][0]));
								// System.out.println(Arrays.toString(coords[triangleI][1]));
								// System.out.println(Arrays.toString(coords[triangleI][2]));
								// System.out.println("---");
								break;
								// break; //this tringle is weird (e.g. just a line)
								// and can't be rendered
								// }else {
								// lambda2Max =
								// (lambdaABCrawler*ab0[2]*bc[1]-lambdaABCrawler*ab0[1]*bc[2]-ab[2]*bc[1]+ab[1]*bc[2])
								// / cacheLambda2Divisor;
								// }
									} else {
										lambda2Max = (lambdaABCrawler * ab0[1] * bc[2] - lambdaABCrawler * ab0[2] * bc[1]
												- ab[1] * bc[2] + ab[2] * bc[1]) / cacheLambda2Divisor;
									}
							} else {
								lambda2Max = (lambdaABCrawler * ab0[0] * bc[2] - lambdaABCrawler * ab0[2] * bc[0]
										- ab[0] * bc[2] + ab[2] * bc[0]) / cacheLambda2Divisor;
							}
						} else {
							lambda2Max = (lambdaABCrawler * ab0[0] * bc[1] - lambdaABCrawler * ab0[1] * bc[0]
									- ab[0] * bc[1] + ab[1] * bc[0]) / cacheLambda2Divisor;
						}
	
						// System.out.println("\t\t\t\t\t\t\t " + lambda2Max);
	
						// System.out.println(lambda2End);
						if (!Double.isFinite(lambda2Max) || lambda2Max > oLength) {
	
							// System.out.println("---");
							// System.err.println("AY " + lambda2Max);
							// System.out.println(Arrays.toString(coords[triangleI][0]));
							// System.out.println(Arrays.toString(coords[triangleI][1]));
							// System.out.println(Arrays.toString(coords[triangleI][2]));
							// System.out.println("---");
	
							// System.out.println("INFINIIIIIIITE");
							// System.out.println("Lambda1: "+lambda1+", Lambda2:
							// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+",
							// B: "+Arrays.toString(coords[x][1])+", C:
							// "+Arrays.toString(coords[x][2]));
							// System.err.println("WHELP " + lambda2Max);
							break;
						}
	
					}
	
					//////////// 2. CRAWL THROUGH lambda2
					for (double lambda2 = 0; lambda2 < lambda2Max + precision; lambda2 += precision) {
						// calculate the point (=> 2D into coordsIntCache) and
						// return its depth
						depth = calcR3Point(
								new double[] { lambda2 * o[0] + coords[triangleI][0][0] + lambdaABCrawler * ab0[0],
										lambda2 * o[1] + coords[triangleI][0][1] + lambdaABCrawler * ab0[1],
										lambda2 * o[2] + coords[triangleI][0][2] + lambdaABCrawler * ab0[2] },
								coordsIntCache, forward, camPos, alpha, beta, factor);
	
						// check if the 2d point is in the screens boundaries and if
						// its depth is smaller that the one noted in the buffer
						if (coordsIntCache[0] > 0 && coordsIntCache[1] > 0 && coordsIntCache[0] < screenWidth
								&& coordsIntCache[1] < screenHeight
								&& (bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] > depth
										|| bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] == 0)) {
							bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] = depth;
	
							// set color
							bufferDepth[coordsIntCache[0]][coordsIntCache[1]][1] = (int) coords[triangleI][3][0];
						}
					}
	
					// System.out.println("ja");
	
					// //do the same one last time
					// //double[] pointEnd = new double[] {lambda2End*o[0] +
					// coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] +
					// coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] +
					// coords[x][0][2] + lambda1*ab[2]};
					// depth = calcR3Point(new double[] {lambda2Max*o[0] +
					// coords[triangleI][0][0] +
					// lambdaABCrawler*ab0[0],lambda2Max*o[1] +
					// coords[triangleI][0][1] +
					// lambdaABCrawler*ab0[1],lambda2Max*o[2] +
					// coords[triangleI][0][2] +
					// lambdaABCrawler*ab0[2]},forward,camPos,alpha,beta,factor);
					// //System.out.println("Bekommen X:"+(int)R3PointEnd[1]+"
					// ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" -
					// Final");
					// if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					//
					// }
					// break;
					// }
	
					// TODO is the following if complex still needed? => bc the
					// coordinates are already manipulated to prevent
					// (lambdaABCrawler+precision > abLength) from happening
					// this ensures the last pixel (at lambaAB, lambda2 = 0) is
					// drawn. not needed bc. for loop makes sure of that
					// if(lambdaABCrawler+precision > abLength)
					// {
					//
					//// System.out.println("YES THERE IS");
					//
					// lambda2Max =
					// (lambdaABCrawler*ab0[1]*ac[0]-lambdaABCrawler*ab0[0]*ac[1])
					// /
					// (o[0]*ac[1]-o[1]*ac[0]);
					// //System.out.println(lambda2End);
					// if(Double.isInfinite(lambda2Max))
					// {
					// //System.out.println("Lambda1: "+lambda1+", Lambda2:
					// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B:
					// "+Arrays.toString(coords[x][1])+", C:
					// "+Arrays.toString(coords[x][2]));
					// break;
					// }
					// for(double lambda2 =
					// 0;lambda2<=lambda2Max;lambda2+=precision)
					// {
					// depth = calcR3Point(new double[] {lambda2*o[0] +
					// coords[triangleI][0][0] + abLength*ab0[0],lambda2*o[1] +
					// coords[triangleI][0][1] + abLength*ab0[1],lambda2*o[2] +
					// coords[triangleI][0][2] +
					// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
					// if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					// //System.out.println(Arrays.toString(coordsINTCache)+",
					// length: "+lengthB);
					// //System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+"
					// ;Deep:"+R3Point[3]);
					// }
					// if(lambda2+precision > lambda2Max&lambda2Max!=lambda2)
					// {
					// //double[] pointEnd = new double[] {lambda2End*o[0] +
					// coords[x][0][0] + abLength*ab[0],lambda2End*o[1] +
					// coords[x][0][1] + abLength*ab[1],lambda2End*o[2] +
					// coords[x][0][2] + abLength*ab[2]};
					// depth = calcR3Point(new double[] {lambda2Max*o[0] +
					// coords[triangleI][0][0] + abLength*ab0[0],lambda2Max*o[1] +
					// coords[triangleI][0][1] + abLength*ab0[1],lambda2Max*o[2] +
					// coords[triangleI][0][2] +
					// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
					// //System.out.println("Bekommen X:"+R3PointEnd[1]+"
					// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
					// if(coordsINTCache[1]>0&&coordsINTCache[2]>0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					// //System.out.println(Arrays.toString(coordsINTCache)+",
					// length: "+lengthB);
					// //System.out.println("X:"+(int)R3PointEnd[1]+"
					// ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" -
					// Final");
					// }
					// break;
					// }
					// }
					// break;
					// }
					// } else {
					// System.out.println("WE ARE NOW ON THE RIGHT SIDE");
					// lambda2Max =
					// (lambdaABCrawler*ab0[0]*bc[1]-lambdaABCrawler*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
					// /
					// (o[1]*bc[0]-o[0]*bc[1]);
	
					// if(lambda2Max > 100)
					// break;
	
					// cacheLambda2Divisor = (o[1]*bc[0]-o[0]*bc[1]);
					// if(cacheLambda2Divisor == 0) {
					// cacheLambda2Divisor = (o[2]*bc[0]-o[0]*bc[2]);
					//// System.err.println(cacheLambda2Divisor);
					// if(cacheLambda2Divisor == 0) {
					//// System.err.println("0");
					//// cacheLambda2Divisor = (o[1]*bc[2]-o[2]*bc[1]);
					//// if(cacheLambda2Divisor == 0) {
					//// System.out.println("WARNING: This model contains an invalid
					// triangle (at calculating R3zBUFF) " + triangleI + ", " +
					// lambdaABCrawler);
					//// System.out.println("---");
					//// System.out.println(Arrays.toString(coords[triangleI][0]));
					//// System.out.println(Arrays.toString(coords[triangleI][1]));
					//// System.out.println(Arrays.toString(coords[triangleI][2]));
					//// System.out.println("---");
					// break;
					//// break; //this tringle is weird (e.g. just a line) and can't
					// be rendered
					//// }else {
					//// lambda2Max =
					// (lambdaABCrawler*ab0[2]*bc[1]-lambdaABCrawler*ab0[1]*bc[2]-ab[2]*bc[1]+ab[1]*bc[2])
					// / cacheLambda2Divisor;
					//// }
					// }else {
					// lambda2Max =
					// (lambdaABCrawler*ab0[0]*bc[2]-lambdaABCrawler*ab0[2]*bc[0]-ab[0]*bc[2]+ab[2]*bc[0])
					// / cacheLambda2Divisor;
					// }
					// } else {
					// lambda2Max =
					// (lambdaABCrawler*ab0[0]*bc[1]-lambdaABCrawler*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
					// / cacheLambda2Divisor;
					// }
					//
					//// System.out.println("\t\t\t\t\t\t\t " + lambda2Max);
					//
					// //System.out.println(lambda2End);
					// if(!Double.isFinite(lambda2Max) || lambda2Max > oLength) {
					//
					//
					//// System.out.println("---");
					//// System.err.println("AY " + lambda2Max);
					//// System.out.println(Arrays.toString(coords[triangleI][0]));
					//// System.out.println(Arrays.toString(coords[triangleI][1]));
					//// System.out.println(Arrays.toString(coords[triangleI][2]));
					//// System.out.println("---");
					//
					//// System.out.println("INFINIIIIIIITE");
					// //System.out.println("Lambda1: "+lambda1+", Lambda2:
					// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B:
					// "+Arrays.toString(coords[x][1])+", C:
					// "+Arrays.toString(coords[x][2]));
					//// System.err.println("WHELP " + lambda2Max);
					// break;
					// }
	
					// for(double lambda2 = 0; lambda2 < lambda2Max + precision;
					// lambda2 += precision) {
					//// System.out.println("hmh " + lambda2 + ", lambdaMax: " +
					// lambda2Max);
					// //Gefundener Punkt
					// //double[] point = new double[] {lambda2*o[0] +
					// coords[x][0][0] + lambda1*ab[0],lambda2*o[1] +
					// coords[x][0][1] + lambda1*ab[1],lambda2*o[2] +
					// coords[x][0][2] + lambda1*ab[2]};
					// //System.out.println("ForwardX1:"+forward[0]);
					// depth = calcR3Point(new double[] {lambda2*o[0] +
					// coords[triangleI][0][0] + lambdaABCrawler*ab0[0],lambda2*o[1]
					// + coords[triangleI][0][1] +
					// lambdaABCrawler*ab0[1],lambda2*o[2] + coords[triangleI][0][2]
					// + lambdaABCrawler*ab0[2]},forward,camPos,alpha,beta,factor);
					// //System.out.println("Bekommen X:"+R3Point[1]+"
					// ;Y:"+R3Point[2]+" ;Deep:"+R3Point[3]);
					// //System.out.println("Bekommen X:"+(int)R3Point[1]+"
					// ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
					// //System.out.println("X:"+(int)R3Point[1]+"
					// ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
					// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					// //System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+"
					// ;Deep:"+R3Point[3]);
					// }
					// if(lambda2+precision > lambda2Max)
					// {
					// //double[] pointEnd = new double[] {lambda2End*o[0] +
					// coords[x][0][0] + lambda1*ab[0],lambda2End*o[1] +
					// coords[x][0][1] + lambda1*ab[1],lambda2End*o[2] +
					// coords[x][0][2] + lambda1*ab[2]};
					// depth = calcR3Point(new double[] {lambda2Max*o[0] +
					// coords[triangleI][0][0] +
					// lambdaABCrawler*ab0[0],lambda2Max*o[1] +
					// coords[triangleI][0][1] +
					// lambdaABCrawler*ab0[1],lambda2Max*o[2] +
					// coords[triangleI][0][2] +
					// lambdaABCrawler*ab0[2]},forward,camPos,alpha,beta,factor);
					// //System.out.println("Bekommen X:"+R3PointEnd[1]+"
					// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
					// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					// //System.out.println(Arrays.toString(coordsINTCache)+",
					// length: "+lengthB);
					// //System.out.println("X:"+R3PointEnd[1]+"
					// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
					// }
					// break;
					// }
					// }
	
					// TODO is the following if complex still needed? => bc the
					// coordinates are already manipulated to prevent
					// (lambdaABCrawler+precision > abLength) from happening
					// if(lambdaABCrawler+precision > abLength)
					// {
					//
					// lambda2Max =
					// (abLength*ab0[0]*bc[1]-abLength*ab0[1]*bc[0]-ab[0]*bc[1]+ab[1]*bc[0])
					// /
					// (o[1]*bc[0]-o[0]*bc[1]);
					// //System.out.println(lambda2End);
					// if(Double.isInfinite(lambda2Max))
					// {
					// //System.out.println("Lambda1: "+lambda1+", Lambda2:
					// "+lambda2End+", A: "+Arrays.toString(coords[x][0])+", B:
					// "+Arrays.toString(coords[x][1])+", C:
					// "+Arrays.toString(coords[x][2]));
					// break;
					// }
					// for(double lambda2 =
					// 0;lambda2<=lambda2Max;lambda2+=precision)
					// {
					// //double[] point = new double[] {lambda2*o[0] +
					// coords[x][0][0] + abLength*ab[0],lambda2*o[1] +
					// coords[x][0][1] + abLength*ab[1],lambda2*o[2] +
					// coords[x][0][2] + abLength*ab[2]};
					// depth = calcR3Point(new double[] {lambda2*o[0] +
					// coords[triangleI][0][0] + abLength*ab0[0],lambda2*o[1] +
					// coords[triangleI][0][1] + abLength*ab0[1],lambda2*o[2] +
					// coords[triangleI][0][2] +
					// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
					// //System.out.println("Bekommen X:"+(int)R3Point[1]+"
					// ;Y:"+(int)R3Point[2]+" ;Deep:"+(int)R3Point[3]);
					// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					// //System.out.println(Arrays.toString(coordsINTCache)+",
					// length: "+lengthB);
					// //System.out.println("X:"+R3Point[1]+" ;Y:"+R3Point[2]+"
					// ;Deep:"+R3Point[3]);
					// }
					// if(lambda2+precision > lambda2Max)
					// {
					// //double[] pointEnd = new double[] {lambda2End*o[0] +
					// coords[x][0][0] + abLength*ab[0],lambda2End*o[1] +
					// coords[x][0][1] + abLength*ab[1],lambda2End*o[2] +
					// coords[x][0][2] + abLength*ab[2]};
					// depth = calcR3Point(new double[] {lambda2Max*o[0] +
					// coords[triangleI][0][0] + abLength*ab0[0],lambda2Max*o[1] +
					// coords[triangleI][0][1] + abLength*ab0[1],lambda2Max*o[2] +
					// coords[triangleI][0][2] +
					// abLength*ab0[2]},forward,camPos,alpha,beta,factor);
					// //System.out.println("Bekommen X:"+R3PointEnd[1]+"
					// ;Y:"+R3PointEnd[2]+" ;Deep:"+R3PointEnd[3]+" - Final");
					// if(coordsINTCache[1]>=0&&coordsINTCache[2]>=0&&coordsINTCache[1]<screenWidth&&coordsINTCache[2]<screenHeight&&(Buffer2D[coordsINTCache[1]][coordsINTCache[2]]
					// > depth||Buffer2D[coordsINTCache[1]][coordsINTCache[2]]==0))
					// {
					// Buffer2D[coordsINTCache[1]][coordsINTCache[2]] = depth;
					// //System.out.println(Arrays.toString(coordsINTCache)+",
					// length: "+lengthB);
					// //System.out.println("X:"+(int)R3PointEnd[1]+"
					// ;Y:"+(int)R3PointEnd[2]+" ;Deep:"+(int)R3PointEnd[3]+" -
					// Final");
					// }
					// break;
					// }
					// }
					// break;
					// }
	
				}
	
			}
		}
		// System.out.println("stop");
		return bufferDepth;
	}

//	private static final double R3MESH_PRECISION = 0.2;
	private double precisionMesh;
	private double lengthMiddle;
	private int[] coordsIntCache = new int[2];
	private double[] meshCacheVectorAB = new double[3];
	private double[] meshCacheVectorAC = new double[3];
	private double[] meshCacheVectorBC = new double[3];
	private double meshCacheVectorABLength;
	private double meshCacheVectorACLength;
	private double meshCacheVectorBCLength;
	private double[] meshCacheVectorABUnit = new double[3];
	private double[] meshCacheVectorACUnit = new double[3];
	private double[] meshCacheVectorBCUnit = new double[3];

	private void calcR3Mesh(double[][][] bufferDepth, double[][] triangleCoords, double[] forward, double[] camPos,double alpha, double beta, double factor) {
		
		double[] middle = new double[] { (triangleCoords[0][0] + triangleCoords[1][0] + triangleCoords[2][0]) / 3,
				(triangleCoords[0][1] + triangleCoords[1][1] + triangleCoords[2][1]) / 3,
				(triangleCoords[0][2] + triangleCoords[1][2] + triangleCoords[2][2]) / 3 };

		lengthMiddle = calcR3Point(middle, coordsIntCache, forward, camPos, alpha, beta, factor);
		
		//TODO decide if this should stay in here or not:
//		if (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
//				|| coordsIntCache[1] > screenHeight)
//			return;

		if (lengthMiddle == 0)
			return;

//		System.out.println(lengthMiddle);
		
		// precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
		// precision = 0.0058*lengthMiddle+0.001;
		precisionMesh = 0.003 * lengthMiddle + 0.001;
//		precisionMesh = 0.2;
		
		meshCacheVectorAB[0] = triangleCoords[1][0] - triangleCoords[0][0];
		meshCacheVectorAB[1] = triangleCoords[1][1] - triangleCoords[0][1];
		meshCacheVectorAB[2] = triangleCoords[1][2] - triangleCoords[0][2];
		meshCacheVectorABLength = Math.sqrt((meshCacheVectorAB[0]*meshCacheVectorAB[0]) + (meshCacheVectorAB[1]*meshCacheVectorAB[1]) + (meshCacheVectorAB[2]*meshCacheVectorAB[2]));		
		meshCacheVectorABUnit[0] = meshCacheVectorAB[0] / meshCacheVectorABLength;
		meshCacheVectorABUnit[1] = meshCacheVectorAB[1] / meshCacheVectorABLength;
		meshCacheVectorABUnit[2] = meshCacheVectorAB[2] / meshCacheVectorABLength;
		
		meshCacheVectorAC[0] = triangleCoords[2][0] - triangleCoords[0][0];
		meshCacheVectorAC[1] = triangleCoords[2][1] - triangleCoords[0][1];
		meshCacheVectorAC[2] = triangleCoords[2][2] - triangleCoords[0][2];
		meshCacheVectorACLength = Math.sqrt((meshCacheVectorAC[0]*meshCacheVectorAC[0]) + (meshCacheVectorAC[1]*meshCacheVectorAC[1]) + (meshCacheVectorAC[2]*meshCacheVectorAC[2]));
		meshCacheVectorACUnit[0] = meshCacheVectorAC[0] / meshCacheVectorACLength;
		meshCacheVectorACUnit[1] = meshCacheVectorAC[1] / meshCacheVectorACLength;
		meshCacheVectorACUnit[2] = meshCacheVectorAC[2] / meshCacheVectorACLength;
		
		meshCacheVectorBC[0] = triangleCoords[2][0] - triangleCoords[1][0];
		meshCacheVectorBC[1] = triangleCoords[2][1] - triangleCoords[1][1];
		meshCacheVectorBC[2] = triangleCoords[2][2] - triangleCoords[1][2];
		meshCacheVectorBCLength = Math.sqrt((meshCacheVectorBC[0]*meshCacheVectorBC[0]) + (meshCacheVectorBC[1]*meshCacheVectorBC[1]) + (meshCacheVectorBC[2]*meshCacheVectorBC[2]));
		meshCacheVectorBCUnit[0] = meshCacheVectorBC[0] / meshCacheVectorBCLength;
		meshCacheVectorBCUnit[1] = meshCacheVectorBC[1] / meshCacheVectorBCLength;
		meshCacheVectorBCUnit[2] = meshCacheVectorBC[2] / meshCacheVectorBCLength;
		
		double iterator;
//		double depth
		double[] cachePoint3D = new double[3];
		int[] cachePoint2D = new int[2];
		double cacheDepth;
		for(iterator = 0; iterator < meshCacheVectorABLength; iterator += precisionMesh) {
			cachePoint3D[0] = (meshCacheVectorABUnit[0] * iterator) + triangleCoords[0][0];
			cachePoint3D[1] = (meshCacheVectorABUnit[1] * iterator) + triangleCoords[0][1];
			cachePoint3D[2] = (meshCacheVectorABUnit[2] * iterator) + triangleCoords[0][2];
			
			cacheDepth = calcR3Point(cachePoint3D, cachePoint2D, forward, camPos, alpha, beta, factor);
			if (cachePoint2D[0] > 0 && cachePoint2D[1] > 0 && cachePoint2D[0] < screenWidth && cachePoint2D[1] < screenHeight
					&& cacheDepth < bufferDepth[cachePoint2D[0]][cachePoint2D[1]][0]) {
			
					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][0] = cacheDepth;

					// set color
//					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][1] = -1;
				
			}
		}
		
		for(iterator = 0; iterator < meshCacheVectorACLength; iterator += precisionMesh) {
			cachePoint3D[0] = (meshCacheVectorACUnit[0] * iterator) + triangleCoords[0][0];
			cachePoint3D[1] = (meshCacheVectorACUnit[1] * iterator) + triangleCoords[0][1];
			cachePoint3D[2] = (meshCacheVectorACUnit[2] * iterator) + triangleCoords[0][2];
			
			cacheDepth = calcR3Point(cachePoint3D, cachePoint2D, forward, camPos, alpha, beta, factor);
			if (cachePoint2D[0] > 0 && cachePoint2D[1] > 0 && cachePoint2D[0] < screenWidth && cachePoint2D[1] < screenHeight
					&& cacheDepth < bufferDepth[cachePoint2D[0]][cachePoint2D[1]][0]) {
			
					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][0] = cacheDepth;

					// set color
//					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][1] = -1;				
				
			}
		}
		
		for(iterator = 0; iterator < meshCacheVectorBCLength; iterator += precisionMesh) {
			cachePoint3D[0] = (meshCacheVectorBCUnit[0] * iterator) + triangleCoords[1][0];
			cachePoint3D[1] = (meshCacheVectorBCUnit[1] * iterator) + triangleCoords[1][1];
			cachePoint3D[2] = (meshCacheVectorBCUnit[2] * iterator) + triangleCoords[1][2];
			
			cacheDepth = calcR3Point(cachePoint3D, cachePoint2D, forward, camPos, alpha, beta, factor);
			if (cachePoint2D[0] > 0 && cachePoint2D[1] > 0 && cachePoint2D[0] < screenWidth && cachePoint2D[1] < screenHeight
					&& cacheDepth < bufferDepth[cachePoint2D[0]][cachePoint2D[1]][0]) {
			
					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][0] = cacheDepth;

					// set color
//					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][1] = -1;
				
			}
		}
		
//		throw new RuntimeException("Ay");
	}
	
	public static void optimizeCoordinates(double[][][] coords) {
		
		double[] ab0;	// vector ab, unit vector		
		double lambda;	// ab0 * lambda gives the point, on which the point of C sits in a 90° angle on
		
//		double[] ac0;	// vector ac
		
		
		double abLength;
		double[] resortCache;
		
		for(int triangleI = 0;triangleI < coords.length;triangleI++) {
			
			//calculate AB (unit)
			ab0 = new double[] {coords[triangleI][1][0]-coords[triangleI][0][0],coords[triangleI][1][1]-coords[triangleI][0][1],coords[triangleI][1][2]-coords[triangleI][0][2]};			
			abLength = Mathstuff.vectorUnify(ab0);
			
			//Vektor AC
//			ac0 = Mathstuff.vectorUnify(new double[] {coords[triangleI][2][0]-coords[triangleI][0][0],coords[triangleI][2][1]-coords[triangleI][0][1],coords[triangleI][2][2]-coords[triangleI][0][2]}, false);

			lambda = 
			(ab0[0]*(coords[triangleI][2][0]-coords[triangleI][0][0])+ab0[1]*(coords[triangleI][2][1]-coords[triangleI][0][1])+ab0[2]*(coords[triangleI][2][2]-coords[triangleI][0][2]))
							/
			(ab0[0]*ab0[0]+ab0[1]*ab0[1]+ab0[2]*ab0[2]);
			
			if(lambda<0) {
				
				resortCache = coords[triangleI][0];
				coords[triangleI][0] = coords[triangleI][2];
				coords[triangleI][2] = resortCache;
				
			} else if(lambda > abLength) {

				resortCache = coords[triangleI][2];
				coords[triangleI][2] = coords[triangleI][1];
				coords[triangleI][1] = resortCache;
			}
		}
	}
	
	public double[][] getClosestTriangleGameObjects(Camera camera) {
		double lambdaCB;
		double lambdaAP;
		double lambdaP;
		double[] camPos = camera.pos;
		double[] forward = camera.forward;
		double[] vectorAC = new double[3];
		double[] vectorAB = new double[3];
		double[] vectorCB = new double[3];
		double[] vectorAP = new double[3];
		double[] pointP = new double[3];
		
		double lastDistance = Double.MAX_VALUE;
		int closestTriangleIndex = -1;
		GameObject gameObjectOfClosestTriangle = null;
		double[][][] coords;		
		for(ThreadProcessor thread : ThreadProcessor.threadRegister) {
			for(GameObject gameObject : thread.getGameObjects()) {
				coords = gameObject.getTrianglesAbsolute();
				for(int i = 0; i < coords.length; i++) {
					vectorAC[0] = coords[i][2][0] - coords[i][0][0];
					vectorAC[1] = coords[i][2][1] - coords[i][0][1];
					vectorAC[2] = coords[i][2][2] - coords[i][0][2];
					
					vectorAB[0] = coords[i][1][0] - coords[i][0][0];
					vectorAB[1] = coords[i][1][1] - coords[i][0][1];
					vectorAB[2] = coords[i][1][2] - coords[i][0][2];
					
					vectorCB[0] = coords[i][1][0] - coords[i][2][0];
					vectorCB[1] = coords[i][1][1] - coords[i][2][1];
					vectorCB[2] = coords[i][1][2] - coords[i][2][2];
					
					lambdaP =
					-((camPos[0]-coords[i][0][0])*(vectorAC[1]*vectorAB[2]-vectorAC[2]*vectorAB[1])+(camPos[1]-coords[i][0][1])*(vectorAC[2]*vectorAB[0]-vectorAC[0]*vectorAB[2])+(camPos[2]-coords[i][0][2])*(vectorAC[0]*vectorAB[1]-vectorAC[1]*vectorAB[0]))
							/
 					(forward[0]*(vectorAC[1]*vectorAB[2]-vectorAC[2]*vectorAB[1]) + forward[1]*(vectorAC[2]*vectorAB[0]-vectorAC[0]*vectorAB[2]) + forward[2]*(vectorAC[0]*vectorAB[1]-vectorAC[1]*vectorAB[0]));
					
					if(lambdaP<0)
					{
						continue;
					}
					
					pointP[0] = lambdaP * forward[0] + camPos[0]; 
					pointP[1] = lambdaP * forward[1] + camPos[1];
					pointP[2] = lambdaP * forward[2] + camPos[2];
					
					vectorAP[0] = pointP[0] - coords[i][0][0];
					vectorAP[1] = pointP[1] - coords[i][0][1];
					vectorAP[2] = pointP[2] - coords[i][0][2];
					
					lambdaCB = 
					((-vectorAC[0]*vectorAP[1])+(vectorAC[1]*vectorAP[0]))
							/
					((vectorCB[0]*vectorAP[1])+(-vectorCB[1]*vectorAP[0]));
					if(Double.isNaN(lambdaCB))
					{
						lambdaCB = 
						((-vectorAC[0]*vectorAP[2])+(vectorAC[2]*vectorAP[0]))
								/
						((vectorCB[0]*vectorAP[2])+(-vectorCB[2]*vectorAP[0]));
						if(Double.isNaN(lambdaCB))
						{
							lambdaCB = 
							((-vectorAC[1]*vectorAP[2])+(vectorAC[2]*vectorAP[1]))
									/
							((vectorCB[1]*vectorAP[2])+(-vectorCB[2]*vectorAP[1]));
						}
					}
//					System.out.println(lambdaCB+">=0,<=1");
					lambdaAP = 
					(lambdaCB*vectorCB[2]+vectorAC[2])
							/
					(vectorAP[2]);
					if(Double.isNaN(lambdaAP))
					{
						lambdaAP = 
						(lambdaCB*vectorCB[1]+vectorAC[1])
								/
						(vectorAP[1]);
						if(Double.isNaN(lambdaAP))
						{
							lambdaAP = 
							(lambdaCB*vectorCB[0]+vectorAC[0])
									/
							(vectorAP[0]);
						}
					}
//					System.out.println(lambdaAP+">=1");
					if(lambdaAP>=1&&lambdaCB<=1&&lambdaCB>=0)
					{
						System.out.println(i);
						double distance = calcR3Depth(pointP, camPos);
						if(distance<lastDistance)
						{
							lastDistance = distance;
							closestTriangleIndex = i;
							gameObjectOfClosestTriangle = gameObject;
						}
					}
				}
			}
		}
		
		if(gameObjectOfClosestTriangle == null)
			return null;
		
		return gameObjectOfClosestTriangle.getTriangles()[closestTriangleIndex]; 
	}
	
	public double[][] getClosestTriangleRaw(Camera camera) {
		double lambdaCB;
		double lambdaAP;
		double lambdaP;
		double[] camPos = camera.pos;
		double[] forward = camera.forward;
		double[] vectorAC = new double[3];
		double[] vectorAB = new double[3];
		double[] vectorCB = new double[3];
		double[] vectorAP = new double[3];
		double[] pointP = new double[3];
		
		double lastDistance = Double.MAX_VALUE;
		double[][] closestTriangle = null;
		double[][][] coords = Main.coords;
		
		
		for(int i = 0; i < coords.length; i++) {
			vectorAC[0] = coords[i][2][0] - coords[i][0][0];
			vectorAC[1] = coords[i][2][1] - coords[i][0][1];
			vectorAC[2] = coords[i][2][2] - coords[i][0][2];
			
			vectorAB[0] = coords[i][1][0] - coords[i][0][0];
			vectorAB[1] = coords[i][1][1] - coords[i][0][1];
			vectorAB[2] = coords[i][1][2] - coords[i][0][2];
			
			vectorCB[0] = coords[i][1][0] - coords[i][2][0];
			vectorCB[1] = coords[i][1][1] - coords[i][2][1];
			vectorCB[2] = coords[i][1][2] - coords[i][2][2];
			
			lambdaP =
			-((camPos[0]-coords[i][0][0])*(vectorAC[1]*vectorAB[2]-vectorAC[2]*vectorAB[1])+(camPos[1]-coords[i][0][1])*(vectorAC[2]*vectorAB[0]-vectorAC[0]*vectorAB[2])+(camPos[2]-coords[i][0][2])*(vectorAC[0]*vectorAB[1]-vectorAC[1]*vectorAB[0]))
					/
			(forward[0]*(vectorAC[1]*vectorAB[2]-vectorAC[2]*vectorAB[1]) + forward[1]*(vectorAC[2]*vectorAB[0]-vectorAC[0]*vectorAB[2]) + forward[2]*(vectorAC[0]*vectorAB[1]-vectorAC[1]*vectorAB[0]));
			
			if(lambdaP<0)
			{
				continue;
			}
			
			pointP[0] = lambdaP * forward[0] + camPos[0]; 
			pointP[1] = lambdaP * forward[1] + camPos[1];
			pointP[2] = lambdaP * forward[2] + camPos[2];
			
			vectorAP[0] = pointP[0] - coords[i][0][0];
			vectorAP[1] = pointP[1] - coords[i][0][1];
			vectorAP[2] = pointP[2] - coords[i][0][2];
			
			lambdaCB = 
			((-vectorAC[0]*vectorAP[1])+(vectorAC[1]*vectorAP[0]))
					/
			((vectorCB[0]*vectorAP[1])+(-vectorCB[1]*vectorAP[0]));
			
			lambdaAP = 
			(lambdaCB*vectorCB[2]+vectorAC[2])
					/
			(vectorAP[2]);
			if(lambdaAP>=1&&lambdaCB<=1&&lambdaCB>=0)
			{
				double distance = calcR3Depth(pointP, camPos);
				if(distance<lastDistance)
				{
					lastDistance = distance;
					closestTriangle = coords[i];
				}
			}
				
		}
		
		return closestTriangle; 
	}

	public static double length(double[] vectorR3) {
		return Math.sqrt(vectorR3[0] * vectorR3[0] + vectorR3[1] * vectorR3[1] + vectorR3[2] * vectorR3[2]);
	}

	/**
	 * unifies passed vector; overwrites vector values, if ~createNewVector~ is
	 * false!
	 * 
	 * @return returns unified vector
	 */
	public static double[] vectorUnify(double[] vector, boolean createNewVector) {
		double[] target;
		if (createNewVector)
			target = new double[vector.length];
		else
			target = vector;

		double len = Math.sqrt((vector[0] * vector[0]) + (vector[1] * vector[1]) + (vector[2] * vector[2]));
		target[0] = vector[0] / len;
		target[1] = vector[1] / len;
		target[2] = vector[2] / len;
		return target;
	}

	/**
	 * unfies passed vector<br>
	 * NOTE: if overwriting values of this vector is not wanted please refer to
	 * {@link #vectorUnify(double[], boolean)}
	 * 
	 * @return returns length of initial vector
	 */
	public static double vectorUnify(double[] vector) {
		double len = Math.sqrt((vector[0] * vector[0]) + (vector[1] * vector[1]) + (vector[2] * vector[2]));
		vector[0] = vector[0] / len;
		vector[1] = vector[1] / len;
		vector[2] = vector[2] / len;
		return len;
	}

	private void printWarningValuesNotInitialized() {
		System.err.println(
				"-----WARNING----- Values of Mathstuff Object were not initialized at this point. INFO: Thread: '"
						+ Thread.currentThread().toString() + "'");
	}
	
	/**
	 * @param colorID colorID as returned by {@link Main#storeColor(int)}
	 */
	public static GameObject generateCube(double[] centerPos, double edgeLength, double colorID) {
		double[][][] triangles = new double[12][4][];
		double halfEdgeLength = edgeLength/2d;
		
		//bottom side
		triangles[0] = 
				new double[][] {
					{-halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{+halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{colorID}
				}
		;
		triangles[1] = 
				new double[][] {
					{+halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{+halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{colorID}
				}
		;
		
		//top side
		triangles[2] = 
				new double[][] {
					{-halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{+halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{colorID}
				}
		;
		triangles[3] = 
				new double[][] {
					{+halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{+halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{colorID}
				}
		;
		
		
		//front side
		triangles[4] = 
				new double[][] {
					{+halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{+halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{+halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{colorID}
				}
		;
		triangles[5] = 
				new double[][] {
					{+halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{+halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{+halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{colorID}
			}
		;
		
		//back side
		triangles[6] = 
				new double[][] {
					{-halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{colorID}
			}
		;
		triangles[7] = 
				new double[][] {		
					{-halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{colorID}
				}
		;
		
		//left side
		triangles[8] = 
				new double[][] {		
					{+halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{-halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{colorID}
				}
		;
		triangles[9] = 
				new double[][] {		
					{+halfEdgeLength, -halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{+halfEdgeLength, -halfEdgeLength, -halfEdgeLength},
					{colorID}
				}
		;
		
		//left side
		triangles[10] = 
				new double[][] {		
					{+halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{colorID}
				}
		;
		triangles[11] = 
				new double[][] {		
					{+halfEdgeLength, +halfEdgeLength, +halfEdgeLength},
					{-halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{+halfEdgeLength, +halfEdgeLength, -halfEdgeLength},
					{colorID}
				}
		;
		
//		System.out.println("This generated cube hets a hitbox of radius " + halfEdgeLength);
		return new GameObject(centerPos, triangles, new Hitbox(halfEdgeLength));
	}
	private static double lambdaCB,lambdaAP;
	private static double[] vectorAP = new double[3],vecAC = new double[3],vecCB = new double[3];
	public static boolean isInsideOfTriangle(double[] pointP,double[][] triangle)
	{
		vecAC[0] = triangle[2][0] - triangle[0][0];
		vecAC[1] = triangle[2][1] - triangle[0][1];
		vecAC[2] = triangle[2][2] - triangle[0][2];
		
		vecCB[0] = triangle[1][0] - triangle[2][0];
		vecCB[1] = triangle[1][1] - triangle[2][1];
		vecCB[2] = triangle[1][2] - triangle[2][2];
		
		vectorAP[0] = pointP[0] - triangle[0][0];
		vectorAP[1] = pointP[1] - triangle[0][1];
		vectorAP[2] = pointP[2] - triangle[0][2];
		
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
//		System.out.println(lambdaCB+">=0,<=1");
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
//		System.out.println(lambdaAP+">=1");
		if(lambdaAP>=1&&lambdaCB<=1&&lambdaCB>=0)
		{
			return true;
		}
		return false;
	}
}
