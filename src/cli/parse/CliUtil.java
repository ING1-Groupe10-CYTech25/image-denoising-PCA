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
 * Pour les autres commandes, aucun chemin par défaut n'est proposé.
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
            case "denoise" -> defaultOutDenoise(input, "local", "hard", "", ".png");
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
        System.out.println("""
            Usage: image-denoising-PCA [command] [options]
            
            Commandes disponibles:
              noise    Ajouter du bruit à une image
              denoise  Débruiter une image
              eval     Évaluer la qualité du débruitage
            
            Pour plus d'informations sur une commande:
              image-denoising-PCA [command] --help
            """);
    }

    public static void printNoiseHelp() {
        System.out.println("""
            Usage: noise [options]
            
            Options:
              -i, --input <path>     Chemin vers l'image ou le dossier d'images à bruiter (obligatoire)
              -o, --output <path>    Chemin pour l'image bruitée ou le dossier de sortie (optionnel)
              -s, --sigma <value>    Écart type du bruit (défaut: 30.0)
              -h, --help            Affiche cette aide
            
            Exemples:
              # Ajout de bruit à une seule image
              noise -i image.png -s 30
              noise -i image.png -o sortie.png -s 25.0
            
              # Ajout de bruit à toutes les images d'un dossier
              noise -i dossier_images/ -o dossier_sortie/ -s 30
            
            Lorsque vous spécifiez un dossier en entrée :
            - Toutes les images du dossier et de ses sous-dossiers seront traitées
            - Les images bruitées seront sauvegardées dans un sous-dossier avec la date (format: YY-MM-DD-HH-mm)
            - Le nom de chaque image bruitée inclura le sigma utilisé
            
            Formats d'image supportés : PNG, JPG, JPEG, BMP, GIF, TIFF
            """);
    }
    
    /**
     * Affiche l'aide pour la commande denoise.
     */
    public static void printDenoiseHelp() {
        System.out.println("""
            Usage: denoise [options]
            
            Options:
              -i, --input <path>     Chemin vers l'image ou le dossier d'images à débruiter (obligatoire)
              -o, --output <path>    Chemin pour l'image débruitée ou le dossier de sortie (optionnel)
              -g, --global          Active la méthode de débruitage globale
              -l, --local           Active la méthode de débruitage locale (défaut)
              -t, --threshold <type> Type de seuillage (hard/h ou soft/s, défaut: hard)
              -sh, --shrink <type>   Type de seuillage adaptatif (v pour VisuShrink, b pour BayesShrink)
              -s, --sigma <value>    Écart type du bruit (défaut: 30.0)
              -pp, --patchPercent <value> Pourcentage de la taille minimale pour le patch (entre 0 et 1, défaut: 0.1)
              -h, --help            Affiche cette aide
            
            Exemples:
              # Débruitage d'une seule image
              denoise -i image.png -t hard -sh v -s 25.0
              denoise -i image.png -g -t soft -sh b
            
              # Débruitage de toutes les images d'un dossier
              denoise -i dossier_images/ -o dossier_sortie/ -t hard -sh v
              denoise -i dossier_images/ -g -t soft -sh b -pp 0.15
            
            Lorsque vous spécifiez un dossier en entrée :
            - Toutes les images du dossier et de ses sous-dossiers seront traitées (formats supportés : PNG, JPG, JPEG, BMP, GIF, TIFF)
            - Les images débruitées seront sauvegardées dans un sous-dossier avec la date (format: YY-MM-DD-HH-mm)
            - Le nom de chaque image débruitée inclura la méthode et les paramètres utilisés
            """);
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

    /**
     * Affiche l'aide pour la commande benchmark.
     */
    public static void printBenchmarkHelp() {
        System.out.println("benchmark -i <chemin_image>... [-o <chemin_sortie>] [-s <sigma>] [-pp <pourcentage>]");
        System.out.println("Effectue un benchmark complet sur une ou plusieurs images.");
        System.out.println();
        System.out.println("Options :");
        System.out.println("  -i, --input    : Chemin(s) vers l'image(s) à tester (obligatoire, peut être multiple)");
        System.out.println("  -o, --output   : Répertoire de sortie pour les résultats (optionnel)");
        System.out.println("  -s, --sigma    : Écart type du bruit (défaut: 30.0)");
        System.out.println("  -pp, --patchPercent : Pourcentage de la taille minimale pour le patch (entre 0 et 1, défaut: 0.1)");
        System.out.println("  -h, --help     : Affiche cette aide");
        System.out.println();
        System.out.println("Exemple :");
        System.out.println("  benchmark -i img/original/lena.png -s 30 -pp 0.1");
        System.out.println("  benchmark -i img/original/lena.png img/original/barbara.png -o results -pp 0.05");
    }
}
