package io.github.onecx.theme.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.theme.rs.internal.ImageV1Api;
import gen.io.github.onecx.theme.rs.internal.model.ImageInfoDTO;
import gen.io.github.onecx.theme.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.theme.domain.daos.ImageDAO;
import io.github.onecx.theme.domain.models.Image;
import io.github.onecx.theme.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.theme.rs.internal.services.ImageService;
import io.quarkus.logging.Log;

@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class ImageRestController implements ImageV1Api {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    ImageDAO imageDAO;

    @Inject
    ImageService imageService;

    @Context
    UriInfo uriInfo;

    @ConfigProperty(name = "Image.small.height", defaultValue = "150")
    Integer smallImgHeight;

    @ConfigProperty(name = "Image.small.width", defaultValue = "150")
    Integer smallImgWidth;

    @Override
    @Transactional
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
    public Response updateImage(String imageId, InputStream imageInputStream) {

        if (imageInputStream == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("File has not been provided").build();
        }
        try {
            // Update the image data
            imageService.updateImage(imageInputStream, imageId);
        } catch (Exception e) {
            Log.error("Error occured when uploading Image", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response uploadImage(InputStream imageInputStream, String themeId, String imageType) {
        Log.info("ImageController entered uploadFile method {}", imageInputStream.toString(), null);
        try {
            ImageInfoDTO imageInfoDTO = imageService.uploadFile(imageInputStream, themeId, imageType);
            if (imageInfoDTO == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.created(uriInfo.getAbsolutePathBuilder().path(imageInfoDTO.getId()).build())
                    .entity(imageInfoDTO)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
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
