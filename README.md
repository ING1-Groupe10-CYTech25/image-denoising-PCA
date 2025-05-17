# Image Denoising PCA

Ce projet implémente une méthode de débruitage d'images basée sur l'Analyse en Composantes Principales (ACP).

## Description

Le projet propose deux commandes principales :
- `noise` : Ajoute du bruit gaussien à une image
- `denoise` : Débruite une image en utilisant la méthode ACP

## Utilisation

### Commande `noise`

```bash
noise -i <chemin_image> [-o <chemin_sortie>] [-s <sigma>]
```

Options :
- `-i, --input` : Chemin vers l'image à bruiter (obligatoire)
- `-o, --output` : Chemin pour l'image bruitée (optionnel)
- `-s, --sigma` : Écart type du bruit (défaut: 30.0)
- `-h, --help` : Affiche l'aide

### Commande `denoise`

```bash
denoise -i <chemin_image> [-o <chemin_sortie>] [-g|--global] [-l|--local] [-t <type>] [-sh <type>] [-s <sigma>]
```

Options :
- `-i, --input` : Chemin vers l'image à débruiter (obligatoire)
- `-o, --output` : Chemin pour l'image débruitée (optionnel)
- `-g, --global` : Active la méthode de débruitage globale
- `-l, --local` : Active la méthode de débruitage locale (défaut)
- `-t, --threshold` : Type de seuillage (hard/h ou soft/s, défaut: hard)
- `-sh, --shrink` : Type de seuillage adaptatif (v pour VisuShrink, b pour BayesShrink)
- `-s, --sigma` : Écart type du bruit (défaut: 30.0)
- `-h, --help` : Affiche l'aide

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur

## Compilation

Pour compiler le projet :

```bash
mvn clean compile
```

## Génération du fichier JAR

Pour générer le fichier JAR exécutable :

```bash
mvn clean package
```

Le fichier JAR sera généré à la racine du projet avec le nom `image-denoising-PCA-jar-with-dependencies.jar`.

Pour exécuter le programme avec le fichier JAR :

```bash
java -jar image-denoising-PCA-jar-with-dependencies.jar
```

## Exemples

### Ajouter du bruit à une image

```bash
java -jar image-denoising-PCA-jar-with-dependencies.jar noise -i img/original/lena.png -s 30
```

### Débruiter une image avec la méthode locale

```bash
java -jar image-denoising-PCA-jar-with-dependencies.jar denoise -i img/img_noised/lena_noised_30.png -t hard -sh v -s 30
```

### Débruiter une image avec la méthode globale

```bash
java -jar image-denoising-PCA-jar-with-dependencies.jar denoise -i img/img_noised/lena_noised_30.png -g -t soft -sh b -s 30
```

## Structure du projet

```
image-denoising-PCA/
├── src/
│   ├── core/           # Code principal du débruitage
│   └── cli/            # Interface en ligne de commande
├── img/
│   ├── original/       # Images originales
│   ├── img_noised/     # Images bruitées
│   └── img_denoised/   # Images débruitées
└── pom.xml            # Configuration Maven
```

## Limitations

- Seules les images en niveaux de gris sont supportées
- Les formats d'image supportés sont : PNG, JPG, JPEG, BMP, GIF, TIFF
- La taille des patchs est fixée à 15x15 pixels

# Manuel d'utilisation - Commande EVAL

Cette documentation détaille l'utilisation de la commande `eval` de l'outil image-denoising-PCA. Cette commande permet d'évaluer la qualité entre deux images en utilisant des métriques objectives comme MSE et PSNR.

## Utilisation en ligne de commande

### Syntaxe

```bash
eval --image1 <chemin> --image2 <chemin> [--metric <type>]
```

### Options

| Option | Format court | Description | Statut |
|--------|-------------|-------------|--------|
| `--image1 <chemin>` | `-i1 <chemin>` | Chemin vers la première image (généralement l'original) | **Obligatoire** |
| `--image2 <chemin>` | `-i2 <chemin>` | Chemin vers la deuxième image (généralement l'image traitée) | **Obligatoire** |
| `--metric <type>` | `-m <type>` | Type de métrique à utiliser: 'mse', 'psnr' ou 'both' | *Facultatif* (défaut: 'both') |

### Métriques disponibles

- **MSE** (Mean Square Error / Erreur Quadratique Moyenne): Mesure la différence moyenne des carrés des erreurs entre pixels. Plus la valeur est basse, plus les images sont similaires.
- **PSNR** (Peak Signal-to-Noise Ratio / Rapport Signal/Bruit de Crête): Évalue la qualité de reconstruction d'une image compressée ou altérée. Plus la valeur est élevée, meilleure est la qualité.

### Exemples

1. Évaluer deux images en utilisant les deux métriques (MSE et PSNR) :
   ```bash
   java -jar image-denoising-PCA-jar-with-dependencies.jar eval --image1 img/original/lena.png --image2 img/img_noised/lena_10.png
   ```

2. Calculer uniquement l'erreur quadratique moyenne (MSE) :
   ```bash
   java -jar image-denoising-PCA-jar-with-dependencies.jar eval -i1 img/original/lena.png -i2 img/img_noised/lena_10.png -m mse
   ```

3. Calculer uniquement le rapport signal/bruit (PSNR) :
   ```bash
   java -jar image-denoising-PCA-jar-with-dependencies.jar eval -i1 img/original/lena.png -i2 img/img_noised/lena_10.png -m psnr
   ```

## Utilisation en mode interactif

Pour une utilisation guidée, lancez le programme sans arguments :

```bash
java -jar image-denoising-PCA-jar-with-dependencies.jar
```

1. Choisissez l'option 3 : "Évaluer la qualité du débruitage (eval)"
2. Suivez les instructions pour spécifier :
   - Le chemin de l'image originale
   - Le chemin de l'image débruitée
   - La métrique à utiliser

## Interprétation des résultats

### MSE (Mean Square Error)
- **Valeur idéale** : 0 (images identiques)
- **Interprétation** :
  - 0-10 : Différences très faibles
  - 10-50 : Différences perceptibles mais limitées
  - 50-100 : Différences notables
  - >100 : Différences importantes

### PSNR (Peak Signal-to-Noise Ratio)
- **Valeur idéale** : ∞ (images identiques)
- **Interprétation** :
  - >40 dB : Excellent (différences imperceptibles)
  - 30-40 dB : Très bon (différences difficilement perceptibles)
  - 20-30 dB : Bon (légères différences visibles)
  - <20 dB : Qualité moyenne à faible (différences notables)

## Limitations

- Les images à comparer doivent avoir exactement les mêmes dimensions
- L'évaluation porte sur les différences pixel par pixel et ne reflète pas nécessairement la perception visuelle humaine
- Pour les images couleur, l'erreur est calculée séparément sur les canaux R, G et B puis moyennée

## Messages d'erreur courants

- `--image1 est obligatoire` : Vous devez spécifier le chemin de la première image
- `--image2 est obligatoire` : Vous devez spécifier le chemin de la deuxième image
- `Les images doivent avoir les mêmes dimensions` : Les dimensions des deux images ne correspondent pas
- `Format d'image non supporté` : L'extension du fichier n'est pas reconnue
- `Métrique non supportée` : La métrique spécifiée n'est pas valide (utilisez 'mse', 'psnr' ou 'both')