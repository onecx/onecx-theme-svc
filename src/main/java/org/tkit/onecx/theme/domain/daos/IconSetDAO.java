package org.tkit.onecx.theme.domain.daos;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.theme.domain.models.IconSet;
import org.tkit.onecx.theme.domain.models.IconSet_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
public class IconSetDAO extends AbstractDAO<IconSet> {

    public IconSet findByPrefixAndRefId(String prefix, String refId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(IconSet.class);
            var root = cq.from(IconSet.class);
            cq.where(cb.and(cb.equal(root.get(IconSet_.prefix), prefix), cb.equal(root.get(IconSet_.refId), refId)));

            return this.getEntityManager().createQuery(cq).getSingleResult();

        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ICONSET_BY_REFID_AND_PREFIX, ex);
        }
    }

    public List<IconSet> findByRefId(String refId) {
        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(IconSet.class);
            var root = cq.from(IconSet.class);
            cq.where(cb.equal(root.get(IconSet_.refId), refId));

            return this.getEntityManager().createQuery(cq).getResultList();

        } catch (Exception ex) {
            throw new DAOException(ErrorKeys.ERROR_FIND_ICONSET_BY_REFID, ex);
        }
    }

    public enum ErrorKeys {
        ERROR_FIND_ICONSET_BY_REFID_AND_PREFIX,
        ERROR_FIND_ICONSET_BY_REFID
    }
}
