package org.yakimovdenis.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {
    public void getImage(File filePath){
        try {
            processImage(ImageIO.read(filePath));
        } catch (IOException e) {
            System.err.println("File is non-existent/corrupt: "+e);
        }
    }

    private void processImage(BufferedImage imageFile){
        Raster raster = imageFile.getRaster();
        DataBuffer data = raster.getDataBuffer();
        System.out.println(data);
    }
}
