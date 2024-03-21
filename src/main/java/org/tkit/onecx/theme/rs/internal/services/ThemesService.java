package org.tkit.onecx.theme.rs.internal.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ThemesService {

    @Inject
    ThemeDAO dao;

    @Inject
    ImageDAO imageDAO;

    @Transactional
    public void deleteTheme(String id) {

        var theme = dao.findById(id);

        if (theme != null) {
            dao.deleteQueryById(id);
            // workaround for images
            imageDAO.deleteQueryByRefId(theme.getName());
        }
    }

}
