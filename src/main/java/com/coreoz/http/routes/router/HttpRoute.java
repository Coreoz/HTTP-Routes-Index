package com.coreoz.http.routes.router;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;

/**
 * An HTTP route that can be indexed in the router
 */
public interface HttpRoute extends HttpRouteDefinition {
    /**
     * Returns an identifier of a route, that can be use later to identify a matched route
     */
    String routeId();
}
