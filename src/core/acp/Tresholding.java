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

    /**
     * Calcule un seuil universel selon la méthode VisuShrink.
     * Le seuil est calculé en fonction de l'écart type du bruit et de la taille des données.
     * 
     * Formule : lambda = sigma * sqrt(2 * ln(N))
     * où sigma est l'écart type du bruit estimé et N est le nombre de coefficients.
     * 
     * @param sigma estimation de l'écart type du bruit
     * @param N nombre de coefficients (généralement la taille du patch ou nombre total de pixels)
     * @return valeur du seuil calculé
     */
    public static double visuShrink(double sigma, int N) {
        if (sigma <= 0 || N <= 0) {
            throw new IllegalArgumentException("Sigma et N doivent être strictement positifs");
        }
        return sigma * Math.sqrt(2 * Math.log(N));
    }
    
    /**
     * Calcule un seuil adaptatif selon la méthode BayesShrink.
     * Le seuil est calculé en fonction de l'écart type du bruit et de l'écart type du signal.
     * Cette méthode est généralement plus efficace que VisuShrink pour préserver les détails.
     * 
     * Formule : lambda = (sigma^2) / max(sigmaX, 0.001)
     * où sigma est l'écart type du bruit et sigmaX est l'écart type du signal sans bruit.
     * 
     * @param sigma estimation de l'écart type du bruit
     * @param sigmaCoeff écart type des coefficients de la composante
     * @return valeur du seuil calculé
     */
    public static double bayesShrink(double sigma, double sigmaCoeff) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma doit être strictement positif");
        }
        
        // Estimation de l'écart type du signal sans bruit
        double sigmaX = Math.sqrt(Math.max(0, sigmaCoeff * sigmaCoeff - sigma * sigma));
        
        // Éviter la division par zéro
        if (sigmaX < 0.001) {
            sigmaX = 0.001;
        }
        
        return (sigma * sigma) / sigmaX;
    }
    
    /**
     * Estime l'écart type du bruit à partir des coefficients de haute fréquence
     * d'une décomposition (par exemple les composantes principales moins significatives).
     * Cette méthode utilise la médiane des valeurs absolues divisée par 0.6745, ce qui est robuste pour une distribution gaussienne.
     * 
     * @param highFreqCoeffs coefficients de haute fréquence
     * @return estimation de l'écart type du bruit
     */
    public static double estimateNoiseStdDev(double[] highFreqCoeffs) {
        if (highFreqCoeffs == null || highFreqCoeffs.length == 0) {
            throw new IllegalArgumentException("Le tableau de coefficients ne peut pas être vide");
        }
        
        // Copier les valeurs et prendre leur valeur absolue
        double[] absCoeffs = new double[highFreqCoeffs.length];
        for (int i = 0; i < highFreqCoeffs.length; i++) {
            absCoeffs[i] = Math.abs(highFreqCoeffs[i]);
        }
        
        // Calculer la médiane
        java.util.Arrays.sort(absCoeffs);
        double median;
        if (absCoeffs.length % 2 == 0) {
            median = (absCoeffs[absCoeffs.length / 2 - 1] + absCoeffs[absCoeffs.length / 2]) / 2.0;
        } else {
            median = absCoeffs[absCoeffs.length / 2];
        }
        
        // Estimer l'écart type (pour un bruit gaussien)
        return median / 0.6745;
    }
    
    /**
     * Calcule un seuil spécifique pour chaque composante principale en fonction de sa valeur propre.
     * Utilise une approche où les composantes avec des valeurs propres plus élevées (plus d'information) ont des seuils plus bas, et vice versa.
     * 
     * @param lambda valeur de base du seuil (peut être calculée avec visuShrink ou bayesShrink)
     * @param eigenValue valeur propre de la composante
     * @param maxEigenValue valeur propre maximale dans le set de données
     * @return valeur du seuil ajustée pour la composante spécifique
     */
    public static double adaptiveComponentThreshold(double lambda, double eigenValue, double maxEigenValue) {
        if (maxEigenValue <= 0) {
            throw new IllegalArgumentException("La valeur propre maximale doit être strictement positive");
        }
        
        // Ratio de la valeur propre par rapport à la maximale (entre 0 et 1)
        double ratio = eigenValue / maxEigenValue;
        
        // Ajuster le seuil en fonction de l'importance de la composante
        // Plus la composante est importante (ratio proche de 1), plus le seuil est bas
        return lambda * (1.0 - 0.7 * ratio);
    }
    
    /**
     * Calcule un seuil selon la méthode VisuShrink pour les coefficients d'une décomposition PCA. Cette méthode est directement utilisable depuis la classe principale du débruitage PCA.
     * 
     * @param sigma estimation de l'écart type du bruit (peut être connu ou estimé)
     * @param totalPixels nombre total de pixels dans l'image ou dans les patchs
     * @return valeur du seuil universel VisuShrink
     */
    public static double calculateVisuShrinkThreshold(double sigma, int totalPixels) {
        return visuShrink(sigma, totalPixels);
    }
    
    /**
     * Calcule un seuil selon la méthode BayesShrink pour les coefficients d'une décomposition PCA. Cette méthode utilise la variance de l'image bruitée et l'écart type du bruit estimé.
     * 
     * @param sigma estimation de l'écart type du bruit
     * @param coeffVariance variance des coefficients dans l'espace transformé
     * @return valeur du seuil BayesShrink
     */
    public static double calculateBayesShrinkThreshold(double sigma, double coeffVariance) {
        return bayesShrink(sigma, Math.sqrt(coeffVariance));
    }
    
    /**
     * Estimation de l'écart type du bruit à partir d'une image bruitée.
     * Cette méthode peut être utilisée quand le niveau de bruit n'est pas connu à l'avance.
     * Elle utilise les coefficients de haute fréquence (dernières composantes principales).
     * 
     * @param coefficients matrice des coefficients dans l'espace PCA
     * @param startIdx indice à partir duquel on considère les composantes de haute fréquence
     * @return estimation de l'écart type du bruit
     */
    public static double estimateNoiseFromPCACoefficients(double[][] coefficients, int startIdx) {
        if (coefficients == null || coefficients.length == 0 || coefficients[0].length == 0) {
            throw new IllegalArgumentException("Les coefficients ne peuvent pas être vides");
        }
        
        int nComps = coefficients.length;
        int nSamples = coefficients[0].length;
        
        if (startIdx >= nComps) {
            throw new IllegalArgumentException("L'indice de départ doit être inférieur au nombre de composantes");
        }
        
        // Collecter tous les coefficients de haute fréquence
        int nbHighFreqCoeffs = (nComps - startIdx) * nSamples;
        double[] highFreqCoeffs = new double[nbHighFreqCoeffs];
        
        int idx = 0;
        for (int i = startIdx; i < nComps; i++) {
            for (int j = 0; j < nSamples; j++) {
                highFreqCoeffs[idx++] = coefficients[i][j];
            }
        }
        
        return estimateNoiseStdDev(highFreqCoeffs);
    }
}