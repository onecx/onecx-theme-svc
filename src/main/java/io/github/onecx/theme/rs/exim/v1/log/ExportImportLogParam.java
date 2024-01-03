package io.github.onecx.theme.rs.exim.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.theme.rs.exim.v1.model.ExportThemeRequestDTOV1;
import gen.io.github.onecx.theme.rs.exim.v1.model.ThemeSnapshotDTOV1;

@ApplicationScoped
public class ExportImportLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, ExportThemeRequestDTOV1.class, x -> x.getClass().getSimpleName()),
                item(10, ThemeSnapshotDTOV1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((ThemeSnapshotDTOV1) x).getId()));
    }
}
