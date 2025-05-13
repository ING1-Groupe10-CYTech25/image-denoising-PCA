/**
 * Classe représentant un patch carré tiré d'une image.
 * Un "patch" est une petite zone de taille s x s de l'image qui retient ses valeurs de pixels 
 * et sa position initiale dans l'image source.
 */
public class Patch {

    private int[][] pixels; // 2D tableau de valeurs de Gris (taille s x s)
    private int xOrigin;   // abscisse du coin gauche supérieur du patch dans l'image d'origine
    private int yOrigin;  // ordonnée du coin gauche supérieur du patch dans l'image d'origine
    /**
     * Constructeur de la Classe Patch 
     * @param pixels 2D pixel de matrice (taille s x s)
     * @param xOrigin Abscisse dans l'image d'origine
     * @param yOrigin Ordonnée dans l'image d'origine the original image
     */
    public Patch(int[][] pixels, int xOrigin, int yOrigin) {
        this.pixels = pixels;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
    }

    /**
     * Retourne la matrice (2D) de pixels du patch
     * @return Un tableau 2D contenant les valeurs de gris
     */
    public int[][] getPixels() {
        return pixels;
    }

    /**
     * Retourne la valeur de gris d'un pixel particulier du patch 
     * @param x Colonne index dans le patch (de 0 à s-1)
     * @param y Ligne index dans le patch (de 0 à s-1)
     * @return Valeur Gris (0–255)
     */
    public int getPixel(int x, int y) {
        return pixels[y][x];
    }

    /**
     * Convertit le patch en un vecteur (1D) pour l'A.C.P. .
     * Les valeurs sont rangées coordonnée par coordonnée
     * @return un tableau 1D  de length s² contenant des valeurs de gris.
     */
    public int[] toVector() {
        int s = pixels.length;
        int[] vector = new int[s * s];
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                vector[i * s + j] = pixels[i][j];
            }
        }
        return vector;
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
     * Procédé utile pour décrire un patch.
     */
    @Override
    public String toString() {
        return "Patch (" + xOrigin + ", " + yOrigin + ") size " + pixels.length + "x" + pixels.length;
    }
}
