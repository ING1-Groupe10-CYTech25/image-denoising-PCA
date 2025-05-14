package core.image;

public class ImageException extends Exception {
    @Override
    public void printStackTrace() {
        System.err.println("Pixel coordinates are not within the image");
        super.printStackTrace();
    }

}
