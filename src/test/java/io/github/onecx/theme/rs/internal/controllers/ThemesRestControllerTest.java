package io.github.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.theme.rs.internal.model.*;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ThemesRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ThemesRestControllerTest extends AbstractTest {

    @Test
    void createNewThemeTest() {

        // create theme
        var themeDto = new CreateThemeDTO();
        themeDto.setName("test01");
        themeDto.setCssFile("cssFile");
        themeDto.setDescription("description");
        themeDto.setAssetsUrl("assets/url");
        themeDto.setPreviewImageUrl("image/url");

        var uri = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(themeDto)
                .post()
                .then().statusCode(CREATED.getStatusCode())
                .extract().header(HttpHeaders.LOCATION);

        var dto = given()
                .contentType(APPLICATION_JSON)
                .get(uri)
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
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createNewTheme.createThemeDTO: must not be null");

        // create theme with existing name
        themeDto = new CreateThemeDTO();
        themeDto.setName("cg");

        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(themeDto)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'theme_name'  Detail: Key (name, tenant_id)=(cg, default) already exists.]");
    }

    @Test
    void deleteThemeTest() {

        // delete theme
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "DELETE_1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if theme exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "DELETE_1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete theme in portal
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "11-111")
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

    }

    @Test
    void getThemeByThemeDefinitionNameTest() {
        var dto = given()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "themeWithoutPortal")
                .get("/name/{name}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");
        assertThat(dto.getId()).isEqualTo("22-222");

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "none-exists")
                .get("/name/{name}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getThemeByIdTest() {

        var dto = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "22-222")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");
        assertThat(dto.getId()).isEqualTo("22-222");

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "___")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "11-111")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("cg");
        assertThat(dto.getId()).isEqualTo("11-111");

    }

    @Test
    void getThemesTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

    }

    @Test
    void searchThemesTest() {
        var criteria = new ThemeSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName(" ");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName("cg");
        data = given()
                .contentType(APPLICATION_JSON)
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
                .get("info")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemeInfoListDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getThemes()).isNotNull().hasSize(3);
    }

    @Test
    void updateThemeTest() {

        // update none existing theme
        var themeDto = new UpdateThemeDTO();
        themeDto.setName("test01");
        themeDto.setDescription("description-update");

        given()
                .contentType(APPLICATION_JSON)
                .body(themeDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // update theme
        given()
                .contentType(APPLICATION_JSON)
                .body(themeDto)
                .when()
                .pathParam("id", "11-111")
                .put("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // download theme
        var dto = given().contentType(APPLICATION_JSON)
                .body(themeDto)
                .when()
                .pathParam("id", "11-111")
                .get("{id}")
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
        themeDto.setDescription("description");

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .body(themeDto)
                .pathParam("id", "11-111")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("MERGE_ENTITY_FAILED", exception.getErrorCode());
        Assertions.assertEquals(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'theme_name'  Detail: Key (name, tenant_id)=(themeWithoutPortal, default) already exists.]",
                exception.getDetail());
        Assertions.assertNull(exception.getInvalidParams());

    }

    @Test
    void updateThemeWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
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
