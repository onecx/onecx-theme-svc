package io.github.onecx.theme.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.theme.domain.models.*;

@ApplicationScoped
public class ImageDAO extends AbstractDAO<Image> {

    public Image findByRefIdAndRefType(String refID, String refType) {

        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Image.class);
            var root = cq.from(Image.class);

            cq.where(cb.equal(root.get(Image_.refID), refID), cb.equal(root.get(Image_.refType), refType));

            return this.getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            throw new DAOException(ImageDAO.ErrorKeys.FIND_ENTITY_BY_REF_ID_REF_TYPE_NO_RESULT, nre);
        } catch (Exception ex) {
            throw new DAOException(ImageDAO.ErrorKeys.FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED, ex);
        }
    }

    public enum ErrorKeys {
        FIND_ENTITY_BY_REF_ID_REF_TYPE_NO_RESULT,
        FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED,

    }
}
