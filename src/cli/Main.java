package cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import cli.parse.BenchmarkArgs;
import cli.parse.CliUtil;
import cli.parse.DenoiseArgs;
import cli.parse.EvalArgs;
import cli.parse.NoiseArgs;
import core.acp.Benchmark;
import core.acp.ImageDenoiser;
import core.eval.ImageQualityMetrics;
import core.image.Album;
import core.image.ImageFile;

/**
 * Classe principale de l'application en ligne de commande pour le traitement d'images.
 * Permet d'ajouter du bruit aux images, de les débruiter et d'évaluer la qualité du débruitage.
 * Supporte à la fois le mode ligne de commande et un mode interactif.
 */
public class Main {
    /**
     * Point d'entrée principal de l'application.
     * Si aucun argument n'est fourni, lance le mode interactif.
     * Sinon, traite les arguments de la ligne de commande.
     * 
     * @param args Arguments de la ligne de commande
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            // Lancez l'interface graphique si aucun argument n'est fourni
            try {
                gui.Main.main(args);
            } catch (NoClassDefFoundError e) {
                // Si JavaFX n'est pas disponible, basculez vers le mode interactif CLI
                System.out.println("Interface graphique non disponible, lancement en mode interactif...");
                runInteractiveMode();
            }
        } else {
            String cmd = args[0]; // 1er argument de la liste
            String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length); // copie toute la liste sauf le 1er élément
    
            try {
                switch (cmd) {
                    case "gui" -> gui.Main.main(args);
                    case "prompt" -> runInteractiveMode();

                    case "noise" -> runNoise(NoiseArgs.parse(rest));
                    case "denoise" -> runDenoise(DenoiseArgs.parse(rest));
                    case "eval" -> runEval(EvalArgs.parse(rest));
                    case "benchmark" -> runBenchmark(BenchmarkArgs.parse(rest));
                    case "--help", "-h", "help" -> CliUtil.printGlobalHelp();

                    default -> throw new IllegalArgumentException("Commande inconnue : " + cmd);
                }
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Lance le mode interactif de l'application.
     * Affiche un menu permettant à l'utilisateur de choisir une action et collecte
     * les arguments nécessaires pour cette action.
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""                                              
 _ *                      _             _     _            _____ _____ _____ 
|_|_____ _*_ _*_ ___    _| |___ ___ ___|_|___|_|___ ___   |  _  |     |  _  |
| |*   *| .'| . | -_|* | . | -_|   | . | |_ -| |   | . |  |   __|   --|     |
|_|_|_|_|__,|_ *|___|  |___|___|_|_|___|_|___|_|_|_|_  |  |__|  |_____|__|__|
*  *     *  |___| *                                |___|                     
*     *        *    *         GROUPE 10
        """);
        System.out.println("Que voulez-vous faire ?");
        System.out.println("1. Ajouter du bruit à une image (noise)");
        System.out.println("2. Débruiter une image (denoise)");
        System.out.println("3. Évaluer la qualité du débruitage (eval)");
        System.out.println("4. Effectuer un benchmark complet (benchmark)");
        
        int choice = getValidChoice(scanner, 1, 4);
        
        switch (choice) {
            case 1 -> collectNoiseArgs(scanner);
            case 2 -> collectDenoiseArgs(scanner);
            case 3 -> collectEvalArgs(scanner);
            case 4 -> collectBenchmarkArgs(scanner);
        }
    }
    
    /**
     * Obtient un choix valide de l'utilisateur dans une plage spécifiée.
     * Continue à demander jusqu'à ce qu'un nombre valide dans la plage soit entré.
     * 
     * @param scanner Scanner pour lire l'entrée utilisateur
     * @param min Valeur minimale acceptée
     * @param max Valeur maximale acceptée
     * @return Le choix valide entré par l'utilisateur
     */
    private static int getValidChoice(Scanner scanner, int min, int max) {
        int choice = 0;
        boolean validInput = false;
        
        while (!validInput) {
            System.out.print("Votre choix [" + min + "-" + max + "]: ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    validInput = true;
                } else {
                    System.out.println("Veuillez entrer un nombre entre " + min + " et " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide");
            }
        }
        
        return choice;
    }
    
    /**
     * Collecte les arguments nécessaires pour ajouter du bruit à une image.
     * Demande à l'utilisateur le chemin d'entrée, l'intensité du bruit et le chemin de sortie.
     * 
     * @param scanner Scanner pour lire l'entrée utilisateur
     */
    private static void collectNoiseArgs(Scanner scanner) {
        System.out.println("\n== Ajout de bruit à une image ==");
        System.out.print("Chemin de l'image d'entrée: ");
        String inputStr = scanner.nextLine().trim();
        Path input = Paths.get(inputStr);
        
        System.out.print("Valeur sigma (intensité du bruit): ");
        int sigma = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Chemin de sortie (laissez vide pour la valeur par défaut): ");
        String outputStr = scanner.nextLine().trim();
        Path output = outputStr.isEmpty() ? 
                      CliUtil.defaultOutNoise(input, sigma) : Paths.get(outputStr);
        
        try {
            NoiseArgs args = new NoiseArgs(input, sigma, output);
            runNoise(args);
        } catch (IOException e) {
            System.err.println("Erreur d'entrée/sortie: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur d'argument: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Collecte les arguments nécessaires pour débruiter une image.
     * Demande à l'utilisateur le chemin de l'image à débruiter et le chemin de sortie.
     * 
     * @param scanner Scanner pour lire l'entrée utilisateur
     */
    private static void collectDenoiseArgs(Scanner scanner) {
        System.out.println("\n== Débruitage d'une image ==");
        
        // Demander le chemin d'entrée
        System.out.print("Chemin de l'image à débruiter: ");
        String inputStr = scanner.nextLine().trim();
        Path input = Paths.get(inputStr);
        
        // Demander le type de méthode (globale ou locale)
        System.out.print("Méthode à utiliser (G:globale/L:locale, défaut: locale): ");
        String methodStr = scanner.nextLine().trim().toLowerCase();
        boolean isGlobal = methodStr.startsWith("g");
        
        // Demander le type de seuillage
        System.out.print("Type de seuillage (hard/soft, défaut: hard): ");
        String thresholdStr = scanner.nextLine().trim();
        String threshold = thresholdStr.isEmpty() ? "hard" : thresholdStr;
        
        // Demander le type de seuillage adaptatif, peu importe la méthode
        System.out.print("Type de seuillage adaptatif (v:VisuShrink/b:BayesShrink, vide: aucun): ");
        String shrinkStr = scanner.nextLine().trim().toLowerCase();
        String shrink = shrinkStr.isEmpty() ? null : shrinkStr;
        
        // Demander sigma (optionnel)
        System.out.print("Valeur sigma (laisser vide pour auto/déduction): ");
        String sigmaStr = scanner.nextLine().trim();
        double sigma;
        if (sigmaStr.isEmpty()) {
            // Essayer d'extraire sigma du nom du fichier
            sigma = DenoiseArgs.parse(new String[]{"-i", inputStr}).getSigma();
        } else {
            try {
                sigma = Double.parseDouble(sigmaStr);
            } catch (NumberFormatException e) {
                System.err.println("Valeur de sigma invalide, utilisation de 30.0 par défaut.");
                sigma = 30.0;
            }
        }
        
        // Demander le pourcentage de taille de patch
        System.out.print("Pourcentage de taille de patch (entre 0 et 1, défaut: 0.05): ");
        String patchPercentStr = scanner.nextLine().trim();
        double patchPercent;
        if (patchPercentStr.isEmpty()) {
            patchPercent = 0.05;
        } else {
            try {
                patchPercent = Double.parseDouble(patchPercentStr);
                if (patchPercent <= 0 || patchPercent > 1) {
                    System.err.println("Valeur invalide, utilisation de 0.05 par défaut.");
                    patchPercent = 0.05;
                }
            } catch (NumberFormatException e) {
                System.err.println("Valeur invalide, utilisation de 0.05 par défaut.");
                patchPercent = 0.05;
            }
        }
        
        // Demander le chemin de sortie
        System.out.print("Chemin de sortie (défault : img/img_denoised/): ");
        String outputStr = scanner.nextLine().trim();
        Path output;
        
        if (!outputStr.isEmpty()) {
            output = Paths.get(outputStr);
        } else {
            // Générer le chemin par défaut
            String method = isGlobal ? "global" : "local";
            String shrinkSuffix = (shrink != null) ? "_" + shrink : "";
            String extension = DenoiseArgs.getFileExtension(input);
            if (extension.isEmpty() || !DenoiseArgs.isImageFile(input)) {
                extension = ".png";
            }
            output = CliUtil.defaultOutDenoise(input, method, threshold, shrinkSuffix, extension);
        }
        
        try {
            DenoiseArgs args = new DenoiseArgs(input, output, isGlobal, threshold, shrink, sigma, patchPercent);
            runDenoise(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur d'argument: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Collecte les arguments nécessaires pour évaluer la qualité du débruitage.
     * Demande à l'utilisateur le chemin de l'image originale et celui de l'image débruitée.
     * 
     * @param scanner Scanner pour lire l'entrée utilisateur
     */
    private static void collectEvalArgs(Scanner scanner) {
        System.out.println("\n== Évaluation du débruitage ==");
        
        // Demander le chemin de la première image
        System.out.print("Chemin de l'image originale: ");
        String originalStr = scanner.nextLine().trim();
        Path original = Paths.get(originalStr);
        
        // Demander le chemin de la deuxième image
        System.out.print("Chemin de l'image débruitée: ");
        String denoisedStr = scanner.nextLine().trim();
        Path denoised = Paths.get(denoisedStr);
        
        // Demander la métrique à utiliser
        System.out.print("Métrique à utiliser (mse/psnr/both, défaut: both): ");
        String metricStr = scanner.nextLine().trim();
        // Si vide, utiliser "both" comme valeur par défaut
        String metric = metricStr.isEmpty() ? "both" : metricStr;
        
        try {
            EvalArgs args = new EvalArgs(original, denoised, metric);
            runEval(args);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void collectBenchmarkArgs(Scanner scanner) {
        System.out.println("\n== Benchmark de débruitage ==");
        
        // Demander le chemin d'entrée avec une valeur par défaut
        System.out.print("Chemin de l'image à tester (défaut: img/original/lena.png): ");
        String inputStr = scanner.nextLine().trim();
        if (inputStr.isEmpty()) {
            inputStr = "img/original/lena.png";
        }
        Path input = Paths.get(inputStr);
        
        // Demander sigma
        System.out.print("Valeur sigma (intensité du bruit, défaut: 30.0): ");
        String sigmaStr = scanner.nextLine().trim();
        double sigma = sigmaStr.isEmpty() ? 30.0 : Double.parseDouble(sigmaStr);
        
        // Demander le pourcentage de taille de patch
        System.out.print("Pourcentage de taille de patch (entre 0 et 1, défaut: 0.05): ");
        String patchPercentStr = scanner.nextLine().trim();
        double patchPercent;
        if (patchPercentStr.isEmpty()) {
            patchPercent = 0.05;
        } else {
            try {
                patchPercent = Double.parseDouble(patchPercentStr);
                if (patchPercent <= 0 || patchPercent > 1) {
                    System.err.println("Valeur invalide, utilisation de 0.05 par défaut.");
                    patchPercent = 0.05;
                }
            } catch (NumberFormatException e) {
                System.err.println("Valeur invalide, utilisation de 0.05 par défaut.");
                patchPercent = 0.05;
            }
        }
        
        // Demander le répertoire de sortie
        System.out.print("Répertoire de sortie (défaut : img/benchmark): ");
        String outputStr = scanner.nextLine().trim();
        Path output = outputStr.isEmpty() ? 
                     Paths.get("img/benchmark") : Paths.get(outputStr);
        
        try {
            BenchmarkArgs args = new BenchmarkArgs(input, sigma, output, patchPercent);
            runBenchmark(args);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Exécute l'opération d'ajout de bruit à une image.
     * 
     * @param a Arguments pour l'opération de bruit
     * @throws IOException 
     */
    private static void runNoise(NoiseArgs a) throws IOException { 
        String pathStr = a.getInput().toString();
        Album album = new Album(pathStr);
        System.out.println(album);

        List<ImageFile> imgArray = album.getAlbum();
        
        // Pour chaque image de l'album, ajouter du bruit et l'enregistrer
        for (ImageFile img : imgArray) {
            // Créer une image bruitée avec le sigma spécifié
            img.noisify(a.getSigma());
            
            // Chemin de sortie pour cette image
            Path outputPath;
            
            // Créer un nom unique basé sur le nom de l'image originale
            String baseName = img.getName();
            String fileName = baseName + "_noised_" + a.getSigma() + ".png";
            
            // Déterminer le répertoire de sortie
            Path outputDir;
            
            // Vérifier si le chemin de sortie est un fichier image (quelle que soit l'extension)
            if (NoiseArgs.isImageFile(a.getOutput())) {
                // Si l'utilisateur a spécifié un fichier, utiliser son répertoire parent
                outputDir = a.getOutput().getParent();
                
                // Si le répertoire parent est null, utiliser le répertoire courant
                if (outputDir == null) {
                    outputDir = Paths.get(".");
                }
            } else {
                // Si l'utilisateur a spécifié un dossier, l'utiliser directement
                outputDir = a.getOutput();
            }
            
            // Si l'entrée est un dossier, créer un sous-dossier avec la date
            if (a.getInput().toFile().isDirectory()) {
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm")); // format de date triable simplement
                outputDir = outputDir.resolve(timestamp);
            }
            
            // Construire le chemin complet
            outputPath = outputDir.resolve(fileName);
            
            // Assurer que le répertoire de sortie existe
            File outputDirFile = outputPath.getParent().toFile();
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
            
            // Sauvegarder l'image bruitée
            img.saveImage(outputPath.toString());
            System.out.println("Image bruitée sauvegardée: " + outputPath);
        }
    }

    /**
     * Exécute l'opération de débruitage d'une image.
     * 
     * @param args Arguments pour l'opération de débruitage
     * @throws IOException 
     */
    private static void runDenoise(DenoiseArgs args) {
        try {
            File inputFile = args.getInput().toFile();
            
            if (inputFile.isDirectory()) {
                // Créer le dossier de sortie avec la date
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm"));
                File outputDir = args.getOutput().resolve(timestamp).toFile();
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                
                // Liste pour stocker tous les fichiers images trouvés
                List<File> imageFiles = new ArrayList<>();
                
                // Fonction récursive pour trouver tous les fichiers images
                findImageFiles(inputFile, imageFiles);
                
                if (imageFiles.isEmpty()) {
                    throw new IllegalArgumentException("Aucune image valide trouvée dans le dossier et ses sous-dossiers");
                }
                
                // Traiter chaque image trouvée
                for (File file : imageFiles) {
                    String inputPath = file.getAbsolutePath();
                    String outputPath = outputDir.getAbsolutePath() + "/" + 
                                      file.getName().replaceFirst("[.][^.]+$", "") + 
                                      "_denoised_" + (args.isGlobal() ? "global" : "local") + 
                                      "_" + args.getThreshold() + 
                                      (args.getShrink() != null ? "_" + args.getShrink() : "") + 
                                      ".png";
                    
                    System.out.println("Traitement de : " + file.getName());
                    
                    // Débruiter l'image
                    ImageDenoiser.ImageDen(
                        inputPath,
                        outputPath,
                        args.isGlobal(),
                        args.getThreshold(),
                        args.getShrink(),
                        args.getSigma(),
                        args.getPatchPercent()
                    );
                    
                    System.out.println("Image débruitée sauvegardée dans: " + outputPath);
                }
            } else {
                // Traiter une seule image
                ImageDenoiser.ImageDen(
                    args.getInput().toString(),
                    args.getOutput().toString(),
                    args.isGlobal(),
                    args.getThreshold(),
                    args.getShrink(),
                    args.getSigma(),
                    args.getPatchPercent()
                );
                
                System.out.println("Image débruitée sauvegardée dans: " + args.getOutput());
            }
                
        } catch (Exception e) {
            System.err.println("Erreur lors du débruitage: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Fonction récursive pour trouver tous les fichiers images dans un dossier et ses sous-dossiers.
     * 
     * @param directory Le dossier à explorer
     * @param imageFiles La liste où stocker les fichiers images trouvés
     */
    private static void findImageFiles(File directory, List<File> imageFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Explorer récursivement les sous-dossiers
                    findImageFiles(file, imageFiles);
                } else {
                    // Vérifier si le fichier est une image
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".png") || name.endsWith(".jpg") || 
                        name.endsWith(".jpeg") || name.endsWith(".bmp") || 
                        name.endsWith(".gif") || name.endsWith(".tiff") || 
                        name.endsWith(".tif")) {
                        imageFiles.add(file);
                    }
                }
            }
        }
    }
    
    /**
     * Exécute l'évaluation de la qualité du débruitage.
     * Compare deux images en utilisant les métriques MSE, PSNR ou les deux.
     * 
     * @param a Arguments pour l'opération d'évaluation
     */
    private static void runEval(EvalArgs a) {
        try {
            // Charger les images
            BufferedImage image1 = ImageIO.read(a.getImage1().toFile());
            BufferedImage image2 = ImageIO.read(a.getImage2().toFile());
            
            // Vérifier que les dimensions correspondent
            if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
                System.err.println("Erreur: Les images doivent avoir les mêmes dimensions");
                System.err.println("Image 1: " + image1.getWidth() + "x" + image1.getHeight());
                System.err.println("Image 2: " + image2.getWidth() + "x" + image2.getHeight());
                return;
            }
            
            // Afficher les informations sur les images
            System.out.println("\n== Évaluation de la qualité d'image ==");
            System.out.println("Image 1: " + a.getImage1());
            System.out.println("Image 2: " + a.getImage2());
            System.out.println("Dimensions: " + image1.getWidth() + "x" + image1.getHeight() + " pixels");
            
            // Évaluer en fonction de la métrique choisie
            String metric = a.getMetric();
            double mse = 0;
            double psnr = 0;
            
            // Calculer MSE si demandé
            if (metric.equals("mse") || metric.equals("both")) {
                mse = ImageQualityMetrics.calculateMSE(image1, image2);
                System.out.printf("\nMSE (Mean Square Error): %.4f\n", mse);
                System.out.println("Plus la valeur est basse, plus les images sont similaires.");
            }
            
            // Calculer PSNR si demandé
            if (metric.equals("psnr") || metric.equals("both")) {
                // Si MSE n'a pas été calculé mais qu'on veut le PSNR, calculer MSE d'abord
                if (mse == 0 && !metric.equals("mse")) {
                    mse = ImageQualityMetrics.calculateMSE(image1, image2);
                }
                
                psnr = ImageQualityMetrics.calculatePSNR(mse, 255);
                if (Double.isInfinite(psnr)) {
                    System.out.println("\nPSNR (Peak Signal-to-Noise Ratio): Infini (images identiques)");
                } else {
                    System.out.printf("\nPSNR (Peak Signal-to-Noise Ratio): %.2f dB\n", psnr);
                    System.out.println("Plus la valeur est élevée, meilleure est la qualité.");
                    
                    // Aide à l'interprétation
                    if (psnr > 40) {
                        System.out.println("Interprétation: Excellent (différences imperceptibles)");
                    } else if (psnr > 30) {
                        System.out.println("Interprétation: Très bon (différences difficilement perceptibles)");
                    } else if (psnr > 20) {
                        System.out.println("Interprétation: Bon (légères différences visibles)");
                    } else {
                        System.out.println("Interprétation: Qualité moyenne à faible (différences notables)");
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'évaluation: " + e.getMessage());
        }
    }

    private static void runBenchmark(BenchmarkArgs args) {
        try {
            // Créer le répertoire de sortie s'il n'existe pas
            File outputDir = args.getOutputDir().toFile();
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Exécuter le benchmark
            Benchmark benchmark = new Benchmark(args.getInput(), args.getSigma(), args.getOutputDir(), args.getPatchPercent());
            benchmark.run();
            
            System.out.println("Benchmark terminé. Résultats sauvegardés dans : " + args.getOutputDir());
            
        } catch (IOException e) {
            System.err.println("Erreur d'entrée/sortie lors du benchmark: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur d'argument lors du benchmark: " + e.getMessage());
            System.exit(1);
        }
    }
}