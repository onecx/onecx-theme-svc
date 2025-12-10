package org.tkit.onecx.theme.domain.models;

import jakarta.persistence.*;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ICON", uniqueConstraints = {
        @UniqueConstraint(name = "ICON_NAME", columnNames = { "NAME", "REF_ID", "TENANT_ID" })
}, indexes = { @Index(columnList = "REF_ID", name = "ICON_REF_ID_IDX") })
@SuppressWarnings("squid:S2160")
public class Icon extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "BODY", columnDefinition = "TEXT")
    private String body;

    @Column(name = "PARENT")
    private String parent;
}
