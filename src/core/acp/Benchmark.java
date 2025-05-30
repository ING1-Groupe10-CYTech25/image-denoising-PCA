package core.acp;

import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import core.eval.ImageQualityMetrics;
import core.image.ImageFile;

/**
 * Classe pour effectuer des benchmarks de débruitage sur une image.
 */
public class Benchmark {
    private final Path input;
    private final double sigma;
    private final Path outputDir;
    private final double patchPercent;

    /**
     * Constructeur pour le benchmark.
     * 
     * @param input Chemin vers l'image à tester
     * @param sigma Écart type du bruit
     * @param outputDir Répertoire de sortie pour les résultats
     * @param patchPercent Pourcentage de la taille minimale pour le patch
     */
    public Benchmark(Path input, double sigma, Path outputDir, double patchPercent) {
        this.input = input;
        this.sigma = sigma;
        this.outputDir = outputDir;
        this.patchPercent = patchPercent;
    }

    /**
     * Exécute le benchmark sur l'image d'entrée.
     * 
     * @throws IOException si une erreur survient lors de la lecture/écriture des fichiers
     */
    public void run() throws IOException {
        String baseName = input.getFileName().toString().replaceFirst("[.][^.]+$", "");
        Path imageOutputDir = outputDir.resolve(baseName + "_benchmark_" + (int)sigma);
        imageOutputDir.toFile().mkdirs();

        // Charger l'image originale (sera automatiquement convertie en niveaux de gris)
        ImageFile originalImage = new ImageFile(input.toString());
        
        // Sauvegarder l'image originale dans le dossier benchmark
        Path originalPath = imageOutputDir.resolve(baseName + ".png");
        originalImage.saveImage(originalPath.toString());

        // Créer le fichier de log dans le dossier de l'image
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(imageOutputDir.resolve("benchmark.txt").toFile()))) {
            logWriter.println("=== Benchmark pour " + input.getFileName() + " ===");
            logWriter.println("Sigma: " + sigma);
            logWriter.println("Taille de patch: " + (patchPercent * 100) + "%");
            logWriter.println();

            // Générer l'image bruitée
            String noisedPath = imageOutputDir.resolve(baseName + "_noised_" + (int)sigma + ".png").toString();
            ImageFile noisedImage = new ImageFile(originalImage);
            noisedImage.noisify((int)sigma);
            noisedImage.saveImage(noisedPath);

            // Tester toutes les configurations
            testAllConfigurations(originalPath.toString(), noisedPath, imageOutputDir.toString(), logWriter);
            logWriter.println();
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
        // Extraire le nom de base de l'image
        String baseName = Path.of(originalPath).getFileName().toString().replaceFirst("[.][^.]+$", "");

        // Charger les images originale et bruitée pour comparaison
        ImageFile originalImage = new ImageFile(originalPath);
        ImageFile noisedImage = new ImageFile(noisedPath);
        BufferedImage originalBuffered = originalImage.getImage();
        BufferedImage noisedBuffered = noisedImage.getImage();

        // Calculer MSE et PSNR pour l'image bruitée par rapport à l'originale
        double noisedMSE = ImageQualityMetrics.calculateMSE(originalBuffered, noisedBuffered);
        double noisedPSNR = ImageQualityMetrics.calculatePSNR(noisedMSE, 255);

        logWriter.println("=== Image bruitée ===");
        logWriter.printf("MSE: %.2f\n", noisedMSE);
        logWriter.printf("PSNR: %.2f dB\n", noisedPSNR);
        logWriter.println();

        // Tester toutes les combinaisons de méthodes
        boolean[] globalOptions = {true, false};
        String[] thresholdOptions = {"hard", "soft"};
        String[] shrinkOptions = {"v", "b"};

        for (boolean isGlobal : globalOptions) {
            for (String threshold : thresholdOptions) {
                for (String shrink : shrinkOptions) {
                    // Construire le nom du fichier de sortie
                    String method = isGlobal ? "global" : "local";
                    String outputName = String.format("%s/%s_denoised_%s_%s_%s.png", 
                                                    outputDir, baseName, method, threshold, shrink);

                    // Débruiter l'image
                    logWriter.println("Débruitage avec méthode: " + method + 
                                    ", seuillage: " + threshold + 
                                    ", seuillage adaptatif: " + shrink);

                    ImageDenoiser.ImageDen(noisedPath, outputName, isGlobal, threshold, shrink, sigma, patchPercent);
                    logWriter.println("Image débruitée sauvegardée dans: " + outputName);
                    System.out.println("Traitement de : " + outputName);

                    // Évaluer le résultat
                    ImageFile denoisedImage = new ImageFile(outputName);
                    BufferedImage denoisedBuffered = denoisedImage.getImage();

                    double denoisedMSE = ImageQualityMetrics.calculateMSE(originalBuffered, denoisedBuffered);
                    double denoisedPSNR = ImageQualityMetrics.calculatePSNR(denoisedMSE, 255);

                    // Calculer les pourcentages d'amélioration
                    double mseImprovement = 100 * (noisedMSE - denoisedMSE) / noisedMSE;
                    double psnrImprovement = denoisedPSNR - noisedPSNR;

                    logWriter.printf("MSE: %.2f (Amélioration: %.2f%%)\n", 
                                   denoisedMSE, mseImprovement);
                    logWriter.printf("PSNR: %.2f dB (Amélioration: %.2f dB)\n", 
                                   denoisedPSNR, psnrImprovement);
                    logWriter.println();
                }
            }
        }
    }
}