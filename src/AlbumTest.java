import java.io.File;

public class AlbumTest {
	public static void main(String[] args) {
		File file = new File(System.getProperty("user.dir") + "/img/original/varied_sizes");
		for(File f : file.listFiles()) {
			System.out.println(f.getPath());
		}
		Album album = new Album(System.getProperty("user.dir") + "/img");
		System.out.println(album);
	}
}
