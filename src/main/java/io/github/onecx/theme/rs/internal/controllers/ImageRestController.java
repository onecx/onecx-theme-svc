package io.github.onecx.theme.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.*;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.image.rs.internal.ImagesInternalApi;
import gen.io.github.onecx.theme.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.theme.domain.daos.ImageDAO;
import io.github.onecx.theme.domain.models.Image;
import io.github.onecx.theme.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.theme.rs.internal.mappers.ImageMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class ImageRestController implements ImagesInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    ImageDAO imageDAO;

    @Context
    UriInfo uriInfo;

    @Inject
    ImageMapper imageMapper;

    @Override
    @Transactional
    public Response getImage(String refId, String refType) {

        Image image = imageDAO.findByRefIdAndRefType(refId, refType);

        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ByteArrayInputStream imageByteInputStream = new ByteArrayInputStream(image.getImageData());
        return Response.ok(imageByteInputStream).header(HttpHeaders.CONTENT_TYPE, image.getMimeType()).build();
    }

    @Override
    public Response updateImage(String refId, String refType, InputStream imageInputStream) {

        Image image = imageDAO.findByRefIdAndRefType(refId, refType);

        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        imageDAO.updateImage(image, imageInputStream);

        return Response.ok(imageMapper.map(image)).build();
    }

    @Override
    public Response uploadImage(String refId, String refType, InputStream imageInputStream) {

        Image image = imageDAO.createImage(imageMapper.create(refId, refType), imageInputStream);

        var imageInfoDTO = imageMapper.map(image);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(imageInfoDTO.getId()).build())
                .entity(imageInfoDTO)
                .build();
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
