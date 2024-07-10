package com.coreoz.http.routes.router;

/**
 * An HTTP route that can be indexed in the router
 */
public interface HttpRoute {
    /**
     * Returns an identifier of a route, that can be use later to identify a matched route
     */
    String routeId();

    /**
     * Returns the HTTP method of the route: GET, POST, etc.
     */
    String method();

    /**
     * Returns the path of the route (including the first slash character), e.g. <code>/users/{userId}/orders</code>
     */
    String path();
}
