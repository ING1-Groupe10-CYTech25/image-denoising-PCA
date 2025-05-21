package gui.controller;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.math3.optim.InitialGuess;

import gui.model.ImagePair;
import javafx.scene.layout.StackPane;

public class ImageComparatorController implements Observer {
    private StackPane centerPane;

    public ImageComparatorController(StackPane centerPane) {
        this.centerPane = centerPane;
    }

    public void update(Observable o, Object message) {
        ImagePair model = (ImagePair) o;
        Integer iMessage = (Integer) message;
        if (iMessage == ImagePair.ZOOM_CHANGE) {
            centerPane.setPrefSize(model.getWidth(), model.getHeight());
        }
        if (iMessage == )
    }
}