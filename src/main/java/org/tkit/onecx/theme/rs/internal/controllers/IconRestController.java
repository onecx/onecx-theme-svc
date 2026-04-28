package org.tkit.onecx.theme.rs.internal.controllers;

import java.io.IOException;
import java.util.HashSet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.theme.domain.daos.IconDAO;
import org.tkit.onecx.theme.domain.daos.IconSetDAO;
import org.tkit.onecx.theme.domain.services.IconService;
import org.tkit.onecx.theme.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.theme.rs.internal.mappers.IconMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.rs.icon.internal.IconsInternalApi;
import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconCriteriaDTO;
import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconListResponseDTO;
import gen.org.tkit.onecx.theme.rs.internal.model.ProblemDetailResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LogService
@ApplicationScoped
public class IconRestController implements IconsInternalApi {

    @Inject
    IconService iconService;

    @Inject
    IconDAO iconDAO;

    @Inject
    IconSetDAO iconSetDAO;

    @Inject
    IconMapper iconMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response uploadIconSet(String refId, byte[] body) {
        try {
            iconService.createIcons(body, refId);
        } catch (IOException e) {
            log.error("Error uploading icon set for refId {}: {}", refId, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response deleteIconSetByRefIdAndPrefix(String refId, String prefix) {
        iconService.deleteIconSetByRefIdAndPrefix(refId, prefix);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response findIconSetsByRefId(String refId) {
        var iconSets = iconSetDAO.findByRefId(refId);
        return Response.status(Response.Status.OK).entity(iconMapper.mapIconSets(iconSets)).build();
    }

    @Override
    public Response findIconsByCriteria(String refId, IconCriteriaDTO iconCriteriaDTO) {
        String iconSetId = null;
        if (iconCriteriaDTO.getPrefix() != null) {
            iconSetId = iconSetDAO.findByPrefixAndRefId(iconCriteriaDTO.getPrefix(), refId).getId();
        }
        var icons = iconDAO.findIconsByRefIdIconSetIdAndNames(refId, iconSetId, new HashSet<>(iconCriteriaDTO.getNames()))
                .toList();
        icons = iconService.resolveAliases(icons);

        IconListResponseDTO res = new IconListResponseDTO();
        res.setIcons(iconMapper.mapList(icons));
        return Response.ok(res).build();
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
