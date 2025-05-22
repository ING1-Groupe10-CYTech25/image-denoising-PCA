// package gui.view;

// import gui.model.ImagePair;
// import javafx.scene.Scene;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.Slider;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

// public class ImageComparatorView {
//     private ImagePair model;

//     @Override
//     public void start(Stage primaryStage) throws Exception {
//         primaryStage.setTitle("ImageComparator");
//         model = new ImagePair(System.getProperty("user.dir") + "/img/original/avion.png", System.getProperty("user.dir") + "/img/original/babouin.png");
//         HBox root = new HBox();
//         Slider clipSlider = new Slider(createSlider());
//         ScrollPane scrollPane = new ScrollPane(createCenter());

//         primaryStage.setScene(new Scene(root));
//         primaryStage.sizeToScene();
//         primaryStage.show();
//     }

//     private ScrollPane createCenter() {
//         StackPane zoomPane = new StackPane();
//         ScrollPane scrollPane = new ScrollPane(zoomPane);
//         ImageView leftImage = new ImageView(model.getLeft());
//         ImageView rightImage = new ImageView(model.getRight());
//         ControlCenterPane controlCenterPane = new ControlCenterPane()
        
//         return scrollPane;
//     }
// }

package gui.view;
import gui.model.*;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import gui.controller.*;

@SuppressWarnings("deprecation")
public class ImageDenoisePCA extends Application {
    
    private ImageClipper model;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Débruitage d'images par ACP");
        model = new ImageClipper(System.getProperty("user.dir") + "/img/original/avion.png", System.getProperty("user.dir") + "/img/original/babouin.png");
        BorderPane root = new BorderPane();
        root.setCenter(buildComparator());
        Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
    }

    private ScrollPane buildComparator() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        StackPane stackPane = new StackPane();
        
        ImageView lView = new ImageView(model.getLImage());
        ImageView rView = new ImageView(model.getRImage());
        lView.setViewport(new Rectangle2D(0, 0, model.getInitWidth() * model.getClip(), model.getInitHeight()));
        model.setZoom(2);
        ComparatorControl comparatorControl = new ComparatorControl(lView, rView);
        model.addObserver(comparatorControl);
        stackPane.getChildren().addAll(rView,lView);
        scrollPane.setContent(stackPane);
        return scrollPane;
    }
    public static void main(String args[]) {
		launch(args);
	}
}