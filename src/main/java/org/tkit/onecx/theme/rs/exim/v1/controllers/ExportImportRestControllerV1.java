package org.tkit.onecx.theme.rs.exim.v1.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.rs.exim.v1.mappers.ExportImportExceptionMapperV1;
import org.tkit.onecx.theme.rs.exim.v1.mappers.ExportImportMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.rs.exim.v1.ThemesExportImportApi;
import gen.org.tkit.onecx.theme.rs.exim.v1.model.*;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ExportImportRestControllerV1 implements ThemesExportImportApi {

    @Inject
    ThemeDAO dao;
    @Inject
    ExportImportExceptionMapperV1 exceptionMapper;

    @Inject
    ExportImportMapperV1 mapper;

    @Override
    public Response exportThemes(ExportThemeRequestDTOV1 request) {
        var themes = dao.findThemeByNames(request.getNames());

        var data = themes.collect(Collectors.toMap(Theme::getName, theme -> theme));

        if (data.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.create(data)).build();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response importThemes(ThemeSnapshotDTOV1 request) {
        var keys = request.getThemes().keySet();
        var themes = dao.findThemeByNames(keys);
        var map = themes.collect(Collectors.toMap(Theme::getName, theme -> theme));

        Map<String, ImportThemeResponseStatusDTOV1> items = new HashMap<>();

        request.getThemes().forEach((name, dto) -> {

            var theme = map.get(name);
            if (theme == null) {

                theme = mapper.create(dto);
                theme.setName(name);
                dao.create(theme);
                items.put(name, ImportThemeResponseStatusDTOV1.CREATED);

            } else {

                mapper.update(dto, theme);
                dao.update(theme);
                items.put(name, ImportThemeResponseStatusDTOV1.UPDATE);
            }
        });

        return Response.ok(mapper.create(request, items)).build();
    }

    @ServerExceptionMapper
    public RestResponse<EximProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
