package org.tkit.onecx.theme.rs.internal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.tkit.onecx.theme.domain.models.Icon;
import org.tkit.onecx.theme.domain.models.IconSet;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.rs.icon.internal.model.GetIconSetsResponseDTO;
import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconDTO;
import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconSetDTO;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface IconMapper {
    List<IconDTO> mapList(List<Icon> icons);

    IconDTO map(Icon icon);

    default GetIconSetsResponseDTO mapIconSets(List<IconSet> iconSets) {
        GetIconSetsResponseDTO response = new GetIconSetsResponseDTO();
        response.setIconSets(iconSets.stream().map(this::map).toList());
        return response;
    }

    IconSetDTO map(IconSet iconSet);

}
