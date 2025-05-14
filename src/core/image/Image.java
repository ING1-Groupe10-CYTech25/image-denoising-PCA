package core.image;
//Importation des Librairies Java utiles
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

//Implémentation de la classe Image
public class Image{
	private BufferedImage img;
	private WritableRaster raster;
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	protected void setRaster(WritableRaster raster) {
		this.raster = raster;
	}
	public BufferedImage getImage() {
		return this.img;
	}
	public WritableRaster getRaster() {
		return this.raster;
	}
	public Image(BufferedImage img) {
		this.setImage(img);
		this.setRaster(this.getImage().getRaster());
	}
	public Image() {
		this.setImage(null);
		this.setRaster(null);
	}
	public int getWidth() {
		return this.getImage().getWidth();
	}
	public int getHeight() {
		return this.getImage().getHeight();
	}
	public int getPixel(int x, int y) {
		try {
			if (x < this.getWidth() && y < this.getHeight()) {
				return (int) this.getRaster().getSample(x, y, 0);
			}
			else {
				throw(new ImageException());
			}
		}
		catch (ImageException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public void setPixel(int x, int y, int grey) {
		try {
			if (x < this.getWidth() && y < this.getHeight()) {
				grey = (grey > 255 ? 255 : (grey < 0 ? 0 : grey)); // restreint la valeur aux entiers entre 0 et 255
			this.getRaster().setSample(x, y, 0, grey);
			}
			else {
				throw(new ImageException());
			}
		}
		catch (ImageException e) {
			e.printStackTrace();
		}
	}
}
