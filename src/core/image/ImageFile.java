package core.image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFile extends Image {
    private String name;
	private String ext;
	private String dir;

    public void setName(String name) {
		this.name = name;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
    public String getName() {
		return this.name;
	}
	public String getExt() {
		return this.ext;
	}
	public String getDir() {
		return this.dir;
	}
    public String getPath() {
		return this.getDir() + "/" + this.getName() + this.getExt();
	}
    public ImageFile(String filePath) throws IOException {
	    super(ImageIO.read(new File(filePath)));		
		splitFilePath(filePath);
	}
    private void splitFilePath(String filePath) {
		String[] splitPath = filePath.split("[/\\.]");
		this.setExt("." + splitPath[splitPath.length - 1]);
		this.setName(splitPath[splitPath.length - 2]);
		String dir = "";
		for(int i = 1; i < splitPath.length - 2; i ++) {
			dir += "/" + splitPath[i];
		}
		this.setDir(dir);
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
