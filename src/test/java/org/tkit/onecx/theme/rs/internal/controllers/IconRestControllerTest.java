package org.tkit.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconCriteriaDTO;
import gen.org.tkit.onecx.theme.rs.icon.internal.model.IconListResponseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(IconRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-th:all", "ocx-th:read", "ocx-th:write", "ocx-th:delete" })
class IconRestControllerTest extends AbstractTest {

    private static final File FILE = new File(
            Objects.requireNonNull(ImageRestControllerTest.class.getResource("/iconsets/mdi-test-iconset.json")).getFile());

    @Test
    void uploadIconSetAndRetrieveIcons_Test() {

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .when()
                .body(FILE)
                .contentType(APPLICATION_JSON)
                .post("/upload")
                .then()
                .statusCode(CREATED.getStatusCode());

        //duplicated upload => duplicated key exception
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .when()
                .body(FILE)
                .contentType(APPLICATION_JSON)
                .post("/upload")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

        var iconCriteria = new IconCriteriaDTO();
        iconCriteria.getNames().add("mdi:ab-testing");
        iconCriteria.getNames().add("mdi:abacus");
        iconCriteria.getNames().add("mdi:car-tyre-warning");
        var output = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .when()
                .body(iconCriteria)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(IconListResponseDTO.class);

        assertThat(output).isNotNull();
        assertThat(3).isEqualTo(output.getIcons().size());

        //constraint exception, missing criteria
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .when()
                .body(new IconCriteriaDTO())
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}
