package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.ParsedRoute;
import org.jetbrains.annotations.NotNull;

public class RoutesMocks {
    @NotNull
    static ParsedRoute<MockedHttpRouteDefinition> makeParsedRoute() {
        MockedHttpRouteDefinition route = new MockedHttpRouteDefinition("GET", "/test/{arg-name}");
        return new ParsedRoute<>(HttpRoutes.parsePath(route.path()), route);
    }
}
