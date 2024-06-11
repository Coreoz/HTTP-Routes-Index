package com.coreoz.http.routes.router.index;

import com.coreoz.http.routes.router.HttpRoute;

import java.util.Map;

/**
 * TODO unit test indexes
 * A route in a route index, see {@link IndexedRoutes}
 * @param routePatternIndexes A <code>Map</code> associating pattern names with their positions in the path. The position indexes starts at 1, so for example for the path <code>/users/{userId}/orders/{orderId}</code>, the indexes will be <code>[userId => 2, orderId => 4]</code>
 * @param httpRoute The base route represented by this index leaf
 */
public record IndexRouteLeaf<T>(Map<String, Integer> routePatternIndexes, HttpRoute<T> httpRoute) {
}
