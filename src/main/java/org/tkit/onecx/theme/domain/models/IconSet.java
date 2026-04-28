package org.tkit.onecx.theme.domain.models;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ICON_SET", uniqueConstraints = {
        @UniqueConstraint(name = "ICON_SET_PREFIX", columnNames = { "PREFIX", "REF_ID", "TENANT_ID" })
})
@SuppressWarnings("squid:S2160")
public class IconSet extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "PREFIX")
    private String prefix;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "ICON_COUNT")
    private Integer iconCount;

    @OneToMany(cascade = { REMOVE, REFRESH, PERSIST, MERGE }, fetch = LAZY, orphanRemoval = true)
    @JoinColumn(name = "ICON_SET_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Icon> icons = new ArrayList<>();
}
