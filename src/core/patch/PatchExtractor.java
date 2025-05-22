package core.patch;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import core.image.Image;
import core.image.ImageTile;

/**
 * Cette classe permet de gérer les patchs (Stockés sous forme de liste) au sein d'une image.
 * Elle utilise les classes {@link Patch} et {@link Image}
 * @author p-cousin
 * @version 1.1
 * @see Patch
 * @see Image
 */
public class PatchExtractor {
	public static double patchCountMultiplier = 2.0; // multiplicateur du nombre de patchs en ordonnée et en abscisse. par exemple si 4x4 patch couvrent l'image, avec patchCountMultiplier=1.5 on utilisera une grille de (4x1.5)x(4x1.5)

	/**
	 * Cette methode créée une liste de patchs carrés, couvrant toute l'image. la taille des patchs est déterminé par le paramtètre {@code side}
	 * @param img image à découper en patchs
	 * @param side taille du coté des patchs
	 * @return liste de patchs couvrant toute l'image
	 */
	public static List<Patch> extractPatchs(Image img, int side) {
		try {
			if (img.getWidth() < side || img.getHeight() < side) {				// vérification de la taille du patch
				throw(new PatchException());
			}
			int countX = (int) (Math.ceil((double) img.getWidth() * patchCountMultiplier / side));		// Calcul du nombre de patchs nécessaire sur l'axe des abscisses
			int countY = (int) (Math.ceil((double) img.getHeight() * patchCountMultiplier / side));		// Calcul du nombre de patchs nécessaire sur l'axe des ordonnées
			
			List<Patch> patchList = new ArrayList<>();							// liste des patchs

			for (int j = 0; j < countY; j++) {									// Parcours de la grille de patchs
				int y = (j * (img.getHeight() - side)) / (countY - 1);			// Calcul de la coordonnée sur l'axe des ordonnées
				for (int i = 0; i < countX; i++) {												
					int x = (i * (img.getWidth() - side)) / (countX - 1);		// Calcul de la coordonnée sur l'axe des abscisses
					x = Math.min(x, img.getWidth() - side);						// Verification des coordonnées pour eviter que le patch dépasse de l'image
					y = Math.min(y, img.getHeight() - side);
					int[] pixels = new int[side * side];						// création du tableau de valeurs a extraire
					img.getRaster().getPixels(x, y, side, side, pixels);		// obtention des données
					patchList.add(new Patch(pixels, x, y, side));				// ajout a la liste de patchs
				}
			}		
			return patchList;
		}
		catch(PatchException e) {
			System.err.println("Le patch est plus grand que l'image");
			return null;
		}
	}

	/**
	 * Reconstruit une image a partir d'une liste de patchs et de ses dimensions
	 * @param patchList	liste de patchs carrés couvrant l'image organisés en grille 
	 * @param width largeur de l'image
	 * @param height hauteur de l'image
	 * @return une image reconstruite
	 * @see Image
	 */
	public static Image reconstructPatchs(List<Patch> patchList, int width, int height) {
		try {
			if (patchList.isEmpty()) {																				// vérification de la validité de la liste de patchs
				throw(new PatchException());
			}
			else {
				Map<Integer, List<Patch>> rowMap = new TreeMap<>();													// Map des patchs classés par lignes
				int side = patchList.get(0).getSide();															// coté d'un patch : tous les patchs sont de même dimension
				
				for (Patch patch : patchList) {																		// parcours des patchs
					rowMap.computeIfAbsent(patch.getYOrigin(), k -> new ArrayList<>()).add(patch);					// classement du patch en fonction de son ordonnée
				}
				
				List<List<Patch>> patchRows = new ArrayList<>();													// liste de lignes de patchs représentés sous forme de liste
				
				for (List<Patch> row : rowMap.values()) {															// tri des patchs dans patchRows par abscisse croissante
					row.sort(Comparator.comparingInt(Patch::getXOrigin));
					patchRows.add(row);
				}
				
				List<ImageTile> tileRows = new ArrayList<>();														// liste des lignes completes sous forme d'imagettes
				
				for (List<Patch> row : patchRows) {																	// parcours des lignes : on moyenne chaque ligne sur la jointure verticale des patchs
					// création de l'imagette de la ligne et ajout du premier patch a celle-ci
					tileRows.add(new ImageTile(new BufferedImage(width, side, BufferedImage.TYPE_BYTE_GRAY), row.get(0).getXOrigin(), row.get(0).getYOrigin()));
					tileRows.get(tileRows.size() - 1).getRaster().setPixels(row.get(0).getXOrigin(), 0, side, side, row.get(0).getPixels());
					for (int i = 0; i < row.size() - 1; i ++) {														// parcours des patchs 2 a 2
						Patch left = row.get(i);																	// patch "de gauche"
						Patch right = row.get(i + 1);																// patch "de droite"
						int overlapStart = right.getXOrigin();														// coordonée du début de la zone de superposition
						int overlapWidth = left.getXOrigin() + side - overlapStart;									// largeur de la zone de superpositon
						// ajout du patch de droite a l'imagette de la ligne (le patch de gauche a déja été ajouté au tour d'avant)
						tileRows.get(tileRows.size() - 1).getRaster().setPixels(overlapStart, 0, side, side, right.getPixels());
						// moyennage de l'overlap seulement s'il existe
						if (overlapWidth > 0) {																		// moyennage de zone de superposition seulement si elle existe
							for (int x = 0; x <= overlapWidth; x ++) {												// parcours sur la largeur de la zone de superposition
								for (int y = 0; y < side; y ++) {													// parcours sur la hauteur du patch
									int leftPixel = left.getPixel(side - 1 - overlapWidth + x,y);					// valeurs des pixels qui se superposent
									int rightPixel = right.getPixel(x, y);
									int grey = ((overlapWidth - x) * leftPixel + x * rightPixel) / overlapWidth;	// valeur finale du pixel calculée par moyenne pondérée linéaire
									tileRows.get(tileRows.size() - 1).setPixel(overlapStart + x, y, grey);			// ecriture de la valeur sur l'imagette
								}
							}
						}
					}
				}
				for (int row = 0; row < tileRows.size() - 1; row ++) {												// parcours des lignes sous forme d'imagette 2 par 2
					ImageTile top = tileRows.get(row);																// ligne "du dessus"
					ImageTile bottom = tileRows.get(row + 1);														// ligne "du dessous"
					int overlapStart = bottom.getPosY();															// ordonnée de début de la zone de superpostion
					int overlapHeight = top.getPosY() + side - overlapStart;										// hauteur de la zone de superposition
					if (overlapHeight > 0) {																		// moyennage de la zone de superpositon seulement si elle existe
						for (int y = 0; y <= overlapHeight; y ++) {													// parcours sur la hauteur de la zone de superposition
							for (int x = 0; x < width; x ++) {														// parcours sur la largeur de l'imagette
								int topPixel = top.getPixel(x, side - 1 - overlapHeight + y);						// valeurs des pixels qui se superposent
								int bottomPixel = bottom.getPixel(x, y);
								int grey = ((overlapHeight - y) * topPixel + y * bottomPixel) / overlapHeight;		// valeur finale du pixel calculée par moyenne pondérée linéaire
								top.setPixel(x, side - 1 - overlapHeight + y, grey);								// ecriture de la valeur sur les deux imagettes aux coordonnées correspondantes
								bottom.setPixel(x, y, grey);
							}
						}
					}
				}
				return reconstructImageTiles(tileRows, width, height);												// fusion des imagettes pour obtenir l'image finale (la superposition des imagettes n'est plus un probleme car elles contiennent les memes valeurs sur ces zones)
			}
		}
		catch (PatchException e) {
			System.err.println("Liste des patchs vides");
			return null;
		}
	}
	/**
	 * Reconstruit une imagette a partir d'une liste de patchs, de ses dimensions et de sa position dans l'image globale
	 * @param patchList	liste de patchs couvrant l'image
	 * @param width largeur de l'imagette
	 * @param height hauteur de l'imagette
	 * @param posX abscisse de l'imagette
	 * @param posY ordonnée de l'imagette
	 * @return une imagette reconstruite
	 * @see ImageTile
	 */
	public static ImageTile reconstructPatchs(List<Patch> patchList, int width, int height, int posX, int posY) {
		try {
			if (patchList.isEmpty()) {																	// vérification de la validité de la liste de patchs
				throw(new PatchException());
			}
			else {
				return new ImageTile(reconstructPatchs(patchList, width, height).getImage(), posX, posY);	// création d'une image vide
			}
		}
		catch (PatchException e) {
			System.err.println("Liste des patchs vide");
			return null;
		}
	}
	/**
	 * Découpe l'image {@code img} donnée en une grille d'imagettes aussi carrées que possible, en visant un nombre d'imagettes aussi proche de {@code n} que possible
	 * @param img image à découper
	 * @param n nombre d'imagettes cible
	 * @return une liste d'imagettes couvrant l'image
	 * @see ImageTile
	 * @see List
	 */
	public static List<ImageTile> decoupeImage(Image img, int n) {
		// etape 1 : calcul des colonnes et lignes optimales

		int bestRows = 1;
		int bestCols = n;
		double bestAspectDiff = Double.MAX_VALUE;														// Cible : minimiser bestAspectDiff
	
		for (int rows = 1; rows <= n; rows++) {															// parcours des combinaisons de lignes et colonnes possibles
			int cols = (int) Math.ceil((double)n / rows);
			if (rows * cols >= n) {
				double cellWidth = (double) img.getWidth() / cols;										// calcul des dimensions d'une case
				double cellHeight = (double) img.getHeight() / rows;
				double aspectRatio = cellWidth / cellHeight;											// Calcul du format de la case
				double aspectDiff = Math.abs(Math.log(aspectRatio)); 									// proche de 0 = presque carré
	
				if ((rows * cols < bestRows * bestCols) || (rows * cols == bestRows * bestCols && aspectDiff < bestAspectDiff)) {
					bestRows = rows;																	// si la combinaison de colonnes et lignes est préférable, on la modifie
					bestCols = cols;
					bestAspectDiff = aspectDiff;
				}
			}
		}

		// Calcul de la taille de chevauchement (20% de la taille minimale)
		int overlapX = (int)(img.getWidth() * 0.2 / bestCols);
		int overlapY = (int)(img.getHeight() * 0.2 / bestRows);

		int[] colStarts = new int[bestCols + 1];														// Stocke les positions des colonnes
		int[] rowStarts = new int[bestRows + 1];														// Stocke les position des lignes
																										// Remplissage des deux tableaux
		for (int col = 0; col <= bestCols; col ++) {
			colStarts[col] = (int) Math.round((double) col * img.getWidth() / bestCols);
		}
		for (int row = 0; row <= bestRows; row ++) {
			rowStarts[row] = (int) Math.round((double) row * img.getHeight() / bestRows);
		}

		List<ImageTile> imageList = new ArrayList<>();													// Liste des imagettes
		for (int row = 0; row < bestRows; row ++) {														// parcours des cases
			for (int col = 0; col < bestCols; col ++) {
				int x = Math.max(0, colStarts[col] - (col > 0 ? overlapX : 0));
				int y = Math.max(0, rowStarts[row] - (row > 0 ? overlapY : 0));
				int w = Math.min(img.getWidth(), colStarts[col + 1] + (col < bestCols - 1 ? overlapX : 0));
				int h = Math.min(img.getHeight(), rowStarts[row + 1] + (row < bestRows - 1 ? overlapY : 0));
				imageList.add(new ImageTile(img.getImage().getSubimage(x, y, w - x, h - y), x, y));		// ajout de l'imagette correspondant a la case
			}
		}
		return imageList;
	}

	/**
	 * Reconstruit une image a partir de ses imagettes et de ses dimensions. Avec chevauchement des imagettes de 20% de leur taille.
	 * @param tileList liste des imagettes
	 * @param width largeur de l'image a reconstruire
	 * @param height hauteur de l'image a reconstruire
	 * @version 2.0
	 * @return l'image obtenue en recollant les imagettes
	 */
	public static Image reconstructImageTiles(List<ImageTile> tileList, int width, int height) {
		Image result = new Image(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));
		int[][] pixelCounts = new int[height][width]; // Compteur de contributions pour chaque pixel
		int[][] pixelSums = new int[height][width];   // Somme des valeurs pour chaque pixel

		// Accumulation des valeurs et comptage des contributions (çàd nombre de patchs contribuant a la valeur de chaque pixel)
		for (ImageTile tile : tileList) {
			int[] pixels = new int[tile.getWidth() * tile.getHeight()];
			tile.getRaster().getPixels(0, 0, tile.getWidth(), tile.getHeight(), pixels);
			
			for (int y = 0; y < tile.getHeight(); y++) {
				for (int x = 0; x < tile.getWidth(); x++) {
					int globalX = tile.getPosX() + x;
					int globalY = tile.getPosY() + y;
					if (globalX < width && globalY < height) {
						pixelSums[globalY][globalX] += pixels[y * tile.getWidth() + x];
						pixelCounts[globalY][globalX]++;
					}
				}
			}
		}

		// Calcul de la moyenne pour chaque pixel et écriture dans l'image finale
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixelCounts[y][x] > 0) {
					int averageValue = pixelSums[y][x] / pixelCounts[y][x];
					result.setPixel(x, y, averageValue);
				}
			}
		}

		return result;
	}
}
