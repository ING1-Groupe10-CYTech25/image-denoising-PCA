# image-denoising-PCA
Projet réalisé dans le cadre d'une SAE à CY Tech Pau. Implémentation en Java d'un algorithme de débruitage d'image basé sur l'Analyse en Composantes Principales (ACP / PCA).

# Manuel d'utilisation - Commande NOISE

Cette documentation détaille l'utilisation de la commande `noise` de l'outil image-denoising-PCA. Cette commande permet d'ajouter un bruit gaussien à une image en niveaux de gris.

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- Structure de dossiers recommandée :
  ```
  image-denoising-PCA/
  ├── src/                    # Code source Java
  │   └── cli/               # Classes de l'interface en ligne de commande
  ├── img/                    # Répertoire pour les images
  │   ├── original/           # Images originales
  │   └── img_noised/         # Images bruitées (sortie de noise)
  └── target/                 # Classes Java compilées (généré par Maven)
  ```

## Compilation

Pour compiler le projet, utilisez la commande Maven suivante :

```bash
mvn clean compile
```

## Génération du fichier JAR

Pour générer un fichier JAR exécutable avec toutes les dépendances, utilisez la commande :

```bash
mvn clean package
```

Le fichier JAR sera généré dans le répertoire `target` avec le nom `image-denoising-PCA-jar-with-dependencies.jar`.

Pour exécuter le programme avec le fichier JAR :

```bash
java -jar target/image-denoising-PCA-jar-with-dependencies.jar
```

## Utilisation en ligne de commande

### Syntaxe

```bash
mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="noise [options]"
```

### Options

| Option | Format court | Description | Statut |
|--------|-------------|-------------|--------|
| `--sigma <valeur>` | `-s <valeur>` | Intensité du bruit (entier positif) | **Obligatoire** |
| `--input <chemin>` | `-i <chemin>` | Chemin vers l'image source | **Obligatoire** |
| `--output <chemin>` | `-o <chemin>` | Chemin de destination pour l'image bruitée | *Facultatif* |
| `--help` | `-h` | Affiche l'aide de la commande noise | *Facultatif* |

Si l'option `--output` est omise, l'image sera automatiquement sauvegardée dans le dossier `img/img_noised/` avec le nom de l'image originale suivi de la valeur sigma, au format PNG : `<nom_image>_<sigma>.png`.

### Exemples

1. Ajouter un bruit faible (sigma=10) à une image :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="noise --sigma 10 --input img/original/lena.png"
   ```
   Résultat : `img/img_noised/lena_10.png`

2. Ajouter un bruit moyen (sigma=25) avec chemin de sortie personnalisé :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="noise -s 25 -i img/original/lena.png -o img/lena_bruit_moyen.png"
   ```
   Résultat : `img/lena_bruit_moyen.png`

3. Ajouter un bruit fort (sigma=50) :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="noise -s 50 -i img/original/lena.png"
   ```
   Résultat : `img/img_noised/lena_50.png`

4. Afficher l'aide de la commande noise :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="noise --help"
   ```

## Utilisation en mode interactif

Pour une utilisation guidée, lancez le programme sans arguments :

```bash
mvn exec:java -Dexec.mainClass="cli.Main"
```

1. Choisissez l'option 1 : "Ajouter du bruit à une image (noise)"
2. Suivez les instructions pour spécifier :
   - Le chemin de l'image d'entrée
   - La valeur sigma (intensité du bruit)
   - Le chemin de sortie (optionnel)

## Traitement par lots

La commande `noise` peut également traiter un dossier entier d'images. Si le chemin spécifié avec `--input` est un dossier, l'application ajoutera du bruit à toutes les images qu'il contient.

```bash
mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="noise -s 20 -i img/original/"
```

Toutes les images du dossier `img/original/` seront bruitées avec un sigma de 20, et les résultats seront enregistrés dans `img/img_noised/`.

## Limitations

- Seules les images en niveaux de gris sont pleinement supportées
- Les valeurs de pixels sont limitées à l'intervalle [0, 255] après l'ajout du bruit
- Pour les très grandes images, le processus peut nécessiter plus de mémoire et de temps

## Messages d'erreur courants

- `--sigma est obligatoire` : Vous devez spécifier une valeur pour le paramètre sigma
- `--input est obligatoire` : Vous devez spécifier le chemin d'une image source
- `Valeur manquante pour --sigma` : L'option --sigma a été spécifiée sans valeur
- `Option inconnue : X` : L'option X n'est pas reconnue par la commande noise



# Manuel d'utilisation - Commande DENOISE

Cette documentation détaille l'utilisation de la commande `denoise` de l'outil image-denoising-PCA. Cette commande permet de débruiter une image en utilisant la méthode de débruitage par Analyse en Composantes Principales (ACP/PCA).

## Utilisation en ligne de commande

### Syntaxe

```bash
mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="denoise [options]"
```

### Options

| Option | Format court | Description | Statut |
|--------|-------------|-------------|--------|
| `--input <chemin>` | `-i <chemin>` | Chemin vers l'image à débruiter | **Obligatoire** |
| `--output <chemin>` | `-o <chemin>` | Chemin de destination pour l'image débruitée | *Facultatif* |
| `--global` | `-g` | Utilise la méthode de débruitage globale | *Facultatif* |
| `--local` | `-l` | Utilise la méthode de débruitage locale | *Facultatif* (défaut) |
| `--threshold <type>` | `-t <type>` | Type de seuillage: 'hard' ou 'soft' | *Facultatif* (défaut: 'hard') |
| `--shrink <type>` | `-s <type>` | Type de seuillage adaptatif: 'v' (VisuShrink) (défaut) ou 'b' (BayesShrink) | *Facultatif* |
| `--help` | `-h` | Affiche l'aide de la commande denoise | *Facultatif* |

### Méthodes de débruitage

- **Globale** : Analyse l'image entière avec une approche globale par ACP.
- **Locale** : Analyse des patches locaux dans l'image, offrant généralement de meilleurs résultats pour préserver les détails.

### Types de seuillage

- **Hard** (dur) : Les composantes inférieures au seuil sont mises à zéro, les autres restent inchangées.
- **Soft** (doux) : Les composantes inférieures au seuil sont mises à zéro, les autres sont réduites par la valeur du seuil.

### Types de seuillage adaptatif

- **VisuShrink (v)** : Méthode basée sur le principe de réduction du bruit minimal.
- **BayesShrink (b)** : Méthode basée sur l'estimation bayésienne, généralement plus efficace pour les bruits variables.

### Exemples

1. Débruiter une image avec la méthode locale (par défaut) et seuillage hard (par défaut) :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="denoise --input img/img_noised/lena_20.png"
   ```

2. Débruiter avec la méthode globale et seuillage soft :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="denoise -i img/img_noised/lena_20.png -g -t soft"
   ```

3. Débruiter avec la méthode locale, seuillage hard et technique BayesShrink :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="denoise -i img/img_noised/lena_20.png -l -t hard -s b"
   ```

4. Spécifier un chemin de sortie personnalisé :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="denoise -i img/img_noised/lena_20.png -o img/lena_denoised.png"
   ```

## Utilisation en mode interactif

Pour une utilisation guidée, lancez le programme sans arguments :

```bash
mvn exec:java -Dexec.mainClass="cli.Main"
```

1. Choisissez l'option 2 : "Débruiter une image (denoise)"
2. Suivez les instructions pour spécifier :
   - Le chemin de l'image à débruiter
   - La méthode à utiliser (globale ou locale)
   - Le type de seuillage
   - Le type de seuillage adaptatif (facultatif)
   - Le chemin de sortie (facultatif)

## Traitement par lots

La commande `denoise` peut également traiter un dossier entier d'images. Si le chemin spécifié avec `--input` est un dossier, l'application débruitera toutes les images qu'il contient.

## Limitations

- Pour les très grandes images, la méthode globale peut nécessiter beaucoup de mémoire
- La méthode locale utilise des patches (blocs d'image) et peut créer des artefacts aux frontières
- L'efficacité du débruitage dépend de l'intensité et du type de bruit présent dans l'image

# Manuel d'utilisation - Commande EVAL

Cette documentation détaille l'utilisation de la commande `eval` de l'outil image-denoising-PCA. Cette commande permet d'évaluer la qualité entre deux images en utilisant des métriques objectives comme MSE et PSNR.

## Utilisation en ligne de commande

### Syntaxe

```bash
mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="eval [options]"
```

### Options

| Option | Format court | Description | Statut |
|--------|-------------|-------------|--------|
| `--image1 <chemin>` | `-i1 <chemin>` | Chemin vers la première image (généralement l'original) | **Obligatoire** |
| `--image2 <chemin>` | `-i2 <chemin>` | Chemin vers la deuxième image (généralement l'image traitée) | **Obligatoire** |
| `--metric <type>` | `-m <type>` | Type de métrique à utiliser: 'mse', 'psnr' ou 'both' | *Facultatif* (défaut: 'both') |
| `--help` | `-h` | Affiche l'aide de la commande eval | *Facultatif* |

### Métriques disponibles

- **MSE** (Mean Square Error / Erreur Quadratique Moyenne): Mesure la différence moyenne des carrés des erreurs entre pixels. Plus la valeur est basse, plus les images sont similaires.
- **PSNR** (Peak Signal-to-Noise Ratio / Rapport Signal/Bruit de Crête): Évalue la qualité de reconstruction d'une image compressée ou altérée. Plus la valeur est élevée, meilleure est la qualité.

### Exemples

1. Évaluer deux images en utilisant les deux métriques (MSE et PSNR) :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="eval --image1 img/original/lena.png --image2 img/img_noised/lena_10.png"
   ```

2. Calculer uniquement l'erreur quadratique moyenne (MSE) :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="eval -i1 img/original/lena.png -i2 img/img_noised/lena_10.png -m mse"
   ```

3. Calculer uniquement le rapport signal/bruit (PSNR) :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="eval -i1 img/original/lena.png -i2 img/img_noised/lena_10.png -m psnr"
   ```

4. Afficher l'aide de la commande eval :
   ```bash
   mvn exec:java -Dexec.mainClass="cli.Main" -Dexec.args="eval --help"
   ```

## Utilisation en mode interactif

Pour une utilisation guidée, lancez le programme sans arguments :

```bash
mvn exec:java -Dexec.mainClass="cli.Main"
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