package org.yakimovdenis.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ImageProcessor {
    IntComparator comparator = new IntComparator();
    ArrayList<Pixel> diffirences = new ArrayList<>();
    int diffirenceMarker = 0;
    BufferedImage firstImage;
    BufferedImage secondImage;
    File resultFile = null;
    String imageType = "png";


    public void compareImages(File firstImageFile, File secondImageFile, File outputFile) {
        try {
            firstImage = ImageIO.read(firstImageFile);
            secondImage = ImageIO.read(secondImageFile);
            String[] nameData = firstImageFile.getName().split("\\.");
            imageType = nameData[nameData.length - 1];
            resultFile = outputFile;
            processImages();
        } catch (IOException e) {
            System.err.println("File is non-existent/corrupt: " + e);
        }
    }

    private void processImages() {
        int imageType = firstImage.getType();
        BufferedImage result = new BufferedImage(firstImage.getWidth() > secondImage.getWidth() ? firstImage.getWidth() : secondImage.getWidth(), firstImage.getHeight() > secondImage.getHeight() ? firstImage.getHeight() : secondImage.getHeight(), imageType);
        result.setData(firstImage.getData());

//        DataBuffer firstImageData = firstImage.getRaster().getDataBuffer();
//        DataBuffer secondImageData = secondImage.getRaster().getDataBuffer();
        for (int x = 0; x < firstImage.getWidth(); x++) {
            for (int y = 0; y < firstImage.getHeight(); y++) {
                if (comparator.compare(firstImage.getRGB(x, y), secondImage.getRGB(x, y)) != 0) {
                    diffirences.add(new Pixel(x, y, 0));
                }
            }
        }
        for (Pixel diffirence : diffirences) {
            diffirence.field = getSurroundField(diffirence.posX, diffirence.posY);
        }
        diffirenceMarker = 1;
        List<DiffirenceRectangle> diffirenceFields = new ArrayList<>();
        for (int i = 0; i < diffirenceMarker; i++) {
            DiffirenceRectangle rectangle = new DiffirenceRectangle(i);
            ListIterator<Pixel> iterator = diffirences.listIterator();
            while (iterator.hasNext()) {
                Pixel pixel = iterator.next();
                if (rectangle.marker == pixel.field) {
                    rectangle.addPixel(pixel);
                    iterator.remove();
                }
            }
            diffirenceFields.add(rectangle);
        }

        for (DiffirenceRectangle rectangle : diffirenceFields) {
            Pixel leftPixel = rectangle.getBoundPixel(true);
            Pixel rightPixel = rectangle.getBoundPixel(false);

            System.out.println("LEFT: " + leftPixel);
            System.out.println("RIGHT: " + rightPixel);

            Graphics2D graph = result.createGraphics();
            graph.setColor(Color.RED);
            Rectangle markedRectangle = new Rectangle();
            markedRectangle.x = leftPixel.getPosX();
            markedRectangle.y = leftPixel.getPosY();
            markedRectangle.height = rightPixel.getPosY() - leftPixel.getPosY();
            markedRectangle.width = rightPixel.getPosX() - leftPixel.getPosX();
            graph.draw(markedRectangle);
            graph.fill(markedRectangle);
            graph.dispose();
        }

        System.out.println("3");

        try {
            ImageIO.write(result, this.imageType, resultFile);
        } catch (IOException e) {
            System.err.println("Rendering went wrong: " + e);
        }
    }

    private int getSurroundField(int posX, int posY) {
            for (int i = posX-1; i<=posX+1; i++){
                for (int j= posY-1; j<=posY; j++){
                    if (i!=posX && j!=posY) {
                       for (Pixel pixel: diffirences){
                           if (i==pixel.posX && j==pixel.posY){
                               return pixel.field;
                           }
                       }
                    }
                }
            }
        return diffirenceMarker++;
    }

    private class DiffirenceRectangle {
        private ArrayList<Pixel> pixels = new ArrayList<>();
        private int marker;

        public DiffirenceRectangle(int marker) {
            this.marker = marker;
        }

        public void addPixel(Pixel pixel) {
            this.pixels.add(pixel);
        }

        Pixel getBoundPixel(boolean leftUpper) {
            HashSet<Integer> posXSet = new HashSet<>();
            HashSet<Integer> posYSet = new HashSet<>();
            for (Pixel currPixel : this.pixels) {
                posXSet.add(currPixel.getPosX());
                posYSet.add(currPixel.getPosY());
            }
            int posX;
            int posY;

            if (leftUpper){
                posX = Collections.min(posXSet);
                posY = Collections.min(posYSet);
            } else {
                posX = Collections.max(posXSet);
                posY = Collections.max(posYSet);
            }
            return new Pixel(posX,posY,0);
        }

    }

    private class Pixel {
        private int posX;
        private int posY;
        private int field;

        public Pixel(int posX, int posY, int field) {
            this.posX = posX;
            this.posY = posY;
            this.field = field;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        @Override
        public String toString() {
            return "Pixel{" +
                    "posX=" + posX +
                    ", posY=" + posY +
                    ", field=" + field +
                    '}';
        }
    }

    private class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (!o1.equals(o2)) {
                int diy = Integer.compare(o1, o2);
                int bigger = Math.max(o1, o2);
                if (diy > bigger / 10) {
                    return diy;
                }
            }
            return 0;
        }
    }

}
