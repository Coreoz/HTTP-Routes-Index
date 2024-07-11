package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;

public record MockedHttpRouteDefinition(String method, String path) implements HttpRouteDefinition {
}
