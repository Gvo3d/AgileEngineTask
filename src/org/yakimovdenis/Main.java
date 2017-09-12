package org.yakimovdenis;

import org.yakimovdenis.ImageProcessor.ImageProcessor;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        ImageProcessor processor = new ImageProcessor();
        File file = new File("c:\\image1.png");
        File file2 = new File("c:\\image2.png");
        File file3 = new File("c:\\image3.png");
        processor.compareImages(file,file2, file3);
    }
}
