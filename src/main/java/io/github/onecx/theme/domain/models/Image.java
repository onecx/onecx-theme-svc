package io.github.onecx.theme.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "IMAGE")
@SuppressWarnings("squid:S2160")
public class Image extends TraceableEntity {

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "REF_TYPE")
    private String refType;

    @Column(name = "REF_ID")
    private String refID;

    @Lob
    private byte[] imageData;

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

}
