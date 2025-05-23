package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Panneau d'affichage des métriques de comparaison d'images
 */
public class MetricsPanel extends VBox {

    private Label mseLabel;
    private Label psnrLabel;
    private Label titleLabel;

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

        mseLabel = new Label("MSE: -");
        mseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        mseLabel.setMaxWidth(Double.MAX_VALUE);
        mseLabel.setAlignment(Pos.CENTER_LEFT);

        psnrLabel = new Label("PSNR: - dB");
        psnrLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        psnrLabel.setMaxWidth(Double.MAX_VALUE);
        psnrLabel.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(titleLabel, mseLabel, psnrLabel);
    }

    public void updateMetrics(double mse, double psnr) {
        mseLabel.setText(String.format("MSE: %.2f", mse));
        psnrLabel.setText(String.format("PSNR: %.2f dB", psnr));
    }

    public void resetMetrics() {
        mseLabel.setText("MSE: -");
        psnrLabel.setText("PSNR: - dB");
    }
} 