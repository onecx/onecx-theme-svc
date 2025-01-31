package org.tkit.onecx.theme.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tkit.onecx.theme.rs.internal.mappers.ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.image.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.image.rs.internal.model.MimeTypeDTO;
import gen.org.tkit.onecx.image.rs.internal.model.RefTypeDTO;
import gen.org.tkit.onecx.theme.rs.internal.model.ProblemDetailResponseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImageRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-th:all", "ocx-th:read", "ocx-th:write", "ocx-th:delete" })
class ImageRestControllerTest extends AbstractTest {

    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";
    private static final String MEDIA_TYPE_IMAGE_JPG = "image/jpg";

    private static final File FILE = new File(
            Objects.requireNonNull(ImageRestControllerTest.class.getResource("/images/Testimage.png")).getFile());

    @Test
    void uploadImage() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .pathParam("refType", RefTypeDTO.LOGO.toString())
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

    }

    @Test
    void uploadImageEmptyBody() {

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "themeName")
                .pathParam("refType", RefTypeDTO.LOGO.toString())
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo("uploadImage.contentLength: must be greater than or equal to 1");
    }

    @Test
    void uploadImage_shouldReturnBadRequest_whenImageIs() {

        var refId = "themeNameUpload";
        var refType = RefTypeDTO.LOGO;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode());

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'image_constraints'  Detail: Key (ref_id, ref_type, tenant_id)=(themeNameUpload, logo, default) already exists.]");
    }

    @Test
    void getImagePngTest() {

        var refId = "themPngTest";
        var refType = RefTypeDTO.FAVICON;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode());

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get("/{refType}")
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_PNG)
                .extract().body().asByteArray();

        assertThat(data).isNotNull().isNotEmpty();
    }

    @Test
    void getImageJpgTest() {

        var refId = "nameJpg";
        var refType = RefTypeDTO.LOGO;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.JPG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_JPG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode());

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get("/{refType}")
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_JPG)
                .extract().body().asByteArray();

        assertThat(data).isNotNull().isNotEmpty();
    }

    @Test
    void getImageTest_shouldReturnNotFound_whenImagesDoesNotExist() {

        var refId = "themeNameGetTest";
        var refType = RefTypeDTO.FAVICON;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode());

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId + "_not_exists")
                .pathParam("refType", refType)
                .get("/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateImage() {

        var refId = "themeUpdateTest";
        var refType = RefTypeDTO.LOGO;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .put("/{refType}/{mimeType}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "does-not-exists")
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .put("/{refType}/{mimeType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void deleteImage() {

        var refId = "themeDeleteTest";
        var refType = RefTypeDTO.LOGO;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .delete("/{refType}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        Assertions.assertNotNull(res);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get("/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void deleteImagesById() {

        var refId = "themedeleteByIdTest";
        var refType = RefTypeDTO.LOGO;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var res = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .delete()
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        Assertions.assertNotNull(res);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get("/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void updateImage_returnNotFound_whenEntryNotExists() {

        var refId = "themeNameUpdateFailed";
        var refType = RefTypeDTO.LOGO;

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageInfoDTO.class);

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", "wrongRefId")
                .pathParam("refType", "wrongRefType")
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .put("/{refType}/{mimeType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        Assertions.assertNotNull(exception);

    }

    @Test
    void testMaxUploadSize() {

        var refId = "themeMaxUpload";

        byte[] body = new byte[110001];
        new Random().nextBytes(body);

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .pathParam("mimeType", MimeTypeDTO.PNG)
                .when()
                .body(body)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post("/{refType}/{mimeType}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo(
                "uploadImage.contentLength: must be less than or equal to 110000");

    }
}
