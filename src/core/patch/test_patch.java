package core.patch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.image.ImageFile;

public class test_patch {
    public static void main(String[] args) {
        try {
            ImageFile img1 = new ImageFile(System.getProperty("user.dir") + "/img/original/1.png");
            img1.noisify(30);
            ImageFile img2 = new ImageFile(System.getProperty("user.dir") + "/img/original/1.png");
            img2.noisify(30);
            List<Patch> patchList1 = PatchExtractor.extractPatchs(img1, 256);
            List<Patch> patchList2 = PatchExtractor.extractPatchs(img2, 256);
            List<Patch> patchListfinal1 = new ArrayList<>();
            patchListfinal1.add(patchList1.get(0));
            patchListfinal1.add(patchList2.get(1));
            patchListfinal1.add(patchList1.get(2));
            patchListfinal1.add(patchList2.get(3));
            patchListfinal1.add(patchList1.get(4));
            patchListfinal1.add(patchList2.get(5));
            patchListfinal1.add(patchList1.get(6));
            patchListfinal1.add(patchList2.get(7));
            patchListfinal1.add(patchList1.get(8));
            ImageFile result = new ImageFile(PatchExtractor.reconstructPatchs(patchListfinal1, 512, 512));
            result.saveImage(System.getProperty("user.dir") + "/test.png");


        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
// si je me plante pas, vu que le résultat 
