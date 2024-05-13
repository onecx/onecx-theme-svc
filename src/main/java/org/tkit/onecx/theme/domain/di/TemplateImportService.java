package org.tkit.onecx.theme.domain.di;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.theme.domain.di.mappers.TemplateImportMapper;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.org.tkit.onecx.theme.di.template.model.TemplateImportDTO;
import gen.org.tkit.onecx.theme.di.template.model.TemplateThemeDTO;

@DataImport("template")
public class TemplateImportService implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(TemplateImportService.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    TemplateImportMapper mapper;

    @Inject
    ThemeImportService importService;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importData(DataImportConfig config) {
        log.info("Import theme from configuration {}", config);
        try {
            List<String> tenants = List.of();
            var tmp = config.getMetadata().get("tenants");
            if (tmp != null) {
                tenants = List.of(tmp.split(","));
            }

            if (tenants.isEmpty()) {
                log.warn("No tenants defined for the templates");
                return;
            }

            TemplateImportDTO data = objectMapper.readValue(config.getData(), TemplateImportDTO.class);

            if (data.getThemes() == null || data.getThemes().isEmpty()) {
                log.warn("Import configuration key {} does not contains any JSON data to import", config.getKey());
                return;
            }

            // execute the import
            importThemes(tenants, data.getThemes());
        } catch (Exception ex) {
            throw new ImportException(ex.getMessage(), ex);
        }
    }

    public void importThemes(List<String> tenants, Map<String, TemplateThemeDTO> data) {

        // create list of theme names to import
        var names = data.keySet();

        tenants.forEach(tenant -> {
            // load names from the database
            var existingData = importService.getData(tenant, names);

            // filter and create themes to import
            var themes = mapper.create(existingData, data);
            var images = mapper.createImage(existingData, data);

            // create themes in database for tenant
            importService.importTheme(tenant, themes, images);
        });
    }

    public static class ImportException extends RuntimeException {

        public ImportException(String message, Throwable ex) {
            super(message, ex);
        }
    }
}
