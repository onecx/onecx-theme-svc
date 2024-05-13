package org.tkit.onecx.theme.domain.di;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.onecx.theme.test.AbstractTest;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.test.WithDBData;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.di.template.model.TemplateImportDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class TemplateImportServiceTest extends AbstractTest {

    @Inject
    TemplateImportService service;

    @Inject
    ThemeDAO dao;

    @Inject
    ObjectMapper mapper;

    @Test
    void importDataNotSupportedActionTest() {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("operation", "CUSTOM_NOT_SUPPORTED");
        DataImportConfig config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return metadata;
            }
        };

        service.importData(config);

        List<Theme> data = dao.findAll().toList();
        assertThat(data).isNotNull().hasSize(3);

    }

    @Test
    void importEmptyDataTest() {
        Assertions.assertThrows(Exception.class, () -> {
            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("tenants", "default");
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("tenants", "default");
                }

                @Override
                public byte[] getData() {
                    return new byte[] {};
                }
            });
        });
        Assertions.assertDoesNotThrow(() -> {

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("tenants", "default");
                }

                @Override
                public byte[] getData() {
                    try {
                        return mapper.writeValueAsBytes(new TemplateImportDTO().themes(null));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            service.importData(new DataImportConfig() {
                @Override
                public Map<String, String> getMetadata() {
                    return Map.of("tenants", "default");
                }

                @Override
                public byte[] getData() {
                    try {
                        return mapper.writeValueAsBytes(new TemplateImportDTO().themes(Map.of()));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

        });

        var config = new DataImportConfig() {
            @Override
            public Map<String, String> getMetadata() {
                return Map.of("tenants", "default");
            }

            @Override
            public byte[] getData() {
                return new byte[] { 0 };
            }
        };
        Assertions.assertThrows(TemplateImportService.ImportException.class, () -> service.importData(config));

    }
}
