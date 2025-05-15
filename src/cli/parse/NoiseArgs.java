package cli.parse;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Gestion des arguments de la commande "noise" qui permet d'ajouter du bruit à une image.
 * 
 * Cette classe immutable encapsule :
 * <ul>
 *   <li>sigma : l'intensité du bruit à appliquer (valeur entière positive)</li>
 *   <li>input : le chemin vers l'image d'entrée</li>
 *   <li>output : le chemin où l'image bruitée sera sauvegardée</li>
 * </ul>
 * 
 * Les arguments reconnus en ligne de commande sont :
 * <ul>
 *   <li>--sigma, -s : intensité du bruit (obligatoire)</li>
 *   <li>--input, -i : chemin de l'image source (obligatoire)</li>
 *   <li>--output, -o : chemin de destination (facultatif)</li>
 *   <li>--help, -h : affiche l'aide et quitte le programme</li>
 * </ul>
 * 
 * Si le chemin de sortie n'est pas spécifié, un chemin par défaut est généré
 * dans le dossier "img/img_noised/" avec le nom de l'image source augmenté du sigma.
 * Supporte différentes extensions d'images (.png, .jpg, .jpeg, .bmp, .gif).
 */
public final class NoiseArgs {
    private final int sigma;
    private final Path input;
    private final Path output;
    
    // Set des extensions d'images supportées
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff", ".tif"));

    /**
     * Crée une nouvelle instance NoiseArgs avec les paramètres spécifiés.
     * 
     * @param input chemin vers l'image d'entrée
     * @param sigma intensité du bruit (valeur entière positive)
     * @param output chemin où l'image bruitée sera sauvegardée
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public NoiseArgs(Path input, int sigma, Path output) {
        // Vérifier que sigma est positif
        if (sigma <= 0) {
            throw new IllegalArgumentException("L'intensité du bruit (sigma) doit être un entier strictement positif");
        }
        
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
        
        this.sigma = sigma;
        this.input = input;
        this.output = output;
    }

    /**
     * @return l'intensité du bruit (valeur entière positive)
     */
    public int getSigma()  { return sigma; }
    
    /**
     * @return le chemin vers l'image d'entrée
     */
    public Path getInput()  { return input; }
    
    /**
     * @return le chemin où l'image bruitée sera enregistrée
     */
    public Path getOutput() { return output; }

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
     * Parse les arguments en ligne de commande pour créer un objet NoiseArgs.
     *
     * @param args arguments de ligne de commande
     * @return une nouvelle instance de NoiseArgs contenant les paramètres validés
     * @throws IllegalArgumentException si un argument est manquant ou invalide
     */
    public static NoiseArgs parse(String[] args) {
        int sigma = -1;
        Path input = null, output = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--sigma", "-s" -> {
                    try {
                        sigma = Integer.parseInt(CliUtil.next(args, ++i, "--sigma"));
                        if (sigma <= 0) {
                            throw new IllegalArgumentException("Sigma doit être un entier strictement positif");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Sigma doit être un nombre entier valide");
                    }
                }
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
                }
                case "--output", "-o" -> output = Paths.get(CliUtil.next(args, ++i, "--output"));
                case "-h", "--help" -> { CliUtil.printNoiseHelp(); System.exit(0); }
                default -> throw new IllegalArgumentException("Option inconnue : " + args[i]);
            }
        }
        if (sigma < 0) throw new IllegalArgumentException("--sigma est obligatoire");
        if (input == null) throw new IllegalArgumentException("--input est obligatoire");
        
        // Générer le chemin de sortie par défaut si non spécifié
        if (output == null) {
            // Préserver l'extension d'origine si c'est un fichier
            if (input.toFile().isFile()) {
                String extension = getFileExtension(input);
                // Si l'extension n'est pas supportée, utiliser PNG par défaut
                if (extension.isEmpty() || !SUPPORTED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
                    extension = ".png";
                }
                output = CliUtil.defaultOutNoise(input, sigma, extension);
            } else {
                // Pour un dossier, utiliser l'extension PNG par défaut
                output = CliUtil.defaultOutNoise(input, sigma, ".png");
            }
        }
        
        return new NoiseArgs(input, sigma, output);
    }
}