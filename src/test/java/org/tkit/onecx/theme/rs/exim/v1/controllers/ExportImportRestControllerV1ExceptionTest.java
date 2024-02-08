package org.tkit.onecx.theme.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import gen.org.tkit.onecx.theme.rs.exim.v1.model.ExportThemeRequestDTOV1;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ExportImportRestControllerV1.class)
class ExportImportRestControllerV1ExceptionTest extends AbstractTest {

    @InjectMock
    ThemeDAO dao;

    @BeforeEach
    void beforeAll() {
        Mockito.when(dao.findThemeByNames(any()))
                .thenThrow(new RuntimeException("Test technical error exception"))
                .thenThrow(new DAOException(ThemeDAO.ErrorKeys.ERROR_FIND_THEMES_BY_CRITERIA, new RuntimeException("Test")));
    }

    @Test
    void exportThemesExceptionTest() {

        var request = new ExportThemeRequestDTOV1();

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode());

    }
}
