package gui;

// /**
//  * Classe simple pour tester que JavaFX fonctionne correctement.
//  * Cette classe ne dépend pas de FXML ou d'autres composants externes.
//  */
// public class Main extends Application {
    
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        ScrollPane pane = new ScrollPane();
        ImageView leftview = new ImageView(new Image("file:" + System.getProperty("user.dir") + "/img/original/avion.png"));
        StackPane stacked = new StackPane(leftview);
        pane.setContent(stacked);
        pane.setPannable(true);
        stacked.setPrefSize(512, 512);
        pane.setPrefSize(256,256);
        Scene main = new Scene(pane);
        primaryStage.setScene(main);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}