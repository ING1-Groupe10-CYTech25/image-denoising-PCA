package gui.components;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant responsable de la gestion de la galerie d'images
 * - Affichage des miniatures
 * - Import/suppression d'images
 * - Filtrage par type
 */
public class ImageGallery extends VBox {

    // Interface de callback pour la sélection d'image
    public interface ImageSelectionListener {
        void onImageSelected(String imagePath);
    }

    public interface ImageListChangeListener {
        void onImageListChange(List<String> images);
    }

    private TilePane imageTilePane;
    private List<String> importedImages = new ArrayList<>();
    private List<String> recentImages = new ArrayList<>();
    private Label noImageLabel;
    private String selectedImagePath = null;
    private String currentFilter = "Toutes";

    // Listeners
    private ImageSelectionListener selectionListener;
    private ImageListChangeListener listChangeListener;

    public ImageGallery() {
        super(15);
        setPadding(new Insets(15));
        setMinWidth(240);
        setPrefWidth(240);
        setMaxWidth(240);
        getStyleClass().add("rounded-box");

        initializeComponents();
        setupEventHandlers();
        loadImagesFromImgFolders();
        updateNoImageLabel();
    }

    private void initializeComponents() {
        Label titleLabel = new Label("Gestion des Images");
        titleLabel.getStyleClass().add("title");

        ComboBox<String> filterCombo = new ComboBox<>(
                FXCollections.observableArrayList("Toutes", "Originales", "Bruitées", "Débruitées", "Récent"));
        filterCombo.setPromptText("Filtrer les images");
        filterCombo.setValue("Toutes");
        filterCombo.setOnAction(e -> {
            currentFilter = filterCombo.getValue();
            updateImageGallery();
        });

        HBox buttonBox = createButtonBox();

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

        getChildren().addAll(titleLabel, filterCombo, buttonBox, scrollPane, noImageLabel);
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);

        // Bouton d'import avec icône
        try {
            javafx.scene.image.Image importIcon = new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/import.png"), 18, 18, true, true);
            ImageView importIconView = new ImageView(importIcon);
            Button importBtn = new Button("", importIconView);
            importBtn.setMinWidth(90);
            importBtn.setPrefWidth(110);
            importBtn.setMaxWidth(Double.MAX_VALUE);
            importBtn.getStyleClass().addAll("gallery-btn", "black-btn", "centered-text");

            Button deleteBtn = new Button("🗑️ Supprimer");
            deleteBtn.setMinWidth(90);
            deleteBtn.setPrefWidth(110);
            deleteBtn.setMaxWidth(Double.MAX_VALUE);
            deleteBtn.getStyleClass().addAll("gallery-btn", "red-btn");

            buttonBox.getChildren().addAll(importBtn, deleteBtn);

            // Event handlers avec des noms de paramètres différents pr éviter les conflits
            importBtn.setOnAction(importEvent -> importImage());
            deleteBtn.setOnAction(deleteEvent -> deleteSelectedImage());

        } catch (Exception ex) {
            // Si jamais l'icône n'est pas trouvée
            Button importBtn = new Button("📁 Importer");
            Button deleteBtn = new Button("🗑️ Supprimer");
            importBtn.getStyleClass().addAll("gallery-btn", "black-btn");
            deleteBtn.getStyleClass().addAll("gallery-btn", "red-btn");
            buttonBox.getChildren().addAll(importBtn, deleteBtn);

            importBtn.setOnAction(importEvent -> importImage());
            deleteBtn.setOnAction(deleteEvent -> deleteSelectedImage());
        }

        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillHeight(false);

        return buttonBox;
    }

    private void setupEventHandlers() {
        // Les event handlers sont déjà configurés dans les méthodes de création
    }

    private void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer une image");

        // Se placer dans le dossier img par défaut
        File imgDir = new File("img");
        if (imgDir.exists() && imgDir.isDirectory()) {
            fileChooser.setInitialDirectory(imgDir);
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif", "*.tif",
                        "*.tiff"));
        File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            if (!importedImages.contains(path)) {
                importedImages.add(path);
                updateImageGallery();
                notifyImageListChange();
            }
        }
        updateNoImageLabel();
    }

    private void deleteSelectedImage() {
        if (selectedImagePath != null) {
            importedImages.remove(selectedImagePath);
            selectedImagePath = null;
            updateImageGallery();
            notifyImageListChange();

            // Notifier la sélection d'image nulle
            if (selectionListener != null) {
                selectionListener.onImageSelected(null);
            }
        }
        updateNoImageLabel();
    }

    private void updateImageGallery() {
        imageTilePane.getChildren().clear();
        List<String> filteredImages = filterImages(importedImages);
        for (String path : filteredImages) {
            VBox card = createImageCard(path);
            imageTilePane.getChildren().add(card);
        }
    }

    private VBox createImageCard(String path) {
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
            card.setStyle(card.getStyle()
                    + "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, #4CAF50, 8, 0.2, 0, 0);");
        } else {
            card.setStyle(card.getStyle() + "-fx-border-color: #E0E0E0; -fx-border-width: 1;");
        }

        card.setOnMouseClicked((MouseEvent e) -> {
            selectedImagePath = path;
            updateImageGallery();

            // Notifier la sélection
            if (selectionListener != null) {
                selectionListener.onImageSelected(path);
            }
        });

        return card;
    }

    private List<String> filterImages(List<String> images) {
        if (currentFilter.equals("Toutes")) {
            return new ArrayList<>(images);
        } else if (currentFilter.equals("Récent")) {
            return new ArrayList<>(recentImages);
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

    private void updateNoImageLabel() {
        boolean empty = filterImages(importedImages).isEmpty();
        noImageLabel.setVisible(empty);
        imageTilePane.setVisible(!empty);
    }

    private void notifyImageListChange() {
        if (listChangeListener != null) {
            listChangeListener.onImageListChange(new ArrayList<>(importedImages));
        }
    }

    // Méthodes publiques pour l'interface
    public void setImageSelectionListener(ImageSelectionListener listener) {
        this.selectionListener = listener;
    }

    public void setImageListChangeListener(ImageListChangeListener listener) {
        this.listChangeListener = listener;
    }

    public List<String> getImportedImages() {
        return new ArrayList<>(importedImages);
    }

    public String getSelectedImagePath() {
        return selectedImagePath;
    }

    public void addImage(String imagePath) {
        if (!importedImages.contains(imagePath)) {
            importedImages.add(imagePath);
            updateImageGallery();
            notifyImageListChange();
        }
    }

    /**
     * Définit l'image sélectionnée et met à jour l'affichage
     * @param imagePath le chemin de l'image à sélectionner
     */
    public void setSelectedImagePath(String imagePath) {
        this.selectedImagePath = imagePath;
        updateImageGallery();
        // Notifier la sélection
        if (selectionListener != null) {
            selectionListener.onImageSelected(imagePath);
        }
    }

    /**
     * Charge automatiquement les images présentes dans les dossiers img au
     * démarrage
     */
    private void loadImagesFromImgFolders() {
        String[] folders = { "img/original", "img/img_noised", "img/img_denoised" };

        for (String folderPath : folders) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                loadImagesFromFolder(folder);
            }
        }

        // Mettre à jour l'affichage après le chargement
        updateImageGallery();
        notifyImageListChange();
    }

    /**
     * Charge récursivement les images d'un dossier
     */
    private void loadImagesFromFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Explorer récursivement les sous-dossiers
                    loadImagesFromFolder(file);
                } else if (isImageFile(file)) {
                    String path = file.getAbsolutePath();
                    if (!importedImages.contains(path)) {
                        importedImages.add(path);
                    }
                }
            }
        }
    }

    /**
     * Vérifie si un fichier est une image supportée
     */
    private static boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") ||
                name.endsWith(".jpeg") || name.endsWith(".bmp") ||
                name.endsWith(".gif") || name.endsWith(".tiff") ||
                name.endsWith(".tif");
    }

    public void addToRecent(String imagePath) {
        if (!recentImages.contains(imagePath)) {
            recentImages.add(0, imagePath); // Ajouter au début de la liste
            // Garder seulement les 10 images les plus récentes
            if (recentImages.size() > 10) {
                recentImages = recentImages.subList(0, 10);
            }
            // Mettre à jour la sélection et l'affichage
            setSelectedImagePath(imagePath);
            // Si le filtre "Récent" est actif, mettre à jour l'affichage
            if (currentFilter.equals("Récent")) {
                updateImageGallery();
            }
        }
    }

    /**
     * Définit le filtre actif et met à jour l'affichage
     * @param filter le nom du filtre à appliquer
     */
    public void setFilter(String filter) {
        this.currentFilter = filter;
        updateImageGallery();
    }
}