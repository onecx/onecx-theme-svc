package org.tkit.onecx.theme.domain.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ThemeInfo(String name, String description) {
}
