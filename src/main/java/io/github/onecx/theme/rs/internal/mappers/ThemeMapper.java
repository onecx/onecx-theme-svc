package io.github.onecx.theme.rs.internal.mappers;

import java.util.List;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.theme.rs.internal.model.*;
import io.github.onecx.theme.domain.criteria.ThemeSearchCriteria;
import io.github.onecx.theme.domain.models.Theme;
import io.github.onecx.theme.domain.models.ThemeInfo;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ThemeMapper {

    @Inject
    ObjectMapper mapper;

    public abstract ThemeSearchCriteria map(ThemeSearchCriteriaDTO dto);

    public ThemeInfoListDTO mapInfoList(Stream<ThemeInfo> data) {
        ThemeInfoListDTO result = new ThemeInfoListDTO();
        result.setThemes(mapInfo(data));
        return result;
    }

    public abstract List<ThemeInfoDTO> mapInfo(Stream<ThemeInfo> page);

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract ThemePageResultDTO mapPage(PageResult<Theme> page);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "properties")
    @Mapping(target = "tenantId", ignore = true)
    public abstract Theme create(CreateThemeDTO object);

    public abstract List<ThemeDTO> map(Stream<Theme> entity);

    @Mapping(target = "properties", qualifiedByName = "propertiesJson")
    public abstract ThemeDTO map(Theme theme);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "properties")
    @Mapping(target = "tenantId", ignore = true)
    public abstract void update(UpdateThemeDTO themeDTO, @MappingTarget Theme entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "properties")
    @Mapping(target = "tenantId", ignore = true)
    public abstract Theme map(UpdateThemeDTO object);

    @Named("properties")
    public String mapToString(Object properties) {

        if (properties == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(properties);
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
            return mapper.readTree(jsonVar);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
