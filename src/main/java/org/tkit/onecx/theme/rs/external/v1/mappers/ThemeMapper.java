package org.tkit.onecx.theme.rs.external.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.domain.models.ThemeInfo;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeDTOV1;
import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeInfoDTOV1;
import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeInfoListDTOV1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ThemeMapper {

    public abstract ThemeDTOV1 map(Theme theme);

    public ThemeInfoListDTOV1 mapInfoList(Stream<ThemeInfo> data) {
        var result = new ThemeInfoListDTOV1();
        result.setThemes(mapInfo(data));
        return result;
    }

    public abstract List<ThemeInfoDTOV1> mapInfo(Stream<ThemeInfo> page);
}
