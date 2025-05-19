package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Classe simple pour tester que JavaFX fonctionne correctement.
 * Cette classe ne dépend pas de FXML ou d'autres composants externes.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox(10); 
        root.setStyle("-fx-padding: 20px;");
        
        // Création de titres
        Label title = new Label("Test JavaFX");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label text = new Label("Test");
        
        // Ajout des composants au conteneur
        root.getChildren().addAll(title, text);
        
        // Création de la scène
        Scene scene = new Scene(root, 400, 200);
        
        // Configuration de la fenêtre
        primaryStage.setTitle("Test JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        // Point d'entrée principal
        launch(args);
    }
}