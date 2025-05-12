import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	private BufferedImage img;
	private String name;
	private String ext;
	private String path;
	
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
	
	
	
	public void saveImage(String filePath) {
	    try {
	        File outputFile = new File(filePath);
	        ImageIO.write(this.getImage(), "PNG", outputFile);
	        System.out.println("Image sauvegardée à : " + filePath);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
