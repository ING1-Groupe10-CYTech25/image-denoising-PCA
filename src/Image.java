import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	private BufferedImage img;
	private String name;
	private String ext;
	private String path;
	private WritableRaster raster;
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public void setPath(String path) {
		this.path = path;
	}
	private void setRaster(WritableRaster raster) {
		this.raster = raster;
	}
	public BufferedImage getImage() {
		return this.img;
	}
	public String getName() {
		return this.name;
	}
	public String getExt() {
		return this.ext;
	}
	public String getPath() {
		return this.path;
	}
	public WritableRaster getRaster() {
		return this.raster;
	}
	public Image(String filePath) {
		try {
		    this.setImage(ImageIO.read(new File(filePath)));		
		} catch (IOException e) {
		    e.printStackTrace();
		}
		this.setRaster(this.getImage().getRaster());
		splitFilePath(filePath);
	}
	public int getWidth() {
		return this.getImage().getWidth();
	}
	public int getHeight() {
		return this.getImage().getHeight();
	}
	public int getPixel(int x, int y) {
		assert(x < this.getWidth() && y < this.getHeight());
		return (int) this.getRaster().getSample(x, y, 0);
	}
	public void setPixel(int x, int y, int grey) {
		assert(x < this.getWidth() && y < this.getHeight());
		grey = (grey > 255 ? 255 : (grey < 0 ? 0 : grey)); // restreint la valeur aux entiers entre 0 et 255
		this.getRaster().setSample(x, y, 0, grey);
	}
	private void splitFilePath(String filePath) {
		String[] splitPath = filePath.split("[/\\.]");
		this.setExt("." + splitPath[splitPath.length - 1]);
		this.setName(splitPath[splitPath.length - 1]);
		String path = "";
		for(int i = 0; i < splitPath.length - 2; i ++) {
			path += splitPath[i];
		}
		this.setPath(path);
	}
	public void saveImage(String path) {
	    try {
	    	String filePath = path + this.getName() + ".png";
	        File outputFile = new File(filePath);
	        ImageIO.write(this.getImage(), "PNG", outputFile);
	        System.out.println("Image sauvegardée à : " + filePath);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
