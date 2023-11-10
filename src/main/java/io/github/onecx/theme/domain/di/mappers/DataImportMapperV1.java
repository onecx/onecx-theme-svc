package io.github.onecx.theme.domain.di.mappers;

import java.util.List;

import jakarta.inject.Inject;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.theme.di.v1.model.DataImportThemeDTOV1;
import io.github.onecx.theme.domain.models.Theme;

@Mapper(uses = OffsetDateTimeMapper.class)
public abstract class DataImportMapperV1 {

    @Inject
    ObjectMapper mapper;

    @IterableMapping(qualifiedByName = "import")
    public abstract List<Theme> importThemes(List<DataImportThemeDTOV1> dto);

    @Named("import")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "properties", qualifiedByName = "properties")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    public abstract Theme theme(DataImportThemeDTOV1 dto);

    @Named("properties")
    String properties(Object properties) {
        if (properties == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
