package cli.parse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestion des arguments de la commande "eval" qui permet d'évaluer la qualité entre deux images.
 * 
 * Cette classe immutable encapsule :
 * <ul>
 *   <li>image1 : chemin vers la première image à comparer (généralement l'original)</li>
 *   <li>image2 : chemin vers la deuxième image à comparer (généralement l'image traitée)</li>
 *   <li>metric : métrique à utiliser pour l'évaluation ("mse" ou "psnr")</li>
 * </ul>
 * 
 * Les arguments reconnus en ligne de commande sont :
 * <ul>
 *   <li>--image1, -i1 : chemin vers la première image (obligatoire)</li>
 *   <li>--image2, -i2 : chemin vers la deuxième image (obligatoire)</li>
 *   <li>--metric, -m : métrique à utiliser ("mse" ou "psnr", par défaut "both")</li>
 *   <li>--help, -h : affiche l'aide et quitte le programme</li>
 * </ul>
 * 
 * @author Martial-png
 * @version 1.0
 */
public class EvalArgs {
    private final Path image1;
    private final Path image2;
    private final String metric;
    
    // Set des métriques supportées
    private static final Set<String> SUPPORTED_METRICS = new HashSet<>(
            Arrays.asList("mse", "psnr", "both"));
    
    // Set des extensions d'images supportées
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".tif"));
    
    /**
     * Crée une nouvelle instance EvalArgs avec les paramètres spécifiés.
     * 
     * @param image1 chemin vers la première image
     * @param image2 chemin vers la deuxième image
     * @param metric métrique à utiliser ("mse", "psnr" ou "both")
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public EvalArgs(Path image1, Path image2, String metric) {
        // Vérifier que les chemins d'images existent
        if (image1 == null || !image1.toFile().exists()) {
            throw new IllegalArgumentException("Le chemin de la première image doit exister: " + image1);
        }
        
        if (image2 == null || !image2.toFile().exists()) {
            throw new IllegalArgumentException("Le chemin de la deuxième image doit exister: " + image2);
        }
        
        // Vérifier que les chemins pointent vers des fichiers image supportés
        if (!isImageFile(image1)) {
            throw new IllegalArgumentException("Format d'image non supporté pour la première image: " + image1);
        }
        
        if (!isImageFile(image2)) {
            throw new IllegalArgumentException("Format d'image non supporté pour la deuxième image: " + image2);
        }
        
        // Vérifier la métrique
        String metricLower = metric.toLowerCase();
        if (!SUPPORTED_METRICS.contains(metricLower)) {
            throw new IllegalArgumentException("Métrique non supportée: " + metric + 
                                             ". Utilisez 'mse', 'psnr' ou 'both'");
        }
        
        this.image1 = image1;
        this.image2 = image2;
        this.metric = metricLower;
    }

    /**
     * @return le chemin vers la première image
     */
    public Path getImage1() { return image1; }
    
    /**
     * @return le chemin vers la deuxième image
     */
    public Path getImage2() { return image2; }
    
    /**
     * @return la métrique à utiliser
     */
    public String getMetric() { return metric; }
    
    /**
     * Vérifie si un fichier est une image supportée en se basant sur son extension.
     * 
     * @param path chemin du fichier à vérifier
     * @return true si le fichier est une image supportée, false sinon
     */
    private static boolean isImageFile(Path path) {
        if (path == null) return false;
        String filename = path.getFileName().toString().toLowerCase();
        return SUPPORTED_EXTENSIONS.stream().anyMatch(filename::endsWith);
    }
    
    /**
     * Parse les arguments en ligne de commande pour créer un objet EvalArgs.
     *
     * @param args arguments de ligne de commande
     * @return une nouvelle instance de EvalArgs contenant les paramètres validés
     * @throws IllegalArgumentException si un argument est manquant ou invalide
     */
    public static EvalArgs parse(String[] args) {
        Path image1 = null;
        Path image2 = null;
        String metric = "both"; // Valeur par défaut

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--image1", "-i1" -> image1 = Paths.get(CliUtil.next(args, ++i, "--image1"));
                case "--image2", "-i2" -> image2 = Paths.get(CliUtil.next(args, ++i, "--image2"));
                case "--metric", "-m" -> metric = CliUtil.next(args, ++i, "--metric");
                case "-h", "--help" -> { CliUtil.printEvalHelp(); System.exit(0); }
                default -> throw new IllegalArgumentException("Option inconnue : " + args[i]);
            }
        }
        
        // Vérification des arguments obligatoires
        if (image1 == null) throw new IllegalArgumentException("--image1 est obligatoire");
        if (image2 == null) throw new IllegalArgumentException("--image2 est obligatoire");
        
        return new EvalArgs(image1, image2, metric);
    }
}
