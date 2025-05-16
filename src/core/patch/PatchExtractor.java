package core.patch;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.awt.image.BufferedImage;
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
	public static double overlap = 1.5; // densité des patchs (1.2 signifie qu'il y aura 120% du nombre minimal couvrant de patchs en abscisse et 120% du nombre minimal couvrant de patchs en ordonnée)

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
			int countX = (int) (Math.ceil((double) img.getWidth() * overlap / side));		// Calcul du nombre de patchs nécessaire sur l'axe des abscisses
			int countY = (int) (Math.ceil((double) img.getHeight() * overlap / side));		// Calcul du nombre de patchs nécessaire sur l'axe des ordonnées
			
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
			System.err.println("Patches cannot be larger than the image");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reconstruit une image a partir d'une liste de patchs et de ses dimensions
	 * @param patchList	liste de patchs couvrant l'image
	 * @param width largeur de l'image
	 * @param height hauteur de l'image
	 * @return une image reconstruite
	 * @see Image
	 */
	public static Image reconstructPatchs(List<Patch> patchList, int width, int height) {
		try {
			if (patchList.isEmpty()) {																	// vérification de la validité de la liste de patchs
				throw(new PatchException());
			}
			else {
				Map<Integer, List<Patch>> rowMap = new TreeMap<>();
				int side = patchList.getFirst().getSide();
				
				for (Patch patch : patchList) {
					int yOrigin = patch.getYOrigin();
					rowMap.computeIfAbsent(yOrigin, k -> new ArrayList<>()).add(patch);
				}
				
				List<List<Patch>> patchRows = new ArrayList<>();
				
				for (List<Patch> row : rowMap.values()) {
					row.sort(Comparator.comparingInt(Patch::getXOrigin));
					patchRows.add(row);
				}
				
				List<ImageTile> tileRows = new ArrayList<>();
				
				for (List<Patch> row : patchRows) {

					tileRows.addLast(new ImageTile(new BufferedImage(width, side, BufferedImage.TYPE_BYTE_GRAY), row.get(0).getXOrigin(), row.get(0).getYOrigin()));
					tileRows.getLast().getRaster().setPixels(row.get(0).getXOrigin(), 0, side, side, row.get(0).getPixels());
					for (int i = 0; i < row.size() - 1; i ++) {
						Patch left = row.get(i);
						Patch right = row.get(i + 1);
						int overlapStart = right.getXOrigin();
						int overlapWidth = left.getXOrigin() + side - overlapStart;
						tileRows.getLast().getRaster().setPixels(overlapStart, 0, side, side, right.getPixels());
						for (int x = 0; x <= overlapWidth; x ++) {
							for (int y = 0; y < side; y ++) {
								int leftPixel = left.getPixel(side - 1 - overlapWidth + x,y);
								int rightPixel = right.getPixel(x, y);
								int grey = ((overlapWidth - x) * leftPixel + x * rightPixel) / overlapWidth;
								tileRows.getLast().setPixel(overlapStart + x, y, grey);
							}
						}
					}
				}
				System.out.println("merging rows");
				for (int row = 0; row < tileRows.size() - 1; row ++) {
					ImageTile top = tileRows.get(row);
					ImageTile bottom = tileRows.get(row + 1);
					int overlapStart = bottom.getPosY();
					int overlapHeight = top.getPosY() + side - overlapStart;
					for (int y = 0; y <= overlapHeight; y ++) {
						for (int x = 0; x < width; x ++) {
							int topPixel = top.getPixel(x, side - 1 - overlapHeight + y);
							int bottomPixel = bottom.getPixel(x, y);
							int grey = ((overlapHeight - y) * topPixel + y * bottomPixel) / overlapHeight;
							top.setPixel(x, side - 1 - overlapHeight + y, grey);
							bottom.setPixel(x, y, grey);
						}
					}
				}
				return reconstructImageTiles(tileRows, width, height);
			}
		}
		catch (PatchException e) {
			System.err.println("Patch list is empty");
			e.printStackTrace();
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
				ImageTile img = new ImageTile(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY), posX, posY);	// création d'une image vide
				for(Patch patch : patchList) {															// parcours des patchs
					int x = patch.getXOrigin();
                	int y = patch.getYOrigin();
                	int side = patch.getSide();
					img.getRaster().setPixels(x, y, side, side, patch.getPixels());						// ajout du patch a l'image
				}
				return img;
			}
		}
		catch (PatchException e) {
			System.err.println("Patch list is empty");
			e.printStackTrace();
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
				int x = colStarts[col];
				int y = rowStarts[row];
				int w = colStarts[col + 1];
				int h = rowStarts[row + 1];
				imageList.add(new ImageTile(img.getImage().getSubimage(x, y, w - x, h - y), x, y));		// ajout de l'imagette correspondant a la case
			}
		}
		return imageList;
	}

	/**
	 * Reconstruit une image a partir de ses imagettes et de ses dimensions.
	 * @param tileList liste des imagettes
	 * @param width largeur de l'image a reconstruire
	 * @param height hauteur de l'image a reconstruire
	 * @return l'image obtenue en recollant les imagettes
	 */
	public static Image reconstructImageTiles(List<ImageTile> tileList, int width, int height) {
		Image result = new Image(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));	    				// création d'une image vide aux bonnes dimensions
        for (ImageTile tile : tileList) {																				// parcours des imagettes
            int[] pixels = new int[tile.getWidth() * tile.getHeight()];													// tableau des pixels de l'imagette
            tile.getRaster().getPixels(0, 0, tile.getWidth(), tile.getHeight(), pixels);								// obtention des valeurs des pixels
            result.getRaster().setPixels(tile.getPosX(), tile.getPosY(), tile.getWidth(), tile.getHeight(), pixels);	// ecriture des valeurs au bon endroit sur l'image
        }
		return result;
	}
}