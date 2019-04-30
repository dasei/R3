package r3.mathstuff;

public class Camera {
	
	public double[] pos = new double[]{0,0,0};
	
	
	// cant be changed, used in Mathstuff calulation	
	public static final double[] forwardDEFAULT = new double[] {1,0,0};
	
	
	//look direction
	public double[] forward = forwardDEFAULT;
	
	//rotation
	public double alpha = 0;
	public double beta = 0;
	
	public final double scaleFactor = 20;
	
}
