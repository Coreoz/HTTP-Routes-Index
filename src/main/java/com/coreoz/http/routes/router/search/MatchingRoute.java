package com.coreoz.http.routes.router.search;

import com.coreoz.http.routes.router.HttpRoute;
import com.coreoz.http.routes.router.index.IndexedRoutes;

import java.util.Map;

/**
 * A route that has been found in the {@link IndexedRoutes} containing the base {@link HttpRoute} object and parameters values.
 * @param httpRoute The base {@link HttpRoute} object
 * @param parameterValues The Map that has the parameter names as keys associated with the path parameter actual values.
 *                        So for the route <code>/users/{userId}</code> and the path <code>/users/123</code>,
 *                        the Map will contain <code>userId -> 123</code>
 * @param <T> The {@link HttpRoute} type
 */
public record MatchingRoute<T extends HttpRoute>(T httpRoute, Map<String, String> parameterValues) {
}
