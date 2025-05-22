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
import javafx.scene.control.ListCell;
import java.nio.file.Paths;
import javafx.scene.layout.TilePane;
import javafx.scene.input.MouseEvent;

public class Main extends Application {
    private TilePane imageTilePane;
    private List<String> importedImages = new ArrayList<>();
    private ComboBox<String> image1Combo;
    private ComboBox<String> image2Combo;
    private Label noImageLabel;
    private String selectedImagePath = null;
    private ImageView centerImageView = new ImageView();

    @Override
    public void start(Stage primaryStage) {
        // Création du conteneur principal
        HBox mainContainer = new HBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("content-area");

        // Colonne de gauche - Gestion des images
        VBox leftColumn = createLeftColumn();
        leftColumn.setMinWidth(240);
        leftColumn.setPrefWidth(240);
        leftColumn.setMaxWidth(240);
        leftColumn.getStyleClass().add("rounded-box");

        // Colonne centrale - Affichage des images
        VBox centerColumn = createCenterColumn();
        HBox.setHgrow(centerColumn, Priority.ALWAYS);
        centerColumn.getStyleClass().add("rounded-box");

        // Colonne de droite - Paramètres
        VBox rightColumn = createRightColumn();
        rightColumn.setMinWidth(360);
        rightColumn.setPrefWidth(360);
        rightColumn.setMaxWidth(360);
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
        leftColumn.setMinWidth(240);
        leftColumn.setPrefWidth(240);
        leftColumn.setMaxWidth(240);

        Label titleLabel = new Label("Gestion des Images");
        titleLabel.getStyleClass().add("title");

        ComboBox<String> filterCombo = new ComboBox<>(
            FXCollections.observableArrayList("Toutes", "Originales", "Bruitées", "Débruitées")
        );
        filterCombo.setPromptText("Filtrer les images");

        HBox buttonBox = new HBox(10);
        Button importBtn = new Button("📁 Importer");
        Button deleteBtn = new Button("🗑️ Supprimer");
        importBtn.setMinWidth(90);
        importBtn.setPrefWidth(110);
        importBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMinWidth(90);
        deleteBtn.setPrefWidth(110);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        importBtn.getStyleClass().add("gallery-btn");
        deleteBtn.getStyleClass().add("gallery-btn");
        buttonBox.getChildren().addAll(importBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillHeight(false);

        imageTilePane = new TilePane();
        imageTilePane.setHgap(10);
        imageTilePane.setVgap(10);
        imageTilePane.setPrefColumns(2);
        imageTilePane.setPrefTileWidth(90);
        imageTilePane.setPrefTileHeight(110);
        imageTilePane.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(imageTilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color:transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

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
                    updateImageGallery();
                }
            }
            updateNoImageLabel();
        });

        // Action Supprimer
        deleteBtn.setOnAction(e -> {
            if (selectedImagePath != null) {
                importedImages.remove(selectedImagePath);
                selectedImagePath = null;
                updateImageGallery();
            }
            updateNoImageLabel();
        });

        leftColumn.getChildren().addAll(titleLabel, filterCombo, buttonBox, scrollPane, noImageLabel);
        updateNoImageLabel();
        return leftColumn;
    }

    private void updateImageGallery() {
        imageTilePane.getChildren().clear();
        for (String path : importedImages) {
            VBox card = new VBox(2);
            card.setAlignment(Pos.CENTER);
            card.setPrefSize(90, 110);
            card.setMinSize(90, 110);
            card.setMaxSize(90, 110);
            card.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4;");

            ImageView imageView = new ImageView();
            try {
                Image img = new Image(Paths.get(path).toUri().toString(), 80, 80, true, true);
                imageView.setImage(img);
            } catch (Exception e) {
                imageView.setImage(null);
            }
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);

            Label nameLabel = new Label(Paths.get(path).getFileName().toString());
            nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444; -fx-alignment: center;");
            nameLabel.setMaxWidth(90);
            nameLabel.setWrapText(true);
            nameLabel.setAlignment(Pos.CENTER);

            card.getChildren().addAll(imageView, nameLabel);

            // Sélection visuelle
            if (path.equals(selectedImagePath)) {
                card.setStyle(card.getStyle() + "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, #4CAF50, 8, 0.2, 0, 0);");
            } else {
                card.setStyle(card.getStyle() + "-fx-border-color: #E0E0E0; -fx-border-width: 1;");
            }

            card.setOnMouseClicked((MouseEvent e) -> {
                selectedImagePath = path;
                updateImageGallery();
                // Afficher l'image sélectionnée dans la colonne centrale
                try {
                    Image img = new Image(Paths.get(path).toUri().toString());
                    centerImageView.setImage(img);
                } catch (Exception ex) {
                    centerImageView.setImage(null);
                }
            });

            imageTilePane.getChildren().add(card);
        }
    }

    private VBox createCenterColumn() {
        VBox centerColumn = new VBox(15);
        centerColumn.setPadding(new Insets(15));

        // Zone d'affichage des images
        StackPane imageDisplay = new StackPane();
        imageDisplay.setStyle("-fx-background-color: #F5F5F5;");
        imageDisplay.setPrefHeight(500);
        centerImageView.setPreserveRatio(true);
        centerImageView.setFitWidth(500);
        centerImageView.setFitHeight(500);
        imageDisplay.getChildren().add(centerImageView);

        centerColumn.getChildren().addAll(imageDisplay);
        return centerColumn;
    }

    private void updateImageCombos() {
        image1Combo.getItems().setAll(importedImages);
        image2Combo.getItems().setAll(importedImages);
    }

    private void updateNoImageLabel() {
        boolean empty = importedImages.isEmpty();
        noImageLabel.setVisible(empty);
        imageTilePane.setVisible(!empty);
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

        // Action : appliquer le bruitage à l'image sélectionnée
        applyNoiseBtn.setOnAction(e -> {
            if (selectedImagePath == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une image à bruiter.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            try {
                // Charger l'image originale
                core.image.ImageFile img = new core.image.ImageFile(selectedImagePath);
                int sigma = (int) sigmaNoiseSlider.getValue();

                // Appliquer le bruit
                img.noisify(sigma);

                // Générer un nom de fichier pour l'image bruitée
                String baseName = Paths.get(selectedImagePath).getFileName().toString();
                String nameSansExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
                String outName = nameSansExt + "_noised_" + sigma + ".png";
                String outPath = "img/img_noised/" + outName;

                // Créer le dossier s'il n'existe pas
                File outDir = new File("img/img_noised");
                if (!outDir.exists()) outDir.mkdirs();

                // Sauvegarder l'image bruitée
                img.saveImage(outPath);

                // Ajouter à la galerie si pas déjà présent
                if (!importedImages.contains(outPath)) {
                    importedImages.add(outPath);
                    updateImageGallery();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du bruitage : " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });

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

        // Action : appliquer le débruitage à l'image sélectionnée
        applyDenoiseBtn.setOnAction(e -> {
            if (selectedImagePath == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une image à débruiter.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            try {
                // Récupérer les paramètres de l'UI
                String type = denoiseTypeCombo.getValue(); // "Global" ou "Local"
                boolean isGlobal = type.equalsIgnoreCase("Global");
                String threshold = thresholdTypeCombo.getValue().toLowerCase(); // "hard" ou "soft"
                String shrink = shrinkMethodCombo.getValue().equals("VisuShrink") ? "v" : "b";
                double sigma = sigmaSlider.getValue();
                double patchPercent = patchSizeSlider.getValue() / 100.0;

                // Générer un nom de fichier pour l'image débruitée
                String baseName = Paths.get(selectedImagePath).getFileName().toString();
                String nameSansExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
                String outName = nameSansExt + "_denoised_" + (isGlobal ? "global" : "local") + "_" + threshold + "_" + shrink + ".png";
                String outPath = "img/img_denoised/" + outName;

                // Créer le dossier s'il n'existe pas
                File outDir = new File("img/img_denoised");
                if (!outDir.exists()) outDir.mkdirs();

                // Appliquer le débruitage PCA
                core.acp.ImageDenoiser.ImageDen(
                    selectedImagePath,
                    outPath,
                    isGlobal,
                    threshold,
                    shrink,
                    sigma,
                    patchPercent
                );

                // Ajouter à la galerie si pas déjà présent
                if (!importedImages.contains(outPath)) {
                    importedImages.add(outPath);
                    updateImageGallery();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du débruitage : " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });

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