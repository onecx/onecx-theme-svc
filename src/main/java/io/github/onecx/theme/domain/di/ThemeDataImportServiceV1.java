package io.github.onecx.theme.domain.di;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.theme.di.v1.model.DataImportDTOV1;
import gen.io.github.onecx.theme.di.v1.model.DataImportThemeDTOV1;

@DataImport("theme")
public class ThemeDataImportServiceV1 implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(ThemeDataImportServiceV1.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ThemeImportService importService;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importData(DataImportConfig config) {
        log.info("Import theme from configuration {}", config);
        try {
            var operation = config.getMetadata().getOrDefault("operation", "NONE");

            Consumer<DataImportDTOV1> action = null;
            if ("CLEAN_INSERT".equals(operation)) {
                action = this::cleanInsert;
            }

            if (action == null) {
                log.warn("Not supported operation '{}' for the import configuration key '{}'", operation, config.getKey());
                return;
            }

            if (config.getData() == null || config.getData().length == 0) {
                log.warn("Import configuration key {} does not contains any data to import", config.getKey());
                return;
            }

            DataImportDTOV1 data = objectMapper.readValue(config.getData(), DataImportDTOV1.class);

            if (data.getThemes() == null || data.getThemes().isEmpty()) {
                log.warn("Import configuration key {} does not contains any JSON data to import", config.getKey());
                return;
            }

            // execute the import
            action.accept(data);
        } catch (Exception ex) {
            throw new ImportException(ex.getMessage(), ex);
        }
    }

    public void cleanInsert(DataImportDTOV1 data) {

        // clean data
        var tenants = data.getThemes().values().stream().map(DataImportThemeDTOV1::getTenantId).collect(Collectors.toSet());
        for (var tenant : tenants) {
            importService.deleteAllByTenantId(tenant);
        }
        //        tenants.forEach(x -> importService.deleteAllByTenantId(x));

        // import themes
        data.getThemes().forEach((name, dto) -> importService.importTheme(name, dto));

    }

    public static class ImportException extends RuntimeException {

        public ImportException(String message, Throwable ex) {
            super(message, ex);
        }
    }
}
