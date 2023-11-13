package io.github.onecx.theme.domain.daos;

import java.util.Set;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.theme.domain.models.Theme;
import io.github.onecx.theme.domain.models.ThemeInfo;
import io.github.onecx.theme.domain.models.Theme_;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ThemeDAO extends AbstractDAO<Theme> {

    @Transactional(Transactional.TxType.SUPPORTS)
    public Stream<Theme> findThemeByNames(Set<String> themeNames) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Theme.class);
            var root = cq.from(Theme.class);

            if (themeNames != null && !themeNames.isEmpty()) {
                cq.where(root.get(Theme_.name).in(themeNames));
            }
            return this.getEntityManager().createQuery(cq).getResultStream();

        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_THEME_BY_NAMES, ex);
        }
    }

    public PageResult<Theme> findAll(Integer pageNumber, Integer pageSize) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Theme.class);
            cq.from(Theme.class);
            return createPageQuery(cq, Page.of(pageNumber, pageSize)).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_THEME_PAGE, ex);
        }
    }

    public Stream<ThemeInfo> findAllInfos() {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(ThemeInfo.class);
            var root = cq.from(Theme.class);
            cq.select(cb.construct(ThemeInfo.class, root.get(Theme_.NAME), root.get(Theme_.DESCRIPTION)));
            return this.getEntityManager().createQuery(cq).getResultStream();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ALL_THEME_INFO, ex);
        }
    }

    public Theme findThemeByName(String themeName) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Theme.class);
            var root = cq.from(Theme.class);
            cq.where(cb.equal(root.get(Theme_.name), themeName));

            return this.getEntityManager().createQuery(cq).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_THEME_BY_NAME, ex);
        }
    }

    public enum ErrorKeys {

        ERROR_FIND_ALL_THEME_INFO,
        ERROR_FIND_ALL_THEME_PAGE,
        ERROR_FIND_THEME_BY_NAMES,
        ERROR_FIND_THEME_BY_NAME,
    }
}
