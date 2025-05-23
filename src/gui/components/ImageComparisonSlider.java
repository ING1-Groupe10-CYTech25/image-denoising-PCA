package gui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Region;

public class ImageComparisonSlider extends StackPane {
    private ImageView leftImageView;
    private ImageView rightImageView;
    private Slider slider;
    private Rectangle clip;
    private Label helpLabel;

    public ImageComparisonSlider() {
        // Configuration du conteneur principal
        setStyle("-fx-background-color: #F5F5F5;");
        setPrefHeight(500);
        setAlignment(Pos.CENTER);

        // Création des ImageView
        leftImageView = new ImageView();
        rightImageView = new ImageView();
        
        // Configuration des ImageView
        leftImageView.setPreserveRatio(true);
        rightImageView.setPreserveRatio(true);
        leftImageView.setFitWidth(Region.USE_COMPUTED_SIZE);
        rightImageView.setFitWidth(Region.USE_COMPUTED_SIZE);
        leftImageView.setFitHeight(Region.USE_COMPUTED_SIZE);
        rightImageView.setFitHeight(Region.USE_COMPUTED_SIZE);

        // Création du clip pour l'image de droite
        clip = new Rectangle();
        clip.setWidth(0);
        clip.setHeight(0);
        rightImageView.setClip(clip);

        // Création du slider
        slider = new Slider(0, 100, 50);
        slider.setOrientation(javafx.geometry.Orientation.VERTICAL);
        slider.setPrefHeight(Region.USE_COMPUTED_SIZE);
        slider.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 0;
            -fx-pref-width: 20;
            -fx-pref-height: 20;
        """);

        // Création du label d'aide
        helpLabel = new Label("Sélectionnez deux images à comparer");
        helpLabel.getStyleClass().add("help-text");
        helpLabel.setVisible(true);

        // Ajout des composants
        getChildren().addAll(leftImageView, rightImageView, slider, helpLabel);

        // Gestion du redimensionnement
        widthProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.doubleValue() * (slider.getValue() / 100));
            clip.setHeight(getHeight());
        });

        heightProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(newVal.doubleValue());
        });

        // Gestion du slider
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(getWidth() * (newVal.doubleValue() / 100));
        });
    }

    public void setLeftImage(Image image) {
        leftImageView.setImage(image);
        updateImageVisibility();
    }

    public void setRightImage(Image image) {
        rightImageView.setImage(image);
        updateImageVisibility();
    }

    private void updateImageVisibility() {
        boolean hasImages = leftImageView.getImage() != null && rightImageView.getImage() != null;
        helpLabel.setVisible(!hasImages);
        slider.setVisible(hasImages);
    }

    public void reset() {
        slider.setValue(50);
        leftImageView.setImage(null);
        rightImageView.setImage(null);
        updateImageVisibility();
    }
} 