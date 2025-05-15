package core.acp;

/**
 * Cette classe permet de calculer et d'appliquer différentes méthodes de seuillage aux vecteurs à débruiter.
 * @author p-cousin
 * @version 1.0
 */
public class Tresholding {
    /**
     * Methode de seuillage dur
     * @param lambda seuil calculé préalablement (>= 0)
     * @param alpha composante a seuiller
     * @return 0 si |{@code alpha}| est inferieur au seuil {@lambda}, {@code alpha} sinon
     */
    public static double hardTresholding(double lambda, double alpha) {
        return (Math.abs(alpha) > lambda ? alpha : 0);
    }

    /**
     * Méthode de seuillage doux
     * @param lambda seuil calculé préalablement (>= 0)
     * @param alpha composante à seuiller
     * @return 0 si |{@code alpha}| est inférieur au seuil {@lambda}, la composante {@code alpha} recalculée sinon
     */
    public static double softTresholding(double lambda, double alpha) {
        return (alpha > lambda ? alpha - lambda : (alpha >= -lambda ? 0 : alpha + lambda));
    }
     
}
