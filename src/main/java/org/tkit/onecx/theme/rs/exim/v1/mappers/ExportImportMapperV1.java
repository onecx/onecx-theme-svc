package org.tkit.onecx.theme.rs.exim.v1.mappers;

import java.time.OffsetDateTime;
import java.util.*;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.rs.exim.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExportImportMapperV1 {

    @Inject
    ObjectMapper objectMapper;

    public List<Image> createImages(String themeName, Map<String, ImageDTOV1> images) {
        if (images == null) {
            return List.of();
        }
        List<Image> result = new ArrayList<>();
        images.forEach((refType, dto) -> result.add(createImage(themeName, refType, dto)));
        return result;
    }

    public Image updateImage(Image image, ImageDTOV1 dto) {
        image.setImageData(dto.getImageData());
        image.setMimeType(dto.getMimeType());
        image.setLength(length(dto.getImageData()));
        return image;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "length", source = "dto.imageData", qualifiedByName = "length")
    public abstract Image createImage(String refId, String refType, ImageDTOV1 dto);

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "themes", source = "themes")
    @Mapping(target = "removeThemesItem", ignore = true)
    public abstract ImportThemeResponseDTOV1 create(ThemeSnapshotDTOV1 request,
            Map<String, ImportThemeResponseStatusDTOV1> themes);

    public ThemeSnapshotDTOV1 create(Map<String, Theme> data, List<Image> images) {
        if (data == null) {
            return null;
        }
        var imagesMap = createImages(images);

        ThemeSnapshotDTOV1 result = new ThemeSnapshotDTOV1();
        result.setId(UUID.randomUUID().toString());
        result.setCreated(OffsetDateTime.now());
        result.setThemes(map(data, imagesMap));
        return result;
    }

    public Map<String, EximThemeDTOV1> map(Map<String, Theme> data, Map<String, Map<String, ImageDTOV1>> images) {
        if (data == null) {
            return Map.of();
        }

        Map<String, EximThemeDTOV1> map = new HashMap<>();
        data.forEach((name, value) -> {
            EximThemeDTOV1 dto = map(value);
            dto.setImages(images.get(name));
            map.put(name, dto);
        });
        return map;
    }

    Map<String, Map<String, ImageDTOV1>> createImages(List<Image> images) {
        if (images == null) {
            return Map.of();
        }
        Map<String, Map<String, ImageDTOV1>> result = new HashMap<>();
        images.forEach(image -> result.computeIfAbsent(image.getRefId(), k -> new HashMap<>())
                .put(image.getRefType(), createImage(image)));
        return result;
    }

    public abstract ImageDTOV1 createImage(Image image);

    @Mapping(target = "removeOverridesItem", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "s2o")
    @Mapping(target = "removeImagesItem", ignore = true)
    @Mapping(target = "images", ignore = true)
    public abstract EximThemeDTOV1 map(Theme theme);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "o2s")
    public abstract void update(EximThemeDTOV1 dto, @MappingTarget Theme entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "o2s")
    @Mapping(target = "displayName", source = "dto.displayName", defaultExpression = "java(name)")
    public abstract Theme create(String name, EximThemeDTOV1 dto);

    @Mapping(target = "themeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract ThemeOverride map(EximThemeOverrideDTOV1 dto);

    public abstract EximThemeOverrideDTOV1 map(ThemeOverride override);

    @Named("o2s")
    public String mapToString(Object properties) {

        if (properties == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("s2o")
    public Object stringToObject(String jsonVar) {

        if (jsonVar == null || jsonVar.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readTree(jsonVar);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("length")
    public Integer length(byte[] data) {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    public static String imageId(Image image) {
        return imageId(image.getRefId(), image.getRefType());
    }

    public static String imageId(String refId, String refType) {
        return refId + "#" + refType;
    }
}
