package org.tkit.onecx.theme.domain.di.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.tkit.onecx.theme.domain.di.models.ExistingData;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.di.template.model.TemplateImageDTO;
import gen.org.tkit.onecx.theme.di.template.model.TemplateOverrideDTO;
import gen.org.tkit.onecx.theme.di.template.model.TemplateThemeDTO;

@Mapper(uses = OffsetDateTimeMapper.class)
public abstract class TemplateImportMapper {

    @Inject
    ObjectMapper mapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "o2s")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    public abstract Theme create(String name, TemplateThemeDTO dto);

    public List<Theme> create(ExistingData existingData, Map<String, TemplateThemeDTO> data) {
        return data.entrySet().stream().filter(e -> !existingData.isThemeInDb(e.getKey()))
                .map(e -> create(e.getKey(), e.getValue())).toList();
    }

    public List<Image> createImage(ExistingData existingData, Map<String, TemplateThemeDTO> data) {
        List<Image> result = new ArrayList<>();
        for (Map.Entry<String, TemplateThemeDTO> entry : data.entrySet()) {
            var images = entry.getValue().getImages();
            if (images == null) {
                continue;
            }

            result.addAll(
                    images.entrySet().stream()
                            .filter(e -> !existingData.isRefIdRefTypeInDb(entry.getKey(), e.getKey()))
                            .map(e -> create(entry.getKey(), e.getKey(), e.getValue()))
                            .toList());

        }
        return result;
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
    public abstract Image create(String refId, String refType, TemplateImageDTO dto);

    @Named("length")
    public Integer length(byte[] data) {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Named("o2s")
    public String properties(Object properties) {
        if (properties == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Mapping(target = "themeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract ThemeOverride map(TemplateOverrideDTO dto);

    public abstract TemplateOverrideDTO map(ThemeOverride override);

}
