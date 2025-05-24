package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Panneau conteneur pour les paramètres de bruitage et débruitage
 * Gère le basculement entre les deux modes
 */
public class ParametersPanel extends VBox {

    // Interface de callback pour le traitement d'images
    public interface ImageProcessingListener {
        void onImageProcessed(String outputPath);
    }

    private ToggleButton noiseBtn;
    private ToggleButton denoiseBtn;
    private StackPane contentPane;
    private NoisePanel noisePanel;
    private DenoisePanel denoisePanel;
    private String selectedImagePath;

    // Listener
    private ImageProcessingListener processingListener;

    public ParametersPanel() {
        super(15);
        setPadding(new Insets(15));
        setMinWidth(360);
        setPrefWidth(360);
        setMaxWidth(360);
        getStyleClass().add("rounded-box");

        initializeComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Deux vrais boutons noirs pour Bruitage et Débruitage
        HBox modeBtnBox = new HBox(10);
        noiseBtn = new ToggleButton("Bruitage");
        denoiseBtn = new ToggleButton("Débruitage");
        noiseBtn.getStyleClass().add("action-btn");
        denoiseBtn.getStyleClass().add("action-btn");
        noiseBtn.setMinWidth(120);
        denoiseBtn.setMinWidth(120);
        
        ToggleGroup modeGroup = new ToggleGroup();
        noiseBtn.setToggleGroup(modeGroup);
        denoiseBtn.setToggleGroup(modeGroup);
        noiseBtn.setSelected(true);
        
        modeBtnBox.getChildren().addAll(noiseBtn, denoiseBtn);
        modeBtnBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(modeBtnBox, new Insets(0, 0, 10, 0));

        // Créer les panneaux de paramètres
        noisePanel = new NoisePanel();
        denoisePanel = new DenoisePanel();

        // Configurer les listeners
        noisePanel.setImageProcessingListener(outputPath -> {
            if (processingListener != null) {
                processingListener.onImageProcessed(outputPath);
            }
        });

        denoisePanel.setImageProcessingListener(outputPath -> {
            if (processingListener != null) {
                processingListener.onImageProcessed(outputPath);
            }
        });

        Label paramsTitle = new Label("Paramètres");
        paramsTitle.getStyleClass().add("section-title");

        contentPane = new StackPane();
        contentPane.getChildren().add(noisePanel);

        getChildren().addAll(modeBtnBox, paramsTitle, contentPane);
    }

    private void setupEventHandlers() {
        // Logique de switch
        noiseBtn.setOnAction(e -> {
            if (!noiseBtn.isSelected()) {
                noiseBtn.setSelected(true);
            }
            contentPane.getChildren().setAll(noisePanel);
        });

        denoiseBtn.setOnAction(e -> {
            if (!denoiseBtn.isSelected()) {
                denoiseBtn.setSelected(true);
            }
            contentPane.getChildren().setAll(denoisePanel);
        });
    }

    // Méthodes publiques pour l'interface
    public void setImageProcessingListener(ImageProcessingListener listener) {
        this.processingListener = listener;
    }

    public void setSelectedImagePath(String imagePath) {
        this.selectedImagePath = imagePath;
        noisePanel.setSelectedImagePath(imagePath);
        denoisePanel.setSelectedImagePath(imagePath);
    }

    public String getSelectedImagePath() {
        return selectedImagePath;
    }

    public NoisePanel getNoisePanel() {
        return noisePanel;
    }

    public DenoisePanel getDenoisePanel() {
        return denoisePanel;
    }

    public void switchToNoiseMode() {
        noiseBtn.setSelected(true);
        denoiseBtn.setSelected(false);
        contentPane.getChildren().setAll(noisePanel);
    }

    public void switchToDenoiseMode() {
        denoiseBtn.setSelected(true);
        noiseBtn.setSelected(false);
        contentPane.getChildren().setAll(denoisePanel);
    }
}