package org.yakimovdenis.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ImageProcessor {
    IntComparator comparator = new IntComparator();
    ArrayList<PixelDiffirence> diffirences = new ArrayList<>();
    int diffirenceMarker = 0;
    BufferedImage firstImage;
    BufferedImage secondImage;


    public void compareImages(File firstImageFile, File secondImageFile) {
        try {
            firstImage = ImageIO.read(firstImageFile);
            secondImage = ImageIO.read(secondImageFile);
            processImages();
        } catch (IOException e) {
            System.err.println("File is non-existent/corrupt: " + e);
        }
    }

    private void processImages() {
        int imageType = firstImage.getType();
        BufferedImage result = new BufferedImage(firstImage.getWidth() > secondImage.getWidth() ? firstImage.getWidth() : secondImage.getWidth(), firstImage.getHeight() > secondImage.getHeight() ? firstImage.getHeight() : secondImage.getHeight(), imageType);
        DataBuffer firstImageData = firstImage.getRaster().getDataBuffer();
        DataBuffer secondImageData = secondImage.getRaster().getDataBuffer();
        boolean hasDiffirence = false;
        for (int x = 0; x<firstImageData.getNumBanks(); x++){
            for (int y = 0; y < firstImageData.getSize(); y++) {
                if (comparator.compare(Integer.valueOf(firstImageData.getElem(y)), Integer.valueOf(secondImageData.getElem(y)))!=0){
                    diffirences.add(new PixelDiffirence(x,y,0));
                }
            }
        }

        System.out.println(data);
    }

    private int getSurroundField(int posX, int posY){
            for (int i = posX-1; i<=posX+1; i++){
                for (int j= posY-1; j<=posY; j++){
                    if (i!=posX && j!=posY) {
                       for (PixelDiffirence pixel: diffirences){
                           if (i==pixel.posX && j==pixel.posY){
                               return pixel.field;
                           }
                       }
                    }
                }
            }
        return diffirenceMarker++;
    }

    private class PixelDiffirence {
        private int posX;
        private int posY;
        private int field;

        public PixelDiffirence(int posX, int posY, int field) {
            this.posX = posX;
            this.posY = posY;
            this.field = field;
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
