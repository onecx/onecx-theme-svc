package io.github.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.image.rs.internal.model.ImageInfoDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
//@TestHTTPEndpoint(ImageRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ImageRestControllerTest {

    @Test
    void uploadImage() {

        var refId = "themeName";
        var refType = "LOGO";
        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var imgPost = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);
    }

    @Test
    void uploadImage_shouldReturnBadRequest_whenImageIs() {

        var refId = "themeNameUpload";
        var refType = "LOGO";
        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var imgPost = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void getImageTest() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var refId = "themeNameGetTest";
        var refType = "FAVICON";

        given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void updateImage() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var refId = "themeNameUpload";
        var refType = "LOGO";

        given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .put("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);
    }

    @Test
    void updateImage_returnNotFound_whenEntryNotExists() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var refId = "themeNameUpdateFailed";
        var refType = "LOGO";

        given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var exception = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", "wrongRefId")
                .pathParam("refType", "wrongRefType")
                .when()
                .put("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        Assertions.assertNotNull(exception);
    }

    //    @Test
    //    void updateImage_returnBadRequest_whenRefTypeNull() {
    //
    //        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
    //        var refId = "themeNameUpdateFailed";
    //        var refType = "LOGO";
    //
    //        given()
    //                .multiPart("image", file)
    //                .contentType("multipart/form-data")
    //                .pathParam("refId", refId)
    //                .pathParam("refType", refType)
    //                .when()
    //                .post("/internal/images/{refId}/{refType}")
    //                .then()
    //                .statusCode(CREATED.getStatusCode())
    //                .extract()
    //                .body().as(ImageInfoDTO.class);
    //
    //        var exception = given()
    //                .multiPart("image", file)
    //                .contentType("multipart/form-data")
    //                .pathParam("refId", refId)
    //                .when()
    //                .put("/internal/images/{refId}")
    //                .then()
    //                .statusCode(BAD_REQUEST.getStatusCode())
    //                .extract().as(ProblemDetailResponseDTO.class);
    //
    //        Assertions.assertNotNull(exception);
    //        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
    //        Assertions.assertNotNull(exception.getInvalidParams());
    //        Assertions.assertEquals(1, exception.getInvalidParams().size());
    //    }

}
