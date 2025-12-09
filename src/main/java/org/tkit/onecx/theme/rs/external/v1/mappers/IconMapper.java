package org.tkit.onecx.theme.rs.external.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.tkit.onecx.theme.domain.models.Icon;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.rs.icon.v1.model.IconDTOV1;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface IconMapper {
    List<IconDTOV1> mapList(List<Icon> icons);

    IconDTOV1 map(Icon icon);
}
