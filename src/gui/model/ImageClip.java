package gui.model;

import java.util.Observable;

import javafx.scene.image.Image;

public class ImageClip extends Observable{

    private ImagePair images;
    private double clipFactor;
	public static final Integer CLIP_CHANGE = new Integer(2); // PAC

    public ImageClip(ImagePair images, double clipFactor) {
        this.images = images;
        this.clipFactor = clipFactor;
    }

    public double getClipFactor() {
        return clipFactor;
    }

    public ImagePair getImages() {
        return images;
    }

    public void changeClipSize(double clipFactor) {
		clipFactor = Math.min(Math.max(0, clipFactor), 1);
        left = new Image(leftPath, width, height, false, false);
        right = new Image(rightPath, width, height, false, false);
        this.setChanged();
        this.notifyObservers(ZOOM_CHANGE);
	}
}