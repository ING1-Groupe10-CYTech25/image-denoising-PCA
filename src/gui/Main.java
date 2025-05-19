package gui;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;

// /**
//  * Classe simple pour tester que JavaFX fonctionne correctement.
//  * Cette classe ne dépend pas de FXML ou d'autres composants externes.
//  */
// public class Main extends Application {
    


//     @Override
//     public void start(Stage primaryStage) {

//         VBox root = new VBox(10); 
//         root.setStyle("-fx-padding: 20px;");
        
//         // Création de titres
//         Label title = new Label("Test JavaFX");
//         title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
//         Label text = new Label("Test");
        
//         // Ajout des composants au conteneur
//         root.getChildren().addAll(title, text);
        
//         // Création de la scène
//         Scene scene = new Scene(root, 400, 200);
        
//         // Configuration de la fenêtre
//         primaryStage.setTitle("Test JavaFX");
//         primaryStage.setScene(scene);
//         primaryStage.show();
//     }
    
//     public static void main(String[] args) {
//         // Point d'entrée principal
//         launch(args);
//     }
// }

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Charge les deux images
        Image imageAvant = new Image("file:" + System.getProperty("user.dir") + "/img/original/avion.png");
        Image imageApres = new Image("file:" + System.getProperty("user.dir") + "/img/original/babouin.png");
        System.out.println("Avant: " + imageAvant.getWidth() + " x " + imageAvant.getHeight());
        System.out.println("Après: " + imageApres.getWidth() + " x " + imageApres.getHeight());

        // ImageView "avant"
        ImageView imageViewAvant = new ImageView(imageAvant);

        // ImageView "après", positionnée par-dessus
        ImageView imageViewApres = new ImageView(imageApres);
        imageViewApres.setPreserveRatio(false);

        imageViewAvant.setFitWidth(512);
        imageViewAvant.setFitHeight(512);
        imageViewAvant.setPreserveRatio(true);

        imageViewApres.setFitWidth(512);
        imageViewApres.setFitHeight(512);
        imageViewApres.setPreserveRatio(true);

        // Slider pour la comparaison (0 -> largeur image)
        Slider slider = new Slider(1, imageAvant.getWidth(), imageAvant.getWidth() / 2);

        // Met à jour le viewport en fonction du slider
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            imageViewAvant.setViewport(new Rectangle2D(0, 0, width, imageAvant.getHeight()));
        });

        // Initialiser le viewport
        imageViewAvant.setViewport(new Rectangle2D(0, 0, slider.getValue(), imageAvant.getHeight()));

        HBox topContainer = new HBox(imageViewAvant);
        topContainer.setPrefSize(imageAvant.getHeight(), imageAvant.getWidth());
        // Empile les images
        StackPane imagesPane = new StackPane(imageViewApres, topContainer);
        imagesPane.setPrefWidth(512);
        imagesPane.setPrefHeight(512);
        VBox root = new VBox(imagesPane, slider);
        imagesPane.setStyle("-fx-background-color: lightgray; -fx-border-color: red;");

        Scene scene = new Scene(root);
        primaryStage.setTitle("Comparaison Avant/Après");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}