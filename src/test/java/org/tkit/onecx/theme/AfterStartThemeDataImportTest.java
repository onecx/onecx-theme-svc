package org.tkit.onecx.theme;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.test.AbstractTest;

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
        assertThat(data).isNotNull().hasSize(3);
        assertThat(data.get(0)).isNotNull();
    }

}
