package core.acp;

public class Tresholding {
    public static double hardTresholding(double lambda, double alpha) {
        return (Math.abs(alpha) > lambda ? alpha : 0);
    }
    public static double softTresholding(double lambda, double alpha) {
        return (alpha > lambda ? alpha - lambda : (alpha >= -lambda ? 0 : alpha + lambda));
    }
     
}
