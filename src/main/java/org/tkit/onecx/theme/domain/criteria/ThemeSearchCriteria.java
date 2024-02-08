package org.tkit.onecx.theme.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class ThemeSearchCriteria {

    private String name;

    private Integer pageNumber;

    private Integer pageSize;

}
