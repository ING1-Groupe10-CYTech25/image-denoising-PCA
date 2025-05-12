# image-denoising-PCA
Projet réalisé dans le cadre d’une SAE à CY Tech Pau. Implémentation en Java d’un algorithme de débruitage d’image basé sur l’Analyse en Composantes Principales (ACP / PCA).

### Fonctionnement du parser
```bash
cli/
├─ Main.java          // analyse arg[0] et délègue
├─ parse/
│  ├─ NoiseArgs.java
│  ├─ DenoiseArgs.java
│  ├─ EvalArgs.java
│  └─ CliUtil.java    // méthodes communes : checkPath(), printGlobalHelp()
└─ ...
```