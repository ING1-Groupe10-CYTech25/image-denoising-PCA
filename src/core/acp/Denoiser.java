package core.acp;

import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Classe pour appliquer le débruitage par PCA en utilisant différentes méthodes de seuillage.
 * Fonctionne en combinaison avec les classes ACP et Tresholding.
 * 
 * @version 1.0
 */
public class Denoiser {
    
    /**
     * Applique un seuillage dur ou doux sur les coefficients projetés (alpha) en utilisant un seuil fixe.
     * 
     * @param alpha matrice des coefficients projetés (taille s² x M)
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param lambda valeur du seuil à appliquer
     * @return matrice des coefficients après seuillage
     */
    public static double[][] applyTresholding(double[][] alpha, String threshold, double lambda) {
        if (alpha == null || alpha.length == 0) {
            throw new IllegalArgumentException("La matrice alpha ne peut pas être vide");
        }
        
        int nComps = alpha.length;
        int nSamples = alpha[0].length;
        
        double[][] thresholdedAlpha = new double[nComps][nSamples];
        
        for (int i = 0; i < nComps; i++) {
            for (int j = 0; j < nSamples; j++) {
                if (threshold.equalsIgnoreCase("hard")) {
                    thresholdedAlpha[i][j] = Tresholding.hardTresholding(lambda, alpha[i][j]);
                } else if (threshold.equalsIgnoreCase("soft")) {
                    thresholdedAlpha[i][j] = Tresholding.softTresholding(lambda, alpha[i][j]);
                } else {
                    throw new IllegalArgumentException("Type de seuillage non reconnu: " + threshold);
                }
            }
        }
        
        return thresholdedAlpha;
    }
    
    /**
     * Applique un seuillage adaptatif en fonction des valeurs propres sur les coefficients projetés.
     * Chaque composante a son propre seuil proportionnel à sa valeur propre.
     * 
     * @param alpha matrice des coefficients projetés (taille s² x M)
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param baseLambda valeur de base du seuil
     * @param eigenValues valeurs propres des composantes
     * @return matrice des coefficients après seuillage
     */
    public static double[][] applyAdaptiveTresholding(double[][] alpha, String threshold, 
                                                     double baseLambda, double[] eigenValues) {
        if (alpha == null || alpha.length == 0 || eigenValues == null || eigenValues.length == 0) {
            throw new IllegalArgumentException("Les matrices alpha et eigenValues ne peuvent pas être vides");
        }
        
        int nComps = alpha.length;
        int nSamples = alpha[0].length;
        
        if (nComps != eigenValues.length) {
            throw new IllegalArgumentException("Le nombre de composantes dans alpha et eigenValues doit être le même");
        }
        
        double[][] thresholdedAlpha = new double[nComps][nSamples];
        double maxEigenValue = Arrays.stream(eigenValues).max().getAsDouble();
        
        for (int i = 0; i < nComps; i++) {
            // Calculer le seuil adaptatif pour cette composante
            double lambda = Tresholding.adaptiveComponentThreshold(
                baseLambda, eigenValues[i], maxEigenValue);
                
            for (int j = 0; j < nSamples; j++) {
                if (threshold.equalsIgnoreCase("hard")) {
                    thresholdedAlpha[i][j] = Tresholding.hardTresholding(lambda, alpha[i][j]);
                } else if (threshold.equalsIgnoreCase("soft")) {
                    thresholdedAlpha[i][j] = Tresholding.softTresholding(lambda, alpha[i][j]);
                } else {
                    throw new IllegalArgumentException("Type de seuillage non reconnu: " + threshold);
                }
            }
        }
        
        return thresholdedAlpha;
    }
    
    /**
     * Débruite des patchs en utilisant l'approche PCA avec seuillage.
     * 
     * @param V matrice des patchs bruitée (s² x M)
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param shrinkType type de calcul du seuil ("v" pour VisuShrink, "b" pour BayesShrink)
     * @param sigma écart type du bruit (si connu, sinon sera estimé)
     * @param isGlobal true pour ACP globale, false pour ACP locale
     * @return matrice des patchs débruités
     */
    public static double[][] denoisePatchesWithPCA(double[][] V, String threshold, 
                                                 String shrinkType, double sigma, boolean isGlobal) {
        // 1. Effectuer l'ACP sur les patchs
        ACP.Triple<double[][], double[], double[][]> acpResult = ACP.ACP(V);
        double[][] vecteursPropres = acpResult.first;    // Vecteurs propres (U)
        double[] valeursPropres = acpResult.second;      // Valeurs propres
        double[][] alpha = acpResult.third;              // Coefficients projetés
        
        // 2. Obtenir le vecteur moyen
        ACP.Triple<double[], double[][], double[][]> moyResult = ACP.MoyCov(V);
        double[] mV = moyResult.first;                   // Vecteur moyen
        
        // 3. Estimer sigma si non fourni
        double sigmaNoise = sigma;
        if (sigmaNoise <= 0) {
            // Utiliser un pourcentage des dernières composantes pour estimer le bruit
            int startIdx = (int) (0.75 * alpha.length);
            sigmaNoise = Tresholding.estimateNoiseFromPCACoefficients(alpha, startIdx);
        }
        
        // 4. Calculer le seuil selon la méthode choisie
        double lambda;
        int totalPixels = V.length * V[0].length;  // Nombre total de pixels
        
        if (shrinkType.equalsIgnoreCase("v")) {
            // VisuShrink
            lambda = Tresholding.calculateVisuShrinkThreshold(sigmaNoise, totalPixels);
        } else if (shrinkType.equalsIgnoreCase("b")) {
            // BayesShrink - calculer la variance des coefficients
            double coeffVariance = calculateVariance(alpha);
            lambda = Tresholding.calculateBayesShrinkThreshold(sigmaNoise, coeffVariance);
        } else {
            throw new IllegalArgumentException("Type de seuillage adaptatif non reconnu: " + shrinkType);
        }
        
        // 5. Appliquer le seuillage sur les coefficients
        double[][] alphaDenoised;
        if (isGlobal) {
            // Pour le débruitage global, on applique un seuil fixe
            alphaDenoised = applyTresholding(alpha, threshold, lambda);
        } else {
            // Pour le débruitage local, on utilise le seuillage adaptatif basé sur les valeurs propres
            alphaDenoised = applyAdaptiveTresholding(alpha, threshold, lambda, valeursPropres);
        }
        
        // 6. Reconstruire les patchs à partir des coefficients seuillés
        // V_denoised = mV + U * alpha_denoised
        RealMatrix U = new Array2DRowRealMatrix(vecteursPropres);
        RealMatrix alphaDenoisedMat = new Array2DRowRealMatrix(alphaDenoised);
        
        // Multiplication matricielle
        RealMatrix projectionMat = U.multiply(alphaDenoisedMat);
        double[][] projection = projectionMat.getData();
        
        // Ajouter le vecteur moyen à chaque patch
        double[][] V_denoised = new double[V.length][V[0].length];
        for (int i = 0; i < V.length; i++) {
            for (int j = 0; j < V[0].length; j++) {
                V_denoised[i][j] = mV[i] + projection[i][j];
            }
        }
        
        return V_denoised;
    }
    
    /**
     * Débruite directement une collection de patchs représentée comme un tableau d'entiers.
     * Cette méthode convertit les patches en doubles, applique le débruitage PCA, 
     * puis reconvertit le résultat en entiers.
     * 
     * @param patches tableau de patchs bruits (sous forme de tableaux d'entiers)
     * @param patchSize taille du côté d'un patch (ex: 8 pour un patch 8x8)
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param shrinkType type de calcul du seuil ("v" pour VisuShrink, "b" pour BayesShrink)
     * @param sigma écart type du bruit (si connu, sinon sera estimé)
     * @param isGlobal true pour ACP globale, false pour ACP locale
     * @return tableau de patchs débruités (sous forme de tableaux d'entiers)
     */
    public static int[][] denoisePatches(int[][] patches, int patchSize, String threshold,
                                       String shrinkType, double sigma, boolean isGlobal) {
        if (patches == null || patches.length == 0) {
            throw new IllegalArgumentException("Le tableau de patchs ne peut pas être vide");
        }
        
        int numPatches = patches.length;
        int patchLength = patchSize * patchSize;
        
        // Convertir les patchs en une matrice de doubles au format attendu par l'ACP
        // (s² x M) où s² est la taille vectorisée du patch et M le nombre de patchs
        double[][] V = new double[patchLength][numPatches];
        
        for (int i = 0; i < numPatches; i++) {
            for (int j = 0; j < patchLength; j++) {
                V[j][i] = patches[i][j];
            }
        }
        
        // Appliquer le débruitage PCA
        double[][] V_denoised = denoisePatchesWithPCA(V, threshold, shrinkType, sigma, isGlobal);
        
        // Convertir le résultat en tableau d'entiers
        int[][] denoisedPatches = new int[numPatches][patchLength];
        
        for (int i = 0; i < numPatches; i++) {
            for (int j = 0; j < patchLength; j++) {
                // Clamp les valeurs entre 0 et 255 et arrondir
                int pixelValue = (int) Math.round(V_denoised[j][i]);
                denoisedPatches[i][j] = Math.min(255, Math.max(0, pixelValue));
            }
        }
        
        return denoisedPatches;
    }
    
    /**
     * Calcule la variance moyenne des coefficients dans la matrice alpha.
     * 
     * @param alpha matrice des coefficients
     * @return variance moyenne des coefficients
     */
    private static double calculateVariance(double[][] alpha) {
        int nComps = alpha.length;
        int nSamples = alpha[0].length;
        double sum = 0;
        int count = 0;
        
        for (int i = 0; i < nComps; i++) {
            double compMean = Arrays.stream(alpha[i]).average().getAsDouble();
            for (int j = 0; j < nSamples; j++) {
                double diff = alpha[i][j] - compMean;
                sum += diff * diff;
                count++;
            }
        }
        
        return sum / count;
    }
}