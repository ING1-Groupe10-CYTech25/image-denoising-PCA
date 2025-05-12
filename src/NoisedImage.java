import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class NoisedImage extends Image{
	private BufferedImage img = null;
	private int sigma;
	public NoisedImage(Image img, int sigma) {
		try {
		    this.setImage(img);
		    this.setSigma(sigma);
		    noisify(this.getSigma());
		    super.setName(super.getName() + '_' + sigma);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	public void setSigma(int sigma) {
		this.sigma = sigma;
	}
	public int getSigma() {
		return this.sigma;
	}
	public void noisify(int sigma) {
		int w = this.getImage().getWidth();
		int h = this.getImage().getHeight();
		Random r = new Random();
		for (int y = 0; y < h; y ++) {
			for (int x = 0; x < w; x ++) {
				int gray = (int) (this.getImage().getRaster().getSample(x, y, 0) + r.nextGaussian() * sigma);
				gray = (gray > 255 ? 255 : (gray < 0 ? 0 : gray)); // correction pour eviter overflow
				this.getImage().getRaster().setSample(x, y, 0, gray);
			}
		}
	}
}
