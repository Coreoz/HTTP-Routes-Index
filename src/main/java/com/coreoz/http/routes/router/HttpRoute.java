package com.coreoz.http.routes.router;

/**
 * An HTTP route that can be indexed in the router
 * @param routeId An identifier of a route, that can be use later to identify a matched route
 * @param method The HTTP method of the route: GET, POST, etc.
 * @param path The path of the route (including the first slash character), e.g. <code>/users/{userId}/orders</code>
 * @param attachedData Some data that can be added to the route to ease later usages
 * @param <T> The type of {@link #attachedData}
 */
public record HttpRoute<T>(String routeId, String method, String path, T attachedData) {
}
