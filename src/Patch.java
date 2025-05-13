/**
 * Class representing a square patch extracted from an image.
 * A patch is a small s × s region of the image and stores its pixel values
 * along with its original position in the source image.
 */
public class Patch {

    private int[][] pixels; // 2D array of grayscale values (size s x s)
    private int xOrigin;   // X-coordinate of the top-left corner of the patch in the original image
    private int yOrigin;  // Y-coordinate of the top-left corner of the patch in the original image
    /**
     * Constructor for the Patch class
     * @param pixels 2D pixel matrix (size s x s)
     * @param xOrigin X position in the original image
     * @param yOrigin Y position in the original image
     */
    public Patch(int[][] pixels, int xOrigin, int yOrigin) {
        this.pixels = pixels;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
    }

    /**
     * Returns the full 2D pixel matrix of the patch.
     * @return A 2D array containing grayscale values.
     */
    public int[][] getPixels() {
        return pixels;
    }

    /**
     * Returns the grayscale value of a specific pixel in the patch.
     * @param x Column index in the patch (0 to s-1)
     * @param y Row index in the patch (0 to s-1)
     * @return Grayscale value (0–255)
     */
    public int getPixel(int x, int y) {
        return pixels[y][x];
    }

    /**
     * Converts the patch into a 1D vector (used for PCA).
     * Values are ordered row-by-row (row-major).
     * @return A 1D array of length s² containing grayscale values.
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
     * Returns the original X position of the patch in the image.
     * @return X-coordinate in the original image.
     */
    public int getXOrigin() {
        return xOrigin;
    }

    /**
     * Returns the original Y position of the patch in the image.
     * @return Y-coordinate in the original image.
     */
    public int getYOrigin() {
        return yOrigin;
    }

    /**
     * Useful method to print a quick summary of the patch.
     */
    @Override
    public String toString() {
        return "Patch (" + xOrigin + ", " + yOrigin + ") size " + pixels.length + "x" + pixels.length;
    }
}
