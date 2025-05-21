package gui.model;

import java.util.Observable;

import javafx.scene.image.Image;

public class ImagePair extends Observable{
    private String leftPath;
    private String rightPath;
    private Image left;
    private Image right;
    private double zoom;
    private int initWidth;
    private int initHeight; 
	public static final Integer ZOOM_CHANGE = new Integer(1); // PAC

    public ImagePair(String leftPath, String rightPath) {
        this.leftPath = leftPath;
        this.rightPath = rightPath;
        this.left = new Image("file:" + leftPath);
        this.right = new Image("file:" + rightPath);
        this.zoom = 1;
        this.initWidth = getWidth();
        this.initHeight = getHeight();
    }

    public double getZoom() {
        return zoom;
    }

    public Image getLeft() {
        return left;
    }

    public Image getRight() {
        return right;
    }

    public int getWidth() {
        return (int) Math.min(left.getWidth(), right.getWidth());
    }

    public int getHeight() {
        return (int) Math.min(left.getHeight(), right.getHeight());
    }

    public void resize(double zoom) {
		zoom = Math.min(Math.max(0.1, zoom), 10);
        int width = (int) (this.initWidth * this.zoom);
		int height = (int) (this.initHeight * this.zoom);
        left = new Image(leftPath, width, height, false, false);
        right = new Image(rightPath, width, height, false, false);
        this.setChanged();
        this.notifyObservers(ZOOM_CHANGE);
	}
}
