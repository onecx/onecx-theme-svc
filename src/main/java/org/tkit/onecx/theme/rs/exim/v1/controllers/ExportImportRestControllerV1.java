package org.tkit.onecx.theme.rs.exim.v1.controllers;

import static org.tkit.onecx.theme.rs.exim.v1.mappers.ExportImportMapperV1.imageId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.services.ThemeService;
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
    ImageDAO imageDAO;

    @Inject
    ThemeDAO dao;
    @Inject
    ExportImportExceptionMapperV1 exceptionMapper;

    @Inject
    ExportImportMapperV1 mapper;

    @Inject
    ThemeService themeService;

    @Override
    public Response exportThemes(ExportThemeRequestDTOV1 request) {
        var themes = dao.findThemeByNames(request.getNames());

        var data = themes.collect(Collectors.toMap(Theme::getName, theme -> theme));

        if (data.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var images = imageDAO.findByRefIds(request.getNames());

        return Response.ok(mapper.create(data, images)).build();
    }

    @Override
    public Response importThemes(ThemeSnapshotDTOV1 request) {
        var names = request.getThemes().keySet();
        var themes = dao.findThemeByNames(names);
        var map = themes.collect(Collectors.toMap(Theme::getName, theme -> theme));

        var images = imageDAO.findByRefIds(names);
        var imagesMap = images.stream().collect(Collectors.toMap(ExportImportMapperV1::imageId, i -> i));

        Map<String, ImportThemeResponseStatusDTOV1> items = new HashMap<>();
        List<Theme> create = new ArrayList<>();
        List<Theme> update = new ArrayList<>();

        List<Image> createImages = new ArrayList<>();
        List<Image> updateImages = new ArrayList<>();

        request.getThemes().forEach((name, dto) -> {
            var theme = map.get(name);
            if (theme == null) {
                theme = mapper.create(name, dto);
                create.add(theme);
                createImages.addAll(mapper.createImages(name, dto.getImages()));
                items.put(name, ImportThemeResponseStatusDTOV1.CREATED);
            } else {
                mapper.update(dto, theme);
                update.add(theme);

                if (dto.getImages() != null) {
                    dto.getImages().forEach((refType, imageDto) -> {
                        var image = imagesMap.get(imageId(name, refType));
                        if (image == null) {
                            createImages.add(mapper.createImage(name, refType, imageDto));
                        } else {
                            updateImages.add(mapper.updateImage(image, imageDto));
                        }
                    });
                }

                items.put(name, ImportThemeResponseStatusDTOV1.UPDATE);
            }
        });

        themeService.importThemes(create, update, createImages, updateImages);

        return Response.ok(mapper.create(request, items)).build();
    }

    @Override
    public Response operatorImportThemes(ThemeSnapshotDTOV1 request) {
        List<Theme> themes = new ArrayList<>();
        List<Image> images = new ArrayList<>();
        request.getThemes().forEach((name, dto) -> {
            var theme = mapper.create(name, dto);
            theme.setOperator(true);
            themes.add(theme);
            var items = mapper.createImages(name, dto.getImages());
            items.forEach(x -> x.setOperator(true));
            images.addAll(items);
        });
        themeService.importOperator(themes, images);
        return Response.ok().build();
    }

    @ServerExceptionMapper
    public RestResponse<EximProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
