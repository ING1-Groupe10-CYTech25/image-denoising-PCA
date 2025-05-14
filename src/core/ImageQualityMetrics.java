import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class ImageQualityMetrics {
    public static double calculateMSE(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        long sumError = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;

                int errR = r1 - r2;
                int errG = g1 - g2;
                int errB = b1 - b2;

                sumError += errR * errR + errG * errG + errB * errB;
            }
        }

        double mse = (double) sumError / (width * height * 3); // 3 for RGB channels
        return mse;
    }

    public static double calculatePSNR(double mse, int maxPixelValue) {
        if (mse == 0) return Double.POSITIVE_INFINITY;
        return 10 * Math.log10((maxPixelValue * maxPixelValue) / mse);
    }

    public static void main(String[] args) throws Exception {
        BufferedImage original = ImageIO.read(new File("original.png"));
        BufferedImage compressed = ImageIO.read(new File("compressed.png"));

        if (original.getWidth() != compressed.getWidth() || original.getHeight() != compressed.getHeight()) {
            System.err.println("Les images doivent être de la même taille.");
            return;
        }

        double mse = calculateMSE(original, compressed);
        double psnr = calculatePSNR(mse, 255); // 255 for 8-bit images

        System.out.printf("MSE: %.2f\n", mse);
        System.out.printf("PSNR: %.2f dB\n", psnr);
    }
    
}
