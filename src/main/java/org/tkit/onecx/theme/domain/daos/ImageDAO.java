package org.tkit.onecx.theme.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.models.*;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

@ApplicationScoped
@Transactional
public class ImageDAO extends AbstractDAO<Image> {

    public Image findByRefIdAndRefType(String refId, String refType) {

        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = this.criteriaQuery();
            var root = cq.from(Image.class);

            cq.where(cb.and(cb.equal(root.get(Image_.refId), refId),
                    cb.equal(root.get(Image_.refType), refType)));

            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception ex) {
            throw new DAOException(ImageDAO.ErrorKeys.FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED, ex);
        }
    }

    public enum ErrorKeys {

        FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED,

    }
}
