package core.patch; 

import java.util.List; 
import java.util.ArrayList; 

/**
 * Classe à l'origine de la conversion des patchs en vecteur, et réciproquement
 * Pour l'A.C.P. et la reconstitution d'images
 */
public class Vectorization {


    /**
     * Convertit une liste de patchs en matrice (1 seul patch par ligne).
     * @param patches Liste de patchs
     * @return Matrice de taille [n x s²] où chaque ligne est un vecteur de patchs
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
     * Convertit une matrice en liste de patchs
     * @param matrix matrice où chaque ligne est un vecteur
     * @param size taille d'un patch 
     * @param origins Liste des coordonnées originales (x, y) de chaque patch
     * @return une liste constituée de patch
     */
    public static List<Patch> matrixToPatches(int[][] matrix, int size, List<int[]> origins) {
        List<Patch> patches = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            patches.add(new Patch(matrix[i], origins.get(i)[0], origins.get(i)[1], size));
        }
        return patches;
    }

    /**
 * Concentre les données en retranchant la moyenne de chaque colonne.
 * C'est une étape préliminaire pour l'A.C.P. (Analyse en Composantes Principales).
 * 
 * @param données d'une matrice de taille (n x s²) où n est le nombre de patchs, et s² celui de pixels par patch.
 * @return Une nouvelle matrice où chaque colonne a été centrée (moyenne retranchée).
 */
public static double[][] centerData(int[][] data) {
    int rows = data.length;         // Nombre de lignes (ie de patchs)
    int cols = data[0].length;      // Nombre de colonnes (pixels par patch)
    
    double[][] centered = new double[rows][cols]; // Matrice qui renferme les données centrées.
    double[] mean = new double[cols];  // tableau qui renferme la moyenne de chaque colonne.

    // Calcule la moyenne de chaque colonne
    for (int j = 0; j < cols; j++) {
        for (int i = 0; i < rows; i++) {
            mean[j] += data[i][j];  // Somme toutes les valeurs de la colonne
        }
        mean[j] /= rows;  // établit la moyenne en divisant par le nombre de lignes
    }

    // Retranche la moyenne à chaque élément de la matrice pour centrer les données
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            centered[i][j] = data[i][j] - mean[j];  // Retranche la moyenne de la colonne
        }
    }

    return centered;  // Renvoie les données centrées
}

}
