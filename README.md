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