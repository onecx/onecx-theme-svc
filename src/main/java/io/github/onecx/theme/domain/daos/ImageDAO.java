package io.github.onecx.theme.domain.daos;

import java.io.InputStream;
import java.sql.Blob;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.github.onecx.theme.domain.models.*;

@ApplicationScoped
@Transactional
public class ImageDAO extends AbstractDAO<Image> {

    public Image findByRefIdAndRefType(String refId, String refType) {

        try {
            var cb = this.getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Image.class);
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

    public Image createImage(Image image, InputStream inputStream) {
        try {
            image = this.create(image);
            Session session = getEntityManager().unwrap(Session.class);
            LobHelper lobHelper = session.getLobHelper();
            Blob blob = lobHelper.createBlob(inputStream, 1024L);
            int blobLength = (int) blob.length();
            image.setImageData(blob.getBytes(1, blobLength));

            this.getEntityManager().persist(image);
            this.getEntityManager().flush();

            return image;
        } catch (Exception ex) {
            throw new DAOException(ImageDAO.ErrorKeys.CREATE_ENTITY_FAILED, ex);
        }
    }

    public Image updateImage(Image image, InputStream inputStream) {

        try {
            Session session = getEntityManager().unwrap(Session.class);
            LobHelper lobHelper = session.getLobHelper();
            Blob blob = lobHelper.createBlob(inputStream, 2048L);
            int blobLength = (int) blob.length();
            image.setImageData(blob.getBytes(1, blobLength));

            this.getEntityManager().merge(image);
            this.getEntityManager().flush();
            return image;
        } catch (Exception ex) {
            throw new DAOException(ImageDAO.ErrorKeys.UPDATE_ENTITY_FAILED, ex);
        }
    }

    public enum ErrorKeys {
        UPDATE_ENTITY_FAILED,
        CREATE_ENTITY_FAILED,
        FIND_ENTITY_BY_REF_ID_REF_TYPE_FAILED,

    }
}
