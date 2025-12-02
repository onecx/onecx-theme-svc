package org.tkit.onecx.theme.rs.internal.mappers;

import java.util.List;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.theme.domain.criteria.ThemeSearchCriteria;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeInfo;
import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.rs.internal.model.*;

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
    @Mapping(target = "properties", qualifiedByName = "o2s")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    public abstract Theme create(CreateThemeDTO object);

    public abstract List<ThemeDTO> map(Stream<Theme> entity);

    @Mapping(target = "removeOverridesItem", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "s2o")
    @Mapping(target = "overrides", ignore = true)
    public abstract ThemeDTO map(Theme theme);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "o2s")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    public abstract void update(UpdateThemeDTO themeDTO, @MappingTarget Theme entity);

    @Mapping(target = "themeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract ThemeOverride map(ThemeOverrideDTO dto);

    public abstract ThemeOverrideDTO map(ThemeOverride override);

    @Named("o2s")
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

    @Named("s2o")
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
