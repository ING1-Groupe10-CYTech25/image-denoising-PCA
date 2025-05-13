package cli;

import cli.parse.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            CliUtil.printGlobalHelp();
            System.exit(1);
        }

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

    private static void runNoise(NoiseArgs a) { /* appel à core.Noiser */ }
    // private static void runDenoise(DenoiseArgs a) { /* appel à core.Denoiser */ }
    // private static void runEval(EvalArgs a) { /* appel à core.Evaluator */ }
}