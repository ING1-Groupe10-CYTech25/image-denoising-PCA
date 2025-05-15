package core.eval;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Utilitaire pour évaluer la qualité des images en calculant des métriques objectives.
 * 
 * Cette classe fournit des méthodes pour calculer:
 * - MSE (Mean Square Error / Erreur Moyenne Quadratique): mesure la différence moyenne
 *   des carrés des erreurs entre les pixels de deux images.
 * - PSNR (Peak Signal-to-Noise Ratio / Rapport du Pic du Signal sur le Bruit): évalue
 *   la qualité de reconstruction d'une image compressée ou altérée.
 *
 * Plus la valeur MSE est basse, plus les images sont similaires.
 * Plus la valeur PSNR est élevée, meilleure est la qualité de l'image reconstruite.
 *
 * @author Martial-png
 * @version 1.0
 */
public class ImageQualityMetrics {
    
    /**
     * Calcule l'erreur quadratique moyenne entre deux images.
     * 
     * La méthode parcourt chaque pixel des deux images et calcule la différence
     * quadratique pour chaque composante de couleur (R, G, B).
     * 
     * @param img1 Première image (généralement l'image originale)
     * @param img2 Deuxième image (généralement l'image modifiée)
     * @return La valeur MSE entre les deux images
     * @throws IllegalArgumentException si les dimensions des images sont différentes
     */
    public static double calculateMSE(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        
        // Vérification des dimensions
        if (width != img2.getWidth() || height != img2.getHeight()) {
            throw new IllegalArgumentException("Les images doivent avoir les mêmes dimensions");
        }

        long sumError = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Extraction des composantes RGB de chaque pixel
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                // Décomposition en composantes R, G, B (0-255)
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;

                // Calcul des erreurs quadratiques pour chaque composante
                int errR = r1 - r2;
                int errG = g1 - g2;
                int errB = b1 - b2;

                // Accumulation des erreurs quadratiques
                sumError += errR * errR + errG * errG + errB * errB;
            }
        }

        // Calcul de la moyenne (3 pour les trois composantes R, G, B)
        double mse = (double) sumError / (width * height * 3);
        return mse;
    }

    /**
     * Calcule le PSNR (Peak Signal-to-Noise Ratio) à partir de la MSE.
     * 
     * Le PSNR est exprimé en décibels (dB). Une valeur plus élevée indique
     * une meilleure qualité de l'image reconstruite/modifiée.
     * 
     * @param mse Erreur quadratique moyenne calculée précédemment
     * @param maxPixelValue Valeur maximale possible d'un pixel (typiquement 255 pour 8 bits par pixel)
     * @return La valeur PSNR en décibels
     */
    public static double calculatePSNR(double mse, int maxPixelValue) {
        // Si MSE est 0, les images sont identiques, donc PSNR est infini
        if (mse == 0) return Double.POSITIVE_INFINITY;
        
        // Formule du PSNR: 10 * log10((MAX^2) / MSE)
        return 10 * Math.log10((maxPixelValue * maxPixelValue) / mse);
    }

    /**
     * Méthode principale pour démonstration et tests.
     * 
     * Compare deux images et affiche leur MSE et PSNR.
     * 
     * @param args Arguments non utilisés
     * @throws Exception Si les fichiers d'images ne peuvent pas être lus
     */
    public static void main(String[] args) throws Exception {
        // Chargement des images à comparer
        BufferedImage original = ImageIO.read(new File("original.png"));
        BufferedImage compressed = ImageIO.read(new File("compressed.png"));

        // Vérification que les images ont les mêmes dimensions
        if (original.getWidth() != compressed.getWidth() || original.getHeight() != compressed.getHeight()) {
            System.err.println("Les images doivent être de la même taille.");
            return;
        }

        // Calcul et affichage des métriques
        double mse = calculateMSE(original, compressed);
        double psnr = calculatePSNR(mse, 255); // 255 pour les images 8 bits par canal

        System.out.printf("MSE: %.2f\n", mse);
        System.out.printf("PSNR: %.2f dB\n", psnr);
    }
}
