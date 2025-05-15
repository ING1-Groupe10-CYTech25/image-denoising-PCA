package core.image;
import java.util.Random;

public final class ImageNoiser {
	// Bruitage gaussien ( N(µ = 0, sigma²) )
	public static void noisify(ImageFile img, int sigma) {
		Random r = new Random();
		for (int y = 0; y < img.getHeight(); y ++) {
			for (int x = 0; x < img.getWidth(); x ++) {
				img.setPixel(x, y, (int) (img.getPixel(x,y) + r.nextGaussian() * sigma));
			}
		}
	}
}