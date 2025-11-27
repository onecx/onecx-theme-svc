package org.tkit.onecx.theme.domain.daos;

import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.theme.domain.models.ThemeOverride;
import org.tkit.onecx.theme.domain.models.ThemeOverride_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
public class ThemeOverrideDAO extends AbstractDAO<ThemeOverride> {

    public Stream<ThemeOverride> findByThemeId(String themeId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(ThemeOverride.class);
            var root = cq.from(ThemeOverride.class);
            cq.where(cb.equal(root.get(ThemeOverride_.THEME_ID), themeId));
            return this.getEntityManager().createQuery(cq).getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ThemeOverrideDAO.ErrorKeys.ERROR_FIND_THEME_OVERRIDES_BY_THEME_ID, ex);
        }
    }

    public enum ErrorKeys {
        ERROR_FIND_THEME_OVERRIDES_BY_THEME_ID
    }

}
