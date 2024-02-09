package org.tkit.onecx.theme.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeDTOV1;
import gen.org.tkit.onecx.theme.rs.external.v1.model.ThemeInfoListDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ThemesRestControllerV1.class)
@WithDBData(value = "data/testdata-external.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ThemesRestControllerV1Test extends AbstractTest {

    @Test
    void getThemeByThemeDefinitionNameTest() {
        var dto = given()
                .contentType(APPLICATION_JSON)
                .get("themeWithoutPortal")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");

        given()
                .contentType(APPLICATION_JSON)
                .get("none-exists")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getThemeInfoListTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemeInfoListDTOV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getThemes()).isNotNull().hasSize(3);
    }

    @Test
    void getThemeByThemeFaviconTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .get("test1/favicon")
                .then()
                .log().all()
                .statusCode(OK.getStatusCode())
                .contentType("image/x-icon")
                .extract()
                .body().asByteArray();

        assertThat(data).isNotNull();

        given()
                .contentType(APPLICATION_JSON)
                .get("none-exists/favicon")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }
}
