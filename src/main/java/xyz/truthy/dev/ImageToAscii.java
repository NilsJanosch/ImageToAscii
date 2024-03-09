package xyz.truthy.dev;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class ImageToAscii {

    public static void main(String[] args) {
        int scaledWidth = 256;
        int asciiWidth = 2;

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the file path of the image: ");
        String imagePath = scanner.nextLine();
        imagePath = imagePath.replace("\"", "");

        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            // Downscale the image
            BufferedImage resizedImage = resizeImage(originalImage, scaledWidth);

            int width = resizedImage.getWidth();
            int height = resizedImage.getHeight();

            // Calculate ASCII character height based on aspect ratio
            int asciiHeight = (int) Math.round((double) height / width * asciiWidth);

            StringBuilder asciiImage = new StringBuilder();

            for (int y = 0; y < height; y += asciiHeight) {
                for (int x = 0; x < width; x++) {
                    Color pixelColor = new Color(resizedImage.getRGB(x, y));
                    int brightness = (pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue()) / 3;
                    char asciiChar = getAsciiChar(brightness);
                    asciiImage.append(asciiChar);
                }
                asciiImage.append("\n");
            }

            String filename = "out/" + System.currentTimeMillis() + "_ASCII.txt";
            File outDir = new File("out");
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            try (PrintWriter writer = new PrintWriter(filename)) {
                writer.println(asciiImage);
            }

            System.out.println("ASCII image saved to " + filename);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetHeight = (int) Math.round((double) originalHeight / originalWidth * targetWidth);

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resizedImage;
    }

    private static char getAsciiChar(int brightness) {
        // Use thresholds to determine brightness levels
        if (brightness > 240) {
            return '*'; // Very bright
        } else if (brightness > 220) {
            return '#'; // Bright
        } else if (brightness > 200) {
            return '&'; // Moderately bright
        } else if (brightness > 180) {
            return '$'; // Fairly bright
        } else if (brightness > 160) {
            return '@'; // Slightly bright
        } else if (brightness > 140) {
            return '8'; // Neutral
        } else if (brightness > 120) {
            return 'o'; // Slightly dark
        } else if (brightness > 100) {
            return ':'; // Fairly dark
        } else if (brightness > 80) {
            return '-'; // Moderately dark
        } else if (brightness > 60) {
            return '.'; // Dark
        } else if (brightness > 40) {
            return ' '; // Very dark
        } else {
            return ' '; // Darkest
        }
    }
}
