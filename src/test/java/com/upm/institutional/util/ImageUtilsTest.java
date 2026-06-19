package com.upm.institutional.util;

import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

public class ImageUtilsTest {

    @Test
    public void testResizeAndCompressBase64_InvalidData() {
        String invalid = "data:image/png;base64,invalidbase64content!!!";
        String result = ImageUtils.resizeAndCompressBase64(invalid, 100, 0.75f);
        assertEquals(invalid, result);
    }

    @Test
    public void testResizeAndCompressBase64_NonBase64Url() {
        String url = "https://upmisiones.com.ar/news";
        String result = ImageUtils.resizeAndCompressBase64(url, 100, 0.75f);
        assertEquals(url, result);
    }

    @Test
    public void testResizeAndCompressBase64_ValidPng() throws Exception {
        // Create a 2000x1000 PNG image
        BufferedImage img = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 2000, 1000);
        g.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        byte[] bytes = bos.toByteArray();

        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        // Resize to max dimension of 500px
        String result = ImageUtils.resizeAndCompressBase64(base64, 500, 0.75f);

        assertNotNull(result);
        assertTrue(result.startsWith("data:image/jpeg;base64,"));

        // Extract and check dimensions of the result
        String processedBase64 = result.substring(result.indexOf(",") + 1);
        byte[] processedBytes = Base64.getDecoder().decode(processedBase64);

        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(processedBytes);
        BufferedImage processedImg = ImageIO.read(bis);

        assertNotNull(processedImg);
        // Dimensions should be scaled down. Max dimension 500. Aspect ratio was 2:1.
        // So new size should be 500x250
        assertEquals(500, processedImg.getWidth());
        assertEquals(250, processedImg.getHeight());

        // The processed size should be significantly smaller than original PNG
        assertTrue(processedBytes.length < bytes.length, 
            "Compressed bytes (" + processedBytes.length + ") should be smaller than original PNG bytes (" + bytes.length + ")");
    }
}
