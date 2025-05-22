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
    private Label centerImageNameLabel = new Label();
    private String currentFilter = "Toutes";
    private boolean compareMode = false;
    private String compareImage1 = null;
    private String compareImage2 = null;

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
        filterCombo.setValue("Toutes");
        filterCombo.setOnAction(e -> {
            currentFilter = filterCombo.getValue();
            updateImageGallery();
        });

        HBox buttonBox = new HBox(10);
        Button importBtn = new Button("📁 Importer");
        Button deleteBtn = new Button("🗑️ Supprimer");
        importBtn.setMinWidth(90);
        importBtn.setPrefWidth(110);
        importBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMinWidth(90);
        deleteBtn.setPrefWidth(110);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        importBtn.getStyleClass().addAll("gallery-btn", "black-btn");
        deleteBtn.getStyleClass().addAll("gallery-btn", "red-btn");
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
                // Effacer l'affichage de l'image centrale
                centerImageView.setImage(null);
                centerImageNameLabel.setText("");
            }
            updateNoImageLabel();
        });

        leftColumn.getChildren().addAll(titleLabel, filterCombo, buttonBox, scrollPane, noImageLabel);
        updateNoImageLabel();
        return leftColumn;
    }

    private void updateImageGallery() {
        imageTilePane.getChildren().clear();
        List<String> filteredImages = filterImages(importedImages);
        for (String path : filteredImages) {
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
                    centerImageNameLabel.setText(Paths.get(path).getFileName().toString());
                } catch (Exception ex) {
                    centerImageView.setImage(null);
                    centerImageNameLabel.setText("");
                }
            });

            imageTilePane.getChildren().add(card);
        }
    }

    private List<String> filterImages(List<String> images) {
        if (currentFilter.equals("Toutes")) {
            return new ArrayList<>(images);
        }
        
        List<String> filtered = new ArrayList<>();
        for (String path : images) {
            if (currentFilter.equals("Originales") && path.contains("/original/")) {
                filtered.add(path);
            } else if (currentFilter.equals("Bruitées") && path.contains("/img_noised/")) {
                filtered.add(path);
            } else if (currentFilter.equals("Débruitées") && path.contains("/img_denoised/")) {
                filtered.add(path);
            }
        }
        return filtered;
    }

    private void updateImageCombos() {
        image1Combo.getItems().setAll(importedImages);
        image2Combo.getItems().setAll(importedImages);
    }

    private void updateNoImageLabel() {
        boolean empty = filterImages(importedImages).isEmpty();
        noImageLabel.setVisible(empty);
        imageTilePane.setVisible(!empty);
    }

    private VBox createCenterColumn() {
        VBox centerColumn = new VBox(15);
        centerColumn.setPadding(new Insets(15));

        // Boutons Affichage et Comparer côte à côte
        Button displayBtn = new Button("Affichage");
        displayBtn.setMaxWidth(120);
        displayBtn.getStyleClass().addAll("action-btn", "black-btn");
        Button compareBtn = new Button("Comparer");
        compareBtn.setMaxWidth(120);
        compareBtn.getStyleClass().addAll("action-btn", "black-btn");
        HBox topBtnBox = new HBox(10, displayBtn, compareBtn);
        topBtnBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(topBtnBox, new Insets(0, 0, 10, 0));

        // Conteneur pour l'affichage dynamique (image simple ou comparaison)
        StackPane dynamicDisplay = new StackPane();
        dynamicDisplay.setPrefHeight(500);
        dynamicDisplay.setStyle("-fx-background-color: #F5F5F5;");

        // Label du nom de l'image sélectionnée (mode simple)
        centerImageNameLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #444; -fx-padding: 0; -fx-background-color: transparent;");
        centerImageNameLabel.setAlignment(Pos.CENTER);
        centerImageNameLabel.setMaxWidth(Double.MAX_VALUE);
        centerImageNameLabel.setWrapText(true);
        VBox.setMargin(centerImageNameLabel, Insets.EMPTY);

        // Affichage normal (image simple)
        StackPane imageDisplay = new StackPane(centerImageView);
        imageDisplay.setPrefHeight(500);
        centerImageView.setPreserveRatio(true);
        centerImageView.setFitWidth(500);
        centerImageView.setFitHeight(500);

        // Affichage comparaison (initialisé à null, créé dynamiquement)
        VBox compareBox = createCompareBox();

        // Affichage dynamique selon le mode
        updateCenterDisplay(dynamicDisplay, imageDisplay, compareBox);

        // Action bouton Affichage
        displayBtn.setOnAction(e -> {
            compareMode = false;
            updateCenterDisplay(dynamicDisplay, imageDisplay, createCompareBox());
        });
        // Action bouton Comparer
        compareBtn.setOnAction(e -> {
            if (importedImages.size() < 2) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez importer au moins deux images pour comparer.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            compareMode = true;
            // Par défaut, on prend l'image sélectionnée et la suivante (ou la première si à la fin)
            int idx = importedImages.indexOf(selectedImagePath);
            if (idx == -1) idx = 0;
            compareImage1 = importedImages.get(idx);
            compareImage2 = importedImages.get((idx + 1) % importedImages.size());
            updateCenterDisplay(dynamicDisplay, imageDisplay, createCompareBox());
        });

        centerColumn.getChildren().addAll(topBtnBox, dynamicDisplay, centerImageNameLabel);
        return centerColumn;
    }

    // Met à jour l'affichage central selon le mode
    private void updateCenterDisplay(StackPane dynamicDisplay, StackPane imageDisplay, VBox compareBox) {
        dynamicDisplay.getChildren().clear();
        if (compareMode) {
            dynamicDisplay.getChildren().add(compareBox);
        } else {
            dynamicDisplay.getChildren().add(imageDisplay);
        }
        // Mettre à jour l'état des boutons bruitage/débruitage
        if (compareModeListener != null) compareModeListener.run();
    }

    // Crée le composant de comparaison interactif
    private VBox createCompareBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);

        // Sélection des images à comparer
        HBox selectors = new HBox(10);
        selectors.setAlignment(Pos.CENTER);
        ComboBox<String> img1Combo = new ComboBox<>(FXCollections.observableArrayList(importedImages));
        ComboBox<String> img2Combo = new ComboBox<>(FXCollections.observableArrayList(importedImages));
        img1Combo.setValue(compareImage1);
        img2Combo.setValue(compareImage2);
        img1Combo.setMaxWidth(180);
        img2Combo.setMaxWidth(180);
        selectors.getChildren().addAll(new Label("Image 1 :"), img1Combo, new Label("Image 2 :"), img2Combo);

        // Zone d'affichage des images superposées
        StackPane comparePane = new StackPane();
        comparePane.setPrefSize(500, 500);
        ImageView img1View = new ImageView();
        ImageView img2View = new ImageView();
        img1View.setPreserveRatio(true);
        img2View.setPreserveRatio(true);
        img1View.setFitWidth(500);
        img1View.setFitHeight(500);
        img2View.setFitWidth(500);
        img2View.setFitHeight(500);
        if (compareImage1 != null) img1View.setImage(new Image(Paths.get(compareImage1).toUri().toString()));
        if (compareImage2 != null) img2View.setImage(new Image(Paths.get(compareImage2).toUri().toString()));

        // Barre verticale (slider)
        Slider slider = new Slider(0, 1, 0.5);
        slider.setPrefWidth(500);
        slider.setStyle("-fx-padding: 0 0 0 0;");

        // Clip dynamique sur img2View
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        img2View.setClip(clip);
        comparePane.widthProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(comparePane.getHeight());
            clip.setWidth(slider.getValue() * comparePane.getWidth());
        });
        comparePane.heightProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(comparePane.getHeight());
            clip.setWidth(slider.getValue() * comparePane.getWidth());
        });
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.doubleValue() * comparePane.getWidth());
        });
        // Initialiser le clip
        comparePane.widthProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(slider.getValue() * comparePane.getWidth());
        });
        comparePane.heightProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(comparePane.getHeight());
        });

        comparePane.getChildren().addAll(img1View, img2View);

        // Labels "Image 1" et "Image 2"
        Label label1 = new Label("Image 1");
        label1.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-font-size: 13px; -fx-padding: 4 10 4 10; -fx-border-radius: 6; -fx-background-radius: 6;");
        label1.setAlignment(Pos.TOP_LEFT);
        label1.setTranslateX(-210);
        label1.setTranslateY(10 - comparePane.getPrefHeight()/2);
        Label label2 = new Label("Image 2");
        label2.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-font-size: 13px; -fx-padding: 4 10 4 10; -fx-border-radius: 6; -fx-background-radius: 6;");
        label2.setAlignment(Pos.TOP_RIGHT);
        label2.setTranslateX(210);
        label2.setTranslateY(10 - comparePane.getPrefHeight()/2);
        comparePane.getChildren().addAll(label1, label2);

        // Slider sous l'image
        HBox sliderBox = new HBox(slider);
        sliderBox.setAlignment(Pos.CENTER);
        sliderBox.setPadding(new Insets(10, 0, 0, 0));

        // Mise à jour des images lors du changement de sélection
        img1Combo.setOnAction(e -> {
            compareImage1 = img1Combo.getValue();
            if (compareImage1 != null) img1View.setImage(new Image(Paths.get(compareImage1).toUri().toString()));
        });
        img2Combo.setOnAction(e -> {
            compareImage2 = img2Combo.getValue();
            if (compareImage2 != null) img2View.setImage(new Image(Paths.get(compareImage2).toUri().toString()));
        });

        box.getChildren().addAll(selectors, comparePane, sliderBox);
        return box;
    }

    private VBox createRightColumn() {
        VBox rightColumn = new VBox(15);
        rightColumn.setPadding(new Insets(15));

        // Deux vrais boutons noirs pour Bruitage et Débruitage
        HBox modeBtnBox = new HBox(10);
        Button noiseBtn = new Button("Bruitage");
        Button denoiseBtn = new Button("Débruitage");
        noiseBtn.getStyleClass().add("black-btn");
        denoiseBtn.getStyleClass().add("black-btn");
        noiseBtn.setMinWidth(120);
        denoiseBtn.setMinWidth(120);
        noiseBtn.setStyle("-fx-background-color: #111; -fx-text-fill: #fff;");
        denoiseBtn.setStyle("-fx-background-color: #111; -fx-text-fill: #fff;");
        modeBtnBox.getChildren().addAll(noiseBtn, denoiseBtn);
        modeBtnBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(modeBtnBox, new Insets(0, 0, 10, 0));

        // === Composants Bruitage ===
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
        applyNoiseBtn.getStyleClass().add("black-btn");
        applyNoiseBtn.setOnAction(e -> {
            if (selectedImagePath == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une image à bruiter.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            try {
                core.image.ImageFile img = new core.image.ImageFile(selectedImagePath);
                int sigma = (int) sigmaNoiseSlider.getValue();
                img.noisify(sigma);
                String baseName = Paths.get(selectedImagePath).getFileName().toString();
                String nameSansExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
                String outName = nameSansExt + "_noised_" + sigma + ".png";
                String outPath = "img/img_noised/" + outName;
                File outDir = new File("img/img_noised");
                if (!outDir.exists()) outDir.mkdirs();
                img.saveImage(outPath);
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

        VBox noiseContent = new VBox(10);
        noiseContent.setPadding(new Insets(10));
        noiseContent.getChildren().addAll(noiseTitle, sigmaNoiseLabel, sigmaBox, applyNoiseBtn);

        // === Composants Débruitage ===
        Label denoiseTitle = new Label("Paramètres de débruitage");
        denoiseTitle.getStyleClass().add("section-title");
        Label denoiseTypeLabel = new Label("Type de débruitage");
        ComboBox<String> denoiseTypeCombo = new ComboBox<>(
            FXCollections.observableArrayList("Global", "Local")
        );
        denoiseTypeCombo.setValue("Local");
        denoiseTypeCombo.setMaxWidth(Double.MAX_VALUE);
        Label thresholdTypeLabel = new Label("Type de seuillage");
        ComboBox<String> thresholdTypeCombo = new ComboBox<>(
            FXCollections.observableArrayList("Hard", "Soft")
        );
        thresholdTypeCombo.setValue("Hard");
        thresholdTypeCombo.setMaxWidth(Double.MAX_VALUE);
        Label shrinkMethodLabel = new Label("Méthode de réduction");
        ComboBox<String> shrinkMethodCombo = new ComboBox<>(
            FXCollections.observableArrayList("VisuShrink", "Bayes")
        );
        shrinkMethodCombo.setValue("VisuShrink");
        shrinkMethodCombo.setMaxWidth(Double.MAX_VALUE);
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
        applyDenoiseBtn.getStyleClass().add("black-btn");
        applyDenoiseBtn.setOnAction(e -> {
            if (selectedImagePath == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une image à débruiter.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            try {
                String type = denoiseTypeCombo.getValue();
                boolean isGlobal = type.equalsIgnoreCase("Global");
                String threshold = thresholdTypeCombo.getValue().toLowerCase();
                String shrink = shrinkMethodCombo.getValue().equals("VisuShrink") ? "v" : "b";
                double sigma = sigmaSlider.getValue();
                double patchPercent = patchSizeSlider.getValue() / 100.0;
                String baseName = Paths.get(selectedImagePath).getFileName().toString();
                String nameSansExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
                String outName = nameSansExt + "_denoised_" + (isGlobal ? "global" : "local") + "_" + threshold + "_" + shrink + ".png";
                String outPath = "img/img_denoised/" + outName;
                File outDir = new File("img/img_denoised");
                if (!outDir.exists()) outDir.mkdirs();
                core.acp.ImageDenoiser.ImageDen(
                    selectedImagePath,
                    outPath,
                    isGlobal,
                    threshold,
                    shrink,
                    sigma,
                    patchPercent
                );
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
        VBox denoiseContent = new VBox(10);
        denoiseContent.setPadding(new Insets(10));
        denoiseContent.getChildren().addAll(
            denoiseTitle,
            denoiseTypeLabel, denoiseTypeCombo,
            thresholdTypeLabel, thresholdTypeCombo,
            shrinkMethodLabel, shrinkMethodCombo,
            sigmaLabel, denoiseSigmaBox,
            patchSizeLabel, patchSizeBox,
            applyDenoiseBtn
        );

        Label paramsTitle = new Label("Paramètres");
        paramsTitle.getStyleClass().add("section-title");

        StackPane contentPane = new StackPane();
        contentPane.getChildren().add(noiseContent);

        // Logique de switch
        noiseBtn.setOnAction(e -> {
            contentPane.getChildren().setAll(noiseContent);
            noiseBtn.setStyle("-fx-background-color: #222; -fx-text-fill: #fff;");
            denoiseBtn.setStyle("-fx-background-color: #111; -fx-text-fill: #fff;");
        });
        denoiseBtn.setOnAction(e -> {
            contentPane.getChildren().setAll(denoiseContent);
            noiseBtn.setStyle("-fx-background-color: #111; -fx-text-fill: #fff;");
            denoiseBtn.setStyle("-fx-background-color: #222; -fx-text-fill: #fff;");
        });
        // Par défaut, Bruitage actif
        noiseBtn.setStyle("-fx-background-color: #222; -fx-text-fill: #fff;");

        rightColumn.getChildren().addAll(modeBtnBox, paramsTitle, contentPane);
        return rightColumn;
    }

    // Listener pour désactiver les boutons bruitage/débruitage selon le mode comparaison
    private Runnable compareModeListener = null;

    public static void main(String[] args) {
        launch(args);
    }
} 