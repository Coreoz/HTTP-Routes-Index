package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.ParsedRoute;
import org.jetbrains.annotations.NotNull;

public class RoutesMocks {
    @NotNull
    static ParsedRoute<String> makeParsedRoute() {
        return HttpRoutes.parseRoute("/test/{arg-name}", "GET", "attached-data");
    }
}
