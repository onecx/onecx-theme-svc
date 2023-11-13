package io.github.onecx.theme.rs.external.v1.mappers;

import java.util.List;

import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.rs.external.v1.model.RestExceptionDTOV1;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExceptionMapper {

    public RestResponse<RestExceptionDTOV1> exception(Exception ex) {
        log.error("Processing theme external v1 rest controller error: {}", ex.getMessage());

        if (ex instanceof DAOException de) {
            return RestResponse.status(Response.Status.BAD_REQUEST,
                    exception(de.getMessageKey().name(), ex.getMessage(), de.parameters));
        }
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
                exception("UNDEFINED_ERROR_CODE", ex.getMessage()));

    }

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "namedParameters", ignore = true)
    @Mapping(target = "removeNamedParametersItem", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTOV1 exception(String errorCode, String message);

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "namedParameters", ignore = true)
    @Mapping(target = "removeNamedParametersItem", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTOV1 exception(String errorCode, String message, List<Object> parameters);

}
