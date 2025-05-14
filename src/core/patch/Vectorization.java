package core.patch; 

import java.util.List; 
import java.util.ArrayList; 

/**
 * Class responsible for converting patches to vectors and vice versa.
 * This is used for PCA and reconstruction of images.
 */
public class Vectorization {


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
        int vectorLength = patches.get(0).getSize();

        int[][] matrix = new int[numPatches][vectorLength];

        for (int i = 0; i < numPatches; i++) {
            matrix[i] = patches.get(i).getPixels();
        }
        return matrix;
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
            patches.add(new Patch(matrix[i], origins.get(i)[0], origins.get(i)[1], size));
        }
        return patches;
    }
}
