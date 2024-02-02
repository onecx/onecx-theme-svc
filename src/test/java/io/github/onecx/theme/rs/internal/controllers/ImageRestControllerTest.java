package io.github.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.theme.rs.internal.model.ImageInfoDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImageRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ImageRestControllerTest {

    @Test
    void uploadImage() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var imgPost = given()
                .multiPart("image", file)
                .multiPart("imageType", "LOGO")
                .multiPart("themeId", "11-111")
                .contentType("multipart/form-data")
                .when()
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .get(imgPost.getId())
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void updateImage() {
        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/cap_logo.png").getFile());

        var imgPost = given()
                .multiPart("image", file)
                .multiPart("imageType", "LOGO")
                .multiPart("themeId", "22-222")
                .contentType("multipart/form-data")
                .when()
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .when()
                .put(imgPost.getId())
                .then()
                .statusCode(OK.getStatusCode());
    }

}
