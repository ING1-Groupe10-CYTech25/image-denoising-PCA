package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Paths;

/**
 * Panneau de paramètres pour le bruitage d'images
 */
public class NoisePanel extends VBox {

    // Interface de callback pour le traitement d'images
    public interface ImageProcessingListener {
        void onImageProcessed(String outputPath);
    }

    private Slider sigmaNoiseSlider;
    private Label sigmaValueLabel;
    private Button applyNoiseBtn;
    private String selectedImagePath;

    // Listener
    private ImageProcessingListener processingListener;
    private ImageDisplay imageDisplay;

    public NoisePanel() {
        super(10);
        setPadding(new Insets(10));

        initializeComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        Label noiseTitle = new Label("Paramètres de bruitage");
        noiseTitle.getStyleClass().add("section-title");

        Label sigmaNoiseLabel = new Label("Intensité du bruit (Sigma)");

        sigmaNoiseSlider = new Slider(0, 50, 15);
        sigmaNoiseSlider.setShowTickMarks(true);
        sigmaNoiseSlider.setShowTickLabels(true);
        sigmaNoiseSlider.setMajorTickUnit(10);
        sigmaNoiseSlider.setMinorTickCount(1);
        sigmaNoiseSlider.setSnapToTicks(true);
        sigmaNoiseSlider.setMaxWidth(Double.MAX_VALUE);

        sigmaValueLabel = new Label("15");

        HBox sigmaBox = new HBox(10, sigmaNoiseSlider, sigmaValueLabel);
        sigmaBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sigmaNoiseSlider, Priority.ALWAYS);

        applyNoiseBtn = new Button("Appliquer le bruit");
        applyNoiseBtn.setMaxWidth(Double.MAX_VALUE);
        applyNoiseBtn.getStyleClass().add("black-btn");

        getChildren().addAll(noiseTitle, sigmaNoiseLabel, sigmaBox, applyNoiseBtn);
    }

    private void setupEventHandlers() {
        sigmaNoiseSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sigmaValueLabel.setText(String.format("%.0f", newVal.doubleValue()));
        });

        applyNoiseBtn.setOnAction(e -> applyNoise());
    }

    private void applyNoise() {
        if (selectedImagePath == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une image à bruiter.",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            // Désactiver le bouton et ajouter la classe processing
            applyNoiseBtn.setDisable(true);
            getScene().getRoot().getStyleClass().add("processing");

            core.image.ImageFile img = new core.image.ImageFile(selectedImagePath);
            int sigma = (int) sigmaNoiseSlider.getValue();
            img.noisify(sigma);

            String baseName = Paths.get(selectedImagePath).getFileName().toString();
            String nameSansExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
            String outName = nameSansExt + "_noised_" + sigma + ".png";
            String outPath = "img/img_noised/" + outName;

            File outDir = new File("img/img_noised");
            if (!outDir.exists())
                outDir.mkdirs();

            img.saveImage(outPath);

            // Réactiver le bouton et retirer la classe processing
            applyNoiseBtn.setDisable(false);
            getScene().getRoot().getStyleClass().remove("processing");

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Image bruitée sauvegardée avec succès :\n" + outPath, 
                    ButtonType.OK,
                    new ButtonType("Comparer les images"));
            successAlert.setTitle("Bruitage terminé");
            
            // Sauvegarde du chemin de l'image originale avant qu'il ne soit modifié
            final String originalImagePath = selectedImagePath;
            
            successAlert.showAndWait().ifPresent(response -> {
                // Notifier le listener pour ajouter l'image à la galerie dans tous les cas
                if (processingListener != null) {
                    processingListener.onImageProcessed(outPath);
                }
                
                if (response.getText().equals("Comparer les images")) {
                    // Notifier pour passer en mode comparaison en utilisant le chemin original
                    if (imageDisplay != null) {
                        imageDisplay.switchToCompareMode(originalImagePath, outPath);
                        imageDisplay.forceCompareButtonSelected();
                    }
                } else {
                    // Mettre à jour l'affichage central avec l'image bruitée
                    if (imageDisplay != null) {
                        imageDisplay.displayImage(outPath);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            // Réactiver le bouton et retirer la classe processing en cas d'erreur
            applyNoiseBtn.setDisable(false);
            getScene().getRoot().getStyleClass().remove("processing");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du bruitage : " + ex.getMessage(),
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Méthodes publiques pour l'interface
    public void setImageProcessingListener(ImageProcessingListener listener) {
        this.processingListener = listener;
    }

    public void setImageDisplay(ImageDisplay imageDisplay) {
        this.imageDisplay = imageDisplay;
    }

    public void setSelectedImagePath(String imagePath) {
        this.selectedImagePath = imagePath;
    }

    public int getSigmaValue() {
        return (int) sigmaNoiseSlider.getValue();
    }

    public void setSigmaValue(double sigma) {
        sigmaNoiseSlider.setValue(sigma);
    }
}