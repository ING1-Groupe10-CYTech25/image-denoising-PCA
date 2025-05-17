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
    private final double patchPercent;

    /**
     * Constructeur pour les arguments de benchmark.
     * 
     * @param input Chemin vers l'image à tester
     * @param sigma Écart type du bruit
     * @param outputDir Répertoire de sortie pour les résultats
     * @param patchPercent Pourcentage de la taille minimale pour le patch
     */
    public BenchmarkArgs(Path input, double sigma, Path outputDir, double patchPercent) {
        this.input = input;
        this.sigma = sigma;
        this.outputDir = outputDir;
        this.patchPercent = patchPercent;
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
        double patchPercent = 0.1; // Valeur par défaut

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
                case "-pp", "--patchPercent" -> {
                    if (i + 1 < args.length) {
                        patchPercent = Double.parseDouble(args[++i]);
                        if (patchPercent <= 0 || patchPercent > 1) {
                            throw new IllegalArgumentException("Le pourcentage de taille de patch doit être entre 0 et 1");
                        }
                    } else {
                        throw new IllegalArgumentException("--patchPercent nécessite une valeur");
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

        return new BenchmarkArgs(input, sigma, outputDir, patchPercent);
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

    public double getPatchPercent() {
        return patchPercent;
    }
} 