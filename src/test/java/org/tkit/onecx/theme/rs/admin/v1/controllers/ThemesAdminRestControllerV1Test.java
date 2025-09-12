package org.tkit.onecx.theme.rs.admin.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.admin.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ThemesAdminRestControllerV1.class)
@WithDBData(value = "data/testdata-admin.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-th:admin-read", "ocx-th:admin-write",
        "ocx-th:admin-delete" })
class ThemesAdminRestControllerV1Test extends AbstractTest {

    @Test
    void createNewThemeTest() {

        // create theme
        var createThemeRequestDTO = new CreateThemeRequestDTOAdminV1();
        var themeDto = new CreateThemeDTOAdminV1().name("test01").displayName("test01");
        themeDto.setCssFile("cssFile");
        themeDto.setDescription("description");
        themeDto.setAssetsUrl("assets/url");
        themeDto.setPreviewImageUrl("image/url");

        createThemeRequestDTO.setResource(themeDto);

        var res = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .body(createThemeRequestDTO)
                .post()
                .then().log().all().statusCode(CREATED.getStatusCode())
                .extract().as(CreateThemeResponseDTOAdminV1.class);

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .get(res.getResource().getId())
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(ThemeDTOAdminV1.class);

        assertThat(dto).isNotNull()
                .returns(themeDto.getName(), from(ThemeDTOAdminV1::getName))
                .returns(themeDto.getDescription(), from(ThemeDTOAdminV1::getDescription))
                .returns(themeDto.getAssetsUrl(), from(ThemeDTOAdminV1::getAssetsUrl))
                .returns(themeDto.getPreviewImageUrl(), from(ThemeDTOAdminV1::getPreviewImageUrl));

        // create theme without body
        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTOAdminV1.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createNewTheme.createThemeRequestDTOAdminV1: must not be null");

        // create theme with existing name
        themeDto = new CreateThemeDTOAdminV1();
        themeDto.setName("cg");
        themeDto.setDisplayName("cg-display");
        createThemeRequestDTO.setResource(themeDto);

        exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).when()
                .contentType(APPLICATION_JSON)
                .body(createThemeRequestDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTOAdminV1.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'theme_name'  Detail: Key (name, tenant_id)=(cg, default) already exists.]");
    }

    @Test
    void deleteThemeTest() {

        // delete theme
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "DELETE_1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if theme exists
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "DELETE_1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete theme in portal
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "11-111")
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void getThemeByIdTest() {

        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "22-222")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTOAdminV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");
        assertThat(dto.getId()).isEqualTo("22-222");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "___")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "11-111")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTOAdminV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("cg");
        assertThat(dto.getId()).isEqualTo("11-111");

    }

    @Test
    void searchThemesTest() {
        var criteria = new ThemeSearchCriteriaDTOAdminV1();

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTOAdminV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName(" ");
        data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTOAdminV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName("cg");
        data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTOAdminV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

    }

    @Test
    void updateThemeTest() {

        var updateThemeRequestDTO = new UpdateThemeRequestDTOAdminV1();
        // update none existing theme
        var themeDto = new UpdateThemeDTOAdminV1();
        themeDto.setName("test01");
        themeDto.setDisplayName("test01");
        themeDto.setModificationCount(0);
        themeDto.setDescription("description-update");

        updateThemeRequestDTO.setResource(themeDto);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateThemeRequestDTO)
                .when()
                .pathParam("id", "does-not-exists")
                .put("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // update theme
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateThemeRequestDTO)
                .when()
                .pathParam("id", "11-111")
                .put("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // download theme
        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient")).contentType(APPLICATION_JSON)
                .body(themeDto)
                .when()
                .pathParam("id", "11-111")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTOAdminV1.class);

        // update theme with wrong modificationCount
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateThemeRequestDTO)
                .when()
                .pathParam("id", "11-111")
                .put("{id}")
                .then().statusCode(BAD_REQUEST.getStatusCode());

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo(themeDto.getDescription());

    }

    @Test
    void updateThemeWithExistingNameTest() {

        var updateThemeRequestDTO = new UpdateThemeRequestDTOAdminV1();

        var themeDto = new UpdateThemeDTOAdminV1();
        themeDto.setName("themeWithoutPortal");
        themeDto.setDisplayName("themeWithoutPortal");
        themeDto.setModificationCount(0);
        themeDto.setDescription("description");

        updateThemeRequestDTO.setResource(themeDto);

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .when()
                .body(updateThemeRequestDTO)
                .pathParam("id", "11-111")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTOAdminV1.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("MERGE_ENTITY_FAILED", exception.getErrorCode());
        Assertions.assertEquals(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'theme_name'  Detail: Key (name, tenant_id)=(themeWithoutPortal, default) already exists.]",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertTrue(exception.getInvalidParams().isEmpty());
    }

    @Test
    void updateThemeWithoutBodyTest() {

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTOAdminV1.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateTheme.updateThemeRequestDTOAdminV1: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }
}
