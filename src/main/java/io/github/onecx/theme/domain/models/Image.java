package io.github.onecx.theme.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "IMAGE")
@SuppressWarnings("squid:S2160")
public class Image extends TraceableEntity {

    @Column(name = "HEIGHT")
    private Integer height;

    @Column(name = "WIDTH")
    private Integer width;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "URL")
    private String url;

    @Lob
    private byte[] imageData;

}