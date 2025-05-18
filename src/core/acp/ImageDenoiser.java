package core.acp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.image.Image;
import core.image.ImageFile;
import core.patch.Patch;
import core.patch.PatchExtractor;

/**
 * Classe utilitaire pour débruiter une image complète en utilisant
 * la méthode PCA (Analyse en Composantes Principales).
 * 
 * Cette classe coordonne le processus complet de débruitage:
 * 1. Extraction des patchs de l'image
 * 2. Application de l'ACP et du seuillage
 * 3. Reconstruction de l'image débruitée
 * 
 * @version 1.0
 */
public class ImageDenoiser {
    
    // private static final int DEFAULT_PATCH_SIZE = 15;
    private static final double DEFAULT_PATCH_PERCENT = 0.05; // 5% par défaut
    
    /**
     * Calcule la taille de patch adaptative en fonction de la taille de l'image.
     * 
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @param patchPercent Pourcentage de la taille minimale (entre 0 et 1)
     * @return Taille de patch adaptative (impair)
     */
    public static int calculateAdaptivePatchSize(int width, int height, double patchPercent) {
        int minDimension = Math.min(width, height);
        int patchSize = (int) (minDimension * patchPercent);
        
        // S'assurer que la taille est impaire
        if (patchSize % 2 == 0) {
            patchSize++;
        }
        
        // Limiter la taille minimale et maximale
        patchSize = Math.max(5, Math.min(patchSize, 31));
        
        return patchSize;
    }
    
    /**
     * Débruite une image en utilisant la méthode PCA globale.
     * Tous les patchs de l'image sont traités ensemble dans une seule ACP.
     * 
     * @param image image à débruiter
     * @param patchSize taille du côté des patchs (généralement 8 ou 16)
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param shrinkType type de seuillage adaptatif ("v" pour VisuShrink, "b" pour BayesShrink)
     * @param sigma écart type du bruit (si connu, sinon sera estimé)
     * @return image débruitée
     */
    public static Image denoiseGlobal(Image image, int patchSize, String threshold, 
                                    String shrinkType, double sigma) {
        // Extraire les patchs de l'image
        List<Patch> patches = PatchExtractor.extractPatchs(image, patchSize);
        
        if (patches == null || patches.isEmpty()) {
            throw new IllegalStateException("Impossible d'extraire les patchs de l'image");
        }
        
        // Préparer les données pour le débruitage
        int numPatches = patches.size();
        int patchLength = patchSize * patchSize;
        int[][] patchArray = new int[numPatches][patchLength];
        
        // Convertir la liste de Patch en tableau d'entiers
        for (int i = 0; i < numPatches; i++) {
            Patch patch = patches.get(i);
            patchArray[i] = patch.getPixels();
        }
        
        // Appliquer le débruitage PCA global
        int[][] denoisedPatchArray = Denoiser.denoisePatches(
            patchArray, patchSize, threshold, shrinkType, sigma, true);
        
        // Reconvertir les patchs débruités en objets Patch
        List<Patch> denoisedPatches = new ArrayList<>();
        for (int i = 0; i < numPatches; i++) {
            Patch originalPatch = patches.get(i);
            Patch denoisedPatch = new Patch(
                denoisedPatchArray[i],
                originalPatch.getXOrigin(),
                originalPatch.getYOrigin(),
                patchSize
            );
            denoisedPatches.add(denoisedPatch);
        }
        
        // Reconstruire l'image à partir des patchs débruités
        return PatchExtractor.reconstructPatchs(denoisedPatches, image.getWidth(), image.getHeight());
    }
    
    /**
     * Débruite une image en utilisant la méthode PCA locale.
     * L'image est divisée en imagettes, et une ACP distincte est appliquée sur chaque imagette.
     * 
     * @param image image à débruiter
     * @param patchSize taille du côté des patchs (généralement 8 ou 16)
     * @param numImagettes nombre approximatif d'imagettes à extraire
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param shrinkType type de seuillage adaptatif ("v" pour VisuShrink, "b" pour BayesShrink)
     * @param sigma écart type du bruit (si connu, sinon sera estimé)
     * @return image débruitée
     */
    public static Image denoiseLocal(Image image, int patchSize, int numImagettes,
                                   String threshold, String shrinkType, double sigma) {
        // 1. Découper l'image en imagettes
        List<core.image.ImageTile> tiles = PatchExtractor.decoupeImage(image, numImagettes);
        
        if (tiles == null || tiles.isEmpty()) {
            throw new IllegalStateException("Impossible de découper l'image en imagettes");
        }
        
        // 2. Pour chaque imagette, calculer la taille de patch adaptative
        List<core.image.ImageTile> denoisedTiles = new ArrayList<>();
        
        for (core.image.ImageTile tile : tiles) {
            // Calculer la taille de patch adaptative pour cette imagette
            int tilePatchSize = calculateAdaptivePatchSize(tile.getWidth(), tile.getHeight(), 0.05);
            
            // Extraire les patchs de cette imagette
            List<Patch> patches = PatchExtractor.extractPatchs(tile, tilePatchSize);
            
            if (patches == null || patches.isEmpty()) {
                // Si l'imagette est trop petite pour extraire des patchs, la conserver telle quelle
                denoisedTiles.add(tile);
                continue;
            }
            
            // Convertir la liste de Patch en tableau d'entiers
            int numPatches = patches.size();
            int patchLength = tilePatchSize * tilePatchSize;
            int[][] patchArray = new int[numPatches][patchLength];
            
            for (int i = 0; i < numPatches; i++) {
                Patch patch = patches.get(i);
                patchArray[i] = patch.getPixels();
            }
            
            // Appliquer le débruitage PCA local
            int[][] denoisedPatchArray = Denoiser.denoisePatches(
                patchArray, tilePatchSize, threshold, shrinkType, sigma, false);
            
            // Reconvertir les patchs débruités en objets Patch
            List<Patch> denoisedPatches = new ArrayList<>();
            for (int i = 0; i < numPatches; i++) {
                Patch originalPatch = patches.get(i);
                Patch denoisedPatch = new Patch(
                    denoisedPatchArray[i],
                    originalPatch.getXOrigin(),
                    originalPatch.getYOrigin(),
                    tilePatchSize
                );
                denoisedPatches.add(denoisedPatch);
            }
            
            // Reconstruire l'imagette débruitée
            core.image.ImageTile denoisedTile = PatchExtractor.reconstructPatchs(
                denoisedPatches, tile.getWidth(), tile.getHeight(), tile.getPosX(), tile.getPosY());
            
            denoisedTiles.add(denoisedTile);
        }
        
        // 3. Reconstruire l'image complète à partir des imagettes débruitées
        return PatchExtractor.reconstructImageTiles(denoisedTiles, image.getWidth(), image.getHeight());
    }
    
    /**
     * Débruite une image en choisissant automatiquement entre méthode globale et locale
     * en fonction de la taille de l'image.
     * 
     * @param image image à débruiter
     * @param patchSize taille du côté des patchs (généralement 8 ou 16)
     * @param isGlobal true pour forcer la méthode globale, false pour forcer la méthode locale 
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param shrinkType type de seuillage adaptatif ("v" pour VisuShrink, "b" pour BayesShrink)
     * @param sigma écart type du bruit (si connu, sinon sera estimé)
     * @return image débruitée
     */
    public static Image denoise(Image image, int patchSize, boolean isGlobal,
                              String threshold, String shrinkType, double sigma) {
        // Vérification des paramètres
        if (image == null) {
            throw new IllegalArgumentException("L'image ne peut pas être nulle");
        }
        
        if (patchSize <= 0 || patchSize > Math.min(image.getWidth(), image.getHeight())) {
            throw new IllegalArgumentException("Taille de patch invalide");
        }
        
        if (!threshold.equalsIgnoreCase("hard") && !threshold.equalsIgnoreCase("soft")) {
            throw new IllegalArgumentException("Type de seuillage invalide: " + threshold);
        }
        
        if (!shrinkType.equalsIgnoreCase("v") && !shrinkType.equalsIgnoreCase("b")) {
            throw new IllegalArgumentException("Type de seuillage adaptatif invalide: " + shrinkType);
        }
        
        // Choix de la méthode
        if (isGlobal) {
            return denoiseGlobal(image, patchSize, threshold, shrinkType, sigma);
        } else {
            // Pour la méthode locale, on vise environ 8-16 imagettes pour une image standard
            int numImagettes = 16;
            return denoiseLocal(image, patchSize, numImagettes, threshold, shrinkType, sigma);
        }
    }
    
    /**
     * Débruite une image en utilisant la méthode ACP.
     * 
     * @param inputPath Chemin de l'image à débruiter
     * @param outputPath Chemin de l'image débruitée
     * @param isGlobal Utiliser la méthode globale
     * @param threshold Type de seuillage (hard/soft)
     * @param shrink Type de seuillage adaptatif (v/b)
     * @param sigma Écart type du bruit
     * @throws IOException si une erreur survient lors de la lecture/écriture des fichiers
     */
    public static void ImageDen(String inputPath, String outputPath, boolean isGlobal, 
                              String threshold, String shrink, double sigma) throws IOException {
        // Charger l'image
        ImageFile image = new ImageFile(inputPath);
        BufferedImage bufferedImage = image.getImage();
        
        // Calculer la taille de patch adaptative
        int patchSize = calculateAdaptivePatchSize(bufferedImage.getWidth(), 
                                                 bufferedImage.getHeight(), 
                                                 DEFAULT_PATCH_PERCENT);
        
        // Débruiter l'image
        Image denoised;
        if (isGlobal) {
            denoised = denoiseGlobal(image, patchSize, threshold, shrink, sigma);
        } else {
            int numImagettes = 16;
            denoised = denoiseLocal(image, patchSize, numImagettes, threshold, shrink, sigma);
        }
        
        // Sauvegarder l'image débruitée
        ImageFile denoisedFile = new ImageFile(denoised, "denoised");
        denoisedFile.saveImage(outputPath);
    }

    /**
     * Débruite une image en utilisant la méthode ACP avec une taille de patch spécifiée.
     * 
     * @param inputPath Chemin de l'image à débruiter
     * @param outputPath Chemin de l'image débruitée
     * @param isGlobal Utiliser la méthode globale
     * @param threshold Type de seuillage (hard/soft)
     * @param shrink Type de seuillage adaptatif (v/b)
     * @param sigma Écart type du bruit
     * @param patchPercent Pourcentage de la taille minimale pour le patch (entre 0 et 1)
     * @throws IOException si une erreur survient lors de la lecture/écriture des fichiers
     */
    public static void ImageDen(String inputPath, String outputPath, boolean isGlobal, 
                              String threshold, String shrink, double sigma, 
                              double patchPercent) throws IOException {
        // Charger l'image
        ImageFile image = new ImageFile(inputPath);
        BufferedImage bufferedImage = image.getImage();
        
        // Calculer la taille de patch adaptative
        int patchSize = calculateAdaptivePatchSize(bufferedImage.getWidth(), 
                                                 bufferedImage.getHeight(), 
                                                 patchPercent);
        
        // Débruiter l'image
        Image denoised;
        if (isGlobal) {
            denoised = denoiseGlobal(image, patchSize, threshold, shrink, sigma);
        } else {
            int numImagettes = 16;
            denoised = denoiseLocal(image, patchSize, numImagettes, threshold, shrink, sigma);
        }
        
        // Sauvegarder l'image débruitée
        ImageFile denoisedFile = new ImageFile(denoised, "denoised");
        denoisedFile.saveImage(outputPath);
    }
}
