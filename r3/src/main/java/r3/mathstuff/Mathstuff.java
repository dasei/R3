package r3.mathstuff;

import java.awt.Color;
import java.util.ArrayList;

import game.Game;
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

		lambdaCamToPointEbenenSchnittpunkt = 
				-(forward[0] * (camPos[0] - cacheAnkerEbene[0]) + forward[1] * (camPos[1] - cacheAnkerEbene[1]) + forward[2] * (camPos[2] - cacheAnkerEbene[2]))
						/ 
				(forward[0] * cacheVectorCamToPoint0[0] + forward[1] * cacheVectorCamToPoint0[1] + forward[2] * cacheVectorCamToPoint0[2]);
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
	private double calcR3PointExact(double[] coords, double[] coordsIntCache, double[] forward, double[] camPos, double alpha,
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

		lambdaCamToPointEbenenSchnittpunkt = 
				-(forward[0] * (camPos[0] - cacheAnkerEbene[0]) + forward[1] * (camPos[1] - cacheAnkerEbene[1]) + forward[2] * (camPos[2] - cacheAnkerEbene[2]))
						/ 
				(forward[0] * cacheVectorCamToPoint0[0] + forward[1] * cacheVectorCamToPoint0[1] + forward[2] * cacheVectorCamToPoint0[2]);
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
					+  (vecCamToEbenenSchnittpunktX3[1] * factor * screenSizeMinimum * fovFactor);
			coordsIntCache[1] = screenCenterY -  (((-Math.sin(-alpha) * vecCamToEbenenSchnittpunktX3[0]
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
		double[] forward = Main.cameraForwardOnIterationStart;
		double alpha = camera.alpha;
		double beta = camera.beta;
		double factor = camera.scaleFactor;
		double[] camPos = Main.cameraPosOnIterationStart;

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
			
			if (Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN && (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
					|| coordsIntCache[1] > screenHeight))
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
		double[] camPos = new double[] {camera.pos[0], camera.pos[1], camera.pos[2]};
		
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
		
		int insideScreenAmount;
		
		double lengthCache = 0;
		
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
//			System.out.println(gameObjects.size());
			gameObject = gameObjects.get(gameObjectI);
			if(gameObject == null||gameObject.isRemoved())
			{
//				System.out.println(gameObjects.size());
//				System.out.println("to remove");
				gameObjects.remove(gameObjectI);
//				Game.getGame().gameObjects.remove(gameObjectI);
//				System.out.println(gameObjects.size());
//				System.out.println("------");
				continue;
			}
			coords = gameObject.getTrianglesAbsolute();
			
			
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
				
				if (Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN && (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
						|| coordsIntCache[1] > screenHeight))
					continue;
	
				if (lengthMiddle == 0)
					continue;
	

						//				System.out.println(".");																					
//				if(!Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN)	 											
//				{		
//					insideScreenAmount = 0;
//					for(int pointsTriangle = 0;pointsTriangle < 3; pointsTriangle++)
//					{
//						lengthCache = calcR3Point(coords[triangleI][pointsTriangle], coordsIntCache, forward, camPos, alpha, beta, factor);
//						
//						if ((coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth || coordsIntCache[1] > screenHeight) || lengthCache == 0)
//							insideScreenAmount--;
//					}
//					if(insideScreenAmount==-3)
//					System.out.println("cont");
//				}
				// precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
				// precision = 0.0058*lengthMiddle+0.001;
				if(Main.lowMode==0) {
					precision = 0.00038 * lengthMiddle + 0.001;
				} else {
//					precision = 0.09/(1.6*Main.lowMode) * lengthMiddle + 0.001;
					precision = 0.0035 * lengthMiddle ;
				}
				// lambdaAB gibt die Stelle auf der Geraden AB*lambda + A an, bei
				// der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
				lambdaAB = (ab0[0] * (coords[triangleI][2][0] - coords[triangleI][0][0])
						+ ab0[1] * (coords[triangleI][2][1] - coords[triangleI][0][1])
						+ ab0[2] * (coords[triangleI][2][2] - coords[triangleI][0][2]))
						/ (ab0[0] * ab0[0] + ab0[1] * ab0[1] + ab0[2] * ab0[2]);
				// check boundaries of lambdaAB
				if (lambdaAB > abLength || lambdaAB < 0)
					continue;
	
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
									
									//break //?
									
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
										System.err.println("cacheLambda2Divisor is equal to 0 (=> Mathstuff.calcR3ZBuff)");
										break;
										
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
						
						
						if (!Double.isFinite(lambda2Max) || lambda2Max > oLength)	
							break;	
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
				}	
			}
		}
		return bufferDepth;
	}
	
	////////////////////////////////////For calcR3ZBuff2DRasterization(both)
	private double[] forward;
	private double alpha;
	private double beta;
	private double factor;
	private double[] camPos = new double[3];
	private double[] a = new double[3];
	private double[] b = new double[3];
	private double[] c = new double[3];
	
	private double[][][] coordsCacheABC = new double[1][3][];
	
	private double[] bc = new double[3];
	private double[] ab = new double[3];
	private double[] ac = new double[3];
	private double[] ab0;
	private double[] o = new double[2];
	
	private double abLength;
	private double lambdaAB;
	
	private double[] coordsDoubleCache = new double[2];
	//Used for plane(EBENE)
	private double[] vecNormal = new double[3];
	private double cPlane;
	private double pixelX1Cache;
	private double pixelX2Cache;
	
	private int pixelX1CacheInt;
	private int pixelX2CacheInt;

//	double[] middle;
	// System.out.println(coords.length);
	private boolean collidingWithAC = true;
	// Lambda1:Stelle auf der Gerade AB*lambda1 + A
	private double lambda2Max = 0;
	// double lambda3 = 0;
	private double depth = 0;
	// double bcLength = 0;
	
//	int insideScreenAmount;
//	
//	double lengthCache = 0;
	
//	double lengthMiddle = 0;
	private double oLength = 0;

	private double precision = 0.71;
	// System.out.println("start");
	private double cacheLambda2Divisor;
	////////////////////////////////////
	public double[][][] calcR3ZBuff2DRasterization(double[][][] coords, Camera camera, int triangleOffset, int triangleAmount,
			boolean createNewBuffer) {
		this.updateValues();

		// Extract constants from camera
		forward = camera.forward;
		alpha = camera.alpha;
		beta = camera.beta;
		factor = camera.scaleFactor;
		camPos[0] = camera.pos[0];
		camPos[1] = camera.pos[1];
		camPos[2] = camera.pos[2];
		
		// System.out.println(Arrays.toString(forward));

		double[][][] bufferDepth;
		if (createNewBuffer)
			bufferDepth = new double[screenWidth][screenHeight][2];
		else
			bufferDepth = ThreadProcessor.getBufferToCalculateOn();

		
		// System.out.println("c " + coords.length);
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
//			ab0 = new double[] { coords[triangleI][1][0] - coords[triangleI][0][0],
//					coords[triangleI][1][1] - coords[triangleI][0][1],
//					coords[triangleI][1][2] - coords[triangleI][0][2] };
//			double abLength = length(ab0);
//			double[] ab = ab0;
//			ab0 = new double[] { ab0[0] / abLength, ab0[1] / abLength, ab0[2] / abLength };
//			// Vektor AC
//			ac = new double[] { coords[triangleI][2][0] - coords[triangleI][0][0],
//					coords[triangleI][2][1] - coords[triangleI][0][1],
//					coords[triangleI][2][2] - coords[triangleI][0][2] };
//			double acLength = length(ac);
//			ac = new double[] { ac[0] / acLength, ac[1] / acLength, ac[2] / acLength };
//
//			// Vektor BC
//			bc = new double[] { coords[triangleI][2][0] - coords[triangleI][1][0],
//					coords[triangleI][2][1] - coords[triangleI][1][1],
//					coords[triangleI][2][2] - coords[triangleI][1][2] };
//			// bcLength = length(bc);
			
//			middle = new double[] { (coords[triangleI][0][0] + coords[triangleI][1][0] + coords[triangleI][2][0]) / 3,
//					(coords[triangleI][0][1] + coords[triangleI][1][1] + coords[triangleI][2][1]) / 3,
//					(coords[triangleI][0][2] + coords[triangleI][1][2] + coords[triangleI][2][2]) / 3 };
//
//			lengthMiddle = calcR3Point(middle, coordsIntCache, forward, camPos, alpha, beta, factor); // dont
																										// calculate
																										// entire
																										// point
																										// with
																										// everything,
																										// but
																										// only
																										// its
																										// depth
			
//			if (Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN && (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
//					|| coordsIntCache[1] > screenHeight))
//				continue;

//			if (lengthMiddle == 0)
//				continue;


					//				System.out.println(".");																					
//			if(!Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN)	 											
//			{		
//				insideScreenAmount = 0;
//				for(int pointsTriangle = 0;pointsTriangle < 3; pointsTriangle++)
//				{
//					lengthCache = calcR3Point(coords[triangleI][pointsTriangle], coordsIntCache, forward, camPos, alpha, beta, factor);
//					
//					if ((coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth || coordsIntCache[1] > screenHeight) || lengthCache == 0)
//						insideScreenAmount--;
//				}
//				if(insideScreenAmount==-3)
//				System.out.println("cont");
//			}
			// precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
			// precision = 0.0058*lengthMiddle+0.001;
//			if(Main.lowMode==0) {
//				precision = 0.00038 * lengthMiddle + 0.001;
//			} else {
////				precision = 0.09/(1.6*Main.lowMode) * lengthMiddle + 0.001;
//				precision = 0.0035 * lengthMiddle ;
//			}
			a[2] = -calcR3PointExact(coords[triangleI][0], coordsDoubleCache, forward, camPos, alpha, beta, factor);
			a[0] = coordsDoubleCache[0];
			a[1] = coordsDoubleCache[1];
			
			b[2] = -calcR3PointExact(coords[triangleI][1], coordsDoubleCache, forward, camPos, alpha, beta, factor);
			b[0] = coordsDoubleCache[0];
			b[1] = coordsDoubleCache[1];
			
			c[2] = -calcR3PointExact(coords[triangleI][2], coordsDoubleCache, forward, camPos, alpha, beta, factor);
			c[0] = coordsDoubleCache[0];
			c[1] = coordsDoubleCache[1];
//			System.out.println("triangleI : "+triangleI+" , a: "+Arrays.toString(a)+" , b: "+Arrays.toString(b)+" , c: "+Arrays.toString(c));
			
			if (Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN && (
				(a[0] < 0 || a[1] < 0 || a[0] > screenWidth || a[1] > screenHeight)
			  &&(b[0] < 0 || b[1] < 0 || b[0] > screenWidth || b[1] > screenHeight)
			  &&(c[0] < 0 || c[1] < 0 || c[0] > screenWidth || c[1] > screenHeight)))
				continue;
			if(a[2]==0||b[2]==0||c[2]==0)
			{
				continue;
			}
			
			coordsCacheABC[0][0] = a;
			coordsCacheABC[0][1] = b;
			coordsCacheABC[0][2] = c;
			
			coordsCacheABC = optimizeCoordinates2D(coordsCacheABC);
			
			a = coordsCacheABC[0][0];
			b = coordsCacheABC[0][1];
			c = coordsCacheABC[0][2];
			
//			System.out.println("triangleI : "+triangleI+" , a: "+Arrays.toString(a)+" , b: "+Arrays.toString(b)+" , c: "+Arrays.toString(c));
			
			bc[0] = c[0]-b[0];
			bc[1] = c[1]-b[1];
			bc[2] = c[2]-b[2];
			
			ab[0] = b[0]-a[0];
			ab[1] = b[1]-a[1];
			ab[2] = b[2]-a[2];
			
			ac[0] = c[0]-a[0];
			ac[1] = c[1]-a[1];
			ac[2] = c[2]-a[2];
		
			
			vecNormal[0] = (bc[1] * ab[2]) - (bc[2] * ab[1]);
			vecNormal[1] = (bc[2] * ab[0]) - (bc[0] * ab[2]);
			vecNormal[2] = (bc[0] * ab[1]) - (bc[1] * ab[0]);
			
			cPlane = -((a[0]*vecNormal[0])+(a[1]*vecNormal[1])+(a[2]*vecNormal[2]));
			
			ab0 = vectorUnify2D(ab,true);
			
			abLength = length(ab);
			
			// lambdaAB gibt die Stelle auf der Geraden AB*lambda + A an, bei
			// der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
			lambdaAB = (ab0[0] * (c[0] - a[0])
				   	  + ab0[1] * (c[1] - a[1]))
					/ (Math.pow(ab0[0], 2) + Math.pow(ab0[1], 2));
			// check boundaries of lambdaAB
//			System.out.println("lambdaAB: "+lambdaAB);
			if (lambdaAB > abLength || lambdaAB < 0)
			{
				System.out.println("skipped: lambdaAB: "+lambdaAB+", abLength: "+abLength);
				continue;
			}
			// Vektor O (Einheitsvektor) (steht senkrecht auf AB und geht durch
			// (bzw. bis) C)
			o[0] = c[0] - (a[0] + ab0[0] * lambdaAB);
			o[1] = c[1] - (a[1] + ab0[1] * lambdaAB);
					
			oLength = vectorUnify2D(o);
//			System.out.println("o: "+Arrays.toString(o));
			if(Double.isNaN(o[0]))
				continue;
			collidingWithAC = true;
//			if(triangleI == 0)
			for (double lambdaABCrawler = 0; lambdaABCrawler < abLength + precision; lambdaABCrawler += precision) {

				// check if lambdaVertical should be calculated by collision
				// with BC
				if (collidingWithAC && lambdaABCrawler >= lambdaAB)
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
						continue;
					} else {
						lambda2Max = (lambdaABCrawler * ((ab0[1] * ac[0]) - (ab0[0] * ac[1])))
								/ cacheLambda2Divisor;
//						System.out.println("lambda2Max: "+lambda2Max);
					}

				} else {

					cacheLambda2Divisor = (o[1] * bc[0] - o[0] * bc[1]);
					if (cacheLambda2Divisor == 0) {
						continue;
					} else {
						lambda2Max = (lambdaABCrawler * ((ab0[0] * bc[1]) - (ab0[1] * bc[0]))
								- ab[0] * bc[1] + ab[1] * bc[0]) / cacheLambda2Divisor;
//						System.out.println("lambda2Max: "+lambda2Max);
					}
					
					if(lambda2Max > 10000)
						continue;
					if (!Double.isFinite(lambda2Max) || lambda2Max > oLength)	
						break;	
				}

				//////////// 2. CRAWL THROUGH lambda2
				for (double lambda2 = 0; lambda2 < lambda2Max + precision; lambda2 += precision) {
					// calculate the point (=> 2D into coordsIntCache) and
					// return its depth
//					depth = calcR3Point(
//							new double[] { lambda2 * o[0] + coords[triangleI][0][0] + lambdaABCrawler * ab0[0],
//									lambda2 * o[1] + coords[triangleI][0][1] + lambdaABCrawler * ab0[1],
//									lambda2 * o[2] + coords[triangleI][0][2] + lambdaABCrawler * ab0[2] },
//							coordsIntCache, forward, camPos, alpha, beta, factor);
//
//					// check if the 2d point is in the screens boundaries and if
//					// its depth is smaller that the one noted in the buffer
//					if (coordsIntCache[0] > 0 
//						&& coordsIntCache[1] > 0 
//						&& coordsIntCache[0] < screenWidth
//						&& coordsIntCache[1] < screenHeight
//						&& (bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] > depth
//						|| bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] == 0)) {
//						bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] = depth;
//
//						// set color
//						bufferDepth[coordsIntCache[0]][coordsIntCache[1]][1] = (int) coords[triangleI][3][0];
//					}
					pixelX1Cache = ((lambda2 * o[0] + a[0] + lambdaABCrawler * ab0[0]));
//					System.out.println("pixelX1Cache : "+pixelX1Cache);
					pixelX2Cache = ((lambda2 * o[1] + a[1] + lambdaABCrawler * ab0[1]));
//					System.out.println("pixelX2Cache : "+pixelX2Cache);
					depth = 
							((pixelX1Cache*vecNormal[0])+(pixelX2Cache*vecNormal[1])+cPlane)
								/
							(vecNormal[2]);
//					System.out.println("depth: "+depth);
					pixelX1CacheInt = (int) pixelX1Cache;
					pixelX2CacheInt = (int) pixelX2Cache;
					
					if (pixelX1Cache > 0 
						&& pixelX2Cache > 0 
						&& pixelX1Cache < screenWidth
						&& pixelX2Cache < screenHeight
						&& (bufferDepth[pixelX1CacheInt][pixelX2CacheInt][0] > depth
						 || bufferDepth[pixelX1CacheInt][pixelX2CacheInt][0] == 0)) {
							bufferDepth[pixelX1CacheInt][pixelX2CacheInt][0] = depth;
	
							// set color
							bufferDepth[pixelX1CacheInt][pixelX2CacheInt][1] = (int) coords[triangleI][3][0];
						}
					
				}
			}	
		
	}
	return bufferDepth;  
}
	
	public double[][][] calcR3ZBuff2DRasterization(ArrayList<GameObject> gameObjects, Camera camera, boolean createNewBuffer)
	{
		this.updateValues();

		// Extract constants from camera
		// Extract constants from camera
		forward = camera.forward;
		alpha = camera.alpha;
		beta = camera.beta;
		factor = camera.scaleFactor;
		camPos[0] = camera.pos[0];
		camPos[1] = camera.pos[1];
		camPos[2] = camera.pos[2];
		
		// System.out.println(Arrays.toString(forward));

		double[][][] bufferDepth;
		if (createNewBuffer)
			bufferDepth = new double[screenWidth][screenHeight][2];
		else
			bufferDepth = ThreadProcessor.getBufferToCalculateOn();

		
		GameObject gameObject;
		double[][][] coords;
		for(int gameObjectI = gameObjects.size()-1; gameObjectI >= 0; gameObjectI--) {
//			System.out.println(gameObjects.size());
			gameObject = gameObjects.get(gameObjectI);
			if(gameObject == null||gameObject.isRemoved())
			{
//				System.out.println(gameObjects.size());
//				System.out.println("to remove");
				gameObjects.remove(gameObjectI);
//				Game.getGame().gameObjects.remove(gameObjectI);
//				System.out.println(gameObjects.size());
//				System.out.println("------");
				continue;
			}
			coords = gameObject.getTrianglesAbsolute();
			
			
			for (int triangleI = 0; triangleI < coords.length; triangleI++) {
				
				// check if no color is given
				if (coords[triangleI].length < 4 || coords[triangleI][3][0] == -1) {
//					throw new RuntimeException("Diiga");
					calcR3Mesh(bufferDepth, coords[triangleI], forward, camPos, alpha, beta, factor);
					continue;
				}
				// System.out.println("t " + triangleI);
				// a = [0], b = [1], c = [2]
				// Vektor AB
//				ab0 = new double[] { coords[triangleI][1][0] - coords[triangleI][0][0],
//						coords[triangleI][1][1] - coords[triangleI][0][1],
//						coords[triangleI][1][2] - coords[triangleI][0][2] };
//				double abLength = length(ab0);
//				double[] ab = ab0;
//				ab0 = new double[] { ab0[0] / abLength, ab0[1] / abLength, ab0[2] / abLength };
//				// Vektor AC
//				ac = new double[] { coords[triangleI][2][0] - coords[triangleI][0][0],
//						coords[triangleI][2][1] - coords[triangleI][0][1],
//						coords[triangleI][2][2] - coords[triangleI][0][2] };
//				double acLength = length(ac);
//				ac = new double[] { ac[0] / acLength, ac[1] / acLength, ac[2] / acLength };
	//
//				// Vektor BC
//				bc = new double[] { coords[triangleI][2][0] - coords[triangleI][1][0],
//						coords[triangleI][2][1] - coords[triangleI][1][1],
//						coords[triangleI][2][2] - coords[triangleI][1][2] };
//				// bcLength = length(bc);
				
//				middle = new double[] { (coords[triangleI][0][0] + coords[triangleI][1][0] + coords[triangleI][2][0]) / 3,
//						(coords[triangleI][0][1] + coords[triangleI][1][1] + coords[triangleI][2][1]) / 3,
//						(coords[triangleI][0][2] + coords[triangleI][1][2] + coords[triangleI][2][2]) / 3 };
	//
//				lengthMiddle = calcR3Point(middle, coordsIntCache, forward, camPos, alpha, beta, factor); // dont
																											// calculate
																											// entire
																											// point
																											// with
																											// everything,
																											// but
																											// only
																											// its
																											// depth
				
//				if (Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN && (coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth
//						|| coordsIntCache[1] > screenHeight))
//					continue;

//				if (lengthMiddle == 0)
//					continue;


						//				System.out.println(".");																					
//				if(!Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN)	 											
//				{		
//					insideScreenAmount = 0;
//					for(int pointsTriangle = 0;pointsTriangle < 3; pointsTriangle++)
//					{
//						lengthCache = calcR3Point(coords[triangleI][pointsTriangle], coordsIntCache, forward, camPos, alpha, beta, factor);
//						
//						if ((coordsIntCache[0] < 0 || coordsIntCache[1] < 0 || coordsIntCache[0] > screenWidth || coordsIntCache[1] > screenHeight) || lengthCache == 0)
//							insideScreenAmount--;
//					}
//					if(insideScreenAmount==-3)
//					System.out.println("cont");
//				}
				// precision = 0.001+Math.pow(1.00146, lengthMiddle)-1;
				// precision = 0.0058*lengthMiddle+0.001;
//				if(Main.lowMode==0) {
//					precision = 0.00038 * lengthMiddle + 0.001;
//				} else {
////					precision = 0.09/(1.6*Main.lowMode) * lengthMiddle + 0.001;
//					precision = 0.0035 * lengthMiddle ;
//				}
				a[2] = -calcR3PointExact(coords[triangleI][0], coordsDoubleCache, forward, camPos, alpha, beta, factor);
				a[0] = coordsDoubleCache[0];
				a[1] = coordsDoubleCache[1];
				
				b[2] = -calcR3PointExact(coords[triangleI][1], coordsDoubleCache, forward, camPos, alpha, beta, factor);
				b[0] = coordsDoubleCache[0];
				b[1] = coordsDoubleCache[1];
				
				c[2] = -calcR3PointExact(coords[triangleI][2], coordsDoubleCache, forward, camPos, alpha, beta, factor);
				c[0] = coordsDoubleCache[0];
				c[1] = coordsDoubleCache[1];
//				System.out.println("triangleI : "+triangleI+" , a: "+Arrays.toString(a)+" , b: "+Arrays.toString(b)+" , c: "+Arrays.toString(c));
				
				if (Game.SKIP_TRIANGLE_IF_MIDDLE_IS_OFFSCREEN && (
					(a[0] < 0 || a[1] < 0 || a[0] > screenWidth || a[1] > screenHeight)
				  &&(b[0] < 0 || b[1] < 0 || b[0] > screenWidth || b[1] > screenHeight)
				  &&(c[0] < 0 || c[1] < 0 || c[0] > screenWidth || c[1] > screenHeight)))
					continue;
				if(a[2]==0||b[2]==0||c[2]==0)
				{
					continue;
				}
				
				coordsCacheABC[0][0] = a;
				coordsCacheABC[0][1] = b;
				coordsCacheABC[0][2] = c;
				
				coordsCacheABC = optimizeCoordinates2D(coordsCacheABC);
				
				a = coordsCacheABC[0][0];
				b = coordsCacheABC[0][1];
				c = coordsCacheABC[0][2];
				
//				System.out.println("triangleI : "+triangleI+" , a: "+Arrays.toString(a)+" , b: "+Arrays.toString(b)+" , c: "+Arrays.toString(c));
				
				bc[0] = c[0]-b[0];
				bc[1] = c[1]-b[1];
				bc[2] = c[2]-b[2];
				
				ab[0] = b[0]-a[0];
				ab[1] = b[1]-a[1];
				ab[2] = b[2]-a[2];
				
				ac[0] = c[0]-a[0];
				ac[1] = c[1]-a[1];
				ac[2] = c[2]-a[2];
			
				
				vecNormal[0] = (bc[1] * ab[2]) - (bc[2] * ab[1]);
				vecNormal[1] = (bc[2] * ab[0]) - (bc[0] * ab[2]);
				vecNormal[2] = (bc[0] * ab[1]) - (bc[1] * ab[0]);
				
				cPlane = -((a[0]*vecNormal[0])+(a[1]*vecNormal[1])+(a[2]*vecNormal[2]));
				
				ab0 = vectorUnify2D(ab,true);
				
				abLength = length(ab);
				
				// lambdaAB gibt die Stelle auf der Geraden AB*lambda + A an, bei
				// der ab senkrecht zu o (o = Stelle auf der Gerade -> C)
				lambdaAB = (ab0[0] * (c[0] - a[0])
					   	  + ab0[1] * (c[1] - a[1]))
						/ (Math.pow(ab0[0], 2) + Math.pow(ab0[1], 2));
				// check boundaries of lambdaAB
//				System.out.println("lambdaAB: "+lambdaAB);
				if (lambdaAB > abLength || lambdaAB < 0)
				{
					System.out.println("skipped: lambdaAB: "+lambdaAB+", abLength: "+abLength);
					continue;
				}
				// Vektor O (Einheitsvektor) (steht senkrecht auf AB und geht durch
				// (bzw. bis) C)
				o[0] = c[0] - (a[0] + ab0[0] * lambdaAB);
				o[1] = c[1] - (a[1] + ab0[1] * lambdaAB);
						
				oLength = vectorUnify2D(o);
//				System.out.println("o: "+Arrays.toString(o));
				if(Double.isNaN(o[0]))
					continue;
				collidingWithAC = true;
//				if(triangleI == 0)
				for (double lambdaABCrawler = 0; lambdaABCrawler < abLength + precision; lambdaABCrawler += precision) {

					// check if lambdaVertical should be calculated by collision
					// with BC
					if (collidingWithAC && lambdaABCrawler >= lambdaAB)
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
							continue;
						} else {
							lambda2Max = (lambdaABCrawler * ((ab0[1] * ac[0]) - (ab0[0] * ac[1])))
									/ cacheLambda2Divisor;
//							System.out.println("lambda2Max: "+lambda2Max);
						}

					} else {

						cacheLambda2Divisor = (o[1] * bc[0] - o[0] * bc[1]);
						if (cacheLambda2Divisor == 0) {
							continue;
						} else {
							lambda2Max = (lambdaABCrawler * ((ab0[0] * bc[1]) - (ab0[1] * bc[0]))
									- ab[0] * bc[1] + ab[1] * bc[0]) / cacheLambda2Divisor;
//							System.out.println("lambda2Max: "+lambda2Max);
						}
						
						if(lambda2Max > 10000)
							continue;
						if (!Double.isFinite(lambda2Max) || lambda2Max > oLength)	
							break;	
					}

					//////////// 2. CRAWL THROUGH lambda2
					for (double lambda2 = 0; lambda2 < lambda2Max + precision; lambda2 += precision) {
						// calculate the point (=> 2D into coordsIntCache) and
						// return its depth
//						depth = calcR3Point(
//								new double[] { lambda2 * o[0] + coords[triangleI][0][0] + lambdaABCrawler * ab0[0],
//										lambda2 * o[1] + coords[triangleI][0][1] + lambdaABCrawler * ab0[1],
//										lambda2 * o[2] + coords[triangleI][0][2] + lambdaABCrawler * ab0[2] },
//								coordsIntCache, forward, camPos, alpha, beta, factor);
	//
//						// check if the 2d point is in the screens boundaries and if
//						// its depth is smaller that the one noted in the buffer
//						if (coordsIntCache[0] > 0 
//							&& coordsIntCache[1] > 0 
//							&& coordsIntCache[0] < screenWidth
//							&& coordsIntCache[1] < screenHeight
//							&& (bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] > depth
//							|| bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] == 0)) {
//							bufferDepth[coordsIntCache[0]][coordsIntCache[1]][0] = depth;
	//
//							// set color
//							bufferDepth[coordsIntCache[0]][coordsIntCache[1]][1] = (int) coords[triangleI][3][0];
//						}
						pixelX1Cache = ((lambda2 * o[0] + a[0] + lambdaABCrawler * ab0[0]));
//						System.out.println("pixelX1Cache : "+pixelX1Cache);
						pixelX2Cache = ((lambda2 * o[1] + a[1] + lambdaABCrawler * ab0[1]));
//						System.out.println("pixelX2Cache : "+pixelX2Cache);
						depth = 
								((pixelX1Cache*vecNormal[0])+(pixelX2Cache*vecNormal[1])+cPlane)
									/
								(vecNormal[2]);
//						System.out.println("depth: "+depth);
						pixelX1CacheInt = (int) pixelX1Cache;
						pixelX2CacheInt = (int) pixelX2Cache;
						
						if (pixelX1Cache > 0 
							&& pixelX2Cache > 0 
							&& pixelX1Cache < screenWidth
							&& pixelX2Cache < screenHeight
							&& (bufferDepth[pixelX1CacheInt][pixelX2CacheInt][0] > depth
							 || bufferDepth[pixelX1CacheInt][pixelX2CacheInt][0] == 0)) {
								bufferDepth[pixelX1CacheInt][pixelX2CacheInt][0] = depth;
		
								// set color
								bufferDepth[pixelX1CacheInt][pixelX2CacheInt][1] = (int) coords[triangleI][3][0];
							}
						
					}
				}	
			}
		}
		return bufferDepth;  
	}
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
					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][1] = Main.storeColor(Color.black.getRGB());;
					
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
					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][1] = Main.storeColor(Color.black.getRGB());;			
				
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
					bufferDepth[cachePoint2D[0]][cachePoint2D[1]][1] = Main.storeColor(Color.black.getRGB());
				
			}
		}
		
//		throw new RuntimeException("Ay");
	}
	
	private static double[] ab0OptimizeCoordinates = new double[3];
	/**
	 * optimizes the passed coordinates. Overwrites the given and array AND returns it for convenience
	 * @param coords
	 * @return
	 */
	public static double[][][] optimizeCoordinates(double[][][] coords) {
		
//		double[] ab0;	// vector ab, unit vector		
		double lambda;	// ab0 * lambda gives the point, on which the point of C sits in a 90° angle on
		
//		double[] ac0;	// vector ac
		
		
		double abLength;
		double[] resortCache;
		
		for(int triangleI = 0;triangleI < coords.length;triangleI++) {
			
			//calculate AB (unit)
			ab0OptimizeCoordinates [0] = coords[triangleI][1][0]-coords[triangleI][0][0];
			ab0OptimizeCoordinates [1]	= coords[triangleI][1][1]-coords[triangleI][0][1];
			ab0OptimizeCoordinates [2]	= coords[triangleI][1][2]-coords[triangleI][0][2];			
			abLength = Mathstuff.vectorUnify(ab0OptimizeCoordinates);
			
			//Vektor AC
//			ac0 = Mathstuff.vectorUnify(new double[] {coords[triangleI][2][0]-coords[triangleI][0][0],coords[triangleI][2][1]-coords[triangleI][0][1],coords[triangleI][2][2]-coords[triangleI][0][2]}, false);

			lambda = 
			(ab0OptimizeCoordinates[0]*(coords[triangleI][2][0]-coords[triangleI][0][0])+ab0OptimizeCoordinates[1]*(coords[triangleI][2][1]-coords[triangleI][0][1])+ab0OptimizeCoordinates[2]*(coords[triangleI][2][2]-coords[triangleI][0][2]))
							/
			(Math.pow(ab0OptimizeCoordinates[0], 2)+Math.pow(ab0OptimizeCoordinates[1], 2)+Math.pow(ab0OptimizeCoordinates[2], 2));
			
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
		return coords;
	}
	/**
	 * optimizes the passed coordinates. Overwrites the given and array AND returns it for convenience
	 * @param coords
	 * @return
	 */
	public static double[][][] optimizeCoordinates2D(double[][][] coords) {
		
//		double[] ab0;	// vector ab, unit vector		
		double lambda;	// ab0 * lambda gives the point, on which the point of C sits in a 90° angle on
		
//		double[] ac0;	// vector ac
		
		
		double abLength;
		double[] resortCache;
		
		for(int triangleI = 0;triangleI < coords.length;triangleI++) {
			
			//calculate AB (unit)
			ab0OptimizeCoordinates [0] = coords[triangleI][1][0]-coords[triangleI][0][0];
			ab0OptimizeCoordinates [1]	= coords[triangleI][1][1]-coords[triangleI][0][1];		
			abLength = Mathstuff.vectorUnify2D(ab0OptimizeCoordinates);
			
			//Vektor AC
//			ac0 = Mathstuff.vectorUnify(new double[] {coords[triangleI][2][0]-coords[triangleI][0][0],coords[triangleI][2][1]-coords[triangleI][0][1],coords[triangleI][2][2]-coords[triangleI][0][2]}, false);

			lambda = 
			(ab0OptimizeCoordinates[0]*(coords[triangleI][2][0]-coords[triangleI][0][0])+ab0OptimizeCoordinates[1]*(coords[triangleI][2][1]-coords[triangleI][0][1]))
							/
			(Math.pow(ab0OptimizeCoordinates[0], 2)+Math.pow(ab0OptimizeCoordinates[1], 2));
			
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
		return coords;
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
			if(!Game.modification)
			{
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

		double len = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
		target[0] = vector[0] / len;
		target[1] = vector[1] / len;
		target[2] = vector[2] / len;
		return target;
	}
	/**
	 * unifies passed vector; overwrites vector values, if ~createNewVector~ is
	 * false!
	 * 
	 * @return returns unified vector
	 */
	public static double[] vectorUnify2D(double[] vector, boolean createNewVector) {
		double[] target;
		if (createNewVector)
			target = new double[vector.length];
		else
			target = vector;

		double len = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
		target[0] = vector[0] / len;
		target[1] = vector[1] / len;
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
		double len = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
		vector[0] = vector[0] / len;
		vector[1] = vector[1] / len;
		vector[2] = vector[2] / len;
		return len;
	}
	/**
	 * unfies passed vector<br>
	 * NOTE: if overwriting values of this vector is not wanted please refer to
	 * {@link #vectorUnify(double[], boolean)}
	 * 
	 * @return returns length of initial vector
	 */
	public static double vectorUnify2D(double[] vector) {
		double len = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
		vector[0] = vector[0] / len;
		vector[1] = vector[1] / len;
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
	public static GameObject generateCube(double[] centerPos, double edgeLength, double colorID, boolean gravityAffected) {
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
		return new GameObject(centerPos, triangles, new Hitbox(halfEdgeLength), gravityAffected, true);
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
