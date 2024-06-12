package org.tkit.onecx.theme.domain.daos;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.criteria.ThemeSearchCriteria;
import org.tkit.onecx.theme.domain.models.*;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;
import org.tkit.quarkus.jpa.models.TraceableEntity_;
import org.tkit.quarkus.jpa.utils.QueryCriteriaUtil;

@ApplicationScoped
public class ThemeDAO extends AbstractDAO<Theme> {

    // https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830
    @Override
    public Theme findById(Object id) throws DAOException {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Theme.class);
            var root = cq.from(Theme.class);
            cq.where(cb.equal(root.get(TraceableEntity_.ID), id));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new DAOException(ErrorKeys.FIND_ENTITY_BY_ID_FAILED, e, entityName, id);
        }
    }

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

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public void deleteQueryByNames(Collection<String> themeNames) throws DAOException {
        try {
            var cq = deleteQuery();
            var root = cq.from(Theme.class);
            cq.where(root.get(Theme_.NAME).in(themeNames));
            getEntityManager().createQuery(cq).executeUpdate();
            getEntityManager().flush();
        } catch (Exception e) {
            throw handleConstraint(e, ErrorKeys.ERROR_DELETE_QUERY_BY_NAMES);
        }
    }

    public List<String> findNamesByThemeByNames(Set<String> themeNames) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(String.class);
            var root = cq.from(Theme.class);
            cq.select(root.get(Theme_.name));
            cq.where(root.get(Theme_.name).in(themeNames));
            return this.getEntityManager().createQuery(cq).getResultList();

        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_NAMES_BY_NAMES, ex);
        }
    }

    public PageResult<Theme> findThemesByCriteria(ThemeSearchCriteria criteria) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Theme.class);
            var root = cq.from(Theme.class);

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                cq.where(cb.like(root.get(Theme_.name), QueryCriteriaUtil.wildcard(criteria.getName())));
            }
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_THEMES_BY_CRITERIA, ex);
        }
    }

    public PageResult<Theme> findAll(Integer pageNumber, Integer pageSize) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Theme.class);
            var root = cq.from(Theme.class);
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));
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

        ERROR_DELETE_QUERY_BY_NAMES,
        FIND_ENTITY_BY_ID_FAILED,
        ERROR_FIND_THEMES_BY_CRITERIA,
        ERROR_FIND_ALL_THEME_INFO,
        ERROR_FIND_ALL_THEME_PAGE,
        ERROR_FIND_NAMES_BY_NAMES,
        ERROR_FIND_THEME_BY_NAMES,
        ERROR_FIND_THEME_BY_NAME,
    }
}
