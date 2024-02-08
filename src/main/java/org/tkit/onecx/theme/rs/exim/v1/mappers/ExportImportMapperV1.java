package org.tkit.onecx.theme.rs.exim.v1.mappers;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.rs.exim.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExportImportMapperV1 {

    @Inject
    ObjectMapper objectMapper;

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "themes", source = "themes")
    public abstract ImportThemeResponseDTOV1 create(ThemeSnapshotDTOV1 request,
            Map<String, ImportThemeResponseStatusDTOV1> themes);

    public ThemeSnapshotDTOV1 create(Map<String, Theme> data) {
        if (data == null) {
            return null;
        }
        ThemeSnapshotDTOV1 result = new ThemeSnapshotDTOV1();
        result.setCreated(OffsetDateTime.now());
        result.setThemes(map(data));
        return result;
    }

    public abstract Map<String, EximThemeDTOV1> map(Map<String, Theme> data);

    @Mapping(target = "properties", qualifiedByName = "propertiesJson")
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
    @Mapping(target = "properties", qualifiedByName = "properties")
    public abstract void update(EximThemeDTOV1 dto, @MappingTarget Theme entity);

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
    @Mapping(target = "properties", qualifiedByName = "properties")
    public abstract Theme create(EximThemeDTOV1 dto);

    @Named("properties")
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

    @Named("propertiesJson")
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
}
