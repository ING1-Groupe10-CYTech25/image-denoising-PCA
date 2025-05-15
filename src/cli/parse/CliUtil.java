package cli.parse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Outils communs aux parseurs CLI.
 *
 * <ul>
 *   <li><code>img/img_noised/</code> pour <em>noise</em></li>
 *   <li><code>img/img_denoised/</code> pour <em>denoise</em></li>
 * </ul>
 *
 * Pour les autres commandes, aucun chemin par défaut n’est proposé.
 */
public final class CliUtil {

    private CliUtil() {}

    /* ------------------------------------------------------------------ */
    /* Validation de paramètres                                           */
    /* ------------------------------------------------------------------ */

    public static String next(String[] args, int index, String flag) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Valeur manquante pour " + flag);
        }
        return args[index];
    }

    /* ------------------------------------------------------------------ */
    /* Chemins de sortie par défaut                                       */
    /* ------------------------------------------------------------------ */

    /**
     * @return chemin par défaut ou <code>null</code> si non défini.
     */
    public static Path defaultOut(Path input, String cmd, int sigma) {
        return switch (cmd.toLowerCase(Locale.ROOT)) {
            case "noise"   -> defaultOutNoise(input, sigma);
            case "denoise" -> defaultOutDenoise(input);
            default         -> null;
        };
    }

    /** 
     * Pour la sous‑commande {@code noise}.
     *
     * @param input chemin d'entrée 
     * @param sigma intensité du bruit
     * @param extension extension du fichier à utiliser (avec le point, ex: ".png")
     * @return chemin de sortie par défaut
     */
    public static Path defaultOutNoise(Path input, int sigma, String extension) {
        String sigmaStr = String.valueOf(sigma);
        
        // Obtenir le nom de base sans extension
        String baseName = baseName(input);
        
        // Construire le nom de fichier avec l'extension spécifiée
        String fileName = baseName + "_" + sigmaStr + extension;
        
        Path dir = Paths.get("img", "img_noised");         // racine projet
        return dir.resolve(fileName);
    }

    /**
     * Version simplifiée utilisant .png par défaut
     */
    public static Path defaultOutNoise(Path input, int sigma) {
        return defaultOutNoise(input, sigma, ".png");
    }

    /** 
     * Pour la sous‑commande {@code denoise}.
     *
     * @param input chemin d'entrée 
     * @param method méthode de débruitage ("global" ou "local")
     * @param threshold type de seuillage ("hard" ou "soft")
     * @param shrink type de seuillage adaptatif (ou chaîne vide si non applicable)
     * @param extension extension du fichier à utiliser (avec le point, ex: ".png")
     * @return chemin de sortie par défaut
     */
    public static Path defaultOutDenoise(Path input, String method, String threshold, String shrink, String extension) {
        // Obtenir le nom de base sans extension
        String baseName = baseName(input);
        
        // Construire le nom de fichier avec la méthode et le type de seuillage
        String fileName = baseName + "_denoised_" + method + "_" + threshold + shrink + extension;
        
        Path dir = Paths.get("img", "img_denoised");       // racine projet
        return dir.resolve(fileName);
    }
    
    /** Pour la sous‑commande {@code denoise}. Version simplifiée. */
    public static Path defaultOutDenoise(Path input) {
        return defaultOutDenoise(input, "local", "hard", "", ".png");
    }

    public static String baseName(Path p) {
        String f = p.getFileName().toString();
        int dot = f.lastIndexOf('.');
        return dot == -1 ? f : f.substring(0, dot);
    }


    /* ------------------------------------------------------------------ */
    /* Commandes de type help                                             */
    /* ------------------------------------------------------------------ */

    public static void printGlobalHelp() {
        System.out.println("Usage : noise <commande> [options]\n" +
                "Commandes : noise, denoise, eval.\n" +
                "‘--help’ après une commande pour le détail des options.");
    }

    public static void printNoiseHelp() {
        System.out.println("noise --sigma <double> --input <path> [--output <path>]\n" +
                "Si --output est omis, le résultat est écrit dans ./img/img_noised/<img>_<σ>.png");
    }
    
    /**
     * Affiche l'aide pour la commande denoise.
     */
    public static void printDenoiseHelp() {
        System.out.println("denoise --input <path> [--output <path>] [--global | --local] [--threshold <type>] [--shrink <type>]\n" +
                "Débruite une image en appliquant la méthode PCA.\n" +
                "  --input, -i      : chemin de l'image à débruiter\n" +
                "  --output, -o     : chemin de destination (facultatif)\n" +
                "  --global, -g     : utilise la méthode globale (défaut: locale si ni global ni local spécifié)\n" +
                "  --local, -l      : utilise la méthode locale (défaut: locale si ni global ni local spécifié)\n" +
                "  --threshold, -t  : type de seuillage ('hard' ou 'soft', défaut: 'hard')\n" +
                "  --shrink, -s     : type de seuillage adaptatif ('v' pour VisuShrink, 'b' pour BayesShrink)\n" +
                "Si --output est omis, le résultat est écrit dans ./img/img_denoised/<img>_denoised_<method>_<threshold>[_<shrink>].png");
    }

    /**
     * Affiche l'aide pour la commande eval.
     */
    public static void printEvalHelp() {
        System.out.println("eval --image1 <path> --image2 <path> [--metric <type>]\n" +
                "Compare deux images et calcule des métriques de qualité.\n" +
                "  --image1, -i1 : chemin de la première image (original)\n" +
                "  --image2, -i2 : chemin de la deuxième image (traitée)\n" +
                "  --metric, -m  : métrique à utiliser ('mse', 'psnr' ou 'both', défaut: 'both')\n" +
                "    mse  : Mean Square Error (Erreur Quadratique Moyenne)\n" +
                "    psnr : Peak Signal-to-Noise Ratio (Rapport Signal/Bruit de Crête)\n" +
                "    both : Calcule les deux métriques\n" +
                "Plus la valeur MSE est basse, plus les images sont similaires.\n" +
                "Plus la valeur PSNR est élevée, meilleure est la qualité de l'image.");
    }
}
