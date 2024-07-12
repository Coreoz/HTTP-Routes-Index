package com.coreoz.http.routes.router;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * An HTTP route that can be indexed in the router
 */
public interface HttpRoute extends HttpRouteDefinition {
    /**
     * Returns an identifier of a route, that can be use later to identify a matched route
     */
    @NotNull String routeId();
}
