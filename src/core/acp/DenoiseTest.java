package core.acp;

import java.awt.image.BufferedImage;
import java.io.IOException;

import core.eval.ImageQualityMetrics;
import core.image.ImageFile;

/**
 * Classe de test pour le débruitage d'image par ACP.
 * Cette classe effectue des tests sur différentes configurations de débruitage
 * et évalue leurs performances en utilisant les métriques MSE et PSNR.
 * 
 * @version 1.0
 */
public class DenoiseTest {
    
    /**
     * Méthode principale pour tester le débruitage PCA.
     * 
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            // Définir le chemin des images
            String basePath = System.getProperty("user.dir") + "/img/";
            String originalImage = basePath + "original/lena.png";
            String noisedImage = basePath + "img_noised/lena_noised_30.png";
            String outputDir = basePath + "img_denoised/";
            
            // Paramètres de débruitage
            int patchSize = 15;
            double sigma = 30; // Écart type du bruit
            
            // Tester toutes les combinaisons de méthodes
            testAllConfigurations(originalImage, noisedImage, outputDir, patchSize, sigma);
            
        } catch (IOException e) {
            System.err.println("Erreur lors du test de débruitage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Teste toutes les configurations de débruitage et affiche les résultats.
     * 
     * @param originalPath chemin de l'image originale (non bruitée)
     * @param noisedPath chemin de l'image bruitée
     * @param outputDir répertoire où sauvegarder les images débruitées
     * @param patchSize taille des patchs
     * @param sigma écart type du bruit
     * @throws IOException si une erreur survient lors de la lecture/écriture des fichiers
     */
    private static void testAllConfigurations(String originalPath, String noisedPath, 
                                             String outputDir, int patchSize, double sigma) 
                                             throws IOException {
        // Charger les images originale et bruitée pour comparaison
        ImageFile originalImage = new ImageFile(originalPath);
        ImageFile noisedImage = new ImageFile(noisedPath);
        BufferedImage originalBuffered = originalImage.getImage();
        BufferedImage noisedBuffered = noisedImage.getImage();
        
        // Calculer MSE et PSNR pour l'image bruitée par rapport à l'originale
        double noisedMSE = ImageQualityMetrics.calculateMSE(originalBuffered, noisedBuffered);
        double noisedPSNR = ImageQualityMetrics.calculatePSNR(noisedMSE, 255);
        
        System.out.println("=== Évaluation du débruitage PCA ===");
        System.out.println("Image originale: " + originalPath);
        System.out.println("Image bruitée: " + noisedPath);
        System.out.println("Taille des patchs: " + patchSize + "x" + patchSize);
        System.out.println("Écart type du bruit (sigma): " + sigma);
        System.out.println();
        
        System.out.println("== Image bruitée ==");
        System.out.printf("MSE: %.2f\n", noisedMSE);
        System.out.printf("PSNR: %.2f dB\n", noisedPSNR);
        System.out.println();
        
        // Tester toutes les combinaisons de méthodes
        boolean[] globalOptions = {true, false};
        String[] thresholdOptions = {"hard", "soft"};
        String[] shrinkOptions = {"v", "b"};
        
        for (boolean isGlobal : globalOptions) {
            for (String threshold : thresholdOptions) {
                for (String shrink : shrinkOptions) {
                    // Construire le nom du fichier de sortie
                    String method = isGlobal ? "global" : "local";
                    String outputName = String.format("%slena_denoised_%s_%s_%s.png", 
                                                    outputDir, method, threshold, shrink);
                    
                    // Débruiter l'image
                    System.out.println("Débruitage avec méthode: " + method + 
                                     ", seuillage: " + threshold + 
                                     ", seuillage adaptatif: " + shrink);
                    
                    ImageDenoiser.ImageDen(noisedPath, outputName, isGlobal, threshold, shrink, sigma);
                    System.out.println("Image débruitée sauvegardée dans: " + outputName);
                    
                    // Évaluer le résultat
                    ImageFile denoisedImage = new ImageFile(outputName);
                    BufferedImage denoisedBuffered = denoisedImage.getImage();
                    
                    double denoisedMSE = ImageQualityMetrics.calculateMSE(originalBuffered, denoisedBuffered);
                    double denoisedPSNR = ImageQualityMetrics.calculatePSNR(denoisedMSE, 255);
                    
                    System.out.printf("MSE: %.2f\n", denoisedMSE);
                    System.out.printf("PSNR: %.2f dB\n", denoisedPSNR);
                    System.out.printf("Amélioration MSE: %.2f%%\n", 
                                    100 * (noisedMSE - denoisedMSE) / noisedMSE);
                    System.out.printf("Amélioration PSNR: %.2f dB\n", 
                                    denoisedPSNR - noisedPSNR);
                    System.out.println();
                }
            }
        }
    }
}
