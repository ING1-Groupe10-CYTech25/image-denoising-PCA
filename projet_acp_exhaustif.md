# 🔍 Description exhaustive du projet Java : Débruitage d’images par Analyse en Composantes Principales (ACP)

## 1. Intitulé
**Débruitage d’images par Analyse en Composantes Principales (ACP)** – Projet académique de traitement d’image en Java dans le cadre de l’unité d’enseignement de 3e année d’école d’ingénieur (ING1 GM/GI, CY Tech 2024–2025).

## 2. Objectif global
Élaborer un programme Java permettant de **restituer une image d’origine à partir d’une version bruitée**, en exploitant les propriétés statistiques de redondance et de parcimonie des pixels. L’approche repose sur l’extraction de **patchs** de l’image bruitée, leur vectorisation, puis l'application d’une **Analyse en Composantes Principales (PCA)** pour projeter les patchs dans une base où les coefficients dominants sont conservés et les composantes bruitées sont réduites via des techniques de **seuillage**.

## 3. Format et contraintes techniques
- Langage : **Java**
- Format d’image : **niveaux de gris**, codés en 8 bits (valeurs de 0 à 255)
- Environnement : projet Maven ou standard Java avec arborescence `src/`
- Bibliothèques tierces autorisées **si et seulement si** elles sont mentionnées dans le rapport
- Trois livrables obligatoires :
  - Livrable 1 : Analyse UML et planification (réalisé)
  - Livrable 2 : **Code Java en mode console (phases 1 à 3)**
  - Livrable 3 : Version complète avec interface graphique (GUI) + évaluation complète
- Tous les traitements doivent être reproductibles en ligne de commande
- Le projet est évalué sur sa rigueur algorithmique, la qualité de code, l’organisation modulaire et la clarté des explications

## 4. Pipeline algorithmique complet

### 4.1. Étape 1 – Génération de bruit : `noising`
**But** : Générer une version bruitée de l’image d’origine X₀, notée Xb.  
**Hypothèse** : le bruit est **gaussien**, **centré**, **additif**, de **variance σ²**.

```java
BufferedImage noising(BufferedImage X0, double sigma)
```

Formule :
```
Xb(i, j) = X0(i, j) + n(i, j)
avec n(i, j) ~ N(0, σ²)
```

### 4.2. Étape 2 – Extraction de patchs
**Patch** : sous-image carrée de taille s × s extraite à partir de Xb.  
Chaque patch est vectorisé en un vecteur de ℝ^s².

Méthodes disponibles :
- **Globale** : ACP appliquée à tous les patchs extraits de l’image entière
- **Locale** : image subdivisée en imagettes W × W, ACP locale dans chaque imagette

Fonctions attendues :
```java
List<Patch> ExtractPatches(BufferedImage Xb, int s)
BufferedImage ReconstructPatchs(List<Patch> Y, int L, int C)
List<BufferedImage> DecoupeImage(BufferedImage X, int W, int n)
double[][] VectorPatchs(List<Patch> patches)
```

### 4.3. Étape 3 – ACP : `MoyCov`, `ACP`
Soit `V` la matrice des vecteurs colonnes des patchs (dimension s² × M).

1. Centrage des vecteurs → `Vc = V - mV`
2. Calcul de la matrice de covariance :
   ```
   Γ = (1/M) ∑ (Vk - mV)(Vk - mV)^T
   ```
3. Diagonalisation → base orthonormale U = {u₁, ..., u_{s²}}

Fonctions attendues :
```java
Triple<double[], double[][], double[][]> MoyCov(double[][] V)
Matrix ACP(double[][] V)
```

### 4.4. Étape 4 – Projection dans la base ACP : `Proj`
But : Projeter chaque vecteur Vc_k dans la base ACP U
```java
double[][] Proj(double[][] U, double[][] Vc)
```

Formule :
```
α_i^(k) = u_i^T · (Vk - mV)
```

### 4.5. Étape 5 – Seuillage
Objectif : Réduire l’influence du bruit dans les coefficients α

Méthodes :
- Hard :
  ```
  if |α_i| ≤ λ → 0
  else        → α_i
  ```
- Soft :
  ```
  if |α_i| ≤ λ → 0
  else if α_i > 0 → α_i - λ
  else            → α_i + λ
  ```

Calcul du seuil λ :
- VisuShrink :
  ```
  λ = σ √(2 log(L))
  ```
- BayesShrink :
  ```
  λ = σ² / σ̂_X, avec σ̂_X = sqrt(|Var(Xb) - σ²|)
  ```

Fonctions à implémenter :
```java
double SeuilV(double sigma, int nbPixels)
double SeuilB(double sigma, double varianceXb)
double[] SeuillageDur(double lambda, double[] alpha)
double[] SeuillageDoux(double lambda, double[] alpha)
```

### 4.6. Étape 6 – Reconstruction finale : `ImageDen`
Formule :
```
Zk = mV + ∑_i Seuillage(α_i^(k)) * u_i
```

Fonction :
```java
BufferedImage ImageDen(...)
```

## 5. Évaluation de la qualité – Fonctions : `MSE`, `PSNR`

### MSE
```
MSE = (1 / (ℓ·c)) ∑_i ∑_j (X(i, j) - Y(i, j))²
```

### PSNR
```
PSNR = 10 log10 (255² / MSE)
```

Fonctions Java :
```java
double MSE(BufferedImage original, BufferedImage reconstructed)
double PSNR(BufferedImage original, BufferedImage reconstructed)
```

## 6. Objectifs spécifiques du livrable 2 (version console)
- Implémenter les fonctions :
  - `noising`
  - `ExtractPatches`
  - `VectorPatchs`
  - (Optionnel : `MoyCov`, `ACP`)
- Fournir une exécution console validant les 3 premières étapes
- Générer fichiers d’image bruitée et patchs

Livrables :
- `.jar` contenant les sources
- `.jar` exécutable
- README utilisateur
- Images de test

## 7. Structure recommandée du code

| Étape | Classe Java          | Fonctions principales                          |
|-------|----------------------|-----------------------------------------------|
| 1     | `Bruitage.java`      | `noising()`                                   |
| 2     | `PatchUtils.java`    | `ExtractPatches()`, `VectorPatchs()`          |
| 3     | `ACPUtils.java`      | `MoyCov()`, `ACP()`                           |
| 4     | `Projection.java`    | `Proj()`                                      |
| 5     | `Seuils.java`        | `SeuilV()`, `SeuilB()`, `SeuillageDur()`, `SeuillageDoux()` |
| 6     | `Reconstruction.java`| `ImageDen()`                                  |
| 7     | `Qualite.java`       | `MSE()`, `PSNR()`                             |
| Main  | `Main.java`          | Point d’entrée console                        |
