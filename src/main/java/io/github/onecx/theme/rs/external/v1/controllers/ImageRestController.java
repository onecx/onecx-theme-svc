package io.github.onecx.theme.rs.external.v1.controllers;

import java.io.InputStream;

import jakarta.ws.rs.core.Response;

import gen.io.github.onecx.theme.rs.external.v1.ImageV1Api;

public class ImageRestController implements ImageV1Api {

    @Override
    public Response deleteImage(String imageId) {

        return null;
    }

    @Override
    public Response getImage(String imageId) {

        return null;
    }

    @Override
    public Response uploadImage(InputStream imageInputStream, String fileName) {

        return null;
    }

}
