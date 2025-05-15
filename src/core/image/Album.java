package core.image;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 * Représente un ensemble d'instances de {@link ImageFile} sous la forme d'une {@link List}
 * @author p-cousin
 * @version 1.0
 * @see ImageFile
 */
public class Album {
	private List<ImageFile> album;		// Album sous forme de List<ImageFile>
	

	/**
	 * Getter pour l'album
	 * @return Liste d'images
	 */
	public List<ImageFile> getAlbum() {
		return this.album;
	}

	/**
	 * Setter pour l'album
	 * @param album Liste d'images
	 */
	public void setAlbum(List<ImageFile> album) {
		this.album = album;
	}

	/**
	 * Construit une instance de {@code Album} a partir d'un chemin de dossier ou de fichier
	 * <p>
	 * Ce constructeur est récursif et explorera tous les sous dossier du chemin fourni, ajoutant toutes les fichiers  d'images trouvées a l'album.
	 * Si le chemin est celui du fichier, seul ce fichier sera ajouté a l'album s'il est une image.
	 * @param path
	 * @throws IOException
	 */
	public Album(String path) throws IOException {
		this.setAlbum(new ArrayList<>());										// album vide
		File file = new File(path);												// chemin donné
		if (file.isFile() && ImageIO.read(file) != null) {						// si le chemin désigne une image 
			this.getAlbum().add(new ImageFile(path));							// on ajoute a l'album
		}
		else if (file.isDirectory()) {											//sinon
			for(File f : file.listFiles()) {									//on itère le constructeur sur les sous-chemins
				this.getAlbum().addAll((new Album(f.getPath())).getAlbum());	// et on ajoute le résultat a l'album
			}
		};
	}

	/**
	 * Renvoie une chaine de caractères contenant la liste des fichiers de l'album (chemin complet)
	 * @return Liste des fichiers de l'album
	 */
	@Override
	public String toString() {
		String fileList = "";
		for (ImageFile i : this.getAlbum()) {
			fileList += i.getPath() + "\n";
		}
		return fileList;
	}
}
