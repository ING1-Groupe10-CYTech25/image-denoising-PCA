package gui.model;

import javafx.scene.image.Image;


public class ImagePair {
    
    private String lPath;
    private String rPath;
    private Image lImage;
    private Image rImage;
    private double zoom;
    private int initWidth;
    private int initHeight;

    public ImagePair(String lPath, String rPath) {
        this.setLPath(lPath);
        this.setRPath(rPath);
        this.setLImage(new Image("file:" + this.getLPath()));
        this.setRImage(new Image("file:" + this.getRPath()));
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();

    }
    public ImagePair(String lPath) {
        this.setLPath(lPath);
        this.setRPath("");
        this.setLImage(new Image("file:" + this.getLPath()));
        this.setRImage(null);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();

    }
    public String getLPath() {
        return this.lPath;
    }
    public String getRPath() {
        return this.rPath;
    }
    public Image getLImage() {
        return this.lImage;
    }
    public Image getRImage() {
        return this.rImage;
    }
    public double getZoom() {
        return this.zoom;
    }
    public double getInitWidth() {
        return this.initWidth;
    }
    public double getInitHeight() {
        return this.initHeight;
    }
    private void setLPath(String lPath) {
        this.lPath = lPath;
    }
    private void setRPath(String rPath) {
        this.rPath = rPath;
    }
    private void setLImage(Image lImage) {
        this.lImage = lImage;
    }
    private void setRImage(Image rImage) {
        this.rImage = rImage;
    }
    public void setZoom(double zoom) {
        this.zoom = zoom;
		int width = (int) (initWidth * this.getZoom());
		int height = (int) (initHeight * this.getZoom());
	    this.setLImage(new Image("file:" + this.getLPath(), width, height, false, false));
        this.setRImage(new Image("file:" + this.getRPath(), width, height, false, false));
    }
    private int setInitWidth() {
        return (int) Math.min(this.getLImage().getWidth(), this.getRImage().getWidth());
    }
    private int setInitHeight() {
        return (int) Math.min(this.getLImage().getHeight(), this.getRImage().getWidth());
    }
}
