package org.tkit.onecx.theme.rs.external.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeInfo;
import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeDTOV1;
import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeInfoDTOV1;
import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeInfoListDTOV1;
import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeOverrideDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ThemeMapper {

    @Mapping(target = "removeOverridesItem", ignore = true)
    public abstract ThemeDTOV1 map(Theme theme);

    public ThemeInfoListDTOV1 mapInfoList(Stream<ThemeInfo> data) {
        var result = new ThemeInfoListDTOV1();
        result.setThemes(mapInfo(data));
        return result;
    }

    public abstract List<ThemeInfoDTOV1> mapInfo(Stream<ThemeInfo> page);

    @Mapping(target = "themeId", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract ThemeOverride map(ThemeOverrideDTOV1 dto);
}
