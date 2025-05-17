package core.image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


/**
 * Représente un fichier d'image et définis toutes les méthodes utiles a sa manipulation
 * Cette classe hérite de la classe {@link Image}
 * @author p-cousin
 * @version 1.2
 * @see Image
 */
public class ImageFile extends Image {
    private String name;    // nom de l'image
	private String ext;     // extension de l'image
	private String dir;     // chemin du dossier de l'image

    /**
     * Construit une instance de {@code ImageFile} à partir de son chemin
     * passe une {@link BufferedImage} au constructeur de la superclasse {@link Image}
     * @param filePath chemin de l'image
     * @throws IOException
     * @see BufferedImage
     * @see Image
     */
    public ImageFile(String filePath) throws IOException {
	    super(ImageIO.read(new File(filePath)));    // lecture du fichier et construction de l'instance
		splitFilePath(filePath);                    // traitement du chemin du fichier pour le séparer en chemin de dossier, nom et extension
	}

    /**
     * Construit une instance de {@code ImageFile} à partir d'une instance de {@link Image} et d'un nom (Downcast) 
     * @param img {@code Image} a convertir en {@code ImageFile}
     * @param name nom de l'image a créer
     * @see Image
     */
    public ImageFile(Image img, String name) {
		this.setImage(img.getImage());
		this.setName(name);
		this.setExt("png");
		this.setDir(System.getProperty("user.dir"));   // emplacement actuel
	}

    /**
     * Construit une instance de {@code ImageFile} à partir d'une instance de {@link Image} (Downcast) 
     * @param img {@code Image} a convertir en {@code ImageFile}
     * @see Image
     */
    public ImageFile(Image img) {
		this(img, null);
	}

    /**
     * Setter pour la variable d'instance {@code name}
     * @param name nom de l'image
     */
    public void setName(String name) {
		this.name = name;
	}

    /**
     * Setter pour la variable d'instance {@code ext}
     * @param ext extension de l'image
     */
	public void setExt(String ext) {
		this.ext = ext;
	}

    /**
     * Setter pour la variable d'instance {@code dir}
     * @param dir chemin du dossier de l'image
     */
	public void setDir(String dir) {
		this.dir = dir;
	}

    /**
     * Getter pour la variable d'instance {@code name}
     * @return nom de l'image
     */
    public String getName() {
		return this.name;
	}

    /**
     * Getter pour la variable d'instance {@code ext}
     * @return extension de l'image
     */
	public String getExt() {
		return this.ext;
	}

    /**
     * Getter pour la variable d'instance {@code dir}
     * @return chemin du dossier de l'image
     */
	public String getDir() {
		return this.dir;
	}

    /**
     * Rreconstruit le chemin de l'image à partir des variables d'instance {@code dir}, {@code name} et {@code ext}
     * @return chemin de l'image
     */
    public String getPath() {
        if (this.getDir() == null || this.getDir().isEmpty()) {
            return this.getName() + this.getExt();
        } else {
            return this.getDir() + "/" + this.getName() + this.getExt();
        }
    }

    /**
     * Traite le chemin de l'image donné pour en extraire les variables d'instance {@code dir}, {@code name} et {@code ext}
     * @param filePath chemin de l'image
     */
    private void splitFilePath(String filePath) {
        String[] splitPath = filePath.split("[/\\.]");          // Séparation du chemin selon les séparateurs '/'  et '.'
        this.setExt("." + splitPath[splitPath.length - 1]);     // Gestion de l'extension
        this.setName(splitPath[splitPath.length - 2]);          // Gestion du nom
        StringBuilder dir = new StringBuilder();                // Gestion du chemin du dossier
        for(int i = 0; i < splitPath.length - 2; i++) {         
            if (i > 0) dir.append("/");
            dir.append(splitPath[i]);                           // Concaténation des différents dossiers de l'arborescence, séparés par '/'
        }
        this.setDir(dir.toString());                            // Definition du chemin du dossier
    }

    /**
     * Sauvegarde l'instance d'image au chemin de dossier fourni
     * Le nom est celui désigné par la variable d'instance {@code name}
     * @param path chemin du dossier ou l'on souhaite sauvegarder l'image
     */
    public void saveImage(String path) {
        try {
            File outputFile = new File(path);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {         // création du dossier s'il n'existe pas encore
                parentDir.mkdirs();
            }
            ImageIO.write(this.getImage(), "PNG", outputFile);
            // System.out.println("Image sauvegardée à : " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
