package io.github.onecx.theme.domain;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.tkit.quarkus.log.cdi.LogParam;

// FIXME: Move this to onecx-core library
@ApplicationScoped
public class CommonLogParam implements LogParam {

    @Override
    public List<Item> getAssignableFrom() {
        return List.of(
                this.item(100, Response.class, x -> "Response:" + ((Response) x).getStatus()),
                this.item(100, RestResponse.class, x -> "Response:" + ((RestResponse<?>) x).getStatus()),
                this.item(100, Exception.class, x -> x.getClass().getSimpleName() + ":" + ((Exception) x).getMessage()));
    }
}
