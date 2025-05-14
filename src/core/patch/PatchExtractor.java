package core.patch;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;


import core.image.Image;
import core.image.ImageExtract;

public class PatchExtractor {
	public static int minOverlap = 5; // en pixels
	public static List<Patch> extractPatchs(Image img, int side) {
		try {
			if (img.getWidth() < 0 || img.getHeight() < 0) {
				throw(new PatchException());
			}
			// Calcul du nombre de patchs et stride en X
			int minStrideX = side - minOverlap;
			int countX = (int) Math.ceil((double)(img.getWidth() - side) / minStrideX) + 1;
			int strideX = (img.getWidth() - side) / (countX - 1);
		
			// Calcul du nombre de patchs et stride en Y
			int minStrideY = side - minOverlap;
			int countY = (int) Math.ceil((double)(img.getHeight() - side) / minStrideY) + 1;
			int strideY = (img.getHeight() - side) / (countY - 1);
		
			List<Patch> patchList = new ArrayList<>();
		
			for (int j = 0; j < countY; j++) {
				int y = j * strideY;
				for (int i = 0; i < countX; i++) {
					int x = i * strideX;
					x = Math.min(x, img.getWidth() - side);
					y = Math.min(y, img.getHeight() - side);
					int[] pixels = new int[side * side];
					img.getRaster().getPixels(x, y, side, side, pixels);
					patchList.add(new Patch(pixels, x, y, side));
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
	public static Image reconstructPatchs(List<Patch> patchList, int width, int height) {
		try {
			if (patchList.isEmpty()) {
				throw(new PatchException());
			}
			else {
				Image img = new Image(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));
				for(Patch patch : patchList) {
					int x = patch.getXOrigin();
                	int y = patch.getYOrigin();
                	int side = patch.getSide();

					img.getRaster().setPixels(x, y, side, side, patch.getPixels());
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
	public static List<ImageExtract> decoupeImage(Image img, int w, int n) {
		 List<ImageExtract> ImageList = new ArrayList<>();
		 return ImageList;
	}
}