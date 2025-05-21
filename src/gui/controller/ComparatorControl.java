package gui.controller;

import java.util.Observable;
import java.util.Observer;

import gui.model.ImageClipper;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

@SuppressWarnings("deprecation")
public class ComparatorControl implements Observer {
    
    private ImageView lView;
    private ImageView rView;
    
    public ComparatorControl(ImageView lView, ImageView rView) {
        this.lView = lView;
        this.rView = rView;
    }

    @Override
    public void update(Observable o, Object message) {
        ImageClipper model = (ImageClipper) o;
        Integer iMessage = (Integer) message;
        if (iMessage == ImageClipper.CLIP_CHANGE) {
            lView.setViewport(new Rectangle2D(0, 0, model.getInitWidth() * model.getClip(), model.getInitHeight()));
        } else if (iMessage == ImageClipper.ZOOM_CHANGE) {
            lView.setFitWidth(model.getWidth());
            lView.setFitHeight(model.getHeight());
            rView.setFitWidth(model.getWidth());
            rView.setFitHeight(model.getHeight());
        } else if (iMessage == ImageClipper.IMAGE_CHANGE) {
            lView.setImage(model.getLImage());
            rView.setImage(model.getRImage());
        }
    }
}
