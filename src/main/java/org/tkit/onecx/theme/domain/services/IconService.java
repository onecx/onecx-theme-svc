package org.tkit.onecx.theme.domain.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.theme.domain.daos.IconDAO;
import org.tkit.onecx.theme.domain.daos.IconSetDAO;
import org.tkit.onecx.theme.domain.models.Icon;
import org.tkit.onecx.theme.domain.models.IconSet;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class IconService {

    @Inject
    IconDAO iconDAO;

    @Inject
    IconSetDAO iconSetDAO;

    public void createIcons(byte[] iconSetData, String refId) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        var jsonData = mapper.readTree(iconSetData);
        String prefix = jsonData.get("prefix").asText();
        var aliases = jsonData.get("aliases");
        var icons = jsonData.get("icons");
        var iconListToCreate = new ArrayList<Icon>();

        var iconSet = new IconSet();
        iconSet.setName(jsonData.get("info").path("name").asText());
        iconSet.setPrefix(prefix);
        iconSet.setVersion(jsonData.get("info").path("version").asText());
        iconSet.setRefId(refId);
        iconSet.setIconCount(jsonData.get("info").path("total").asInt());

        icons.forEachEntry((iconName, jsonNode) -> {
            Icon iconToCreate = new Icon();
            iconToCreate.setName(prefix + ":" + iconName);
            iconToCreate.setType("SVG");
            iconToCreate.setRefId(refId);
            iconToCreate.setBody(jsonNode.get("body").asText());
            iconListToCreate.add(iconToCreate);
        });
        aliases.forEachEntry((alias, jsonNode) -> {
            Icon iconToCreate = new Icon();
            iconToCreate.setName(prefix + ":" + alias);
            iconToCreate.setType("SVG");
            iconToCreate.setRefId(refId);
            iconToCreate.setParent(prefix + ":" + jsonNode.get("parent").asText());
            iconListToCreate.add(iconToCreate);
        });

        iconSet.setIcons(iconListToCreate);
        iconSetDAO.create(iconSet);
    }

    public List<Icon> resolveAliases(List<Icon> icons) {
        List<Icon> resolvedIcons = new ArrayList<>();
        for (Icon icon : icons) {
            if (icon.getParent() != null) {
                Icon resolvedParent = resolveParentRecursively(icon.getParent(), icon.getRefId());
                if (resolvedParent != null) {
                    icon.setBody(resolvedParent.getBody());
                    icon.setParent(resolvedParent.getName());
                    resolvedIcons.add(icon);
                } else {
                    log.warn("Parent icon '{}' not found for icon '{}' with refId '{}'. Icon will be removed.",
                            icon.getParent(), icon.getName(), icon.getRefId());
                }
            } else {
                resolvedIcons.add(icon);
            }
        }
        return resolvedIcons;
    }

    private Icon resolveParentRecursively(String parentName, String refId) {
        Icon parent = iconDAO.findByNameAndRefId(parentName, refId);
        if (parent == null) {
            return null;
        }
        if (parent.getParent() != null) {
            return resolveParentRecursively(parent.getParent(), refId);
        }
        return parent;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteIconSetByRefIdAndPrefix(String refId, String prefix) {
        var iconSet = iconSetDAO.findByPrefixAndRefId(prefix, refId);
        iconSetDAO.delete(iconSet);
    }
}
