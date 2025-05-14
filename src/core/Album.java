package core;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Album {
	private ArrayList<Image> album;
	public ArrayList<Image> getAlbum() {
		return this.album;
	}
	public void setAlbum(ArrayList<Image> album) {
		this.album = album;
	}
	public Album(String path) {
		this.setAlbum(new ArrayList<Image>());					// album vide
		try {
		    File file = new File(path);							// chemin donné
		    if (file.isFile() && ImageIO.read(file) != null) {	// si le chemin désigne une image 
		    	this.getAlbum().add(new Image(path));			// on ajoute a l'album
		    }
		    else if (file.isDirectory()) {												//sinon
		    	for(File f : file.listFiles()) {				//on itère le constructeur sur les sous-chemins
		    		this.getAlbum().addAll((new Album(f.getPath())).getAlbum());	// et on ajoute a l'album
		    	}
		    };
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		String fileList = "";
		for (Image i : this.getAlbum()) {
			fileList += i.getPath() + "\n";
		}
		return fileList;
	}
}
