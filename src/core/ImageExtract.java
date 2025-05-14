package core;

import java.awt.image.BufferedImage;

public class ImageExtract extends Image {
    private int posX;
    private int posY;

    public int getPosX() {
        return this.posX;
    }
    public int getPosY() {
        return this.posY;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }
    public void setPosY(int posY) {
        this.posY = posY;
    }


    public ImageExtract(BufferedImage img, int posX, int posY) {
        super(img);
        this.setPosX(posX);
        this.setPosY(posY);
    }
}
