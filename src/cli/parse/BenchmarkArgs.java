package cli.parse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer les arguments de la commande benchmark.
 */
public class BenchmarkArgs {
    private final List<Path> inputs;
    private final double sigma;
    private final Path outputDir;

    /**
     * Constructeur pour les arguments de benchmark.
     * 
     * @param inputs Liste des chemins des images originales
     * @param sigma Écart type du bruit
     * @param outputDir Répertoire de sortie pour les résultats
     */
    public BenchmarkArgs(List<Path> inputs, double sigma, Path outputDir) {
        this.inputs = inputs;
        this.sigma = sigma;
        this.outputDir = outputDir;
    }

    /**
     * Parse les arguments de la ligne de commande pour la commande benchmark.
     * 
     * @param args Arguments de la ligne de commande
     * @return Instance de BenchmarkArgs
     * @throws IllegalArgumentException si les arguments sont invalides
     */
    public static BenchmarkArgs parse(String[] args) {
        List<Path> inputs = new ArrayList<>();
        double sigma = 30.0;
        Path outputDir = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-i", "--input" -> {
                    while (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                        inputs.add(Paths.get(args[++i]));
                    }
                }
                case "-s", "--sigma" -> {
                    if (i + 1 < args.length) {
                        sigma = Double.parseDouble(args[++i]);
                    } else {
                        throw new IllegalArgumentException("Valeur manquante pour l'option " + arg);
                    }
                }
                case "-o", "--output" -> {
                    if (i + 1 < args.length) {
                        outputDir = Paths.get(args[++i]);
                    } else {
                        throw new IllegalArgumentException("Valeur manquante pour l'option " + arg);
                    }
                }
                case "-h", "--help" -> {
                    CliUtil.printBenchmarkHelp();
                    System.exit(0);
                }
                default -> throw new IllegalArgumentException("Option inconnue : " + arg);
            }
        }

        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("Au moins une image d'entrée est requise (-i)");
        }

        if (outputDir == null) {
            outputDir = Paths.get("img", "benchmark");
        }

        return new BenchmarkArgs(inputs, sigma, outputDir);
    }

    public List<Path> getInputs() {
        return inputs;
    }

    public double getSigma() {
        return sigma;
    }

    public Path getOutputDir() {
        return outputDir;
    }
} 