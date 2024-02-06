package io.github.onecx.theme.rs.internal.services;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.io.IOException;
import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;

import gen.io.github.onecx.theme.rs.internal.model.ImageInfoDTO;
import io.github.onecx.theme.domain.daos.ImageDAO;
import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.domain.models.Image;
import io.github.onecx.theme.rs.internal.mappers.ImageMapper;
import io.quarkus.logging.Log;

@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class ImageService {

    @Inject
    ImageDAO imageDAO;

    @Inject
    ThemeDAO themeDAO;

    @Inject
    ImageMapper imageMapper;

    @Context
    UriInfo uriInfo;

    public ImageInfoDTO uploadFile(String refId, String refType, InputStream inputStream) throws IOException {
        try {
            byte[] imageData = IOUtils.toByteArray(inputStream);
            Log.info("Byte Array was created from InputStream " + imageData.length);
            Image image = new Image();
            image.setImageData(imageData);
            image.setRefID(refId);
            image.setRefType(refType);
            String imageContentType = detectMimeTypeType(imageData);
            image.setMimeType(imageContentType);
            imageDAO.create(image);
            return imageMapper.map(image);
        } catch (Exception e) {
            Log.info("Error saving Image into database ", e);
            throw e;
        }
    }

    @Transactional
    public ImageInfoDTO updateImage(InputStream inputStream, String refId, String refType) {
        try {
            byte[] imageData = IOUtils.toByteArray(inputStream);
            Image image = imageDAO.findByRefIdAndRefType(refId, refType);
            // Update the image data
            image.setImageData(imageData);
            String imageContentType = detectMimeTypeType(imageData);
            image.setMimeType(imageContentType);
            Log.info("Image was updated with ref_id {}", refId, null);
            return imageMapper.map(image);
        } catch (IOException e) {
            Log.error("Error occured when uploading Image", e);
            return null;
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

        return "Unknown";
    }

}
