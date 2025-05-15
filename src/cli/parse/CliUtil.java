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

    /** Pour la sous‑commande {@code denoise}. */
    public static Path defaultOutDenoise(Path input) {
        String fileName = baseName(input) + "_denoised.png";
        Path dir = Paths.get("img", "img_denoised");       // racine projet
        return dir.resolve(fileName);
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
}
