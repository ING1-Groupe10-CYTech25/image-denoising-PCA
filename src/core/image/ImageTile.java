package core.image;

import java.awt.image.BufferedImage;

/**
 * Représente une imagette appartenant a une image entièrere.
 * Cette classe hérite de la classe {@link Image}
 * @author p-cousin
 * @version 1.0
 * @see Image
 */
public class ImageTile extends Image {
    private int posX;   // position de l'imagette dans l'image
    private int posY;

    /**
     * Construit une instance de la classe {@code ImageExtract} a partir des paramètres donnés
     * @param img contenu de l'imagette (la sélection des données est faite par la classe {@link PatchExtractor})
     * @param posX Abscisse de l'imagette
     * @param posY Ordonnée de l'imagette
     */
    public ImageTile(BufferedImage img, int posX, int posY) {
        super(img);
        this.setPosX(posX);
        this.setPosY(posY);
    }
    
    /**
     * Getter pour l'abscisse de l'imagette
     * @return abscisse de l'imagette
     */
    public int getPosX() {
        return this.posX;
    }

    /**
     * Getter pour l'ordonnée de l'imagette
     * @return ordonnée de l'imagette
     */
    public int getPosY() {
        return this.posY;
    }

    /**
     * Setter pour l'abscisse de l'imagette
     * @param posX abscisse de l'imagette
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Setter pour l'ordonnée de l'imagette
     * @param posY ordonnée de l'imagette
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }
}
