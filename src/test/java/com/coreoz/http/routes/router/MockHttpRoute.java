package com.coreoz.http.routes.router;

import org.jetbrains.annotations.NotNull;

public record MockHttpRoute(String routeId, String method, String downstreamPath, String upstreamPath) implements HttpRoute {
    @Override
    public @NotNull String path() {
        return downstreamPath();
    }
}
