package org.tkit.onecx.theme.rs.admin.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.theme.rs.admin.v1.model.CreateThemeRequestDTOAdminV1;
import gen.org.tkit.onecx.theme.rs.admin.v1.model.ThemeSearchCriteriaDTOAdminV1;
import gen.org.tkit.onecx.theme.rs.admin.v1.model.UpdateThemeRequestDTOAdminV1;

@ApplicationScoped
public class AdminThemesLogParamV1 implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, CreateThemeRequestDTOAdminV1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((CreateThemeRequestDTOAdminV1) x).getResource().getName()),
                item(10, ThemeSearchCriteriaDTOAdminV1.class, x -> {
                    ThemeSearchCriteriaDTOAdminV1 d = (ThemeSearchCriteriaDTOAdminV1) x;
                    return ThemeSearchCriteriaDTOAdminV1.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }),
                item(10, UpdateThemeRequestDTOAdminV1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateThemeRequestDTOAdminV1) x).getResource().getName()));
    }
}
