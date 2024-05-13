package org.tkit.onecx.theme.domain.di;

import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.daos.ImageDAO;
import org.tkit.onecx.theme.domain.daos.ThemeDAO;
import org.tkit.onecx.theme.domain.di.models.ExistingData;
import org.tkit.onecx.theme.domain.models.Image;
import org.tkit.onecx.theme.domain.models.Theme;
import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ThemeImportService {

    @Inject
    ThemeDAO dao;

    @Inject
    ImageDAO imageDAO;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importTheme(String tenant, List<Theme> themes, List<Image> images) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenant)
                    .build();

            ApplicationContext.start(ctx);

            // create themes
            dao.create(themes);

            // create images
            imageDAO.create(images);

        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public ExistingData getData(String tenant, Set<String> names) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenant)
                    .build();

            ApplicationContext.start(ctx);
            var tn = dao.findNamesByThemeByNames(names);
            var tr = imageDAO.findRefIdRefTypesByRefId(names);

            return new ExistingData(tn, tr);
        } finally {
            ApplicationContext.close();
        }
    }
}
