package org.tkit.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.internal.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ThemesRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ThemesRestControllerTenantTest extends AbstractTest {

    @Test
    void createNewThemeTest() {

        // create theme
        var themeDto = new CreateThemeDTO();
        themeDto.setName("test01");
        themeDto.setDisplayName("test01");
        themeDto.setCssFile("cssFile");
        themeDto.setDescription("description");
        themeDto.setAssetsUrl("assets/url");
        themeDto.setPreviewImageUrl("image/url");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(themeDto)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ThemeDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .get(dto.getId())
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get(dto.getId())
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull()
                .returns(themeDto.getName(), from(ThemeDTO::getName))
                .returns(themeDto.getDescription(), from(ThemeDTO::getDescription))
                .returns(themeDto.getAssetsUrl(), from(ThemeDTO::getAssetsUrl))
                .returns(themeDto.getPreviewImageUrl(), from(ThemeDTO::getPreviewImageUrl));

        // create theme without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createNewTheme.createThemeDTO: must not be null");

        // create theme with existing name
        themeDto = new CreateThemeDTO();
        themeDto.setName("cg");
        themeDto.setDisplayName("cg_display");

        exception = given().when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(themeDto)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'theme_name'  Detail: Key (name, tenant_id)=(cg, tenant-100) already exists.]");
    }

    @Test
    void deleteThemeTest() {

        // delete entity with wrong tenant
        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .delete("t-DELETE_1")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // delete entity with wrong tenant still exists
        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .get("t-DELETE_1")
                .then().statusCode(OK.getStatusCode());

        // delete theme
        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .delete("t-DELETE_1")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if theme exists
        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .get("t-DELETE_1")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete theme in portal
        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .delete("t-11-111")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void getThemeByThemeDefinitionNameTest() {
        var dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("name", "themeWithoutPortal")
                .get("/name/{name}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");
        assertThat(dto.getId()).isEqualTo("t-22-222");

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("name", "themeWithoutPortal")
                .get("/name/{name}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getThemeByIdTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get("t-22-222")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");
        assertThat(dto.getId()).isEqualTo("t-22-222");

        given()
                .contentType(APPLICATION_JSON)
                .get("t-22-222")
                .then().statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get("t-11-111")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("cg");
        assertThat(dto.getId()).isEqualTo("t-11-111");

    }

    @Test
    void getThemesTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);

        data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

    }

    @Test
    void searchThemesTest() {
        var criteria = new ThemeSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);

        criteria.setName(" ");
        data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);

        criteria.setName("cg");
        data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

    }

    @Test
    void getThemeInfoListTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .get("info")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemeInfoListDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getThemes()).isNotNull().hasSize(1);
    }

    @Test
    void updateThemeTest() {

        // update none existing theme
        var themeDto = new UpdateThemeDTO();
        themeDto.setName("test01");
        themeDto.setDisplayName("test01");
        themeDto.setModificationCount(0);
        themeDto.setDescription("description-update");

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .body(themeDto)
                .when()
                .put("t-11-111")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // update theme
        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(themeDto)
                .when()
                .put("t-11-111")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // download theme
        var dto = given().contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(themeDto)
                .when()
                .get("t-11-111")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo(themeDto.getDescription());

    }

    @Test
    void updateThemeWithExistingNameTest() {

        var themeDto = new UpdateThemeDTO();
        themeDto.setName("themeWithoutPortal");
        themeDto.setDisplayName("themeWithoutPortal");
        themeDto.setModificationCount(0);
        themeDto.setDescription("description");

        var exception = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .when()
                .body(themeDto)
                .put("t-11-111")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("MERGE_ENTITY_FAILED", exception.getErrorCode());
        Assertions.assertEquals(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'theme_name'  Detail: Key (name, tenant_id)=(themeWithoutPortal, tenant-100) already exists.]",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertTrue(exception.getInvalidParams().isEmpty());

    }

    @Test
    void updateThemeWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .when()
                .put("update_create_new")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateTheme.updateThemeDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }

}
