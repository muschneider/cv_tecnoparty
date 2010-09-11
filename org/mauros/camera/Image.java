package org.mauros.camera;


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Image {

    private BufferedImage image;

    public void open(String fileName) {
        try {
            image = ImageIO.read(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(java.awt.Image img) {
        image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        RenderedImage ri = (RenderedImage) img;
        image.setData(ri.getData());
    }

    public void updateImage(int[][] im) {
        int height = im.length;
        int width = im[0].length;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = newImage.getRaster();
        for (int y = 0; y < height; y++) {  // altura
            for (int x = 0; x < width; x++) { // largura
                int[] cor = {im[y][x], im[y][x], im[y][x]};
                raster.setPixel(x, y, cor);
            }
        }
        image = newImage;
    }

    private double contrast(double band, double perc) {
        double c = band * (1 + perc / 100);
        if (c > 255) {
            c = 255;
        }
        if (c < 0) {
            c = 0;
        }
        return c;
    }

    public void contrast(double perc) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = newImage.getRaster();
        for (int y = 0; y < height; y++) {  // altura
            for (int x = 0; x < width; x++) { // largura
                int rgb = image.getRGB(x, y);
                double alpha = (rgb >> 24) & 0xff;
                double r = (rgb >> 16) & 0xff; // Nivel de Vermelho
                double g = (rgb >> 8) & 0xff;  // Nivel de Verde
                double b = (rgb) & 0xff;       // Nivel de Azul

                r = contrast(r, perc);
                g = contrast(g, perc);
                b = contrast(b, perc);

                double[] cor = {r, b, b};
                raster.setPixel(x, y, cor);
            }
        }
        image = newImage;
    }

    private double brightness(double r, int value) {
        double c = r + value;
        if (c > 255) {
            c = 255;
        }
        if (c < 0) {
            c = 0;
        }
        return c;
    }

    public void brightness(int value) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = newImage.getRaster();
        for (int y = 0; y < height; y++) {  // altura
            for (int x = 0; x < width; x++) { // largura
                int rgb = image.getRGB(x, y);
                double alpha = (rgb >> 24) & 0xff;
                double r = (rgb >> 16) & 0xff; // Nivel de Vermelho
                double g = (rgb >> 8) & 0xff;  // Nivel de Verde
                double b = (rgb) & 0xff;       // Nivel de Azul

                r = brightness(r, value);
                g = brightness(g, value);
                b = brightness(b, value);

                double[] cor = {r, b, b};
                raster.setPixel(x, y, cor);
            }
        }
        image = newImage;
    }

    public void grayScale() {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = newImage.getRaster();

        for (int y = 0; y < height; y++) {  // altura
            for (int x = 0; x < width; x++) { // largura
                int rgb = image.getRGB(x, y);

                double r = (rgb >> 16) & 0xff; // Nivel de Vermelho
                double g = (rgb >> 8) & 0xff;  // Nivel de Verde
                double b = (rgb) & 0xff;       // Nivel de Azul
                double pixel = (r + g + b)  / 3;

                int[] cor = {(int) pixel, (int) pixel, (int) pixel};
                raster.setPixel(x, y, cor);
            }
        }
        image = newImage;
    }

    public ImageIcon getImageIcon() {
        return new ImageIcon(image);
    }

    public ImageIcon getImageIcon(int IMG_WIDTH, int IMG_HEIGHT) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return new ImageIcon(resizedImage);
    }

    public int[][] getImageMatrix() {
        int height = image.getHeight();
        int width = image.getWidth();

        int[][] im = new int[height][width];

        for (int y = 0; y < height; y++) {  // altura
            for (int x = 0; x < width; x++) { // largura
                int rgb = image.getRGB(x, y);
                double r = (int) ((rgb & 0x00FF0000) >>> 16); // Nivel de Vermelho
                im[y][x] = (int) r;
            }
        }
        return im;
    }

    public void thresholding (int threshold) {
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] imageBin = new int[height][width];
        int[][] image = getImageMatrix();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image[y][x] > threshold) {
                    imageBin[y][x] = 255;
                } else {
                    imageBin[y][x] = 0;
                }

            }
        }
        updateImage(imageBin);
    }

    public int getRGB(int y, int x) {
        if (y < 0) {
            y = 0;
        }
        if (x < 0) {
            x = 0;
        }
        if (y >= getHeight()) {
            y = getHeight()-1;
        }
        if (x >= getWidth()) {
            x = getWidth()-1;
        }
        int rgb = image.getRGB(x, y);
        return rgb;
    }

    public double getPixelRed(int y, int x) {
        int rgb = getRGB(y, x);
        double r = (rgb >> 16) & 0xff; // Nivel de Vermelho
        return r;
    }

    public double getPixelGreen(int y, int x) {
        int rgb = getRGB(y, x);
        double g = (rgb >> 8) & 0xff;  // Nivel de Verde
        return g;
    }

    public double getPixelBlue(int y, int x) {
        int rgb = getRGB(y, x);
        double b = (rgb) & 0xff;       // Nivel de Azul
        return b;
    }

    public int[] getPixel(int y, int x) {
        int rgb = getRGB(y, x);
        double r = (rgb >> 16) & 0xff; // Nivel de Vermelho
        double g = (rgb >> 8) & 0xff;  // Nivel de Verde
        double b = (rgb) & 0xff;       // Nivel de Azul
        int[] pixel = {(int) r, (int) g, (int) b};
        return pixel;
    }

    public static int getPixel(int[][] im, int y, int x) {
        int h = im.length;
        int w = im[0].length;
        if (y < 0) {
            y = 0;
        }
        if (y >= h) {
            y = h - 1;
        }
        if (x < 0) {
            x = 0;
        }
        if (x >= w) {
            x = w - 1;
        }
        return im[y][x];
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int getBands() {
        return image.getRaster().getNumBands();
    }

}

