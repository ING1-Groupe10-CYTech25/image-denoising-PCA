package core.patch; 

import java.util.List; 
import java.util.ArrayList; 

/**
 * Class responsible for converting patches to vectors and vice versa.
 * This is used for PCA and reconstruction of images.
 */
public class Vectorization {

    /**
     * Converts a single Patch into a 1D vector (row-major order).
     * @param patch The patch to convert
     * @return A 1D array containing pixel values
     */
    public static int[] patchToVector(Patch patch) {
        return patch.toVector(); // Uses the method already defined in Patch
    }

    /**
     * Converts a list of patches into a 2D matrix (one patch per row).
     * @param patches List of patches
     * @return Matrix [n x s²] where each row is a patch vector
     */
    public static int[][] patchesToMatrix(List<Patch> patches) {
        if (patches.isEmpty()) {
            return new int[0][0];
        }

        int numPatches = patches.size();
        int vectorLength = patches.get(0).toVector().length;

        int[][] matrix = new int[numPatches][vectorLength];

        for (int i = 0; i < numPatches; i++) {
            matrix[i] = patchToVector(patches.get(i));
        }

        return matrix;
    }

    /**
     * Converts a 1D vector back into a Patch object.
     * @param vector The 1D array of pixel values
     * @param size Patch size (s)
     * @param xOrigin Original x position in the image
     * @param yOrigin Original y position in the image
     * @return A new Patch reconstructed from the vector
     */
    public static Patch vectorToPatch(int[] vector, int size, int xOrigin, int yOrigin) {
        int[][] pixels = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                pixels[i][j] = vector[i * size + j]; // Flattened row-major format
            }
        }

        return new Patch(pixels, xOrigin, yOrigin);
    }

    /**
     * Converts a full matrix of vectors back into a list of patches.
     * @param matrix Matrix where each row is a vector
     * @param size Patch size (s)
     * @param origins List of original (x, y) positions for each patch
     * @return A list of Patch objects
     */
    public static List<Patch> matrixToPatches(int[][] matrix, int size, List<int[]> origins) {
        List<Patch> patches = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] vector = matrix[i];
            int[] origin = origins.get(i);
            patches.add(vectorToPatch(vector, size, origin[0], origin[1]));
        }

        return patches;
    }
}
