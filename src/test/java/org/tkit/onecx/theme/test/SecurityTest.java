package org.tkit.onecx.theme.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/themes/id", 404, List.of("ocx-th:read"), "get");
        config.addConfig("write", "/internal/themes", 400, List.of("ocx-th:write"), "post");
        return config;
    }
}
