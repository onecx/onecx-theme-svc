package io.github.onecx.theme.rs.internal.mappers;

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

import gen.io.github.onecx.theme.rs.internal.model.RestExceptionDTO;
import gen.io.github.onecx.theme.rs.internal.model.ValidationConstraintDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExceptionMapper {

    public RestResponse<RestExceptionDTO> constraint(ConstraintViolationException ex) {

        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setValidations(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    public RestResponse<RestExceptionDTO> exception(Exception ex) {
        log.error("Processing portal internal rest controller error: {}", ex.getMessage());

        ex.printStackTrace();
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
    public abstract RestExceptionDTO exception(String errorCode, String message);

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "namedParameters", ignore = true)
    @Mapping(target = "removeNamedParametersItem", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTO exception(String errorCode, String message, List<Object> parameters);

    public abstract List<ValidationConstraintDTO> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "parameter", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ValidationConstraintDTO createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}
