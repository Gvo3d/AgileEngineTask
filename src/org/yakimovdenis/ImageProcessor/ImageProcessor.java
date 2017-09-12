package org.yakimovdenis.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {
    private BufferedImage imageFile;

    private void getImage(File filePath){
        try {
            imageFile = ImageIO.read(filePath);
        } catch (IOException e) {
            System.err.println("File is non-existent/corrupt: "+e);
        }
    }

    private void processImage(){

    }
}
