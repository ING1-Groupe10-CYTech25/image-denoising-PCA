package core.image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

/**
 * Simplification de la classe {@link BufferedImage} pour nos besoins (images greyscale)
 * @author p-cousin
 * @version 1.2
 * @see BufferedImage
 */
public class Image{
	private BufferedImage img;			// Image sous forme de BufferedImage
	
	/**
	 * Connstruit une instance de {@code Image} à partir d'une image sous forme de {@link BufferedImage}
	 * @param img image
	 * @see BufferedImage
	 */
	public Image(BufferedImage img) {
		this.setImage(img);
	}

	/**
	 * Construit une instance de {@code Image} vide
	 * Ce constructeur a pour but de permettre le downcast vers la classe {@link ImageFile}
	 */
	public Image() {
		this.setImage(null);
	}
	
	/**
	 * Setter pour la variable d'instance img
	 * @param img
	 */
	public void setImage(BufferedImage img) {
		this.img = img;
	}

	/**
	 * Getter pour la variable d'instance img
	 * @return
	 */
	public BufferedImage getImage() {
		return this.img;
	}

	/**
	 * Raccourci pour la methode getRaster() de la classe {@link BufferedImage}
	 * @return {@link WritableRaster} contenant les données de l'image
	 */
	public WritableRaster getRaster() {
		return this.getImage().getRaster();
	}

	/**
	 * Raccourci pour la methode getWidth() de la classe {@link BufferedImage}
	 * @return largeur de l'image (entier)
	 */
	public int getWidth() {
		return this.getImage().getWidth();
	}

	/**
	 * Raccourci pour la methode getHeight() de la classe {@link BufferedImage}
	 * @return hauteur de l'image (entier)
	 */
	public int getHeight() {
		return this.getImage().getHeight();
	}

	/**
	 * Permet d'obtenir l'intensité du pixel aux coordonnés données en paramètre
	 * @param x abscisse du pixel recherché
	 * @param y ordonnée du pixel recherché
	 * @return intensité du pixel désigné si il existe, -1 sinon.
	 */
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

	/**
	 * change/définis l'intensité du pixel aux coordonnées en paramètre à la valeur de gris en paramètre
	 * @param x abscisse du pixel
	 * @param y	ordonnée du pixel
	 * @param grey valeur de gris à donner au pixel désigné
	 */
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
	/**
	 * Modifie l'image en lui ajoutant un bruit Gaussien paramétré par son écart type
	 * @param sigma écart type du bruit Gaussien
	 */
	public void noisify(int sigma) {
		Random r = new Random();
		for (int y = 0; y < this.getHeight(); y ++) {
			for (int x = 0; x < this.getWidth(); x ++) {
				this.setPixel(x, y, (int) (this.getPixel(x,y) + r.nextGaussian() * sigma));		// ajout du bruit Gaussien. un dépassement de la valeur au dessus de 255 ou end essous de 0 est géré par la methode setPixel
			}
		}
	}
}
