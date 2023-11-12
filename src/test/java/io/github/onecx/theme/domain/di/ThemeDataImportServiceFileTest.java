package io.github.onecx.theme.domain.di;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.test.AbstractTest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@DisplayName("Theme data import test from example file")
@TestProfile(ThemeDataImportServiceFileTest.CustomProfile.class)
class ThemeDataImportServiceFileTest extends AbstractTest {

    @Inject
    ThemeDAO dao;

    @Test
    @DisplayName("Import theme data from file")
    void importDataFromFileTest() {
        var data = dao.findAll().toList();
        assertThat(data).isNotNull().hasSize(2);
    }

    public static class CustomProfile implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            return "test";
        }

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "tkit.dataimport.enabled", "true",
                    "tkit.dataimport.configurations.theme.enabled", "true",
                    "tkit.dataimport.configurations.theme.file", "./src/test/resources/import/theme-import.json",
                    "tkit.dataimport.configurations.theme.metadata.operation", "CLEAN_INSERT",
                    "tkit.dataimport.configurations.theme.stop-at-error", "true");
        }
    }

}
