package io.github.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.theme.rs.internal.model.RestExceptionDTO;
import gen.io.github.onecx.theme.rs.internal.model.ThemeDTO;
import gen.io.github.onecx.theme.rs.internal.model.UpdateThemeDTO;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

@QuarkusTest
@TestHTTPEndpoint(ThemesRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ThemesRestControllerTest extends AbstractTest {

    @Test
    void createNewThemeTest() {

        // create theme
        var themeDto = new ThemeDTO();
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

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(themeDto.getName());
        assertThat(dto.getName()).isEqualTo(themeDto.getName());
        assertThat(dto.getDescription()).isEqualTo(themeDto.getDescription());
        assertThat(dto.getAssetsUrl()).isEqualTo(themeDto.getAssetsUrl());
        assertThat(dto.getPreviewImageUrl()).isEqualTo(themeDto.getPreviewImageUrl());

        // create theme without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getMessage()).isEqualTo("createNewTheme.createThemeDTO: must not be null");

        // create theme with existing name
        themeDto = new ThemeDTO();
        themeDto.setName("cg");

        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(themeDto)
                .post()
                .then().log().all()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getMessage()).isEqualTo("Errors,key:PERSIST_ENTITY_FAILED,parameters:[Theme],namedParameters:{}");
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
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(new TypeRef<List<ThemeDTO>>() {
                });

        assertThat(data).hasSize(3);

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
                .then().log().all()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("MERGE_ENTITY_FAILED", exception.getErrorCode());
        Assertions.assertEquals("Errors,key:MERGE_ENTITY_FAILED,parameters:[Theme],namedParameters:{}",
                exception.getMessage());
        Assertions.assertNull(exception.getValidations());

    }

    @Test
    void updateThemeWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
                .then().log().all()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(RestExceptionDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateTheme.updateThemeDTO: must not be null",
                exception.getMessage());
        Assertions.assertNotNull(exception.getValidations());
        Assertions.assertEquals(1, exception.getValidations().size());
    }
}
