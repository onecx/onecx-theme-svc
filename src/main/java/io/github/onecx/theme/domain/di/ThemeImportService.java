package io.github.onecx.theme.domain.di;

import gen.io.github.onecx.theme.di.v1.model.DataImportThemeDTOV1;
import io.github.onecx.theme.domain.daos.ThemeDAO;
import io.github.onecx.theme.domain.di.mappers.DataImportMapperV1;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ThemeImportService {

    @Inject
    ThemeDAO dao;

    @Inject
    DataImportMapperV1 mapper;

    public void importTheme(String name, DataImportThemeDTOV1 dto) {
        try {
            System.out.println("########### DTO " + dto.getTenantId());
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
            System.out.println("########### " + tenantId);
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
