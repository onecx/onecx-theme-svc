package org.tkit.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import java.io.File;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.image.rs.internal.model.ImageInfoDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImageRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ImageRestControllerTest {

    @Test
    void uploadImage() {

        var refId = "themeName";
        var refType = "LOGO";
        File file = new File(ImageRestControllerTest.class.getResource("/images/Testimage.png").getFile());
        var imgPost = given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);
    }

    @Test
    void uploadImage_shouldReturnBadRequest_whenImageIs() {

        var refId = "themeNameUpload";
        var refType = "LOGO";
        File file = new File(ImageRestControllerTest.class.getResource("/images/Testimage.png").getFile());
        var imgPost = given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void getImageTest() {

        File file = new File(ImageRestControllerTest.class.getResource("/images/Testimage.png").getFile());
        var refId = "themeNameGetTest";
        var refType = "FAVICON";

        given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void getImageTest_shouldReturnNotFound_whenImagesDoesNotExist() {

        File file = new File(ImageRestControllerTest.class.getResource("/images/Testimage.png").getFile());
        var refId = "themeNameGetTest";
        var refType = "FAVICON";

        given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", "wrongRefType")
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateImage() {

        File file = new File(ImageRestControllerTest.class.getResource("/images/Testimage.png").getFile());
        var refId = "themeNameUpload";
        var refType = "LOGO";

        given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .put()
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);
    }

    @Test
    void updateImage_returnNotFound_whenEntryNotExists() {

        File file = new File(ImageRestControllerTest.class.getResource("/images/Testimage.png").getFile());
        var refId = "themeNameUpdateFailed";
        var refType = "LOGO";

        given()
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var exception = given()
                .pathParam("refId", "wrongRefId")
                .pathParam("refType", "wrongRefType")
                .when()
                .body(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .put()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        Assertions.assertNotNull(exception);
    }

}
