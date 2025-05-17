package cli.parse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Classe pour gérer les arguments de la commande benchmark.
 */
public class BenchmarkArgs {
    private final Path input;
    private final double sigma;
    private final Path outputDir;

    /**
     * Constructeur pour les arguments de benchmark.
     * 
     * @param input Chemin vers l'image à tester
     * @param sigma Écart type du bruit
     * @param outputDir Répertoire de sortie pour les résultats
     */
    public BenchmarkArgs(Path input, double sigma, Path outputDir) {
        this.input = input;
        this.sigma = sigma;
        this.outputDir = outputDir;
    }

    /**
     * Parse les arguments de la ligne de commande pour la commande benchmark.
     * 
     * @param args Arguments de la ligne de commande
     * @return Instance de BenchmarkArgs
     */
    public static BenchmarkArgs parse(String[] args) {
        Path input = null;
        double sigma = 30.0;
        Path outputDir = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i", "--input" -> {
                    if (i + 1 < args.length) {
                        input = Path.of(args[++i]);
                    } else {
                        throw new IllegalArgumentException("--input nécessite un chemin d'image");
                    }
                }
                case "-s", "--sigma" -> {
                    if (i + 1 < args.length) {
                        sigma = Double.parseDouble(args[++i]);
                    } else {
                        throw new IllegalArgumentException("--sigma nécessite une valeur");
                    }
                }
                case "-o", "--output" -> {
                    if (i + 1 < args.length) {
                        outputDir = Path.of(args[++i]);
                    } else {
                        throw new IllegalArgumentException("--output nécessite un chemin");
                    }
                }
                case "-h", "--help" -> CliUtil.printBenchmarkHelp();
                default -> throw new IllegalArgumentException("Option inconnue: " + args[i]);
            }
        }

        if (input == null) {
            throw new IllegalArgumentException("--input est obligatoire");
        }

        if (outputDir == null) {
            outputDir = Paths.get("img/benchmark");
        }

        return new BenchmarkArgs(input, sigma, outputDir);
    }

    public Path getInput() {
        return input;
    }

    public double getSigma() {
        return sigma;
    }

    public Path getOutputDir() {
        return outputDir;
    }
} 