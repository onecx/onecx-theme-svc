package org.tkit.onecx.theme.rs.internal.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.rs.internal.mappers.ImageMapper;

import gen.org.tkit.onecx.image.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.image.rs.internal.model.RefTypeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ImagesService {

    @Context
    HttpHeaders httpHeaders;

    @Inject
    ImageMapper imageMapper;

    @Inject
    ImageDAO imageDAO;

    public ImageInfoDTO uploadImage(Integer contentLength, String refId, RefTypeDTO refType, byte[] body) {
        var contentType = httpHeaders.getMediaType();
        contentType = new MediaType(contentType.getType(), contentType.getSubtype());
        var image = imageMapper.create(refId, refType.toString(), contentType.toString(), contentLength);
        image.setLength(contentLength);
        image.setImageData(body);
        image = imageDAO.create(image);

        return imageMapper.map(image);
    }

    public Image updateImage(String refId, RefTypeDTO refType, byte[] body, Integer contentLength) {
        Image image = imageDAO.findByRefIdAndRefType(refId, refType.toString());
        if (image == null) {
            System.out.println("ITS NULLLLLLL_____________");
            return null;
        }

        var contentType = httpHeaders.getMediaType();
        contentType = new MediaType(contentType.getType(), contentType.getSubtype());

        image.setLength(contentLength);
        image.setMimeType(contentType.toString());
        image.setImageData(body);

        return imageDAO.update(image);
    }

    @Transactional
    public void deleteImage(String refId, RefTypeDTO refType) {

        imageDAO.deleteQueryByRefIdAndRefType(refId, refType);
    }

    @Transactional
    public void deleteImagesById(String refId) {

        try {
            imageDAO.deleteQueryByRefId(refId);
        }catch (Exception e){
            System.out.println("NO DEL SUCCESS_______");
        }

    }

}
