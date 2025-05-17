package cli;

import cli.parse.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import core.image.Album;
import core.image.ImageFile;
import core.eval.ImageQualityMetrics;
import core.acp.ImageDenoiser;

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
            runInteractiveMode();
        } else {
            String cmd = args[0]; // 1er argument de la liste
            String[] rest = java.util.Arrays.copyOfRange(args, 1, args.length); // copie toute la liste sauf le 1er élément

            try {
                switch (cmd) {
                    case "noise" -> runNoise(NoiseArgs.parse(rest));
                    case "denoise" -> runDenoise(DenoiseArgs.parse(rest));
                    case "eval" -> runEval(EvalArgs.parse(rest));
                    case "--help", "-h" -> CliUtil.printGlobalHelp();
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
        System.out.println("// image-denoising-PCA //");
        System.out.println("Que voulez-vous faire ?");
        System.out.println("1. Ajouter du bruit à une image (noise)");
        System.out.println("2. Débruiter une image (denoise)");
        System.out.println("3. Évaluer la qualité du débruitage (eval)");
        
        int choice = getValidChoice(scanner, 1, 3);
        
        switch (choice) {
            case 1 -> collectNoiseArgs(scanner);
            case 2 -> collectDenoiseArgs(scanner);
            case 3 -> collectEvalArgs(scanner);
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
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
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
        
        // Demander le chemin de sortie
        System.out.print("Chemin de sortie (laissez vide pour la valeur par défaut): ");
        String outputStr = scanner.nextLine().trim();
        Path output = null;
        
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
            DenoiseArgs args = new DenoiseArgs(input, output, isGlobal, threshold, shrink);
            runDenoise(args);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
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
    private static void runDenoise(DenoiseArgs args) throws IOException {
        // Paramètres de débruitage
        int patchSize = 15; // Taille des patchs par défaut
        double sigma = 30;  // Écart type du bruit par défaut
        
        // Débruiter l'image
        System.out.println("Débruitage de l'image...");
        System.out.println("Méthode: " + (args.isGlobal() ? "globale" : "locale"));
        System.out.println("Seuillage: " + args.getThreshold());
        if (args.getShrink() != null) {
            System.out.println("Seuillage adaptatif: " + args.getShrink());
        }
        
        // Appeler la méthode de débruitage
        ImageDenoiser.ImageDen(
            args.getInput().toString(),
            args.getOutput().toString(),
            patchSize,
            args.isGlobal(),
            args.getThreshold(),
            args.getShrink(),
            sigma
        );
        
        System.out.println("Image débruitée sauvegardée dans: " + args.getOutput());
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
}