package core.patch;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.image.Image;
import core.image.ImageFile;
import core.image.ImageTile;

/**
 * Classe de test pour les fonctionnalités de gestion des patchs.
 * Cette classe teste les différentes opérations sur les patchs :
 * - Extraction de patchs d'une image
 * - Reconstruction d'image à partir de patchs
 * - Découpage en imagettes et reconstruction
 * - Combinaison des approches imagettes et patchs
 * 
 * @author p-cousin
 * @version 1.1
 * @see Patch
 * @see PatchExtractor
 * @see ImageTile
 */
public class test_patch {

    /**
     * Point d'entrée du programme de test.
     * Effectue plusieurs tests sur les patchs et les imagettes :
     * 1. Extraction de patchs d'une image complète
     * 2. Découpage de l'image en imagettes
     * 3. Extraction de patchs de chaque imagette
     * 4. Reconstruction des imagettes et de l'image finale
     * 
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            // Chargement de l'image de test
            ImageFile img1 = new ImageFile(System.getProperty("user.dir") + "/img/original/1.png");
            // img1.noisify(30);
            // ImageFile img2 = new ImageFile(System.getProperty("user.dir") + "/img/original/1.png");
            // img2.noisify(30);
            // List<Patch> patchList1 = PatchExtractor.extractPatchs(img1, 256);
            // List<Patch> patchList2 = PatchExtractor.extractPatchs(img2, 256);
            // List<Patch> patchListfinal1 = new ArrayList<>();
            // patchListfinal1.add(patchList1.get(0));
            // patchListfinal1.add(patchList2.get(1));
            // patchListfinal1.add(patchList1.get(2));
            // patchListfinal1.add(patchList2.get(3));
            // patchListfinal1.add(patchList1.get(4));
            // patchListfinal1.add(patchList2.get(5));
            // patchListfinal1.add(patchList1.get(6));
            // patchListfinal1.add(patchList2.get(7));
            // patchListfinal1.add(patchList1.get(8));
            // ImageFile result = new ImageFile(PatchExtractor.reconstructPatchs(patchListfinal1, 512, 512));
            // result.saveImage(System.getProperty("user.dir") + "/test.png");

            // Test 1 : Extraction et reconstruction directe avec des patchs
            List<Patch> patches = PatchExtractor.extractPatchs(img1, 100);
            ImageFile result2 = new ImageFile(
                PatchExtractor.reconstructPatchs(patches, img1.getWidth(), img1.getHeight()), 
                "testPatch"
            );

            // Test 2 : Découpage en imagettes puis traitement par patchs
            // Étape 2.1 : Découpage de l'image en 72 imagettes
            List<ImageTile> imagetteList = PatchExtractor.decoupeImage(img1, 72);
            List<ImageTile> reconstructImagetteList = new ArrayList<>();

            // Étape 2.2 : Pour chaque imagette, extraction et reconstruction de patchs
            for (ImageTile imagette : imagetteList) {
                List<Patch> patchList = PatchExtractor.extractPatchs(imagette, 10);
                reconstructImagetteList.add(
                    PatchExtractor.reconstructPatchs(
                        patchList, 
                        imagette.getWidth(), 
                        imagette.getHeight(), 
                        imagette.getPosX(), 
                        imagette.getPosY()
                    )
                );
            }

            // Étape 2.3 : Reconstruction finale de l'image à partir des imagettes
            ImageFile result = new ImageFile(
                PatchExtractor.reconstructImageTiles(
                    reconstructImagetteList, 
                    img1.getWidth(), 
                    img1.getHeight()
                ), 
                "testImagettePatch"
            );

            // Sauvegarde des résultats
            result.saveImage(System.getProperty("user.dir") + "/test.png");
            result2.saveImage(System.getProperty("user.dir") + "/" + result2.getName() + ".png");

        } catch(IOException e) {
            System.err.println("Erreur lors du traitement des images :");
            e.printStackTrace();
        }
    }
}