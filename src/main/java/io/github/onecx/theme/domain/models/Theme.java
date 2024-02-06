package io.github.onecx.theme.domain.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "THEME", uniqueConstraints = {
        @UniqueConstraint(name = "THEME_NAME", columnNames = { "NAME", "TENANT_ID" })
})
@SuppressWarnings("java:S2160")
public class Theme extends TraceableEntity {

    @Column(name = "NAME")
    private String name;

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "CSS_FILE")
    private String cssFile;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ASSETS_URL")
    private String assetsUrl;

    @Column(name = "LOGO_URL")
    private String logoUrl;

    @Column(name = "FAVICON_URL")
    private String faviconUrl;

    @Column(name = "PREVIEW_IMAGE_URL")
    private String previewImageUrl;

    @Column(name = "ASSETS_UPDATE_DATE")
    private LocalDateTime assetsUpdateDate;

    @Column(name = "PROPERTIES", columnDefinition = "TEXT")
    private String properties;
}
