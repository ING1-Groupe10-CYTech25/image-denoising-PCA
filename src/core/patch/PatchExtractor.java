package core.patch;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import core.image.Image;
import core.image.ImageExtract;

/**
 * Cette classe permet de gérer les patchs (Stockés sous forme de liste) au sein d'une image.
 * Elle utilise les classes {@link Patch} et {@link Image}
 * @author p-cousin
 * @version 1.1
 * @see Patch
 * @see Image
 */
public class PatchExtractor {
	public static int minOverlap = 5; 		// superposition minimale entre deux patchs en pixels

	/**
	 * Cette methode créée une liste de patchs carrés qui se superposent d'au moins {@code minOverlap} pixel, couvrant toute l'image. la taille des patchs est déterminé par le paramtètre {@code side}
	 * @param img image à découper en patchs
	 * @param side taille du coté des patchs
	 * @return liste de patchs couvrant toute l'image
	 */
	public static List<Patch> extractPatchs(Image img, int side) {
		try {
			if (img.getWidth() < 0 || img.getHeight() < 0) {									//	vérification de la taille du patch
				throw(new PatchException());
			}
			// Calcul du nombre de patchs et stride en X
			int minStrideX = side - minOverlap;													// pas minimum entre deux patchs sur l'axe des abscisses
			int countX = (int) Math.ceil((double)(img.getWidth() - side) / minStrideX) + 1;		// nombre de patchs sur l'axe des abscisses
			int strideX = (img.getWidth() - side) / (countX - 1);								// pas effectif entre les patchs sur l'axe des abscisses
		
			// Calcul du nombre de patchs et stride en Y
			int minStrideY = side - minOverlap;													// pas minimum entre deux patch sur l'axe des ordonnées
			int countY = (int) Math.ceil((double)(img.getHeight() - side) / minStrideY) + 1;	// nombre de patchs sur l'axe des ordonnées
			int strideY = (img.getHeight() - side) / (countY - 1);								// pas effectif entre les patchs sur l'axe des ordonnées
		
			List<Patch> patchList = new ArrayList<>();											// liste des patchs
		
			for (int j = 0; j < countY; j++) {													// Parcours de la grille de patchs
				int y = j * strideY;															// coordonnée sur l'axe des ordonnées
				for (int i = 0; i < countX; i++) {												
					int x = i * strideX;														// coordonnée sur l'axe des abscisses
					x = Math.min(x, img.getWidth() - side);										// verification des coordonnées pour eviter dépassement
					y = Math.min(y, img.getHeight() - side);
					int[] pixels = new int[side * side];										// création du tableau de valeurs a extraire
					img.getRaster().getPixels(x, y, side, side, pixels);						// obtention des données
					patchList.add(new Patch(pixels, x, y, side));								// ajout a la liste de patchs
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
				Image img = new Image(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY));	// création d'une image vide
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
	
	public static List<ImageExtract> decoupeImage(Image img, int w, int n) {
		 List<ImageExtract> ImageList = new ArrayList<>();
		 return ImageList;
	}
}