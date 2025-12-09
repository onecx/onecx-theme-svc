package org.tkit.onecx.theme.domain.daos;

import java.util.Set;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.models.Icon;
import org.tkit.onecx.theme.domain.models.Icon_;
import org.tkit.onecx.theme.domain.models.Image_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
public class IconDAO extends AbstractDAO<Icon> {

    @Transactional(Transactional.TxType.SUPPORTS)
    public Stream<Icon> findIconsByNamesAndRefId(Set<String> iconNames, String refId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Icon.class);
            var root = cq.from(Icon.class);
            cq.where(cb.and(cb.equal(root.get(Icon_.REF_ID), refId), root.get(Icon_.name).in(iconNames)));
            return this.getEntityManager().createQuery(cq).getResultStream();

        } catch (Exception ex) {
            throw new DAOException(IconDAO.ErrorKeys.ERROR_FIND_ICONS_BY_NAMES_AND_REF_ID, ex);
        }
    }

    public Icon findByNameAndRefId(String parentName, String refId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Icon.class);
            var root = cq.from(Icon.class);
            cq.where(cb.and(cb.equal(root.get(Icon_.REF_ID), refId), cb.equal(root.get(Icon_.NAME), parentName)));
            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } catch (Exception e) {
            throw new DAOException(IconDAO.ErrorKeys.FIND_ENTITY_BY_PARENT_NAME_FAILED, e);
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public void deleteQueryByRefId(String refId) throws DAOException {
        try {
            var cq = deleteQuery();
            var root = cq.from(Icon.class);
            var cb = this.getEntityManager().getCriteriaBuilder();

            cq.where(cb.equal(root.get(Image_.REF_ID), refId));
            getEntityManager().createQuery(cq).executeUpdate();
            getEntityManager().flush();
        } catch (Exception e) {
            throw handleConstraint(e, IconDAO.ErrorKeys.FAILED_TO_DELETE_BY_REF_ID_QUERY);
        }
    }

    public enum ErrorKeys {
        ERROR_FIND_ICONS_BY_NAMES_AND_REF_ID,
        FIND_ENTITY_BY_PARENT_NAME_FAILED,
        FAILED_TO_DELETE_BY_REF_ID_QUERY
    }
}
