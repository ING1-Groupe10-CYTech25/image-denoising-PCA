package core.image;
import java.io.File;
import java.io.IOException;

/**
 * Classe de test pour la fonctionnalité {@link Album}.
 * Cette classe permet de vérifier :
 * - La lecture récursive des dossiers d'images
 * - La gestion des différentes tailles d'images
 * - L'affichage correct des chemins d'accès
 * 
 * @author p-cousin
 * @version 1.0
 * @see Album
 */
public class AlbumTest {

	/**
	 * Point d'entrée du programme de test.
	 * Effectue deux tests principaux :
	 * 1. Liste tous les fichiers dans le dossier 'varied_sizes' pour vérifier l'accès aux fichiers
	 * 2. Crée un album à partir du dossier 'img' et affiche son contenu pour vérifier la lecture récursive
	 * 
	 * @param args Arguments de la ligne de commande (non utilisés)
	 */
	public static void main(String[] args) {
		// Test 1 : Vérification de l'accès aux fichiers de différentes tailles
		File file = new File(System.getProperty("user.dir") + "/img/original/varied_sizes");
		for(File f : file.listFiles()) {
			System.out.println(f.getPath());
		}

		// Test 2 : Vérification de la création d'album et de la lecture récursive
		try {
			Album album = new Album(System.getProperty("user.dir") + "/img");
			System.out.println(album);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
