// ============================================================================
// Classe ACP.java : Analyse en Composantes Principales (PCA) pour patchs d'image
// Utilise Apache Commons Math pour simplifier les calculs matriciels et spectraux
// ============================================================================
//
// Dépendance Maven à ajouter dans pom.xml :
// <dependency>
//   <groupId>org.apache.commons</groupId>
//   <artifactId>commons-math3</artifactId>
//   <version>3.6.1</version>
// </dependency>
//
// Cette classe fournit :
//   - MoyCov : calcul du vecteur moyen, matrice de covariance, données centrées
//   - ACP    : calcul des vecteurs propres, valeurs propres, projections
//   - Proj   : projection des vecteurs centrés dans la base ACP
//
// Toutes les méthodes sont statiques et robustes (gestion d'erreurs explicite)
// ============================================================================

import org.apache.commons.math3.linear.*;
import java.util.Arrays;

public class ACP {
    // ==========================
    // 1. MoyCov : Moyenne, Cov
    // ==========================
    /**
     * Calcule le vecteur moyen, la matrice de covariance et les données centrées
     * @param V matrice (s² x M) où chaque colonne est un patch vectorisé
     * @return Triple : [vecteur moyen, matrice covariance, données centrées]
     * @throws IllegalArgumentException si V est null, vide, ou incohérent
     */
    public static Triple<double[], double[][], double[][]> MoyCov(double[][] V) {
        // Vérification des entrées
        if (V == null || V.length == 0 || V[0] == null || V[0].length == 0)
            throw new IllegalArgumentException("La matrice d'entrée V est vide ou nulle.");
        int dim = V.length;      // s² (taille d'un patch vectorisé)
        int nb = V[0].length;    // M (nombre de patchs)
        for (double[] col : V) if (col.length != nb)
            throw new IllegalArgumentException("Tous les patchs doivent avoir la même taille.");
        if (nb < dim)
            throw new IllegalArgumentException("Impossible de calculer la covariance : il faut au moins autant de patchs ("+nb+") que la dimension d'un patch ("+dim+").");

        // Calcul du vecteur moyen (mV)
        double[] mV = new double[dim];
        for (int i = 0; i < dim; i++) {
            double sum = 0;
            for (int j = 0; j < nb; j++) sum += V[i][j];
            mV[i] = sum / nb;
        }
        // Centrage des données (Vc = V - mV)
        double[][] Vc = new double[dim][nb];
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < nb; j++)
                Vc[i][j] = V[i][j] - mV[i];
        // Calcul de la matrice de covariance (Γ = (1/M) * Vc * Vc^T)
        RealMatrix VcMat = new Array2DRowRealMatrix(Vc);
        RealMatrix cov = VcMat.multiply(VcMat.transpose()).scalarMultiply(1.0 / nb);
        return new Triple<>(mV, cov.getData(), Vc);
    }

    // ==========================
    // 2. ACP : Diagonalisation
    // ==========================
    /**
     * Effectue l'ACP sur la matrice des patchs vectorisés
     * @param V matrice (s² x M) où chaque colonne est un patch vectorisé
     * @return Triple : [vecteurs propres (U), valeurs propres, projections alpha]
     * @throws IllegalArgumentException si V est null, vide, ou incohérent
     */
    public static Triple<double[][], double[], double[][]> ACP(double[][] V) {
        // On commence par centrer et calculer la covariance
        Triple<double[], double[][], double[][]> res = MoyCov(V);
        double[] mV = res.first;
        double[][] cov = res.second;
        double[][] Vc = res.third;
        // Diagonalisation de la matrice de covariance
        RealMatrix covMat = new Array2DRowRealMatrix(cov);
        EigenDecomposition eig = new EigenDecomposition(covMat);
        double[] valeursPropres = eig.getRealEigenvalues();
        double[][] vecteursPropres = new double[cov.length][cov.length];
        for (int i = 0; i < cov.length; i++) {
            double[] v = eig.getEigenvector(i).toArray();
            for (int j = 0; j < cov.length; j++) vecteursPropres[j][i] = v[j];
        }
        // Projeter les données centrées dans la base des vecteurs propres
        double[][] alpha = Proj(vecteursPropres, Vc);
        return new Triple<>(vecteursPropres, valeursPropres, alpha);
    }

    // ==========================
    // 3. Proj : Projection
    // ==========================
    /**
     * Projette les vecteurs centrés dans la base des vecteurs propres (U)
     * @param U matrice (s² x s²) des vecteurs propres (colonnes)
     * @param Vc matrice (s² x M) des patchs centrés
     * @return matrice (s² x M) des coefficients projetés (alpha)
     */
    public static double[][] Proj(double[][] U, double[][] Vc) {
        if (U == null || Vc == null)
            throw new IllegalArgumentException("U ou Vc est null");
        int dim = U.length;
        int nb = Vc[0].length;
        double[][] alpha = new double[dim][nb];
        // Pour chaque patch (colonne de Vc), calculer alpha = U^T * Vc
        RealMatrix Umat = new Array2DRowRealMatrix(U);
        RealMatrix VcMat = new Array2DRowRealMatrix(Vc);
        RealMatrix alphaMat = Umat.transpose().multiply(VcMat);
        return alphaMat.getData();
    }

    // ==========================
    // Classe utilitaire Triple
    // ==========================
    /**
     * Petite classe utilitaire pour retourner 3 objets de types différents
     */
    public static class Triple<A, B, C> {
        public final A first;
        public final B second;
        public final C third;
        public Triple(A a, B b, C c) { this.first = a; this.second = b; this.third = c; }
    }

    // ==========================
    // Exemple d'utilisation
    // ==========================
    /*
    // Supposons que vous ayez une matrice de patchs vectorisés V (s² x M)
    double[][] V = ...;
    // 1. Calcul du vecteur moyen, covariance, données centrées
    ACP.Triple<double[], double[][], double[][]> res = ACP.MoyCov(V);
    double[] mV = res.first;
    double[][] cov = res.second;
    double[][] Vc = res.third;
    // 2. ACP complète (vecteurs propres, valeurs propres, projections)
    ACP.Triple<double[][], double[], double[][]> acpRes = ACP.ACP(V);
    double[][] U = acpRes.first;      // Vecteurs propres (base orthonormale)
    double[] valPropres = acpRes.second; // Valeurs propres
    double[][] alpha = acpRes.third;  // Projections (coefficients)
    // 3. Projection seule (si besoin)
    double[][] alpha2 = ACP.Proj(U, Vc);
    */

    // ==========================
    // Raisonnement exhaustif suivi pour la conception de cette classe
    // ==========================
    /*
    1. Librairie choisie : Apache Commons Math (facilite la manipulation de matrices, calculs spectraux, etc.)
       - Ajoutée via Maven (voir en-tête du fichier)
    2. Signatures : conformes au cahier des charges et à la structure du projet
       - MoyCov : retourne le vecteur moyen, la covariance, les données centrées
       - ACP : retourne la base orthonormale (vecteurs propres), valeurs propres, projections
       - Proj : projette les patchs centrés dans la base ACP
    3. Format des données : tout en double[][] (matrices 2D), chaque colonne = un patch vectorisé
    4. Retour des méthodes : tout ce qui est utile pour la suite du pipeline (vecteurs propres, valeurs propres, projections)
    5. Gestion des erreurs :
       - Matrice covariance non définissable (nb patchs < dimension) : message explicite
       - Entrées nulles ou vides : message explicite
       - Patchs de dimensions incohérentes : message explicite
    6. Commentaires : pédagogiques, chaque étape expliquée pour un débutant
    7. Exemple d'utilisation fourni à la fin du fichier
    */
}
