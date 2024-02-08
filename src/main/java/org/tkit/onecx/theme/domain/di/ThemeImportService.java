package org.tkit.onecx.theme.domain.di;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.di.mappers.DataImportMapperV1;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import gen.org.tkit.onecx.theme.di.v1.model.DataImportThemeDTOV1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ThemeImportService {

    @Inject
    ThemeDAO dao;

    @Inject
    DataImportMapperV1 mapper;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importTheme(String name, DataImportThemeDTOV1 dto) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(dto.getTenantId())
                    .build();

            ApplicationContext.start(ctx);
            // import themes
            var theme = mapper.importTheme(dto);
            theme.setName(name);
            dao.create(theme);
        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteAllByTenantId(String tenantId) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);
            dao.deleteAll();
        } finally {
            ApplicationContext.close();
        }
    }
}
