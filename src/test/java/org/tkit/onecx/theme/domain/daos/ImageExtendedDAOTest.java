package org.tkit.onecx.theme.domain.daos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.test.AbstractTest;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ImageExtendedDAOTest extends AbstractTest {
    @Inject
    ImageDAO dao;

    @Test
    void methodNoErrorTest() {
        assertDoesNotThrow(() -> dao.deleteQueryByRefId(null));
    }

}
