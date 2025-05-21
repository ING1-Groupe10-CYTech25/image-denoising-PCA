package gui.view;

import gui.model.ImagePair;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ImageComparatorView {
    private ImagePair model;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ImageComparator");
        model = new ImagePair(System.getProperty("user.dir") + "/img/original/avion.png", System.getProperty("user.dir") + "/img/original/babouin.png");
        HBox root = new HBox();
        Slider clipSlider = new Slider(createSlider());
        ScrollPane scrollPane = new ScrollPane(createCenter());

        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private ScrollPane createCenter() {
        StackPane zoomPane = new StackPane();
        ScrollPane scrollPane = new ScrollPane(zoomPane);
        ImageView leftImage = new ImageView(model.getLeft());
        ImageView rightImage = new ImageView(model.getRight());
        ControlCenterPane controlCenterPane = new ControlCenterPane()
        
        return scrollPane;
    }
}