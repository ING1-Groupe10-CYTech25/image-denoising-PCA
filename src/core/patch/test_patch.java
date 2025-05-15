package core.patch;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.image.Image;
import core.image.ImageFile;
import core.image.ImageTile;


public class test_patch {
    public static void main(String[] args) {
        try {
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

            List<ImageTile> tileList = PatchExtractor.decoupeImage(img1, 10);
            Image result1 = new Image(new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_BYTE_GRAY));	// création d'une image vide
            for (ImageTile img : tileList) {
                int[] pixels = new int[img.getWidth() * img.getHeight()];
                img.getRaster().getPixels(0, 0, img.getWidth(), img.getHeight(), pixels);
                result1.getRaster().setPixels(img.getPosX(), img.getPosY(), img.getWidth(), img.getHeight(), pixels);
            }
            ImageFile result = new ImageFile(result1);
            result.saveImage(System.getProperty("user.dir") + "/test.png");


        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}