package io.github.onecx.theme.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.theme.rs.exim.v1.model.EximExportRequestDTOV1;
import gen.io.github.onecx.theme.rs.exim.v1.model.EximImportRequestDTOV1;
import gen.io.github.onecx.theme.rs.exim.v1.model.EximRestExceptionDTOV1;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ExportImportRestControllerV1.class)
@WithDBData(value = "data/testdata-exim.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ExportImportRestControllerV1Test extends AbstractTest {

    @Test
    void exportThemesTest() {

        var request = new EximExportRequestDTOV1();

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(EximImportRequestDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(3);

        request.setNames(new HashSet<>());
        dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(EximImportRequestDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(3);

        request.setNames(Set.of("cg", "themeWithoutPortal"));
        dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(EximImportRequestDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(2);
    }

    @Test
    void exportThemesWrongNamesTest() {

        var request = new EximExportRequestDTOV1();
        request.setNames(Set.of("does-not-exists"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void exportThemesEmptyBodyTest() {

        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post("export")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(EximRestExceptionDTOV1.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getMessage()).isEqualTo("exportThemes.eximExportRequestDTOV1: must not be null");
    }

    @Test
    void importThemesTest() {

    }
}
