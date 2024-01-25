package io.github.onecx.theme.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.theme.domain.models.Image;

@ApplicationScoped
public class ImageDAO extends AbstractDAO<Image> {

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = DAOException.class)
    public Image createImage(Image entity) throws DAOException {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
        } catch (Exception e) {
            throw handleConstraint(e, Errors.PERSIST_ENTITY_FAILED);
        }
        return entity;
    }

}
