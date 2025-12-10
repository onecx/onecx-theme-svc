package org.tkit.onecx.theme.rs.external.v1.controllers;

import java.util.HashSet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.theme.domain.daos.IconDAO;
import org.tkit.onecx.theme.domain.services.IconService;
import org.tkit.onecx.theme.rs.external.v1.mappers.ExceptionMapper;
import org.tkit.onecx.theme.rs.external.v1.mappers.IconMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.rs.icon.v1.IconsV1Api;
import gen.org.tkit.onecx.theme.rs.icon.v1.model.IconCriteriaDTOV1;
import gen.org.tkit.onecx.theme.rs.icon.v1.model.IconListResponseDTOV1;
import gen.org.tkit.onecx.theme.rs.icon.v1.model.ProblemDetailResponseDTOV1;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class IconRestControllerV1 implements IconsV1Api {

    @Inject
    IconDAO iconDAO;

    @Inject
    IconService iconService;

    @Inject
    IconMapper iconMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response findIconsByNamesAndRefId(String refId, IconCriteriaDTOV1 iconCriteriaDTO) {
        var icons = iconDAO.findIconsByNamesAndRefId(new HashSet<>(iconCriteriaDTO.getNames()), refId).toList();
        icons = iconService.resolveAliases(icons);

        IconListResponseDTOV1 res = new IconListResponseDTOV1();
        res.setIcons(iconMapper.mapList(icons));
        return Response.ok(res).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
