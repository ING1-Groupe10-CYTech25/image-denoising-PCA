package core.image;

import java.awt.image.BufferedImage;

/**
 * Représente une sous-partie (tuile) d'une image plus grande.
 * Cette classe étend {@link Image} en ajoutant des coordonnées de position
 * pour situer la tuile dans l'image d'origine.
 * 
 * Une tuile est utile pour :
 * - Traiter une image par morceaux
 * - Paralléliser les traitements sur différentes parties de l'image
 * - Appliquer des algorithmes locaux sur des zones spécifiques
 * 
 * @author p-cousin
 * @version 1.0
 * @see Image
 */
public class ImageTile extends Image {
    private int posX;    // Position X de la tuile dans l'image d'origine
    private int posY;    // Position Y de la tuile dans l'image d'origine

    /**
     * Construit une nouvelle tuile d'image à partir d'une {@link BufferedImage} et de sa position.
     * 
     * @param img L'image source pour cette tuile
     * @param posX Position X de la tuile dans l'image d'origine (en pixels)
     * @param posY Position Y de la tuile dans l'image d'origine (en pixels)
     */
    public ImageTile(BufferedImage img, int posX, int posY) {
        super(img);
        this.setPosX(posX);
        this.setPosY(posY);
    }

    /**
     * Définit la position X de la tuile dans l'image d'origine.
     * 
     * @param posX Nouvelle position X en pixels (depuis le bord gauche)
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Définit la position Y de la tuile dans l'image d'origine.
     * 
     * @param posY Nouvelle position Y en pixels (depuis le bord supérieur)
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Récupère la position X de la tuile dans l'image d'origine.
     * 
     * @return Position X en pixels depuis le bord gauche de l'image d'origine
     */
    public int getPosX() {
        return this.posX;
    }

    /**
     * Récupère la position Y de la tuile dans l'image d'origine.
     * 
     * @return Position Y en pixels depuis le bord supérieur de l'image d'origine
     */
    public int getPosY() {
        return this.posY;
    }
}
