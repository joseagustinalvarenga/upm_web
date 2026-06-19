package com.upm.institutional.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Iterator;

public class ImageUtils {

    /**
     * Resizes and compresses an image base64 data URI to JPEG format.
     * If processing fails, returns the original URI.
     */
    public static String resizeAndCompressBase64(String dataUri, int maxDimension, float quality) {
        if (dataUri == null || !dataUri.startsWith("data:")) {
            return dataUri;
        }

        try {
            int commaIndex = dataUri.indexOf(",");
            if (commaIndex == -1) {
                return dataUri;
            }

            String base64Data = dataUri.substring(commaIndex + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            byte[] processedBytes = compressAndResizeImage(imageBytes, maxDimension, quality);
            if (processedBytes != null) {
                String newBase64 = Base64.getEncoder().encodeToString(processedBytes);
                return "data:image/jpeg;base64," + newBase64;
            }
        } catch (Exception e) {
            System.err.println("Error processing base64 image: " + e.getMessage());
        }

        return dataUri;
    }

    /**
     * Compresses and resizes raw image bytes. Returns compressed JPEG bytes.
     */
    public static byte[] compressAndResizeImage(byte[] imageBytes, int maxDimension, float quality) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            BufferedImage originalImage = ImageIO.read(bis);
            if (originalImage == null) {
                return null;
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            int newWidth = width;
            int newHeight = height;

            if (width > maxDimension || height > maxDimension) {
                if (width > height) {
                    newWidth = maxDimension;
                    newHeight = (height * maxDimension) / width;
                } else {
                    newHeight = maxDimension;
                    newWidth = (width * maxDimension) / height;
                }
            }

            // Create canvas with White background (to support transparency of PNG/GIF transparent zones correctly)
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, newWidth, newHeight);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                ImageIO.write(resizedImage, "jpg", bos);
            } else {
                ImageWriter writer = writers.next();
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
                    writer.setOutput(ios);
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    if (param.canWriteCompressed()) {
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(quality);
                    }
                    writer.write(null, new IIOImage(resizedImage, null, null), param);
                } finally {
                    writer.dispose();
                }
            }

            return bos.toByteArray();
        } catch (Exception e) {
            System.err.println("Error resizing and compressing image bytes: " + e.getMessage());
            return null;
        }
    }
}
