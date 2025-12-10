package org.tkit.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tkit.onecx.theme.domain.services.IconService;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(IconRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-th:all", "ocx-th:read", "ocx-th:write", "ocx-th:delete" })
class IconRestControllerExceptionTest extends AbstractTest {
    @InjectMock
    IconService service;

    @BeforeEach
    void beforeAll() throws Exception {
        Mockito.doThrow(new IOException())
                .when(service).createIcons(Mockito.any(byte[].class), Mockito.any(String.class));
    }

    private static final File FILE = new File(
            Objects.requireNonNull(ImageRestControllerTest.class.getResource("/iconsets/mdi-test-iconset.json")).getFile());

    @Test
    void exceptionTest() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .when()
                .body(FILE)
                .contentType(APPLICATION_JSON)
                .post("/upload")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}
