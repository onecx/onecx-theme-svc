package org.tkit.onecx.theme.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.exim.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ExportImportRestControllerV1.class)
@WithDBData(value = "data/testdata-exim.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ExportImportRestControllerV1Test extends AbstractTest {

    @Test
    void exportThemesTest() {

        var request = new ExportThemeRequestDTOV1();

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(3);

        dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(new ExportThemeRequestDTOV1().names(null))
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
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
                .extract().as(ThemeSnapshotDTOV1.class);
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
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(2);
    }

    @Test
    void exportThemesWrongNamesTest() {

        var request = new ExportThemeRequestDTOV1();
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
                .extract().as(EximProblemDetailResponseDTOV1.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("exportThemes.exportThemeRequestDTOV1: must not be null");
    }

    @Test
    void importThemesTest() {

        var request = new ThemeSnapshotDTOV1();

        var data = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(data).isNotNull();
        assertThat(data.getThemes()).hasSize(3);

        var new_theme = new EximThemeDTOV1();
        new_theme.setDescription("new theme description");
        new_theme.putImagesItem("logo", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        new_theme.putImagesItem("logo2", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        data.getThemes().put("new_theme", new_theme);
        // add new image to existing theme
        data.getThemes().get("cg").putImagesItem("logo2",
                new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post("import")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImportThemeResponseDTOV1.class);

        assertThat(dto).isNotNull().returns(data.getId(), from(ImportThemeResponseDTOV1::getId));

        assertThat(dto.getThemes()).isNotNull().hasSize(4);
        assertThat(dto.getThemes().get("cg")).returns(ImportThemeResponseStatusDTOV1.UPDATE.toString(),
                from(ImportThemeResponseStatusDTOV1::toString));
        assertThat(dto.getThemes().get("new_theme")).returns(ImportThemeResponseStatusDTOV1.CREATED.toString(),
                from(ImportThemeResponseStatusDTOV1::toString));
    }

    @Test
    void importOperatorThemesTest() {

        var request = new ThemeSnapshotDTOV1();

        var data = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(data).isNotNull();
        assertThat(data.getThemes()).hasSize(3);

        data.getThemes().remove("themeWithoutPortal");
        data.getThemes().remove("toDelete");

        var new_theme = new EximThemeDTOV1();
        new_theme.setDescription("new theme description");
        new_theme.putImagesItem("logo", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        new_theme.putImagesItem("logo2", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        data.getThemes().put("new_theme", new_theme);
        // add new image to existing theme
        data.getThemes().get("cg").putImagesItem("logo2",
                new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post("operator")
                .then()
                .statusCode(OK.getStatusCode());

        data = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(data).isNotNull();
        assertThat(data.getThemes()).hasSize(4);

        assertThat(data.getThemes().get("cg").getImages()).hasSize(3);
        assertThat(data.getThemes().get("new_theme")).isNotNull();
        assertThat(data.getThemes().get("new_theme").getImages()).hasSize(2);
    }

}
