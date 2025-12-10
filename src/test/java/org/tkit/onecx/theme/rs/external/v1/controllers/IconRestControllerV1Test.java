package org.tkit.onecx.theme.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.theme.rs.icon.v1.model.IconCriteriaDTOV1;
import gen.org.tkit.onecx.theme.rs.icon.v1.model.IconListResponseDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(IconRestControllerV1.class)
@WithDBData(value = "data/testdata-external.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-th:read" })
class IconRestControllerV1Test extends AbstractTest {

    @Test
    void retrieveIcons_Test() {

        var iconCriteria = new IconCriteriaDTOV1();
        iconCriteria.getNames().add("prime:icon1");
        iconCriteria.getNames().add("prime:icon2");
        iconCriteria.getNames().add("prime:icon3");
        iconCriteria.getNames().add("prime:icon4");

        var output = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "cg")
                .when()
                .body(iconCriteria)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(IconListResponseDTOV1.class);

        assertThat(output).isNotNull();
        assertThat(3).isEqualTo(output.getIcons().size());
    }

    @Test
    void retrieveIcons_missing_criteria_Test() {

        var iconCriteria = new IconCriteriaDTOV1();
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .when()
                .body(iconCriteria)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}
