package io.github.onecx.theme.rs.exim.v1.mappers;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.theme.rs.exim.v1.model.*;
import io.github.onecx.theme.domain.models.Theme;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExportImportMapperV1 {

    @Inject
    ObjectMapper objectMapper;

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "themes", source = "themes")
    public abstract EximImportResultDTOV1 create(EximImportRequestDTOV1 request, Map<String, EximThemeResultDTOV1> themes);

    public abstract EximThemeResultDTOV1 create(EximThemeResultStatusDTOV1 status);

    public EximImportRequestDTOV1 create(Map<String, Theme> data) {
        if (data == null) {
            return null;
        }
        EximImportRequestDTOV1 result = new EximImportRequestDTOV1();
        result.setCreated(OffsetDateTime.now());
        result.setThemes(map(data));
        return result;
    }

    public abstract Map<String, EximThemeDTOV1> map(Map<String, Theme> data);

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
}
