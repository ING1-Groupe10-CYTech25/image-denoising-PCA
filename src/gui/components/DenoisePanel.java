package gui.components;

import java.io.File;
import java.nio.file.Paths;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Panneau de paramètres pour le débruitage d'images
 */
public class DenoisePanel extends VBox {

    // Interface de callback pour le traitement d'images
    public interface ImageProcessingListener {
        void onImageProcessed(String outputPath);
    }

    private ComboBox<String> denoiseTypeCombo;
    private ComboBox<String> thresholdTypeCombo;
    private ComboBox<String> shrinkMethodCombo;
    private Slider sigmaSlider;
    private Label denoiseSigmaValueLabel;
    private Slider patchSizeSlider;
    private Label patchSizeValueLabel;
    private Button applyDenoiseBtn;
    private String selectedImagePath;

    // Listener
    private ImageProcessingListener processingListener;
    private ImageDisplay imageDisplay;

    public DenoisePanel() {
        super(10);
        setPadding(new Insets(10));

        initializeComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        Label denoiseTitle = new Label("Paramètres de débruitage");
        denoiseTitle.getStyleClass().add("section-title");

        Label denoiseTypeLabel = new Label("Type de débruitage");
        denoiseTypeCombo = new ComboBox<>(
                FXCollections.observableArrayList("Global", "Local"));
        denoiseTypeCombo.setValue("Local");
        denoiseTypeCombo.setMaxWidth(Double.MAX_VALUE);

        Label thresholdTypeLabel = new Label("Type de seuillage");
        thresholdTypeCombo = new ComboBox<>(
                FXCollections.observableArrayList("Hard", "Soft"));
        thresholdTypeCombo.setValue("Hard");
        thresholdTypeCombo.setMaxWidth(Double.MAX_VALUE);

        Label shrinkMethodLabel = new Label("Méthode de réduction");
        shrinkMethodCombo = new ComboBox<>(
                FXCollections.observableArrayList("VisuShrink", "Bayes"));
        shrinkMethodCombo.setValue("VisuShrink");
        shrinkMethodCombo.setMaxWidth(Double.MAX_VALUE);

        Label sigmaLabel = new Label("Sigma estimé");
        sigmaSlider = new Slider(0, 50, 20);
        sigmaSlider.setShowTickMarks(true);
        sigmaSlider.setShowTickLabels(true);
        sigmaSlider.setMajorTickUnit(10);
        sigmaSlider.setMinorTickCount(1);
        sigmaSlider.setSnapToTicks(true);
        sigmaSlider.setMaxWidth(Double.MAX_VALUE);
        denoiseSigmaValueLabel = new Label("20");
        HBox denoiseSigmaBox = new HBox(10, sigmaSlider, denoiseSigmaValueLabel);
        denoiseSigmaBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sigmaSlider, Priority.ALWAYS);

        Label patchSizeLabel = new Label("Taille de patch (%)");
        patchSizeSlider = new Slider(1, 10, 5);
        patchSizeSlider.setShowTickMarks(true);
        patchSizeSlider.setShowTickLabels(true);
        patchSizeSlider.setMajorTickUnit(1);
        patchSizeSlider.setMinorTickCount(0);
        patchSizeSlider.setSnapToTicks(true);
        patchSizeSlider.setMaxWidth(Double.MAX_VALUE);
        patchSizeValueLabel = new Label("5%");
        HBox patchSizeBox = new HBox(10, patchSizeSlider, patchSizeValueLabel);
        patchSizeBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(patchSizeSlider, Priority.ALWAYS);

        applyDenoiseBtn = new Button("Appliquer le débruitage");
        applyDenoiseBtn.setMaxWidth(Double.MAX_VALUE);
        applyDenoiseBtn.getStyleClass().add("black-btn");

        getChildren().addAll(
                denoiseTitle,
                denoiseTypeLabel, denoiseTypeCombo,
                thresholdTypeLabel, thresholdTypeCombo,
                shrinkMethodLabel, shrinkMethodCombo,
                sigmaLabel, denoiseSigmaBox,
                patchSizeLabel, patchSizeBox,
                applyDenoiseBtn);
    }

    private void setupEventHandlers() {
        sigmaSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            denoiseSigmaValueLabel.setText(String.format("%.0f", newVal.doubleValue()));
        });

        patchSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            patchSizeValueLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });

        applyDenoiseBtn.setOnAction(e -> applyDenoise());
    }

    private void applyDenoise() {
        if (selectedImagePath == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une image à débruiter.",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            // Désactiver le bouton et ajouter la classe processing
            applyDenoiseBtn.setDisable(true);
            getScene().getRoot().getStyleClass().add("processing");

            String type = denoiseTypeCombo.getValue();
            boolean isGlobal = type.equalsIgnoreCase("Global");
            String threshold = thresholdTypeCombo.getValue().toLowerCase();
            String shrink = shrinkMethodCombo.getValue().equals("VisuShrink") ? "v" : "b";
            double sigma = sigmaSlider.getValue();
            double patchPercent = patchSizeSlider.getValue() / 100.0;

            String baseName = Paths.get(selectedImagePath).getFileName().toString();
            String nameSansExt = baseName.contains(".") ? baseName.substring(0, baseName.lastIndexOf('.')) : baseName;
            String outName = nameSansExt + "_denoised_" + (isGlobal ? "global" : "local") + "_" + threshold + "_"
                    + shrink + ".png";
            String outPath = "img/img_denoised/" + outName;

            File outDir = new File("img/img_denoised");
            if (!outDir.exists())
                outDir.mkdirs();

            core.acp.ImageDenoiser.ImageDen(
                    selectedImagePath,
                    outPath,
                    isGlobal,
                    threshold,
                    shrink,
                    sigma,
                    patchPercent);

            // Réactiver le bouton et retirer la classe processing
            applyDenoiseBtn.setDisable(false);
            getScene().getRoot().getStyleClass().remove("processing");

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Image débruitée sauvegardée avec succès :\n" + outPath, 
                    ButtonType.OK,
                    new ButtonType("Comparer les images"));
            successAlert.setTitle("Débruitage terminé");
            
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
                    // Mettre à jour l'affichage central avec l'image débruitée
                    if (imageDisplay != null) {
                        imageDisplay.displayImage(outPath);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            // Réactiver le bouton et retirer la classe processing en cas d'erreur
            applyDenoiseBtn.setDisable(false);
            getScene().getRoot().getStyleClass().remove("processing");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du débruitage : " + ex.getMessage(),
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

    public String getDenoiseType() {
        return denoiseTypeCombo.getValue();
    }

    public void setDenoiseType(String type) {
        denoiseTypeCombo.setValue(type);
    }

    public String getThresholdType() {
        return thresholdTypeCombo.getValue();
    }

    public void setThresholdType(String type) {
        thresholdTypeCombo.setValue(type);
    }

    public String getShrinkMethod() {
        return shrinkMethodCombo.getValue();
    }

    public void setShrinkMethod(String method) {
        shrinkMethodCombo.setValue(method);
    }

    public double getSigmaValue() {
        return sigmaSlider.getValue();
    }

    public void setSigmaValue(double sigma) {
        sigmaSlider.setValue(sigma);
    }

    public double getPatchSizePercent() {
        return patchSizeSlider.getValue();
    }

    public void setPatchSizePercent(double percent) {
        patchSizeSlider.setValue(percent);
    }
}