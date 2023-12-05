package io.github.onecx.theme.rs.internal.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.theme.rs.internal.model.CreateThemeDTO;
import gen.io.github.onecx.theme.rs.internal.model.ThemeSearchCriteriaDTO;
import gen.io.github.onecx.theme.rs.internal.model.UpdateThemeDTO;

@ApplicationScoped
public class ThemesLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, CreateThemeDTO.class, x -> x.getClass().getSimpleName() + ":" + ((CreateThemeDTO) x).getName()),
                item(10, ThemeSearchCriteriaDTO.class, x -> {
                    ThemeSearchCriteriaDTO d = (ThemeSearchCriteriaDTO) x;
                    return ThemeSearchCriteriaDTO.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize() + "]";
                }),
                item(10, UpdateThemeDTO.class, x -> x.getClass().getSimpleName() + ":" + ((UpdateThemeDTO) x).getName()));
    }
}
