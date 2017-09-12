package org.yakimovdenis.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ImageProcessor {
    private IntComparator comparator = new IntComparator();
    private BufferedImage firstImage;
    private BufferedImage secondImage;
    private File resultFile = null;
    private int[][] differenceField;
    private String imageType = "png";
    private static final int OFFSET = 5;


    public void compareImages(File firstImageFile, File secondImageFile, File outputFile) {
        try {
            firstImage = ImageIO.read(firstImageFile);
            secondImage = ImageIO.read(secondImageFile);
            String[] nameData = firstImageFile.getName().split("\\.");
            imageType = nameData[nameData.length - 1];
            differenceField = new int[firstImage.getWidth()][firstImage.getHeight()];
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

        for (int x = 0; x < firstImage.getWidth(); x++) {
            for (int y = 0; y < firstImage.getHeight(); y++) {
                if (comparator.compare(firstImage.getRGB(x, y), secondImage.getRGB(x, y)) != 0) {
                    differenceField[x][y] = -1;
                }
            }
        }

        List<DifferenceRectangle> rectangles = new ArrayList<>();
        int differenceMarker = 1;
        for (int i = 0; i < firstImage.getWidth(); i++) {
            for (int j = 0; j < firstImage.getHeight(); j++) {
                if (differenceField[i][j] == -1) {
                    DifferenceRectangle rectangle = new DifferenceRectangle(differenceMarker++);
                    differenceField[i][j] = rectangle.marker;
                    rectangle.addPixel(i, j);
                    rectangles.add(rectangle);
                    markSurroundedRecursively(i, j, rectangle);
                }
            }
        }

        for (DifferenceRectangle rectangle : rectangles) {
            Pixel leftPixel = rectangle.getBoundPixel(true);
            Pixel rightPixel = rectangle.getBoundPixel(false);
            Graphics2D graph = result.createGraphics();
            graph.setColor(Color.RED);
            Rectangle markedRectangle = new Rectangle();
            markedRectangle.x = leftPixel.getPosX() - 2;
            markedRectangle.y = leftPixel.getPosY() - 2;
            markedRectangle.height = (rightPixel.getPosY() - leftPixel.getPosY()) + 3;
            markedRectangle.width = (rightPixel.getPosX() - leftPixel.getPosX()) + 3;
            graph.draw(markedRectangle);
            graph.dispose();
        }

        try {
            ImageIO.write(result, this.imageType, resultFile);
        } catch (IOException e) {
            System.err.println("Rendering went wrong: " + e);
        }
    }


    private void markSurroundedRecursively(int posX, int posY, DifferenceRectangle rectangle) {
        int minX = (posX - OFFSET)<0?0:posX-OFFSET;
        int minY = (posY - OFFSET)<0?0:posY-OFFSET;
        int maxX = (posX + OFFSET)>firstImage.getWidth()-1?firstImage.getWidth():posX + OFFSET;
        int maxY = (posY + OFFSET)>firstImage.getHeight()-1?firstImage.getHeight():posY + OFFSET;

        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                if (differenceField[i][j] != 0 && differenceField[i][j] != rectangle.marker) {
                    differenceField[i][j] = rectangle.marker;
                    rectangle.addPixel(i, j);
                    markSurroundedRecursively(i, j, rectangle);
                }
            }
        }
    }

    private class DifferenceRectangle {
        private ArrayList<Pixel> pixels = new ArrayList<>();
        private int marker;

        DifferenceRectangle(int marker) {
            this.marker = marker;
        }

        void addPixel(int posX, int posY) {
            this.pixels.add(new Pixel(posX, posY, this.marker));
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

            if (leftUpper) {
                posX = Collections.min(posXSet);
                posY = Collections.min(posYSet);
            } else {
                posX = Collections.max(posXSet);
                posY = Collections.max(posYSet);
            }
            return new Pixel(posX, posY, 0);
        }

    }

    private class Pixel {
        private int posX;
        private int posY;
        private int field;

        Pixel(int posX, int posY, int field) {
            this.posX = posX;
            this.posY = posY;
            this.field = field;
        }

        int getPosX() {
            return posX;
        }

        int getPosY() {
            return posY;
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
