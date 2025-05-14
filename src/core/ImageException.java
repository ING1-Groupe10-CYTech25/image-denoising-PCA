package core;

public class ImageException extends Exception {
    @Override
    public void printStackTrace() {
        System.out.println("Pixel coordinates are not within the image");
        super.printStackTrace();
    }

}
