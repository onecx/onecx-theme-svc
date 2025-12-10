package org.tkit.onecx.theme.rs.external.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeInfo;
import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.rs.external.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ThemeMapper {

    @Mapping(target = "removeOverridesItem", ignore = true)
    ThemeDTOV1 map(Theme theme);

    default ThemeInfoListDTOV1 mapInfoList(Stream<ThemeInfo> data) {
        var result = new ThemeInfoListDTOV1();
        result.setThemes(mapInfo(data));
        return result;
    }

    List<ThemeInfoDTOV1> mapInfo(Stream<ThemeInfo> page);

    @Mapping(target = "themeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    ThemeOverride map(ThemeOverrideDTOV1 dto);
}
