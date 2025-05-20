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
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.exim.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ExportImportRestControllerV1.class)
@WithDBData(value = "data/testdata-exim.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-th:read", "ocx-th:write" })
class ExportImportRestControllerV1Test extends AbstractTest {

    @Test
    void exportThemesTest() {

        var request = new ExportThemeRequestDTOV1();

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(data).isNotNull();
        assertThat(data.getThemes()).hasSize(3);

        var importTheme = new EximThemeDTOV1();
        importTheme.setDescription("new theme description");
        importTheme.putImagesItem("logo", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        importTheme.putImagesItem("logo2", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        data.getThemes().put("importTheme", importTheme);
        // add new image to existing theme
        data.getThemes().get("cg").putImagesItem("logo2",
                new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
        assertThat(dto.getThemes().get("importTheme")).returns(ImportThemeResponseStatusDTOV1.CREATED.toString(),
                from(ImportThemeResponseStatusDTOV1::toString));
    }

    @Test
    void importThemeWithoutDisplayNameTest() {

        var request = new ThemeSnapshotDTOV1();

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(data).isNotNull();
        assertThat(data.getThemes()).hasSize(3);

        var importTheme = new EximThemeDTOV1();
        importTheme.setDescription("new theme description");
        importTheme.putImagesItem("logo", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        importTheme.putImagesItem("logo2", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        data.getThemes().put("themeWithoutDisplayName", importTheme);

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post("import")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImportThemeResponseDTOV1.class);

        assertThat(dto).isNotNull().returns(data.getId(), from(ImportThemeResponseDTOV1::getId));

        //check if fallback displayname was used
        var exportResponse = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);

        assertThat(exportResponse).isNotNull();
        assertThat(exportResponse.getThemes()).hasSize(4);
        assertThat(exportResponse.getThemes().get("themeWithoutDisplayName").getDisplayName())
                .isEqualTo("themeWithoutDisplayName");
    }

    @Test
    void importOperatorThemesTest() {

        var request = new ThemeSnapshotDTOV1();

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
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

        var importTheme = new EximThemeDTOV1();
        importTheme.setDescription("new theme description");
        importTheme.putImagesItem("logo", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        importTheme.putImagesItem("logo2", new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));
        data.getThemes().put("importTheme", importTheme);
        // add new image to existing theme
        data.getThemes().get("cg").putImagesItem("logo2",
                new ImageDTOV1().imageData(new byte[] { 1, 2, 3 }).mimeType("image/*"));

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(data)
                .post("operator")
                .then()
                .statusCode(OK.getStatusCode());

        data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
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
        assertThat(data.getThemes().get("importTheme")).isNotNull();
        assertThat(data.getThemes().get("importTheme").getImages()).hasSize(2);
    }

    @Test
    void importOperatorThemesEmptyTest() {

        var request = new ThemeSnapshotDTOV1();

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("operator")
                .then()
                .statusCode(OK.getStatusCode());

        request.setThemes(null);
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post("operator")
                .then()
                .statusCode(OK.getStatusCode());
    }

}
