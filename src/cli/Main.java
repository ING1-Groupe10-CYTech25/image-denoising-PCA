package cli;

import cli.parse.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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

    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenue dans l'application image-denoising-PCA");
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
    
    private static void collectNoiseArgs(Scanner scanner) {
        System.out.println("\n== Ajout de bruit à une image ==");
        System.out.print("Chemin de l'image d'entrée: ");
        String inputStr = scanner.nextLine().trim();
        Path input = Paths.get(inputStr);
        
        System.out.print("Valeur sigma (intensité du bruit): ");
        int sigma = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Chemin de sortie (laissez vide pour la valeur par défaut): ");
        String outputStr = scanner.nextLine().trim();
        Path output = outputStr.isEmpty() ? null : Paths.get(outputStr);
        
        try {
            // Construire manuellement un objet NoiseArgs ou appeler une méthode statique
            NoiseArgs args = new NoiseArgs(input, sigma, output);
            runNoise(args);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
    
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

    private static void runNoise(NoiseArgs a) { /* appel à core.Noiser */ }
    // private static void runDenoise(DenoiseArgs a) { /* appel à core.Denoiser */ }
    // private static void runEval(EvalArgs a) { /* appel à core.Evaluator */ }
}