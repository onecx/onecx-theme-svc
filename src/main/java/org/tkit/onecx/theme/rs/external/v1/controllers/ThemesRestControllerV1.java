package org.tkit.onecx.theme.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.rs.external.v1.mappers.ThemeMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.rs.external.v1.ThemesV1Api;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ThemesRestControllerV1 implements ThemesV1Api {

    @Inject
    ThemeDAO dao;

    @Inject
    ImageDAO imageDAO;

    @Inject
    ThemeMapper mapper;

    @Override
    public Response getThemeByName(String name) {
        var theme = dao.findThemeByName(name);
        if (theme == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(theme)).build();
    }

    @Override
    public Response getThemeFaviconByName(String name) {
        Image image = imageDAO.findByRefIdAndRefType(name, "favicon");
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image.getImageData(), image.getMimeType())
                .header(HttpHeaders.CONTENT_LENGTH, image.getLength()).build();
    }

    @Override
    public Response getThemeLogoByName(String name) {
        Image image = imageDAO.findByRefIdAndRefType(name, "logo");
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image.getImageData(), image.getMimeType())
                .header(HttpHeaders.CONTENT_LENGTH, image.getLength()).build();
    }

    @Override
    public Response getThemesInfo() {
        var items = dao.findAllInfos();
        return Response.ok(mapper.mapInfoList(items)).build();
    }

}
