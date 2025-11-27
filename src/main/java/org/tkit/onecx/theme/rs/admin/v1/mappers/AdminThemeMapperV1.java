package org.tkit.onecx.theme.rs.admin.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.theme.domain.criteria.ThemeSearchCriteria;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.rs.admin.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class AdminThemeMapperV1 {

    @Inject
    ObjectMapper mapper;

    public abstract ThemeSearchCriteria map(ThemeSearchCriteriaDTOAdminV1 dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    public abstract ThemePageResultDTOAdminV1 mapPage(PageResult<Theme> page);

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
    public abstract Theme create(CreateThemeDTOAdminV1 object);

    public abstract List<ThemeDTOAdminV1> map(Stream<Theme> entity);

    @Mapping(target = "resource.properties", qualifiedByName = "s2o")
    @Mapping(target = "resource", source = "theme")
    public abstract CreateThemeResponseDTOAdminV1 mapCreate(Theme theme);

    @Mapping(target = "removeOverridesItem", ignore = true)
    @Mapping(target = "overrides", ignore = true)
    public abstract ThemeDTOAdminV1 map(Theme theme);

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
    public abstract void update(UpdateThemeDTOAdminV1 themeDTOAdminV1, @MappingTarget Theme entity);

    @Mapping(target = "themeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract ThemeOverride map(OverrideDTOAdminV1 dto);

    public abstract OverrideDTOAdminV1 map(ThemeOverride override);

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
