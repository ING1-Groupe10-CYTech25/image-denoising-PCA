import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
//Commentaire
public class NoisedImage extends Image{
	private int sigma;
	public NoisedImage(String filePath, int sigma) {
		super(filePath);
		this.setSigma(sigma);
		noisify(this.getSigma());
		this.setName(this.getName() + '_' + sigma);
	}
	public void setSigma(int sigma) {
		this.sigma = sigma;
	}
	public int getSigma() {
		return this.sigma;
	}
	public void noisify(int sigma) {
		Random r = new Random();
		for (int y = 0; y < this.getHeight(); y ++) {
			for (int x = 0; x < this.getWidth(); x ++) {
				this.setPixel(x, y, (int) (this.getPixel(x,y) + r.nextGaussian() * sigma));
			}
		}
	}
	@Override
	public void saveImage(String path) {
	    super.saveImage((path == null ? path : "img") + "/noised" );
	}
}
