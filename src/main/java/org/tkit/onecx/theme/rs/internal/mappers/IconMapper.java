package org.tkit.onecx.theme.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.tkit.onecx.theme.domain.models.Icon;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconDTO;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface IconMapper {
    List<IconDTO> mapList(List<Icon> icons);

    IconDTO map(Icon icon);
}
