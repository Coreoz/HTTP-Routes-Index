package com.coreoz.http.routes.router;

public record MockHttpRoute(String routeId, String method, String downstreamPath, String upstreamPath) implements HttpRoute {
    @Override
    public String path() {
        return downstreamPath();
    }
}
