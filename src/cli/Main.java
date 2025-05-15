package cli;

import cli.parse.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;
import java.io.File;

import core.image.Album;
import core.image.Image;
import core.image.ImageFile;
import core.image.NoisedImage;

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
                    // case "denoise" -> runDenoise(DenoiseArgs.parse(rest));
                    // case "eval" -> runEval(EvalArgs.parse(rest));
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
        System.out.print("Chemin de l'image à débruiter: ");
        String inputStr = scanner.nextLine().trim();
        Path input = Paths.get(inputStr);
        
        System.out.print("Chemin de sortie (laissez vide pour la valeur par défaut): ");
        String outputStr = scanner.nextLine().trim();
        Path output = outputStr.isEmpty() ? null : Paths.get(outputStr);
        
        System.out.println("Fonctionnalité en cours de développement");
        // TODO: Implémenter quand DenoiseArgs sera disponible
    }
    
    /**
     * Collecte les arguments nécessaires pour évaluer la qualité du débruitage.
     * Demande à l'utilisateur le chemin de l'image originale et celui de l'image débruitée.
     * 
     * @param scanner Scanner pour lire l'entrée utilisateur
     */
    private static void collectEvalArgs(Scanner scanner) {
        System.out.println("\n== Évaluation du débruitage ==");
        System.out.print("Chemin de l'image originale: ");
        String originalStr = scanner.nextLine().trim();
        Path original = Paths.get(originalStr);
        
        System.out.print("Chemin de l'image débruitée: ");
        String denoisedStr = scanner.nextLine().trim();
        Path denoised = Paths.get(denoisedStr);
        
        System.out.println("Fonctionnalité en cours de développement");
        // TODO: Implémenter quand EvalArgs sera disponible
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
            try {
                // Créer une image bruitée avec le sigma spécifié
                NoisedImage noisedImg = new NoisedImage(img.getPath(), a.getSigma());
                
                // Chemin de sortie pour cette image
                Path outputPath;
                
                if (imgArray.size() > 1) {
                    // Si plusieurs images, créer un nom unique basé sur le nom de l'image originale
                    // sans inclure le chemin complet dans le nom du fichier
                    String baseName = noisedImg.getName();
                    String fileName = baseName + "_noised_" + a.getSigma() + ".png";
                    
                    // Déterminer le répertoire de sortie
                    Path outputDir;
                    if (a.getOutput().toString().endsWith(".png")) {
                        // Si l'utilisateur a spécifié un fichier de sortie, utiliser son répertoire parent
                        outputDir = a.getOutput().getParent();
                    } else {
                        // Si l'utilisateur a spécifié un dossier, l'utiliser directement
                        outputDir = a.getOutput();
                    }
                    
                    // Construire le chemin complet
                    outputPath = outputDir.resolve(fileName);
                } else {
                    // Si une seule image, utiliser le chemin de sortie directement
                    outputPath = a.getOutput();
                }
                
                // Créer le répertoire parent si nécessaire
                File outputDir = outputPath.getParent().toFile();
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }
                
                // Sauvegarder l'image bruitée
                noisedImg.saveImage(outputPath.toString());
                System.out.println("Image bruitée sauvegardée: " + outputPath);
            } catch (IOException e) {
                System.err.println("Erreur lors du traitement de l'image " + img.getPath() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Exécute l'opération de débruitage d'une image.
     * 
     * @param a Arguments pour l'opération de débruitage
     */
    // private static void runDenoise(DenoiseArgs a) { /* appel à core.Denoiser */ }
    
    /**
     * Exécute l'évaluation de la qualité du débruitage.
     * 
     * @param a Arguments pour l'opération d'évaluation
     */
    // private static void runEval(EvalArgs a) { /* appel à core.Evaluator */ }
}