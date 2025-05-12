package cli.parse;

import java.nio.file.Path;
import java.nio.file.Paths;

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
 */
public final class NoiseArgs {
    private final int sigma;
    private final Path input;
    private final Path output;

    private NoiseArgs(int sigma, Path input, Path output) {
        this.sigma  = sigma;
        this.input  = input;
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
                case "--sigma", "-s" -> sigma = Integer.parseInt(CliUtil.next(args, ++i, "--sigma"));
                case "--input", "-i" -> input = Paths.get(CliUtil.next(args, ++i, "--input"));
                case "--output", "-o" -> output = Paths.get(CliUtil.next(args, ++i, "--output"));
                case "-h", "--help" -> { CliUtil.printNoiseHelp(); System.exit(0); }
                default -> throw new IllegalArgumentException("Option inconnue : " + args[i]);
            }
        }
        if (sigma < 0) throw new IllegalArgumentException("--sigma est obligatoire");
        if (input == null) throw new IllegalArgumentException("--input est obligatoire");
        if (output == null) output = CliUtil.defaultOutNoise(input, sigma);
        return new NoiseArgs(sigma, input, output);
    }
}