package io.github.onecx.theme.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.theme.rs.external.v1.ThemesV1Api;
import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.rs.external.v1.mappers.ThemeMapper;

@LogService
@Path("/v1/themes")
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ThemesRestControllerV1 implements ThemesV1Api {

    @Inject
    ThemeDAO dao;

    @Inject
    ThemeMapper mapper;

    @Override
    public Response getThemeByThemeDefinitionName(String name) {
        var theme = dao.findThemeByName(name);
        if (theme == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(theme)).build();
    }

    @Override
    public Response getThemeInfoList() {
        var items = dao.findAllInfos();
        return Response.ok(mapper.mapInfoList(items)).build();
    }

}
