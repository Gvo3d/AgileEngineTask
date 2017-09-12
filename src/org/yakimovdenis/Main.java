package org.yakimovdenis;

import org.yakimovdenis.ImageProcessor.ImageProcessor;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        ImageProcessor processor = new ImageProcessor();
        File file = new File("c:\\image.png");
        processor.getImage(file);
    }
}
