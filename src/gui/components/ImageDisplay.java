package gui.components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import core.eval.ImageQualityMetrics;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Composant responsable de l'affichage des images
 * - Affichage simple d'une image
 * - Mode comparaison avec slider
 * - Boutons de basculement entre les modes
 */
public class ImageDisplay extends VBox {

    // Interface de callback pour le changement de mode
    public interface ModeChangeListener {
        void onModeChange(boolean isCompareMode);
    }

    // Interface de callback pour la mise à jour des métriques
    public interface MetricsUpdateListener {
        void onMetricsUpdate(String image1Path, String image2Path, List<String> availableImages);
    }

    private ImageView centerImageView = new ImageView();
    private Label centerImageNameLabel = new Label();
    private boolean compareMode = false;
    private String compareImage1 = null;
    private String compareImage2 = null;
    private ComboBox<String> compareImg1Combo = null;
    private ComboBox<String> compareImg2Combo = null;
    private StackPane dynamicDisplay;
    private StackPane imageDisplay;
    private List<String> availableImages;

    // ImageView pour l'image de remplacement
    private ImageView placeholderImageView;

    // Listeners
    private ModeChangeListener modeChangeListener;
    private MetricsUpdateListener metricsUpdateListener;

    // Références aux boutons pour contrôle externe
    private ToggleButton displayBtnRef;
    private ToggleButton compareBtnRef;

    public ImageDisplay() {
        super(15);
        setPadding(new Insets(15));
        getStyleClass().add("rounded-box");

        initializeComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Boutons Affichage et Comparer côte à côte
        ToggleButton displayBtn = new ToggleButton("Vue simple");
        displayBtn.setSelected(true);
        displayBtn.getStyleClass().addAll("action-btn");
        this.displayBtnRef = displayBtn;
        
        ToggleButton compareBtn = new ToggleButton("Comparaison");
        compareBtn.getStyleClass().addAll("action-btn");
        this.compareBtnRef = compareBtn;
        
        ToggleGroup modeGroup = new ToggleGroup();
        displayBtn.setToggleGroup(modeGroup);
        compareBtn.setToggleGroup(modeGroup);
        
        HBox topBtnBox = new HBox(10, displayBtn, compareBtn);
        topBtnBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(displayBtn, Priority.ALWAYS);
        HBox.setHgrow(compareBtn, Priority.ALWAYS);
        VBox.setMargin(topBtnBox, new Insets(0, 0, 10, 0));
        topBtnBox.setFillHeight(true);
        topBtnBox.setMaxWidth(Double.MAX_VALUE);

        // Conteneur pour l'affichage dynamique (image simple ou comparaison)
        dynamicDisplay = new StackPane();
        dynamicDisplay.setPrefHeight(500);
        dynamicDisplay.setStyle("-fx-background-color: #F5F5F5;");

        // Label du nom de l'image sélectionnée (mode simple)
        centerImageNameLabel.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #444; -fx-padding: 0; -fx-background-color: transparent;");
        centerImageNameLabel.setAlignment(Pos.CENTER);
        centerImageNameLabel.setMaxWidth(Double.MAX_VALUE);
        centerImageNameLabel.setWrapText(true);
        VBox.setMargin(centerImageNameLabel, Insets.EMPTY);

        // Affichage normal (image simple)
        imageDisplay = new StackPane(centerImageView);
        imageDisplay.setPrefHeight(500);
        centerImageView.setPreserveRatio(true);
        centerImageView.setFitWidth(500);
        centerImageView.setFitHeight(500);

        // Charger l'image de remplacement
        try {
            Image placeholderImage = new Image(getClass().getResourceAsStream("/placeholder.png"));
            placeholderImageView = new ImageView(placeholderImage);
            placeholderImageView.setPreserveRatio(true);
            placeholderImageView.setFitWidth(500);
            placeholderImageView.setFitHeight(500);
            placeholderImageView.setStyle("-fx-opacity: 0.5;");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du placeholder : " + e.getMessage());
            placeholderImageView = new ImageView(); // Fallback si le placeholder ne charge pas
        }

        // Affichage initial
        updateCenterDisplay();

        // Event handlers pour les boutons
        displayBtn.setOnAction(e -> {
            if (!displayBtn.isSelected()) {
                displayBtn.setSelected(true);
            }
            compareMode = false;
            updateCenterDisplay();
            notifyModeChange();
        });

        compareBtn.setOnAction(e -> {
            if (!compareBtn.isSelected()) {
                compareBtn.setSelected(true);
            }
            if (availableImages == null || availableImages.size() < 2) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Il faut au moins deux images pour comparer. Images disponibles : " + 
                        (availableImages == null ? "0" : availableImages.size()), ButtonType.OK);
                alert.showAndWait();
                compareBtn.setSelected(false);
                displayBtn.setSelected(true);
                return;
            }
            compareMode = true;
            // Par défaut, prendre les deux images les plus récentes
            if (compareImage1 == null && !availableImages.isEmpty()) {
                compareImage1 = availableImages.get(0); // La plus récente
                compareImage2 = availableImages.get(1); // La deuxième plus récente
                // Mettre à jour les métriques avec les nouvelles images
                updateMetrics(compareImage1, compareImage2);
            }
            updateCenterDisplay();
            notifyModeChange();
        });

        getChildren().addAll(topBtnBox, dynamicDisplay, centerImageNameLabel);
    }

    private void setupEventHandlers() {
        // Les event handlers sont déjà configurés dans initializeComponents
    }

    // Met à jour l'affichage central selon le mode
    private void updateCenterDisplay() {
        dynamicDisplay.getChildren().clear();
        if (compareMode) {
            VBox compareBox = createCompareBox();
            dynamicDisplay.getChildren().add(compareBox);
            centerImageNameLabel.setVisible(false);
        } else {
            // Afficher l'image normale ou le placeholder si aucune image n'est chargée
            if (centerImageView.getImage() != null) {
                dynamicDisplay.getChildren().add(imageDisplay);
                centerImageNameLabel.setVisible(true);
            } else {
                dynamicDisplay.getChildren().add(placeholderImageView);
                centerImageNameLabel.setVisible(false); // Cacher le label quand le placeholder est affiché
            }
        }
    }

    // Crée le composant de comparaison interactif
    private VBox createCompareBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);

        // Sélection des images à comparer
        HBox selectors = new HBox(10);
        selectors.setAlignment(Pos.CENTER);
        compareImg1Combo = new ComboBox<>(FXCollections
                .observableArrayList(availableImages != null ? availableImages : FXCollections.emptyObservableList()));
        compareImg2Combo = new ComboBox<>(FXCollections
                .observableArrayList(availableImages != null ? availableImages : FXCollections.emptyObservableList()));
        compareImg1Combo.setValue(compareImage1);
        compareImg2Combo.setValue(compareImage2);
        compareImg1Combo.setMaxWidth(240);
        compareImg2Combo.setMaxWidth(240);

        // Petite taille de font pour les nom de fichiers dans la ComboBox
        compareImg1Combo.setStyle("-fx-font-size: 10px;");
        compareImg2Combo.setStyle("-fx-font-size: 10px;");
        compareImg1Combo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(String path) {
                return (path == null) ? "" : java.nio.file.Paths.get(path).getFileName().toString();
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });
        compareImg2Combo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(String path) {
                return (path == null) ? "" : java.nio.file.Paths.get(path).getFileName().toString();
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });
        selectors.getChildren().addAll(compareImg1Combo, compareImg2Combo);

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
        // Inverser l'affichage : img1View affiche compareImage2 et img2View affiche
        // compareImage1
        if (compareImage2 != null)
            img1View.setImage(new Image(Paths.get(compareImage2).toUri().toString()));
        if (compareImage1 != null)
            img2View.setImage(new Image(Paths.get(compareImage1).toUri().toString()));

        // Barre horizontale (slider)
        Slider slider = new Slider(0, 1, 0.5);
        slider.setPrefWidth(500);
        slider.setStyle("-fx-padding: 0 0 0 0;");

        // Clip dynamique sur img2View (qui affiche maintenant compareImage1)
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

        comparePane.getChildren().addAll(img1View, img2View);

        // Labels "Image 1" et "Image 2"
        Label label1 = new Label("Image 1");
        label1.setStyle(
                "-fx-background-color: rgba(255,255,255,0.7); -fx-font-size: 13px; -fx-padding: 4 10 4 10; -fx-border-radius: 6; -fx-background-radius: 6;");
        label1.setAlignment(Pos.TOP_LEFT);
        label1.setTranslateX(-210);
        label1.setTranslateY(10 - comparePane.getPrefHeight() / 2);
        Label label2 = new Label("Image 2");
        label2.setStyle(
                "-fx-background-color: rgba(255,255,255,0.7); -fx-font-size: 13px; -fx-padding: 4 10 4 10; -fx-border-radius: 6; -fx-background-radius: 6;");
        label2.setAlignment(Pos.TOP_RIGHT);
        label2.setTranslateX(210);
        label2.setTranslateY(10 - comparePane.getPrefHeight() / 2);
        comparePane.getChildren().addAll(label1, label2);

        // Slider sous l'image
        HBox sliderBox = new HBox(slider);
        sliderBox.setAlignment(Pos.CENTER);
        sliderBox.setPadding(new Insets(10, 0, 0, 0));

        // Mise à jour des images lors du changement de sélection
        compareImg1Combo.setOnAction(e -> {
            compareImage1 = compareImg1Combo.getValue();
            // img2View affiche maintenant compareImage1 (après inversion)
            if (compareImage1 != null)
                img2View.setImage(new Image(Paths.get(compareImage1).toUri().toString()));
            // Mettre à jour les métriques
            if (compareImage1 != null && compareImage2 != null) {
                updateMetrics(compareImage1, compareImage2);
            }
        });
        compareImg2Combo.setOnAction(e -> {
            compareImage2 = compareImg2Combo.getValue();
            // img1View affiche maintenant compareImage2 (après inversion)
            if (compareImage2 != null)
                img1View.setImage(new Image(Paths.get(compareImage2).toUri().toString()));
            // Mettre à jour les métriques
            if (compareImage1 != null && compareImage2 != null) {
                updateMetrics(compareImage1, compareImage2);
            }
        });

        // Mettre à jour les métriques initiales
        if (compareImage1 != null && compareImage2 != null) {
            updateMetrics(compareImage1, compareImage2);
        }

        box.getChildren().addAll(selectors, comparePane, sliderBox);
        return box;
    }

    private void notifyModeChange() {
        if (modeChangeListener != null) {
            modeChangeListener.onModeChange(compareMode);
        }
    }

    // Méthodes publiques pour l'interface
    public void setModeChangeListener(ModeChangeListener listener) {
        this.modeChangeListener = listener;
    }

    public void setMetricsUpdateListener(MetricsUpdateListener listener) {
        this.metricsUpdateListener = listener;
    }

    public void displayImage(String imagePath) {
        if (imagePath != null) {
            try {
                Image img = new Image(Paths.get(imagePath).toUri().toString());
                centerImageView.setImage(img);
                centerImageNameLabel.setText(Paths.get(imagePath).getFileName().toString());
                
                // Basculer en mode affichage simple et mettre à jour le bouton
                if (compareMode) {
                    compareMode = false;
                    displayBtnRef.setSelected(true);
                    compareBtnRef.setSelected(false);
                    notifyModeChange();
                }
            } catch (Exception ex) {
                System.err.println("Erreur lors du chargement de l'image : " + ex.getMessage());
                centerImageView.setImage(null); // Définit l'image sur null en cas d'erreur
                centerImageNameLabel.setText("");
            }
        } else {
            centerImageView.setImage(null); // Définit l'image sur null si le chemin est null
            centerImageNameLabel.setText("");
        }
        updateCenterDisplay(); // Mettre à jour l'affichage (image ou placeholder)
    }

    public void updateAvailableImages(List<String> images) {
        // Trier les images par date de création (les plus récentes d'abord)
        List<String> sortedImages = new ArrayList<>(images);
        sortedImages.sort((path1, path2) -> {
            try {
                long time1 = java.nio.file.Files.getLastModifiedTime(java.nio.file.Paths.get(path1)).toMillis();
                long time2 = java.nio.file.Files.getLastModifiedTime(java.nio.file.Paths.get(path2)).toMillis();
                return Long.compare(time2, time1); // Ordre décroissant (plus récent d'abord)
            } catch (java.io.IOException e) {
                return 0;
            }
        });
        
        this.availableImages = sortedImages;
        if (compareImg1Combo != null && compareImg2Combo != null) {
            compareImg1Combo.setItems(FXCollections.observableArrayList(sortedImages));
            compareImg2Combo.setItems(FXCollections.observableArrayList(sortedImages));
            
            // Si on est en mode comparaison, mettre à jour les métriques
            if (compareMode && compareImage1 != null && compareImage2 != null) {
                updateMetrics(compareImage1, compareImage2);
            }
        }
    }

    public void switchToCompareMode(String image1, String image2) {
        compareMode = true;
        compareImage1 = image1;
        compareImage2 = image2;
        updateCenterDisplay();
        // Mettre à jour les métriques avec les nouvelles images
        if (compareImage1 != null && compareImage2 != null) {
            updateMetrics(compareImage1, compareImage2);
        }
        notifyModeChange();
    }

    public boolean isCompareMode() {
        return compareMode;
    }

    private void updateMetrics(String image1Path, String image2Path) {
        try {
            BufferedImage img1 = ImageIO.read(new File(image1Path));
            BufferedImage img2 = ImageIO.read(new File(image2Path));
            
            double mse = ImageQualityMetrics.calculateMSE(img1, img2);
            double psnr = ImageQualityMetrics.calculatePSNR(mse, 255);
            
            // Notifier le listener des métriques avec les chemins des images et la liste des images disponibles
            if (metricsUpdateListener != null) {
                metricsUpdateListener.onMetricsUpdate(image1Path, image2Path, availableImages);
            }
        } catch (Exception e) {
            if (metricsUpdateListener != null) {
                metricsUpdateListener.onMetricsUpdate(null, null, availableImages);
            }
        }
    }

    // Méthode publique pour forcer la sélection du bouton Comparaison
    public void forceCompareButtonSelected() {
        if (compareBtnRef != null) {
            compareBtnRef.setSelected(true);
        }
    }
}

