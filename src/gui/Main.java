package gui;

// /**
//  * Classe simple pour tester que JavaFX fonctionne correctement.
//  * Cette classe ne dépend pas de FXML ou d'autres composants externes.
//  */
// public class Main extends Application {
    
import gui.controller.ImageComparatorController;
import javafx.application.Application;
import javafx.stage.Stage;
import gui.model.ImagePair;
import gui.view.ImageComparatorView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        String pathAvant = System.getProperty("user.dir") + "/img/original/avion.png";
        String pathApres = System.getProperty("user.dir") + "/img/original/babouin.png";

        ImagePair model = new ImagePair(pathAvant, pathApres);
        ImageComparatorView view = new ImageComparatorView();
        view.setup(primaryStage, model.getWidth(), model.getHeight());

        new ImageComparatorController(model, view);
    }

    public static void main(String[] args) {
        launch(args);
    }
}