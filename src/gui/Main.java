package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.collections.FXCollections;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Main extends Application {
    private ListView<String> imageListView;
    private List<String> importedImages = new ArrayList<>();
    private ComboBox<String> image1Combo;
    private ComboBox<String> image2Combo;
    private Label noImageLabel;

    @Override
    public void start(Stage primaryStage) {
        // Création du conteneur principal
        HBox mainContainer = new HBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("content-area");

        // Colonne de gauche - Gestion des images
        VBox leftColumn = createLeftColumn();
        leftColumn.setPrefWidth(250);
        leftColumn.getStyleClass().add("rounded-box");

        // Colonne centrale - Affichage des images
        VBox centerColumn = createCenterColumn();
        HBox.setHgrow(centerColumn, Priority.ALWAYS);
        centerColumn.getStyleClass().add("rounded-box");

        // Colonne de droite - Paramètres
        VBox rightColumn = createRightColumn();
        rightColumn.setPrefWidth(300);
        rightColumn.getStyleClass().add("rounded-box");

        mainContainer.getChildren().addAll(leftColumn, centerColumn, rightColumn);

        // Configuration de la scène
        Scene scene = new Scene(mainContainer, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Configuration de la fenêtre
        primaryStage.setTitle("Traitement d'Image : Bruitage & Débruitage");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftColumn() {
        VBox leftColumn = new VBox(15);
        leftColumn.setPadding(new Insets(15));

        // Titre
        Label titleLabel = new Label("Gestion des Images");
        titleLabel.getStyleClass().add("title");

        // ComboBox pour le filtrage
        ComboBox<String> filterCombo = new ComboBox<>(
            FXCollections.observableArrayList("Toutes", "Originales", "Bruitées", "Débruitées")
        );
        filterCombo.setPromptText("Filtrer les images");

        // Boutons d'action
        HBox buttonBox = new HBox(10);
        Button importBtn = new Button("📁 Importer");
        Button deleteBtn = new Button("🗑️ Supprimer");
        buttonBox.getChildren().addAll(importBtn, deleteBtn);

        imageListView = new ListView<>();
        imageListView.setPrefHeight(200);
        imageListView.setPlaceholder(new Label("Aucune image à afficher"));

        noImageLabel = new Label("Aucune image à afficher");
        noImageLabel.getStyleClass().add("help-text");
        noImageLabel.setVisible(false);

        // Action Importer
        importBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importer une image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif", "*.tif", "*.tiff")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String path = selectedFile.getAbsolutePath();
                if (!importedImages.contains(path)) {
                    importedImages.add(path);
                    imageListView.getItems().add(path);
                    updateImageCombos();
                }
            }
            updateNoImageLabel();
        });

        // Action Supprimer
        deleteBtn.setOnAction(e -> {
            String selected = imageListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                importedImages.remove(selected);
                imageListView.getItems().remove(selected);
                updateImageCombos();
            }
            updateNoImageLabel();
        });

        leftColumn.getChildren().addAll(titleLabel, filterCombo, buttonBox, imageListView, noImageLabel);
        updateNoImageLabel();
        return leftColumn;
    }

    private VBox createCenterColumn() {
        VBox centerColumn = new VBox(15);
        centerColumn.setPadding(new Insets(15));

        // Sélection des images
        HBox imageSelection = new HBox(10);
        image1Combo = new ComboBox<>();
        image2Combo = new ComboBox<>();
        image1Combo.setPromptText("Image 1");
        image2Combo.setPromptText("Image 2");
        imageSelection.getChildren().addAll(image1Combo, image2Combo);

        // Zone d'affichage des images
        StackPane imageDisplay = new StackPane();
        imageDisplay.setStyle("-fx-background-color: #F5F5F5;");
        imageDisplay.setPrefHeight(500);

        // Slider de comparaison
        Slider comparisonSlider = new Slider(0, 100, 50);
        comparisonSlider.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Message d'aide
        Label helpLabel = new Label("Sélectionnez deux images à comparer");
        helpLabel.getStyleClass().add("help-text");

        centerColumn.getChildren().addAll(imageSelection, imageDisplay, comparisonSlider, helpLabel);
        return centerColumn;
    }

    private void updateImageCombos() {
        image1Combo.getItems().setAll(importedImages);
        image2Combo.getItems().setAll(importedImages);
    }

    private void updateNoImageLabel() {
        boolean empty = importedImages.isEmpty();
        noImageLabel.setVisible(empty);
        imageListView.setVisible(!empty);
    }

    private VBox createRightColumn() {
        VBox rightColumn = new VBox(15);
        rightColumn.setPadding(new Insets(15));

        // Toggle Mode Image/Dossier
        ToggleButton modeToggle = new ToggleButton("Mode Image");
        modeToggle.setSelected(true);

        // Onglets pour Bruitage/Débruitage
        TabPane tabPane = new TabPane();
        
        // Onglet Bruitage
        Tab noiseTab = new Tab("Bruitage");
        VBox noiseContent = new VBox(10);
        noiseContent.setPadding(new Insets(10));

        // Paramètres de bruitage
        Label noiseTitle = new Label("Paramètres de bruitage");
        noiseTitle.getStyleClass().add("section-title");

        Label sigmaNoiseLabel = new Label("Intensité du bruit (Sigma)");
        Slider sigmaNoiseSlider = new Slider(0, 30, 15);
        sigmaNoiseSlider.setShowTickMarks(true);
        sigmaNoiseSlider.setShowTickLabels(true);
        sigmaNoiseSlider.setMajorTickUnit(5);
        sigmaNoiseSlider.setMinorTickCount(1);
        sigmaNoiseSlider.setSnapToTicks(true);

        Label sigmaValueLabel = new Label("15");
        sigmaNoiseSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sigmaValueLabel.setText(String.format("%.0f", newVal.doubleValue()));
        });

        HBox sigmaBox = new HBox(10, sigmaNoiseSlider, sigmaValueLabel);
        sigmaBox.setAlignment(Pos.CENTER_LEFT);

        Button applyNoiseBtn = new Button("Appliquer le bruit");
        applyNoiseBtn.setMaxWidth(Double.MAX_VALUE);

        noiseContent.getChildren().addAll(noiseTitle, sigmaNoiseLabel, sigmaBox, applyNoiseBtn);
        noiseTab.setContent(noiseContent);

        // Onglet Débruitage
        Tab denoiseTab = new Tab("Débruitage");
        VBox denoiseContent = new VBox(10);
        denoiseContent.setPadding(new Insets(10));

        // Titre
        Label denoiseTitle = new Label("Paramètres de débruitage");
        denoiseTitle.getStyleClass().add("section-title");

        // Type de débruitage (Global/Local)
        Label denoiseTypeLabel = new Label("Type de débruitage");
        ComboBox<String> denoiseTypeCombo = new ComboBox<>(
            FXCollections.observableArrayList("Global", "Local")
        );
        denoiseTypeCombo.setValue("Local");
        denoiseTypeCombo.setMaxWidth(Double.MAX_VALUE);

        // Type de seuillage (Hard/Soft)
        Label thresholdTypeLabel = new Label("Type de seuillage");
        ComboBox<String> thresholdTypeCombo = new ComboBox<>(
            FXCollections.observableArrayList("Hard", "Soft")
        );
        thresholdTypeCombo.setValue("Hard");
        thresholdTypeCombo.setMaxWidth(Double.MAX_VALUE);

        // Méthode de réduction (VisuShrink/Bayes)
        Label shrinkMethodLabel = new Label("Méthode de réduction");
        ComboBox<String> shrinkMethodCombo = new ComboBox<>(
            FXCollections.observableArrayList("VisuShrink", "Bayes")
        );
        shrinkMethodCombo.setValue("VisuShrink");
        shrinkMethodCombo.setMaxWidth(Double.MAX_VALUE);

        // Sigma estimé
        Label sigmaLabel = new Label("Sigma estimé");
        Slider sigmaSlider = new Slider(0, 50, 30);
        sigmaSlider.setShowTickMarks(true);
        sigmaSlider.setShowTickLabels(true);
        sigmaSlider.setMajorTickUnit(10);
        sigmaSlider.setMinorTickCount(1);
        sigmaSlider.setSnapToTicks(true);
        Label denoiseSigmaValueLabel = new Label("30");
        sigmaSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            denoiseSigmaValueLabel.setText(String.format("%.0f", newVal.doubleValue()));
        });
        HBox denoiseSigmaBox = new HBox(10, sigmaSlider, denoiseSigmaValueLabel);
        denoiseSigmaBox.setAlignment(Pos.CENTER_LEFT);

        // Taille de patch (%)
        Label patchSizeLabel = new Label("Taille de patch (%)");
        Slider patchSizeSlider = new Slider(1, 10, 5);
        patchSizeSlider.setShowTickMarks(true);
        patchSizeSlider.setShowTickLabels(true);
        patchSizeSlider.setMajorTickUnit(1);
        patchSizeSlider.setMinorTickCount(0);
        patchSizeSlider.setSnapToTicks(true);
        Label patchSizeValueLabel = new Label("5%");
        patchSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            patchSizeValueLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });
        HBox patchSizeBox = new HBox(10, patchSizeSlider, patchSizeValueLabel);
        patchSizeBox.setAlignment(Pos.CENTER_LEFT);

        Button applyDenoiseBtn = new Button("Appliquer le débruitage");
        applyDenoiseBtn.setMaxWidth(Double.MAX_VALUE);

        denoiseContent.getChildren().addAll(
            denoiseTitle,
            denoiseTypeLabel, denoiseTypeCombo,
            thresholdTypeLabel, thresholdTypeCombo,
            shrinkMethodLabel, shrinkMethodCombo,
            sigmaLabel, denoiseSigmaBox,
            patchSizeLabel, patchSizeBox,
            applyDenoiseBtn
        );
        denoiseTab.setContent(denoiseContent);

        tabPane.getTabs().addAll(noiseTab, denoiseTab);

        rightColumn.getChildren().addAll(modeToggle, tabPane);
        return rightColumn;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 