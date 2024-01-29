package io.github.onecx.theme.rs.internal.services;

import java.io.IOException;
import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;

import gen.io.github.onecx.theme.rs.external.v1.model.ImageInfoDTOV1;
import io.github.onecx.theme.domain.daos.ImageDAO;
import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.domain.models.Image;
import io.github.onecx.theme.domain.models.Theme;
import io.github.onecx.theme.rs.internal.mappers.ImageMapper;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ImageService {

    @Inject
    ImageDAO imageDAO;

    @Inject
    ThemeDAO themeDAO;

    @Inject
    ImageMapper imageMapper;

    @Context
    UriInfo uriInfo;

    public ImageInfoDTOV1 uploadFile(InputStream inputStream, String themeId, String imageType) throws IOException {
        Image image = saveImage(inputStream);

        Log.info("Image was saved" + image);

        Theme theme = themeDAO.findById(themeId);
        if (imageType.equals("LOGO")) {
            theme.setLogoId(image);
        } else if (imageType.equals("FAVICON")) {
            theme.setFaviconId(image);
        }
        return imageMapper.map(image);
    }

    public ImageInfoDTOV1 updateImage(InputStream inputStream, String imageId) {
        try {
            byte[] imageData = IOUtils.toByteArray(inputStream);

            Image image = imageDAO.findById(imageId);
            // Update the image data
            image.setImageData(imageData);
            String imageContentType = detectMimeTypeType(imageData);
            image.setMimeType(imageContentType);
            Log.info("Image was updated with id {}", imageId, null);
            return imageMapper.map(image);
        } catch (IOException e) {
            Log.error("Error occured when uploading Image", e);
            return null;
        }
    }

    private Image saveImage(InputStream inputStream) throws IOException {
        try {
            byte[] imageData = IOUtils.toByteArray(inputStream);
            Log.info("Byte Array was created from InputStream " + imageData.length);
            Image image = new Image();
            image.setImageData(imageData);
            image.setUrl(uriInfo.getPath()
                    .concat("/")
                    .concat(image.getId()));
            String imageContentType = detectMimeTypeType(imageData);
            image.setMimeType(imageContentType);

            imageDAO.create(image);
            return image;
        } catch (Exception e) {
            Log.info("Error saving Image into database ", e);
            throw e;
        }
    }

    public static String detectMimeTypeType(byte[] data) {
        if (data == null || data.length < 2) {
            return "Unknown";
        }

        // Check for JPEG
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8) {
            return "image/jpeg";
        }

        // Check for PNG
        if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50) {
            return "image/png";
        }

        // Add more checks for other image formats as needed

        return "Unknown";
    }

}
