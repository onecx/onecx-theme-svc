package org.tkit.onecx.theme.domain.criteria;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class IconSearchCriteria {
    private String prefix;
    private List<String> names;
}
