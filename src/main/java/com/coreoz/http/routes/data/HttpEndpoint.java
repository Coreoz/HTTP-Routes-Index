package com.coreoz.http.routes.data;

public record HttpEndpoint(String routeId, String method, String downstreamPath, String upstreamPath) {
}
