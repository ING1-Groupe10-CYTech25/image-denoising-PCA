package gui.model;

import java.awt.image.BufferedImage;
import java.util.Observable;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

@SuppressWarnings("deprecation")
public class ImageClipper extends Observable { 
    public static final Integer ZOOM_CHANGE = new Integer(0); // PAC
	public static final Integer CLIP_CHANGE = new Integer(1); // PAC
	public static final Integer IMAGE_CHANGE = new Integer(2);

    private String lPath;
    private String rPath;
    private Image lImage;
    private Image rImage;
    private double zoom;
    private int initWidth;
    private int initHeight;
    private double clip;

    public ImageClipper(String lPath, String rPath) {
        this.setLPath(lPath);
        this.setRPath(rPath);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();
        this.setClip(0.5);
    }
    public ImageClipper(BufferedImage lImage, BufferedImage rImage) {
        this.setLPath(null);
        this.setRPath(null);
        this.setLImage(lImage);
        this.setLImage(rImage);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();
        this.setClip(0.5);
    }
    public ImageClipper(String lPath, BufferedImage rImage) {
        this.setLPath(lPath);
        this.setRImage(rImage);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();
        this.setClip(0.5);
    }
    public ImageClipper(BufferedImage lImage, String rPath) {
        this.setRPath(rPath);
        this.setRImage(lImage);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();
        this.setClip(0.5);
    }
    public ImageClipper(String lPath) {
        this.setLPath(lPath);
        this.setRPath(null);
        this.setLImage(new Image("file:" + this.getLPath()));
        this.setRImage((Image) null);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();
        this.setClip(0);
    }
    
    public ImageClipper(BufferedImage lImage) {
        this.setLPath(lPath);
        this.setRPath(null);
        this.setLImage(lImage);
        this.setRImage((BufferedImage) null);
        this.setZoom(1);
        this.setInitWidth();
        this.setInitHeight();
        this.setClip(0);
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
    public double getClip() {
        return this.clip;
    }
    public void setLPath(String lPath) {
        this.lPath = lPath;
        this.setLImage(new Image("file:" + this.getLPath()));
    }
    public void setRPath(String rPath) {
        this.rPath = rPath;
        this.setRImage(new Image("file:" + this.getRPath()));
    }
    public void setLImage(Image lImage) {
        this.lImage = lImage;
        this.setChanged(); // PAC
		this.notifyObservers(IMAGE_CHANGE);
    }
    public void setLImage(BufferedImage lImage){
        this.lImage = (SwingFXUtils.toFXImage(lImage, null));
        this.setChanged(); // PAC
		this.notifyObservers(IMAGE_CHANGE);
    }
    public void setRImage(Image rImage) {
        this.rImage = rImage;
        this.setChanged(); // PAC
		this.notifyObservers(IMAGE_CHANGE);
    }
    public void setRImage(BufferedImage rImage){
        this.rImage = (SwingFXUtils.toFXImage(rImage, null));
        this.setChanged(); // PAC
		this.notifyObservers(IMAGE_CHANGE);
    }
    public void setZoom(double zoom) {
        this.zoom = zoom;
		int width = (int) (initWidth * this.getZoom());
		int height = (int) (initHeight * this.getZoom());
	    this.setLImage(new Image("file:" + this.getLPath(), width, height, false, false));
        this.setRImage(this.getRPath() != null ? new Image("file:" + this.getRPath(), width, height, false, false) : null);
        this.setChanged(); // PAC
		this.notifyObservers(ZOOM_CHANGE);
    }
    private int setInitWidth() {
        return (int) Math.min(this.getLImage().getWidth(), this.getRImage() != null ? this.getRImage().getWidth() : Integer.MAX_VALUE);
    }
    private int setInitHeight() {
        return (int) Math.min(this.getLImage().getHeight(),  this.getRImage() != null ? this.getRImage().getHeight() : Integer.MAX_VALUE);
    }
    public void setClip(double clip) {
        this.clip = Math.min(0, Math.max(clip, 1));
        this.setChanged(); // PAC
		this.notifyObservers(CLIP_CHANGE);
    }
}
