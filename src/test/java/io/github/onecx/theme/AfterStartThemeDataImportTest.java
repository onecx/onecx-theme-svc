package io.github.onecx.theme;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("Theme data import test from example file")
class AfterStartThemeDataImportTest extends AbstractTest {

    @Inject
    ThemeDAO dao;

    @Test
    @DisplayName("Import theme data from file")
    void importDataFromFileTest() {
        var data = dao.findAll().toList();
        assertThat(data).isNotNull().hasSize(2);
        assertThat(data.get(0)).isNotNull();
    }

}
