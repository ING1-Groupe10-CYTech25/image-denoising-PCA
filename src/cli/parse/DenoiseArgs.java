package cli.parse;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Gestion des arguments de la commande "denoise" qui permet de débruiter une image.
 * 
 * Cette classe immutable encapsule :
 * <ul>
 *   <li>input : le chemin vers l'image à débruiter</li>
 *   <li>output : le chemin où l'image débruitée sera sauvegardée</li>
 *   <li>isGlobal : indique si on utilise la méthode globale (true) ou locale (false)</li>
 *   <li>threshold : type de seuillage à appliquer ("hard" ou "soft")</li>
 *   <li>shrink : type de seuillage adaptatif ("v" pour VisuShrink, "b" pour BayesShrink)</li>
 *   <li>sigma : écart type du bruit</li>
 *   <li>patchPercent : pourcentage de la taille minimale pour le patch</li>
 * </ul>
 * 
 * Les arguments reconnus en ligne de commande sont :
 * <ul>
 *   <li>--input, -i : chemin de l'image à débruiter (obligatoire)</li>
 *   <li>--output, -o : chemin de destination (facultatif)</li>
 *   <li>--global, -g : active la méthode de débruitage globale</li>
 *   <li>--local, -l : active la méthode de débruitage locale (défaut si ni global ni local n'est spécifié)</li>
 *   <li>--threshold, -t : type de seuillage ("hard" ou "soft", défaut: "hard")</li>
 *   <li>--shrink, -sh : type de seuillage adaptatif ("v" pour VisuShrink, "b" pour BayesShrink)</li>
 *   <li>--sigma, -s : écart type du bruit</li>
 *   <li>--patchPercent, -pp : pourcentage de la taille minimale pour le patch (entre 0 et 1)</li>
 *   <li>--help, -h : affiche l'aide et quitte le programme</li>
 * </ul>
 * 
 * Si le chemin de sortie n'est pas spécifié, un chemin par défaut est généré
 * dans le dossier "img/img_denoised/" avec le nom de l'image source et le type de débruitage.
 * 
 * @author Martial-png
 * @version 1.0
 */
public final class DenoiseArgs {
    private final Path input;
    private final Path output;
    private final boolean isGlobal;
    private final String threshold;
    private final String shrink;
    private final double sigma;
    private final double patchPercent;
    
    // Set des extensions d'images supportées
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".tif"));
    
    // Set des types de seuillage supportés
    private static final Set<String> SUPPORTED_THRESHOLDS = new HashSet<>(
            Arrays.asList("hard", "soft", "h", "s"));
    
    // Set des types de seuillage adaptatif supportés
    private static final Set<String> SUPPORTED_SHRINKS = new HashSet<>(
            Arrays.asList("v", "b"));
    
    /**
     * Crée une nouvelle instance DenoiseArgs avec les paramètres spécifiés.
     * 
     * @param input chemin vers l'image à débruiter
     * @param output chemin où l'image débruitée sera sauvegardée
     * @param isGlobal indique si la méthode de débruitage est globale (true) ou locale (false)
     * @param threshold type de seuillage à appliquer ("hard" ou "soft")
     * @param shrink type de seuillage adaptatif ("v" ou "b")
     * @param sigma écart type du bruit
     * @param patchPercent pourcentage de la taille minimale pour le patch (entre 0 et 1)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public DenoiseArgs(Path input, Path output, boolean isGlobal, String threshold, 
                      String shrink, double sigma, double patchPercent) {
        // Vérifier que le chemin d'entrée existe
        if (input == null || !input.toFile().exists()) {
            throw new IllegalArgumentException("Le chemin d'entrée doit exister: " + input);
        }
        
        // Vérifier que le chemin de sortie n'est pas null
        if (output == null) {
            throw new IllegalArgumentException("Le chemin de sortie ne peut pas être null");
        }
        
        // Vérifier si l'entrée est un fichier image supporté
        if (input.toFile().isFile() && !isImageFile(input)) {
            throw new IllegalArgumentException("Format d'image non supporté pour le fichier d'entrée: " + input);
        }
        
        // Vérifier le type de seuillage
        String thresholdLower = threshold.toLowerCase();
        if (!SUPPORTED_THRESHOLDS.contains(thresholdLower)) {
            throw new IllegalArgumentException(
                "Type de seuillage non supporté: " + threshold + 
                ". Utilisez 'hard'/'h' ou 'soft'/'s'");
        }
        
        // Convertir les versions courtes en versions longues
        if (thresholdLower.equals("h")) {
            thresholdLower = "hard";
        } else if (thresholdLower.equals("s")) {
            thresholdLower = "soft";
        }
        
        // Vérifier le type de seuillage adaptatif si fourni
        String shrinkLower = shrink != null ? shrink.toLowerCase() : "v";
        if (shrinkLower != null && !SUPPORTED_SHRINKS.contains(shrinkLower)) {
            throw new IllegalArgumentException(
                "Type de seuillage adaptatif non supporté: " + shrink + 
                ". Utilisez 'v' (VisuShrink) ou 'b' (BayesShrink)");
        }
        
        // Vérifier que sigma est positif
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma doit être un nombre strictement positif");
        }
        
        // Vérifier que patchPercent est entre 0 et 1
        if (patchPercent <= 0 || patchPercent > 1) {
            throw new IllegalArgumentException("Le pourcentage de taille de patch doit être entre 0 et 1");
        }
        
        this.input = input;
        this.output = output;
        this.isGlobal = isGlobal;
        this.threshold = thresholdLower;
        this.shrink = shrinkLower;
        this.sigma = sigma;
        this.patchPercent = patchPercent;
    }

    /**
     * @return le chemin vers l'image à débruiter
     */
    public Path getInput() { return input; }
    
    /**
     * @return le chemin où l'image débruitée sera enregistrée
     */
    public Path getOutput() { return output; }
    
    /**
     * @return true si la méthode de débruitage est globale, false si elle est locale
     */
    public boolean isGlobal() { return isGlobal; }
    
    /**
     * @return le type de seuillage à appliquer ("hard" ou "soft")
     */
    public String getThreshold() { return threshold; }
    
    /**
     * @return le type de seuillage adaptatif (null si méthode globale ou non spécifié)
     */
    public String getShrink() { return shrink; }
    
    /**
     * @return l'écart type du bruit
     */
    public double getSigma() { return sigma; }
    
    /**
     * @return le pourcentage de la taille minimale pour le patch
     */
    public double getPatchPercent() { return patchPercent; }
    
    /**
     * Vérifie si un fichier est une image supportée en se basant sur son extension.
     * 
     * @param path chemin du fichier à vérifier
     * @return true si le fichier est une image supportée, false sinon
     */
    public static boolean isImageFile(Path path) {
        String filename = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return SUPPORTED_EXTENSIONS.stream().anyMatch(filename::endsWith);
    }
    
    /**
     * Extrait l'extension d'un fichier à partir de son chemin.
     * 
     * @param path chemin du fichier
     * @return l'extension du fichier (avec le point) ou une chaîne vide si pas d'extension
     */
    public static String getFileExtension(Path path) {
        String filename = path.getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }

    /**
     * Extrait la valeur de sigma du nom du fichier si elle est présente dans le format _noised_<sigma>.
     * 
     * @param path chemin du fichier
     * @return la valeur de sigma extraite, ou -1 si non trouvée
     */
    private static double extractSigmaFromFilename(Path path) {
        String filename = path.getFileName().toString();
        int index = filename.indexOf("_noised_");
        if (index != -1) {
            try {
                // Extraire la partie après "_noised_"
                String afterNoised = filename.substring(index + 8);
                // Trouver la fin du nombre (jusqu'au prochain underscore ou point)
                int endIndex = afterNoised.indexOf('_');
                if (endIndex == -1) {
                    endIndex = afterNoised.indexOf('.');
                }
                if (endIndex != -1) {
                    afterNoised = afterNoised.substring(0, endIndex);
                }
                return Double.parseDouble(afterNoised);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Parse les arguments en ligne de commande pour créer un objet DenoiseArgs.
     *
     * @param args arguments de ligne de commande
     * @return une nouvelle instance de DenoiseArgs contenant les paramètres validés
     * @throws IllegalArgumentException si un argument est manquant ou invalide
     */
    public static DenoiseArgs parse(String[] args) {
        Path input = null, output = null;
        boolean isGlobal = false;
        boolean explicitLocal = false;
        String threshold = "hard"; // Valeur par défaut
        String shrink = "v";      // VisuuShrink par défaut
        double sigma = 30.0;      // Valeur par défaut pour sigma
        double patchPercent = 0.5; // Valeur par défaut pour patchPercent

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input", "-i" -> {
                    input = Paths.get(CliUtil.next(args, ++i, "--input"));
                    // Vérifier l'existence du chemin d'entrée
                    if (!input.toFile().exists()) {
                        throw new IllegalArgumentException("Le chemin d'entrée spécifié n'existe pas: " + input);
                    }
                    // Vérifier si c'est un fichier image valide (si c'est un fichier)
                    File inputFile = input.toFile();
                    if (inputFile.isFile() && !isImageFile(input)) {
                        throw new IllegalArgumentException("Format d'image non supporté pour le fichier d'entrée: " + input);
                    }
                    // Essayer d'extraire sigma du nom du fichier
                    double extractedSigma = extractSigmaFromFilename(input);
                    if (extractedSigma > 0) {
                        sigma = extractedSigma;
                    }
                }
                case "--output", "-o" -> output = Paths.get(CliUtil.next(args, ++i, "--output"));
                case "--global", "-g" -> isGlobal = true;
                case "--local", "-l" -> explicitLocal = true;
                case "--threshold", "-t" -> {
                    threshold = CliUtil.next(args, ++i, "--threshold").toLowerCase();
                    if (!SUPPORTED_THRESHOLDS.contains(threshold)) {
                        throw new IllegalArgumentException(
                            "Type de seuillage non supporté: " + threshold + 
                            ". Utilisez 'hard'/'h' ou 'soft'/'s'");
                    }
                }
                case "--shrink", "-sh" -> {
                    shrink = CliUtil.next(args, ++i, "--shrink").toLowerCase();
                    if (!SUPPORTED_SHRINKS.contains(shrink)) {
                        throw new IllegalArgumentException(
                            "Type de seuillage adaptatif non supporté: " + shrink + 
                            ". Utilisez 'v' (VisuShrink) ou 'b' (BayesShrink)");
                    }
                }
                case "--sigma", "-s" -> {
                    try {
                        sigma = Double.parseDouble(CliUtil.next(args, ++i, "--sigma"));
                        if (sigma <= 0) {
                            throw new IllegalArgumentException("Sigma doit être un nombre strictement positif");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Sigma doit être un nombre valide");
                    }
                }
                case "--patchPercent", "-pp" -> {
                    try {
                        patchPercent = Double.parseDouble(CliUtil.next(args, ++i, "--patchPercent"));
                        if (patchPercent <= 0 || patchPercent > 1) {
                            throw new IllegalArgumentException("Le pourcentage de taille de patch doit être entre 0 et 1");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Le pourcentage de taille de patch doit être un nombre valide");
                    }
                }
                case "-h", "--help" -> { CliUtil.printDenoiseHelp(); System.exit(0); }
                default -> throw new IllegalArgumentException("Option inconnue : " + args[i]);
            }
        }
        
        // Vérification des arguments obligatoires
        if (input == null) throw new IllegalArgumentException("--input est obligatoire");
        
        // Vérifier que les options global et local ne sont pas utilisées ensemble
        if (isGlobal && explicitLocal) {
            throw new IllegalArgumentException("Les options --global et --local ne peuvent pas être utilisées ensemble");
        }
        
        // Si --local est spécifié, s'assurer que isGlobal est false
        if (explicitLocal) {
            isGlobal = false;
        }
        
        // Générer le chemin de sortie par défaut si non spécifié
        if (output == null) {
            String method = isGlobal ? "global" : "local";
            // Toujours inclure le shrink s'il est spécifié, quelle que soit la méthode
            String shrinkStr = (shrink != null) ? "_" + shrink : "";
            
            // Préserver l'extension d'origine si c'est un fichier
            if (input.toFile().isFile()) {
                String extension = getFileExtension(input);
                // Si l'extension n'est pas supportée, utiliser PNG par défaut
                if (extension.isEmpty() || !SUPPORTED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
                    extension = ".png";
                }
                output = CliUtil.defaultOutDenoise(input, method, threshold, shrinkStr, extension);
            } else {
                // Pour un dossier, utiliser l'extension PNG par défaut
                output = CliUtil.defaultOutDenoise(input, method, threshold, shrinkStr, ".png");
            }
        }
        
        return new DenoiseArgs(input, output, isGlobal, threshold, shrink, sigma, patchPercent);
    }
}
