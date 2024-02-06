package io.github.onecx.theme.rs.external.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.rs.external.v1.model.ThemeDTOV1;
import gen.io.github.onecx.theme.rs.external.v1.model.ThemeInfoDTOV1;
import gen.io.github.onecx.theme.rs.external.v1.model.ThemeInfoListDTOV1;
import io.github.onecx.theme.domain.models.Theme;
import io.github.onecx.theme.domain.models.ThemeInfo;

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
