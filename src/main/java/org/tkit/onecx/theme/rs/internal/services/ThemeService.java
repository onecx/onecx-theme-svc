package org.tkit.onecx.theme.rs.internal.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.models.Theme;

@ApplicationScoped
public class ThemeService {
    @Inject
    ThemeDAO themeDAO;

    @Transactional
    public void updateTheme(Theme theme) {
        themeDAO.update(theme);
    }
}
