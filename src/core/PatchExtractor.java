package core;
import java.util.ArrayList;
import java.util.List;

public class PatchExtractor {
	public static List<Patch> ExtractPatchs(Image img, int s) {
		int overlap = 5; // en pixels
		int rangeX = img.getWidth() - s;
		int rangeY = img.getHeight() - s;
		List<Patch> patchList = new ArrayList<>();
		return patchList;
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
