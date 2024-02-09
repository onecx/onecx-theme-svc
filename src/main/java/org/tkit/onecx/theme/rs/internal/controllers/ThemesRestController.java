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
import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
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
    ImageDAO imageDAO;

    @Inject
    ThemeMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Override
    @Transactional
    public Response createNewTheme(CreateThemeDTO createThemeDTO) {
        var theme = mapper.create(createThemeDTO);
        theme = dao.create(theme);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(theme.getId()).build())
                .entity(mapper.map(theme))
                .build();
    }

    @Override
    @Transactional
    public Response deleteTheme(String id) {
        var image = dao.findById(id);
        if (image == null) {
            return Response.noContent().build();
        }
        dao.deleteQueryById(id);

        // workaround for images
        imageDAO.deleteQueryByRefId(image.getName());

        return Response.noContent().build();
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
        return Response.ok(mapper.map(theme)).build();
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
    public Response getThemes(Integer pageNumber, Integer pageSize) {
        var items = dao.findAll(pageNumber, pageSize);
        return Response.ok(mapper.mapPage(items)).build();
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
