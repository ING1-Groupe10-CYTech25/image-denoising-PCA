package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import gui.components.ImageGallery;
import gui.components.ImageDisplay;
import gui.components.ParametersPanel;

/**
 * Classe principale de l'application JavaFX refactorisée
 * Orchestre les différents composants de l'interface
 */
public class Main extends Application {

    private ImageGallery imageGallery;
    private ImageDisplay imageDisplay;
    private ParametersPanel parametersPanel;

    @Override
    public void start(Stage primaryStage) {
        // Création du conteneur principal
        HBox mainContainer = new HBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("content-area");

        // Création des composants
        imageGallery = new ImageGallery();
        imageDisplay = new ImageDisplay();
        parametersPanel = new ParametersPanel();

        // Initialisation de la liste des images disponibles
        imageDisplay.updateAvailableImages(imageGallery.getImportedImages());

        // Configuration des liaisons entre composants
        setupComponentBindings();

        // Configuration des listeners de traitement d'image
        parametersPanel.getNoisePanel().setImageProcessingListener(outputPath -> {
            imageGallery.addImage(outputPath);
            imageGallery.addToRecent(outputPath);
            // Ajouter aussi l'image source dans les récents
            String sourcePath = parametersPanel.getSelectedImagePath();
            if (sourcePath != null) {
                imageGallery.addToRecent(sourcePath);
            }
        });
        parametersPanel.getNoisePanel().setImageDisplay(imageDisplay);

        parametersPanel.getDenoisePanel().setImageProcessingListener(outputPath -> {
            imageGallery.addImage(outputPath);
            imageGallery.addToRecent(outputPath);
            // Ajouter aussi l'image source dans les récents
            String sourcePath = parametersPanel.getSelectedImagePath();
            if (sourcePath != null) {
                imageGallery.addToRecent(sourcePath);
            }
        });
        parametersPanel.getDenoisePanel().setImageDisplay(imageDisplay);

        // Configuration de la croissance horizontale
        HBox.setHgrow(imageDisplay, Priority.ALWAYS);

        // Ajout des composants au conteneur principal
        mainContainer.getChildren().addAll(imageGallery, imageDisplay, parametersPanel);

        // Configuration de la scène
        Scene scene = new Scene(mainContainer, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Configuration de la fenêtre
        primaryStage.setTitle("Traitement d'Image : Bruitage & Débruitage");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Configure les liaisons et callbacks entre les composants
     */
    private void setupComponentBindings() {
        // Liaison galerie -> affichage d'image
        imageGallery.setImageSelectionListener(imagePath -> {
            imageDisplay.displayImage(imagePath);
            parametersPanel.setSelectedImagePath(imagePath);
        });

        // Liaison galerie -> liste d'images disponibles pour la comparaison
        imageGallery.setImageListChangeListener(images -> {
            imageDisplay.updateAvailableImages(images);
        });

        // Liaison traitement d'image -> ajout automatique à la galerie
        parametersPanel.setImageProcessingListener(outputPath -> {
            imageGallery.addImage(outputPath);
            imageGallery.addToRecent(outputPath);
            // Ajouter aussi l'image source dans les récents
            String sourcePath = parametersPanel.getSelectedImagePath();
            if (sourcePath != null) {
                imageGallery.addToRecent(sourcePath);
            }
        });

        // Optionnel : Liaison mode comparaison -> désactivation des paramètres
        imageDisplay.setModeChangeListener(isCompareMode -> {
            // Ici on pourrait désactiver les panneaux de paramètres en mode comparaison
            // si souhaité, mais ce n'était pas implémenté dans l'original
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
} 