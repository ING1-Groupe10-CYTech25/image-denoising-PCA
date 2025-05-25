package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import core.eval.ImageQualityMetrics;

/**
 * Panneau d'affichage des métriques de comparaison d'images
 */
public class MetricsPanel extends VBox {

    private Label mseLabel;
    private Label psnrLabel;
    private Label titleLabel;
    private Label originalMseLabel;
    private Label originalPsnrLabel;
    private ImageView img1View;
    private ImageView img2View;
    private ImageView originalImgView;
    private Label img1NameLabel;
    private Label img2NameLabel;
    private Label originalImgNameLabel;
    private List<String> availableImages;

    public MetricsPanel() {
        super(15);
        setPadding(new Insets(15));
        setMinWidth(360);
        setPrefWidth(360);
        setMaxWidth(360);
        getStyleClass().add("rounded-box");

        initializeComponents();
    }

    private void initializeComponents() {
        titleLabel = new Label("Métriques de comparaison");
        titleLabel.getStyleClass().add("section-title");

        // Création des conteneurs pour les images
        HBox imagesBox = new HBox(10);
        imagesBox.setAlignment(Pos.CENTER);

        // Image 1
        VBox img1Box = new VBox(5);
        img1View = new ImageView();
        img1View.setFitWidth(100);
        img1View.setFitHeight(100);
        img1View.setPreserveRatio(true);
        img1NameLabel = new Label();
        img1NameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        img1Box.getChildren().addAll(img1View, img1NameLabel);
        img1Box.setAlignment(Pos.CENTER);

        // Image 2
        VBox img2Box = new VBox(5);
        img2View = new ImageView();
        img2View.setFitWidth(100);
        img2View.setFitHeight(100);
        img2View.setPreserveRatio(true);
        img2NameLabel = new Label();
        img2NameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        img2Box.getChildren().addAll(img2View, img2NameLabel);
        img2Box.setAlignment(Pos.CENTER);

        // Image originale (initialement cachée)
        VBox originalImgBox = new VBox(5);
        originalImgView = new ImageView();
        originalImgView.setFitWidth(100);
        originalImgView.setFitHeight(100);
        originalImgView.setPreserveRatio(true);
        originalImgNameLabel = new Label();
        originalImgNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        originalImgBox.getChildren().addAll(originalImgView, originalImgNameLabel);
        originalImgBox.setAlignment(Pos.CENTER);
        originalImgBox.setVisible(false);

        imagesBox.getChildren().addAll(img1Box, img2Box, originalImgBox);

        // Labels des métriques
        mseLabel = new Label("MSE entre les deux images:\n-");
        mseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        mseLabel.setMaxWidth(Double.MAX_VALUE);
        mseLabel.setAlignment(Pos.CENTER_LEFT);

        psnrLabel = new Label("PSNR entre les deux images:\n- dB");
        psnrLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        psnrLabel.setMaxWidth(Double.MAX_VALUE);
        psnrLabel.setAlignment(Pos.CENTER_LEFT);

        originalMseLabel = new Label("MSE vs Original:\nImage 1: - (Δ: -)\nImage 2: - (Δ: -)");
        originalMseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        originalMseLabel.setMaxWidth(Double.MAX_VALUE);
        originalMseLabel.setAlignment(Pos.CENTER_LEFT);
        originalMseLabel.setVisible(false);

        originalPsnrLabel = new Label("PSNR vs Original:\nImage 1: - dB (Δ: - dB)\nImage 2: - dB (Δ: - dB)");
        originalPsnrLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        originalPsnrLabel.setMaxWidth(Double.MAX_VALUE);
        originalPsnrLabel.setAlignment(Pos.CENTER_LEFT);
        originalPsnrLabel.setVisible(false);

        getChildren().addAll(titleLabel, imagesBox, mseLabel, psnrLabel, originalMseLabel, originalPsnrLabel);
    }

    public void updateMetrics(String image1Path, String image2Path, List<String> availableImages) {
        this.availableImages = availableImages;
        
        // Mettre à jour les images et leurs noms
        updateImageDisplay(img1View, img1NameLabel, image1Path);
        updateImageDisplay(img2View, img2NameLabel, image2Path);

        // Vérifier si les images sont de la même famille
        String baseName1 = getBaseName(image1Path);
        String baseName2 = getBaseName(image2Path);

        if (baseName1.equals(baseName2)) {
            // Chercher l'image originale
            String originalPath = findOriginalImage(baseName1);
            if (originalPath != null) {
                updateImageDisplay(originalImgView, originalImgNameLabel, originalPath);
                originalImgView.getParent().setVisible(true);
                originalMseLabel.setVisible(true);
                originalPsnrLabel.setVisible(true);

                // Calculer les métriques avec l'originale
                try {
                    BufferedImage original = ImageIO.read(new File(originalPath));
                    BufferedImage img1 = ImageIO.read(new File(image1Path));
                    BufferedImage img2 = ImageIO.read(new File(image2Path));

                    double mse1 = ImageQualityMetrics.calculateMSE(original, img1);
                    double psnr1 = ImageQualityMetrics.calculatePSNR(mse1, 255);
                    double mse2 = ImageQualityMetrics.calculateMSE(original, img2);
                    double psnr2 = ImageQualityMetrics.calculatePSNR(mse2, 255);

                    // Calculer les différences
                    double mseDiff1 = mse1 - mse2;
                    double mseDiff2 = mse2 - mse1;
                    double psnrDiff1 = psnr1 - psnr2;
                    double psnrDiff2 = psnr2 - psnr1;

                    originalMseLabel.setText(String.format("MSE vs Original:\nImage 1: %.2f (Δ: %.2f)\nImage 2: %.2f (Δ: %.2f)", 
                        mse1, mseDiff1, mse2, mseDiff2));
                    originalPsnrLabel.setText(String.format("PSNR vs Original:\nImage 1: %.2f dB (Δ: %.2f dB)\nImage 2: %.2f dB (Δ: %.2f dB)", 
                        psnr1, psnrDiff1, psnr2, psnrDiff2));
                } catch (Exception e) {
                    originalMseLabel.setText("Erreur de calcul des métriques");
                    originalPsnrLabel.setText("Erreur de calcul des métriques");
                }
            } else {
                originalImgView.getParent().setVisible(false);
                originalMseLabel.setVisible(false);
                originalPsnrLabel.setVisible(false);
            }
        } else {
            originalImgView.getParent().setVisible(false);
            originalMseLabel.setVisible(false);
            originalPsnrLabel.setVisible(false);
        }

        // Calculer les métriques entre les deux images
        try {
            BufferedImage img1 = ImageIO.read(new File(image1Path));
            BufferedImage img2 = ImageIO.read(new File(image2Path));
            
            double mse = ImageQualityMetrics.calculateMSE(img1, img2);
            double psnr = ImageQualityMetrics.calculatePSNR(mse, 255);
            
            mseLabel.setText(String.format("MSE entre les deux images:\n%.2f", mse));
            psnrLabel.setText(String.format("PSNR entre les deux images:\n%.2f dB", psnr));
        } catch (Exception e) {
            mseLabel.setText("Erreur de calcul des métriques");
            psnrLabel.setText("Erreur de calcul des métriques");
        }
    }

    private void updateImageDisplay(ImageView imageView, Label nameLabel, String imagePath) {
        try {
            Image img = new Image(Paths.get(imagePath).toUri().toString());
            imageView.setImage(img);
            nameLabel.setText(Paths.get(imagePath).getFileName().toString());
        } catch (Exception e) {
            imageView.setImage(null);
            nameLabel.setText("Erreur de chargement");
        }
    }

    private String getBaseName(String path) {
        String fileName = Paths.get(path).getFileName().toString();
        // Extraire le nom de base (avant le premier underscore ou point)
        int underscoreIndex = fileName.indexOf('_');
        int dotIndex = fileName.indexOf('.');
        int endIndex = underscoreIndex == -1 ? dotIndex : 
                      (dotIndex == -1 ? underscoreIndex : Math.min(underscoreIndex, dotIndex));
        return endIndex == -1 ? fileName : fileName.substring(0, endIndex);
    }

    private String findOriginalImage(String baseName) {
        if (availableImages == null) return null;
        
        for (String path : availableImages) {
            String fileName = Paths.get(path).getFileName().toString();
            if (fileName.startsWith(baseName) && !fileName.contains("_noised") && !fileName.contains("_denoised")) {
                return path;
            }
        }
        return null;
    }

    public void resetMetrics() {
        mseLabel.setText("MSE entre les deux images:\n-");
        psnrLabel.setText("PSNR entre les deux images:\n- dB");
        originalMseLabel.setText("MSE vs Original:\nImage 1: - (Δ: -)\nImage 2: - (Δ: -)");
        originalPsnrLabel.setText("PSNR vs Original:\nImage 1: - dB (Δ: - dB)\nImage 2: - dB (Δ: - dB)");
        originalImgView.getParent().setVisible(false);
        originalMseLabel.setVisible(false);
        originalPsnrLabel.setVisible(false);
    }
} 