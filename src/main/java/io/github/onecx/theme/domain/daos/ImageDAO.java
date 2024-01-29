package io.github.onecx.theme.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import io.github.onecx.theme.domain.models.Image;

@ApplicationScoped
public class ImageDAO extends AbstractDAO<Image> {

}
