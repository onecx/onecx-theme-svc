package org.tkit.onecx.theme.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

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
class ExportImportRestControllerV1TenantTest extends AbstractTest {

    @Test
    void exportThemesTest() {

        var request = new ExportThemeRequestDTOV1();

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(2);

        request.setNames(new HashSet<>());
        dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(1);

        request.setNames(Set.of("cg", "themeWithoutPortal"));
        dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(dto).isNotNull();
        assertThat(dto.getThemes()).hasSize(2);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .body(request)
                .post("export")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void exportThemesWrongNamesTest() {

        var request = new ExportThemeRequestDTOV1();
        request.setNames(Set.of("does-not-exists"));

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
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
                .header(APM_HEADER_PARAM, createToken("org1"))
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
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(request)
                .post("export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ThemeSnapshotDTOV1.class);
        assertThat(data).isNotNull();
        assertThat(data.getThemes()).hasSize(2);

        var importTheme = new EximThemeDTOV1();
        importTheme.setDescription("new theme description");
        data.getThemes().put("importTheme", importTheme);

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(data)
                .post("import")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImportThemeResponseDTOV1.class);

        assertThat(dto).isNotNull().returns(data.getId(), from(ImportThemeResponseDTOV1::getId));

        assertThat(dto.getThemes()).isNotNull().hasSize(3);
        assertThat(dto.getThemes().get("cg")).returns(ImportThemeResponseStatusDTOV1.UPDATE.toString(),
                from(ImportThemeResponseStatusDTOV1::toString));
        assertThat(dto.getThemes().get("importTheme")).returns(ImportThemeResponseStatusDTOV1.CREATED.toString(),
                from(ImportThemeResponseStatusDTOV1::toString));
    }

}
