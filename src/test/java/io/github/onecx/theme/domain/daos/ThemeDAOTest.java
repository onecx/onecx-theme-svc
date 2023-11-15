package io.github.onecx.theme.domain.daos;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.tkit.quarkus.jpa.exceptions.DAOException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ThemeDAOTest {
    @Inject
    ThemeDAO dao;

    @InjectMock
    EntityManager em;

    @BeforeEach
    void beforeAll() {
        Mockito.when(em.getCriteriaBuilder()).thenThrow(new RuntimeException("Test technical error exception"));
    }

    @Test
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findThemesByCriteria(null),
                ThemeDAO.ErrorKeys.ERROR_FIND_THEMES_BY_CRITERIA);
        methodExceptionTests(() -> dao.findThemeByName(null),
                ThemeDAO.ErrorKeys.ERROR_FIND_THEME_BY_NAME);
        methodExceptionTests(() -> dao.findThemeByNames(null),
                ThemeDAO.ErrorKeys.ERROR_FIND_THEME_BY_NAMES);
        methodExceptionTests(() -> dao.findAll(0, 2),
                ThemeDAO.ErrorKeys.ERROR_FIND_ALL_THEME_PAGE);
        methodExceptionTests(() -> dao.findAllInfos(),
                ThemeDAO.ErrorKeys.ERROR_FIND_ALL_THEME_INFO);
    }

    void methodExceptionTests(Executable fn, Enum<?> key) {
        var exc = Assertions.assertThrows(DAOException.class, fn);
        Assertions.assertEquals(key, exc.key);
    }
}
