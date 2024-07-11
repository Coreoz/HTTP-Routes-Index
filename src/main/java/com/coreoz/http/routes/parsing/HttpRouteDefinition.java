package com.coreoz.http.routes.parsing;

/**
 * An HTTP route that can be indexed
 */
public interface HttpRouteDefinition {
    /**
     * Returns the HTTP method of the route: GET, POST, etc.
     */
    String method();

    /**
     * Returns the path of the route (including the first slash character), e.g. <code>/users/{userId}/orders</code>
     */
    String path();
}
