package core.patch;
/**
 * Classe représentant un patch carré tiré d'une image.
 * Un "patch" est une petite zone de taille s x s de l'image qui retient ses valeurs de pixels 
 * et sa position initiale dans l'image source.
 */
public class Patch {

    private int side;       // taille du coté des patchs
    private int[] pixels;   // tableau de valeurs de Gris (taille side²)
    private int xOrigin;    // abscisse du coin gauche supérieur du patch dans l'image d'origine
    private int yOrigin;    // ordonnée du coin gauche supérieur du patch dans l'image d'origine
    /**
     * Constructeur de la Classe Patch 
     * @param pixels tableau des pixels (taille side²)
     * @param xOrigin Abscisse dans l'image d'origine
     * @param yOrigin Ordonnée dans l'image d'origine the original image
     * @param side coté des patchs en pixels
     */
    public Patch(int[] pixels, int xOrigin, int yOrigin, int side) {
        this.pixels = pixels;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.side = side;

    }

    /**
     * Retourne le tableau de pixels du patch
     * @return Un tableau contenant les valeurs de gris
     */
    public int[] getPixels() {
        return pixels;
    }

    /**
     * Retourne la valeur de gris d'un pixel particulier du patch 
     * @param x Colonne index dans le patch (de 0 à s-1)
     * @param y Ligne index dans le patch (de 0 à s-1)
     * @return Valeur Gris (0–255)
     */
    public int getPixel(int x, int y) {
        return pixels[y * this.getSide() + x];
    }
    /**
     * Getter pour la taille du patch
     * @return taille du coté du patch en pixels
     */
    public int getSide() {
        return this.side;
    }

    /**
     * calcule la taille du tableau de valeurs {@code pixels}
     * @return taille du tableu de valeurs {@code pixels}
     */
    public int getSize() {
        return this.getSide() * this.getSide();
    }

    /**
     * Retourne l'abscisse originale du patch dans l'image .
     * @return X-coordinate in the original image.
     */
    public int getXOrigin() {
        return xOrigin;
    }

    /**
     * Retourne l'ordonnée originale du patch dans l'image. 
     * @return Y-coordinate in the original image.
     */
    public int getYOrigin() {
        return yOrigin;
    }

    /**
     * Setter pour le tableau des pixels
     * @param pixels tableau des pixels
     */
    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    /**
     * Setter pour l'abscisse de l'origine du patch
     * @param xOrigin abscisse de l'origine du patch
     */
    public void setXOrigin(int xOrigin) {
        this.xOrigin = xOrigin;
    }

    /**
     * Setter pour l'ordonnée de l'origine du patch
     * @param yOrigin ordonnée de l'origine du patch
     */
    public void setYOrigin(int yOrigin) {
        this.yOrigin = yOrigin;
    }

    /**
     * Setter pour la taille du patch
     * @param side taille du patch
     */
    public void setSide(int side) {
        this.side = side;
    }

    /**
     * Procédé utile pour décrire un patch.
     */
    @Override
    public String toString() {
        return "Patch (" + this.getXOrigin() + ", " + this.getYOrigin() + ") size " + this.getSize() + "x" + this.getSize();
    }
}
