package com.coreoz.http.routes.data;

import java.util.Map;

/**
 * A route in a route index, see {@link IndexedRoutes}
 * @param routePatternIndexes A <code>Map</code> associating pattern names with their positions in the path.
 * @param httpRoute
 */
public record IndexRouteLeaf<T>(Map<String, Integer> routePatternIndexes, HttpRoute<T> httpRoute) {
}
