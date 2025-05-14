package core.patch;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;


import core.image.Image;
import core.image.ImageExtract;

public class PatchExtractor {
	public static int targetOverlap = 5; // en pixels
	public static List<Patch> ExtractPatchs(Image img, int s) {
		try {
			int rangeX = img.getWidth() - s;
			int rangeY = img.getHeight() - s;
			if (rangeX < 0 || rangeY < 0) {
				throw(new PatchException());
			}
			int countX = rangeX/(s - targetOverlap) + 1;
			int countY = rangeX/(s - targetOverlap) + 1;
			List<Patch> patchList = new ArrayList<>();
			for (int i = 0; i < countX; i ++) {
				for (int j = 0; i < countY; j ++) {
					patchList.add(new Patch(img.getRaster().getPixels((rangeX * i)/countX , (rangeY * j)/countY,s,s,(int[]) null),(rangeX * i)/countX , (rangeY * j)/countY, s));
				}
			}
			return patchList;
		}
		catch(PatchException e) {
			System.err.println("Patches cannot be larger than the image");
			e.printStackTrace();
			return null;
		}
	}
	public static Image ReconstructPatchs(List<Patch> patchList, int l, int c) {
		try {
			if (patchList.isEmpty()) {
				throw(new PatchException());
			}
			else {
				Image img = new Image(new BufferedImage(l, c, 10));
				// determination du nombre de patchs en longueur et en largeur
				for(Patch patch : patchList) {
					img.getRaster().setPixels(patch.getXOrigin(),patch.getYOrigin(),patch.getSide(), patch.getSide(), patch.getPixels());
				}
				return img;
			}
		}
		catch (PatchException e) {
			System.err.println("Patch list is empty");
			e.printStackTrace();
			return null;
		}
	}
	public static List<ImageExtract> DecoupeImage(Image img, int w, int n) {
		 List<ImageExtract> ImageList = new ArrayList<>();
		 return ImageList;
	}
}