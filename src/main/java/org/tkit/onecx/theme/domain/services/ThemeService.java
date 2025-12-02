package org.tkit.onecx.theme.domain.services;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.domain.models.Theme;

@ApplicationScoped
public class ThemeService {

    @Inject
    ThemeDAO themeDAO;

    @Inject
    ThemeDAO dao;

    @Inject
    ImageDAO imageDAO;

    @Transactional
    public void deleteTheme(String id) {

        var theme = dao.findById(id);
        if (theme != null && !Boolean.TRUE.equals(theme.getMandatory())) {
            dao.delete(theme);
            // workaround for images
            imageDAO.deleteQueryByRefId(theme.getName());
        }
    }

    @Transactional
    public void importThemes(List<Theme> create, List<Theme> update, List<Image> createImages, List<Image> updateImages) {
        imageDAO.update(updateImages);
        imageDAO.create(createImages);
        themeDAO.create(create);
        themeDAO.update(update);
    }

    @Transactional
    public void importOperator(List<Theme> themes, List<Image> images) {
        if (themes.isEmpty()) {
            return;
        }
        var names = themes.stream().map(Theme::getName).collect(Collectors.toSet());
        // delete existing data
        themeDAO.deleteQueryByNames(names);
        imageDAO.deleteQueryByRefIds(names);

        // create new data
        themeDAO.create(themes);
        imageDAO.create(images);
    }
}
