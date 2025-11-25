package org.tkit.onecx.theme.domain.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "THEME_OVERRIDE", uniqueConstraints = {
        @UniqueConstraint(name = "OVERRIDE_TYPE", columnNames = { "THEME_ID", "TYPE" })
})
public class ThemeOverride implements Serializable {

    @Id
    @Column(name = "GUID")
    private String id = UUID.randomUUID().toString();

    @Column(name = "THEME_ID", insertable = false, updatable = false)
    private String themeId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "VALUE")
    private String value;
}
