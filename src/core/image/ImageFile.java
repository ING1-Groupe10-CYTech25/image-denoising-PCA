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
        if (this.getDir() == null || this.getDir().isEmpty()) {
            return this.getName() + this.getExt();
        } else {
            return this.getDir() + "/" + this.getName() + this.getExt();
        }
    }
    public ImageFile(String filePath) throws IOException {
	    super(ImageIO.read(new File(filePath)));		
		splitFilePath(filePath);
	}
	public ImageFile(Image img) {
		this.setImage(img.getImage());
		this.setRaster(img.getRaster());
		this.setName("unnamed_image");
		this.setExt("png");
		this.setDir(System.getProperty("user.dir") + "/img");
	}
    private void splitFilePath(String filePath) {
        String[] splitPath = filePath.split("[/\\.]");
        this.setExt("." + splitPath[splitPath.length - 1]);
        this.setName(splitPath[splitPath.length - 2]);
        StringBuilder dir = new StringBuilder();
        for(int i = 0; i < splitPath.length - 2; i++) {
            if (i > 0) dir.append("/");
            dir.append(splitPath[i]);
        }
        this.setDir(dir.toString());
    }
    public void saveImage(String path) {
        try {
            File outputFile = new File(path);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            ImageIO.write(this.getImage(), "PNG", outputFile);
            System.out.println("Image sauvegardée à : " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
