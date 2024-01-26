package io.github.onecx.theme.rs.internal.controllers;

import static io.smallrye.config.ConfigLogging.log;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;

import javax.imageio.ImageIO;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.theme.rs.external.v1.ImageV1Api;
import gen.io.github.onecx.theme.rs.external.v1.model.ImageInfoDTOV1;
import gen.io.github.onecx.theme.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.theme.domain.daos.ImageDAO;
import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.domain.models.Image;
import io.github.onecx.theme.domain.models.Theme;
import io.github.onecx.theme.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.theme.rs.internal.mappers.ImageMapper;
import io.github.onecx.theme.rs.internal.services.ImageUtilService;

public class ImageRestController implements ImageV1Api {
    private static final List<String> ALLOWED_Image_FORMATS = Arrays.asList("png", "jpg", "jpeg");
    @Inject
    ImageMapper imageMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    ImageDAO imageDAO;
    @Inject
    ThemeDAO themeDAO;

    @Context
    UriInfo uriInfo;

    @ConfigProperty(name = "Image.small.height", defaultValue = "150")
    Integer smallImgHeight;

    @ConfigProperty(name = "Image.small.width", defaultValue = "150")
    Integer smallImgWidth;

    @Override
    public Response getImage(String imageId) {
        Image image = imageDAO.findById(imageId);
        CacheControl cc = new CacheControl();
        cc.setPrivate(true);

        if (image == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        ByteArrayInputStream imageByteInputStream = new ByteArrayInputStream(image.getImageData());
        return Response.ok(imageByteInputStream).header("Content-Type", image.getMimeType()).cacheControl(cc).build();
    }

    @Override
    public Response updateImage(String imageId, InputStream imageInputStream, String fileName) {
        Image image = imageDAO.findById(imageId);

        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            // Update the image data
            image.setImageData(readInputStreamToByteArray(imageInputStream));
        } catch (IOException e) {
            log.error("Error occured when uploading Image", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        // Call the DAO to update the image in the database
        imageDAO.update(image);

        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response uploadImage(InputStream imageInputStream, String themeId, String imageType) {

        if (imageInputStream == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            InputStream inputStream = imageInputStream;
            byte[] imageBytes = IOUtils.toByteArray(inputStream);

            String imageContentType = URLConnection.guessContentTypeFromStream(inputStream);

            if (imageContentType != null
                    && (!imageContentType.matches("image/[a-zA-Z]*$")
                            || !ALLOWED_Image_FORMATS.contains(imageContentType.split("/")[1]))) {

                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            byte[] compressedImageData = this.compressImageData(imageBytes);
            Image image = saveImage(compressedImageData, imageContentType);

            Theme theme = themeDAO.findById(themeId);

            if (imageType == "LOGO") {
                theme.setLogoId(image);
            } else if (imageType == "FAVICON") {
                theme.setFaviconId(image);
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            ImageInfoDTOV1 imageDTOV1 = imageMapper.map(image);
            return Response.ok(imageDTOV1).build();

        } catch (IOException | DAOException e) {
            log.error("Error occured when uploading Image", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096]; // You can adjust the buffer size as needed

        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] compressImageData(byte[] imgBytesArray) throws IOException {

        return ImageUtilService.resizeImage(imgBytesArray, smallImgWidth, smallImgHeight);
    }

    private Image saveImage(byte[] compressedImageData, String mimeType) {
        Image image = new Image();
        image.setImageData(compressedImageData);
        image.setUrl(uriInfo.getPath()
                .concat("/")
                .concat(image.getId()));
        image.setMimeType(mimeType);

        try {
            BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(image.getImageData()));
            image.setHeight(buffImage.getHeight());
            image.setWidth(buffImage.getWidth());
        } catch (IOException e) {
            log.error("Exception thrown when setting up small Image's metadata", e);
        }
        imageDAO.create(image);
        return image;
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

}
