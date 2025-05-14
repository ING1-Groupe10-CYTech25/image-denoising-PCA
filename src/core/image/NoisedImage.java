package core.image;
import java.io.IOException;
// Bruitage d'une image
import java.util.Random;
//Héritage de la classe Image
public class NoisedImage extends ImageFile {
	private int sigma;
	public NoisedImage(String filePath, int sigma) throws IOException{
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
	// Bruitage gaussien ( N(µ = 0, sigma²) )
	public void noisify(int sigma) {
		Random r = new Random();
		for (int y = 0; y < this.getHeight(); y ++) {
			for (int x = 0; x < this.getWidth(); x ++) {
				this.setPixel(x, y, (int) (this.getPixel(x,y) + r.nextGaussian() * sigma));
			}
		}
	}
}