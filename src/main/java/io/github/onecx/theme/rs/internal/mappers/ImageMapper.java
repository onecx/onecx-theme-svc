package io.github.onecx.theme.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.rs.external.v1.model.ImageInfoDTOV1;
import io.github.onecx.theme.domain.models.Image;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface ImageMapper {

    ImageInfoDTOV1 map(Image image);
}
