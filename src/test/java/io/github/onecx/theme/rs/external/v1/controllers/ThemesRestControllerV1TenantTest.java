package io.github.onecx.theme.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.theme.rs.external.v1.model.ThemeDTOV1;
import gen.io.github.onecx.theme.rs.external.v1.model.ThemeInfoListDTOV1;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestHTTPEndpoint(ThemesRestControllerV1.class)
@WithDBData(value = "data/testdata-external-tenant.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@TestProfile(AbstractTest.TenantTestProfile.class)
class ThemesRestControllerV1TenantTest extends AbstractTest {

    @Test
    void getThemeByThemeDefinitionNameTest() {
        var dto = given()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "themeWithoutPortal")
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get("/name/{name}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ThemeDTOV1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("themeWithoutPortal");
        assertThat(dto.getId()).isEqualTo("22-222");

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("name", "themeWithoutPortal")
                .get("/name/{name}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getThemeInfoListTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .get("info")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ThemeInfoListDTOV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getThemes()).isNotNull().hasSize(2);

        data = given()
                .contentType(APPLICATION_JSON)
                .get("info")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .as(ThemeInfoListDTOV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getThemes()).isNotNull().isEmpty();
    }
}
