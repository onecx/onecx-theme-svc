package io.github.onecx.theme.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.io.github.onecx.theme.rs.external.v1.model.RestExceptionDTOV1;
import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ThemesRestControllerV1.class)
class ThemesRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    ThemeDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findAllInfos())
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(ErrorKey.ERROR_TEST, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {
        var exception = given()
                .contentType(APPLICATION_JSON)
                .get("info")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .extract().as(RestExceptionDTOV1.class);

        assertThat(exception.getErrorCode()).isEqualTo("UNDEFINED_ERROR_CODE");

        exception = given()
                .contentType(APPLICATION_JSON)
                .get("info")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTOV1.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorKey.ERROR_TEST.name());
    }

    public enum ErrorKey {
        ERROR_TEST;
    }
}
