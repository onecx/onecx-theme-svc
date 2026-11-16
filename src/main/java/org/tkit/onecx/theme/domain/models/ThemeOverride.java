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
        @UniqueConstraint(name = "OVERRIDE_TYPE", columnNames = { "THEME_ID", "POSITION", "TYPE" })
})
public class ThemeOverride implements Serializable {

    @Id
    @Column(name = "GUID")
    private String id = UUID.randomUUID().toString();

    @Column(name = "THEME_ID", insertable = false, updatable = false)
    private String themeId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "ALIAS")
    private String alias;

    @Column(name = "POSITION", nullable = false)
    private int position = 0;

    public void setPosition(Integer position) {
        this.position = position == null ? 0 : position;
    }

    @Convert(converter = Utf8BytesConverter.class)
    @Column(name = "VALUE", columnDefinition = "BYTEA")
    private String value;
}
