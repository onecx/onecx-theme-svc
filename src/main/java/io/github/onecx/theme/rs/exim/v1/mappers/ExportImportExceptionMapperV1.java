package io.github.onecx.theme.rs.exim.v1.mappers;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.rs.exim.v1.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExportImportExceptionMapperV1 {

    public RestResponse<EximRestExceptionDTOV1> constraint(ConstraintViolationException ex) {
        log.error("Processing theme export import rest controller error: {}", ex.getMessage());

        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setValidations(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    public RestResponse<EximRestExceptionDTOV1> exception(Exception ex) {
        log.error("Processing theme export import rest controller error: {}", ex.getMessage());

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
    public abstract EximRestExceptionDTOV1 exception(String errorCode, String message);

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "namedParameters", ignore = true)
    @Mapping(target = "removeNamedParametersItem", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract EximRestExceptionDTOV1 exception(String errorCode, String message, List<Object> parameters);

    public abstract List<EximValidationConstraintDTOV1> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "parameter", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract EximValidationConstraintDTOV1 createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}
