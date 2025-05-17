package core.acp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import core.eval.ImageQualityMetrics;
import core.image.ImageFile;

/**
 * Classe pour effectuer des benchmarks sur les méthodes de débruitage.
 */
public class Benchmark {
    private final List<Path> inputs;
    private final double sigma;
    private final Path outputDir;

    /**
     * Constructeur pour le benchmark.
     * 
     * @param inputs Liste des chemins des images originales
     * @param sigma Écart type du bruit
     * @param outputDir Répertoire de sortie pour les résultats
     * @throws IOException si une erreur survient lors de la création du fichier de log
     */
    public Benchmark(List<Path> inputs, double sigma, Path outputDir) throws IOException {
        this.inputs = inputs;
        this.sigma = sigma;
        this.outputDir = outputDir;
    }

    /**
     * Exécute le benchmark sur toutes les images d'entrée.
     * 
     * @throws IOException si une erreur survient lors de la lecture/écriture des fichiers
     */
    public void run() throws IOException {
        for (Path input : inputs) {
            String baseName = input.getFileName().toString().replaceFirst("[.][^.]+$", "");
            Path imageOutputDir = outputDir.resolve(baseName + "_benchmark_" + (int)sigma);
            imageOutputDir.toFile().mkdirs();

            // Copier l'image originale dans le dossier de benchmark
            java.nio.file.Files.copy(
                input,
                imageOutputDir.resolve(input.getFileName()),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            // Créer le fichier de log dans le dossier de l'image
            try (PrintWriter logWriter = new PrintWriter(new FileWriter(imageOutputDir.resolve("benchmark.txt").toFile()))) {
                logWriter.println("=== Benchmark pour " + input.getFileName() + " ===");
                logWriter.println("Sigma: " + sigma);
                logWriter.println();

                // Générer l'image bruitée
                String noisedPath = imageOutputDir.resolve(baseName + "_noised_" + (int)sigma + ".png").toString();
                ImageFile originalImage = new ImageFile(input.toString());
                originalImage.noisify((int)sigma);
                originalImage.saveImage(noisedPath);

                // Tester toutes les configurations
                testAllConfigurations(input.toString(), noisedPath, imageOutputDir.toString(), logWriter);
                logWriter.println();
            }
        }
    }

    /**
     * Teste toutes les configurations de débruitage pour une image.
     * 
     * @param originalPath chemin de l'image originale
     * @param noisedPath chemin de l'image bruitée
     * @param outputDir répertoire de sortie
     * @param logWriter writer pour le fichier de log
     * @throws IOException si une erreur survient lors de la lecture/écriture des fichiers
     */
    private void testAllConfigurations(String originalPath, String noisedPath, String outputDir, PrintWriter logWriter) 
            throws IOException {
        // Charger les images
        ImageFile originalImage = new ImageFile(originalPath);
        ImageFile noisedImage = new ImageFile(noisedPath);

        // Calculer les métriques pour l'image bruitée
        double noisedMSE = ImageQualityMetrics.calculateMSE(originalImage.getImage(), noisedImage.getImage());
        double noisedPSNR = ImageQualityMetrics.calculatePSNR(noisedMSE, 255);

        logWriter.println("== Image bruitée ==");
        logWriter.printf("MSE: %.2f\n", noisedMSE);
        logWriter.printf("PSNR: %.2f dB\n", noisedPSNR);
        logWriter.println();

        // Tester toutes les combinaisons
        boolean[] globalOptions = {true, false};
        String[] thresholdOptions = {"hard", "soft"};
        String[] shrinkOptions = {"v", "b"};

        List<BenchmarkResult> results = new ArrayList<>();

        for (boolean isGlobal : globalOptions) {
            for (String threshold : thresholdOptions) {
                for (String shrink : shrinkOptions) {
                    String method = isGlobal ? "global" : "local";
                    String outputName = String.format("%s/%s_denoised_%s_%s_%s.png", 
                                                    outputDir, 
                                                    Paths.get(originalPath).getFileName().toString().replaceFirst("[.][^.]+$", ""),
                                                    method, threshold, shrink);

                    // Débruiter l'image
                    logWriter.println("Débruitage avec méthode: " + method + 
                                    ", seuillage: " + threshold + 
                                    ", seuillage adaptatif: " + shrink);

                    ImageDenoiser.ImageDen(noisedPath, outputName, isGlobal, threshold, shrink, sigma);
                    logWriter.println("Image débruitée sauvegardée dans: " + outputName);

                    // Évaluer le résultat
                    ImageFile denoisedImage = new ImageFile(outputName);
                    double denoisedMSE = ImageQualityMetrics.calculateMSE(originalImage.getImage(), denoisedImage.getImage());
                    double denoisedPSNR = ImageQualityMetrics.calculatePSNR(denoisedMSE, 255);

                    logWriter.printf("MSE: %.2f\n", denoisedMSE);
                    logWriter.printf("PSNR: %.2f dB\n", denoisedPSNR);
                    logWriter.printf("Amélioration MSE: %.2f%%\n", 
                                    100 * (noisedMSE - denoisedMSE) / noisedMSE);
                    logWriter.printf("Amélioration PSNR: %.2f dB\n", 
                                    denoisedPSNR - noisedPSNR);
                    logWriter.println();

                    results.add(new BenchmarkResult(method, threshold, shrink, denoisedMSE, denoisedPSNR));
                }
            }
        }

        // Afficher le résumé des meilleurs résultats
        logWriter.println("== Résumé des meilleurs résultats ==");
        results.sort((a, b) -> Double.compare(a.mse, b.mse));
        logWriter.println("Meilleur MSE:");
        logWriter.printf("  %s, %s, %s: %.2f\n", 
                        results.get(0).method, results.get(0).threshold, results.get(0).shrink, results.get(0).mse);

        results.sort((a, b) -> Double.compare(b.psnr, a.psnr));
        logWriter.println("Meilleur PSNR:");
        logWriter.printf("  %s, %s, %s: %.2f dB\n", 
                        results.get(0).method, results.get(0).threshold, results.get(0).shrink, results.get(0).psnr);
    }

    /**
     * Classe interne pour stocker les résultats du benchmark.
     */
    private static class BenchmarkResult {
        final String method;
        final String threshold;
        final String shrink;
        final double mse;
        final double psnr;

        BenchmarkResult(String method, String threshold, String shrink, double mse, double psnr) {
            this.method = method;
            this.threshold = threshold;
            this.shrink = shrink;
            this.mse = mse;
            this.psnr = psnr;
        }
    }
} 