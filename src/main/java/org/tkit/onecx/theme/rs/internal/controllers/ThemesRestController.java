package org.tkit.onecx.theme.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.daos.ThemeOverrideDAO;
import org.tkit.onecx.theme.domain.services.ThemeService;
import org.tkit.onecx.theme.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.theme.rs.internal.mappers.ThemeMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.rs.internal.ThemesInternalApi;
import gen.org.tkit.onecx.theme.rs.internal.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class ThemesRestController implements ThemesInternalApi {

    @Inject
    ThemeDAO dao;

    @Inject
    ThemeOverrideDAO overrideDAO;

    @Inject
    ThemeService themeService;

    @Inject
    ThemeMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createNewTheme(CreateThemeDTO createThemeDTO) {
        var theme = mapper.create(createThemeDTO);
        theme = dao.create(theme);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(theme.getId()).build())
                .entity(mapper.map(theme))
                .build();
    }

    @Override
    public Response deleteTheme(String id) {
        themeService.deleteTheme(id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response getThemeInfoList() {
        var items = dao.findAllInfos();
        return Response.ok(mapper.mapInfoList(items)).build();
    }

    @Override
    public Response getThemeById(String id) {
        var theme = dao.findById(id);
        if (theme == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var mappedTheme = mapper.map(theme);
        overrideDAO.findByThemeId(theme.getId()).forEach(o -> mappedTheme.getOverrides().add(mapper.map(o)));
        return Response.ok(mappedTheme).build();
    }

    @Override
    public Response getThemeByThemeDefinitionName(String name) {
        var theme = dao.findThemeByName(name);
        if (theme == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(theme)).build();
    }

    @Override
    public Response searchThemes(ThemeSearchCriteriaDTO themeSearchCriteriaDTO) {
        var criteria = mapper.map(themeSearchCriteriaDTO);
        var result = dao.findThemesByCriteria(criteria);
        return Response.ok(mapper.mapPage(result)).build();
    }

    @Override
    public Response updateTheme(String id, UpdateThemeDTO updateThemeDTO) {

        var theme = dao.findById(id);
        if (theme == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateThemeDTO, theme);
        dao.update(theme);
        return Response.noContent().build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> optimisticLockException(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }
}
