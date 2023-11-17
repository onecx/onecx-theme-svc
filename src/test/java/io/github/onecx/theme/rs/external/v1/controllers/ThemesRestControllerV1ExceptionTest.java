package io.github.onecx.theme.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

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
                .thenThrow(new DAOException(ThemeDAO.ErrorKeys.ERROR_FIND_THEMES_BY_CRITERIA, new RuntimeException("Test")));
    }

    @Test
    void exceptionTest() {
        given()
                .contentType(APPLICATION_JSON)
                .get("info")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .get("info")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

    }

}
