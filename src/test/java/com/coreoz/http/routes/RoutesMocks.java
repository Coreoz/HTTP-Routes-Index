package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.ParsedRoute;
import org.jetbrains.annotations.NotNull;

public class RoutesMocks {
    @NotNull
    static ParsedRoute<MockedHttpRouteDefinition> makeParsedRoute() {
        return HttpRoutes.parseRoute(new MockedHttpRouteDefinition("GET", "/test/{arg-name}"));
    }
}
