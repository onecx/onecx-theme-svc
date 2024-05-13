package org.tkit.onecx.theme.domain.di.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExistingData {

    private final Set<String> names;

    private final Set<String> refTypes;

    public ExistingData(List<String> names, List<String> refTypes) {
        this.names = new HashSet<>(names);
        this.refTypes = new HashSet<>(refTypes);
    }

    public boolean isRefIdRefTypeInDb(String refId, String refType) {
        return refTypes.contains(refId + refType);
    }

    public boolean isThemeInDb(String name) {
        return names.contains(name);
    }
}
