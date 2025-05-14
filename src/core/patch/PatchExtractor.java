package core.patch;
import java.util.ArrayList;
import java.util.List;

import core.image.Image;
import core.image.ImageExtract;

public class PatchExtractor {
	public static List<Patch> ExtractPatchs(Image img, int s) {
		try {
			int rangeX = img.getWidth() - s;
			int rangeY = img.getHeight() - s;
			if (rangeX < 0 || rangeY < 0) {
				throw(new PatchException());
			}
			int targetOverlap = 5; // en pixels
			int countX = rangeX/(s - targetOverlap) + 1;
			int countY = rangeX/(s - targetOverlap) + 1;
			List<Patch> patchList = new ArrayList<>();
			for (int i = 0; i < countX; i ++) {
				for (int j = 0; i < countY; j ++) {
					//int[] vector = img.getRaster().getPixels((rangeX * i)/countX , (rangeY * j)/countY,s,s,(int[]) null);
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
		Image img = new Image(null);
		return img;
	}
	public static List<ImageExtract> DecoupeImage(Image img, int w, int n) {
		 List<ImageExtract> ImageList = new ArrayList<>();
		 return ImageList;
	}
}