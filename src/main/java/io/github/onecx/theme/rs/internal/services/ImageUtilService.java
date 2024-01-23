package io.github.onecx.theme.rs.internal.services;

import static java.awt.Image.SCALE_DEFAULT;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageUtilService {

    public static byte[] resizeImage(byte[] imgBytesArray, Integer smallImgWidth, Integer smallImgHeight) throws IOException {

        var image = ImageIO.read(new ByteArrayInputStream(imgBytesArray));

        var scaledInstance = image.getScaledInstance(smallImgWidth, smallImgHeight, SCALE_DEFAULT);
        var bufferedImage = new BufferedImage(scaledInstance.getWidth(null),
                scaledInstance.getHeight(null),
                TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(scaledInstance, 0, 0, null);

        var os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        return os.toByteArray();
    }
}
