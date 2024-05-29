package com.coreoz.http.routes;

import com.coreoz.http.routes.data.DestinationRoute;
import com.coreoz.http.routes.data.HttpRoute;
import com.coreoz.http.routes.data.IndexedRoutes;
import com.coreoz.http.routes.data.MatchingRoute;
import com.coreoz.http.routes.routes.ParsedSegment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handle index and search operations for HTTP routing.<br>
 * This should be used:<br>
 * 1. To index the available routes using constructor, and if necessary using {@link #addRoute(HttpRoute)}<br>
 * 2. To search for a route for a method and a path using {@link #searchRoute(String, String)}<br>
 * 3. To compute a destination path when using route rewriting using {@link #computeDestinationRoute(MatchingRoute, List)}
 */
public class HttpRouter<T> {
    private final @NotNull Map<String, IndexedRoutes<T>> routerIndex;

    public HttpRouter(@NotNull Iterable<HttpRoute<T>> routes) {
        this.routerIndex = SearchRouteIndexer.indexRoutes(routes);
    }

    /**
     * @param routesIndex The routes index by HTTP method: <code>{GET: IndexedRoutes, POST: IndexedRoutes, ...}</code>
     */
    public HttpRouter(@NotNull Map<String, IndexedRoutes<T>> routesIndex) {
        this.routerIndex = routesIndex;
    }

    /**
     * Add a new route to the routes index.
     * @return The route passed as an argument if it is the new route was added. If there were an already existing route
     * for the specified path, then the new route is NOT added and the existing route is returned.
     */
    public @NotNull HttpRoute<T> addRoute(@NotNull HttpRoute<T> route) {
        return SearchRouteIndexer.addRouteToIndex(routerIndex, route).httpRoute();
    }

    // SEARCH

    /**
     * Search a route in the index
     * @param method The HTTP method, like GET or POST
     * @param path The searched path, like /users
     * @return The optional matching route
     */
    public @NotNull Optional<MatchingRoute<T>> searchRoute(@NotNull String method, @NotNull String path) {
        IndexedRoutes<T> methodIndex = routerIndex.get(method);
        if (methodIndex == null) {
            return Optional.empty();
        }
        return SearchRouteEngine.searchRoute(methodIndex, path);
    }

    /**
     * Compute the destination path by replacing the pattern names by their values.
     * So for example:<br>
     * 1. Path in the router: <code>GET /users/{userId}/orders/{orderId}</code><br>
     * 2. <code>destinationPathSegments = HttpRoutes.parsePathAsSegments("/users-orders/{userId}/{orderId}")</code><br>
     * 3. <code>matchedRoute = searchRoute("GET", "/users/123/orders/456")</code><br>
     * 4. <code>destinationPath = computeDestinationRoute(matchedRoute, destinationPathSegments)</code><br>
     * 5. <code>destinationPath</code> => <code>/users-orders/123/456</code><br>
     * <br>
     * The destination path has to use the same pattern names as the names used in the router. So<br>
     * - Correct: <code>router path = /users/{userId}/addresses</code>, <code>destination path = /{userId}/addresses</code><br>
     * - Incorrect: <code>router path = /users/{userId}/addresses</code>, <code>destination path = /{id}/addresses</code>
     * @param matchingRoute A matching found using @{link {@link #searchRoute(String, String)}}
     * @param destinationPathSegments The destination path segments generated by {@link com.coreoz.http.routes.routes.HttpRoutes#parsePathAsSegments(String)}
     * @return The computed destination path associated with the original routeId
     */
    public @NotNull DestinationRoute computeDestinationRoute(@NotNull MatchingRoute<T> matchingRoute, @NotNull List<ParsedSegment> destinationPathSegments) {
        return SearchRouteEngine.computeDestinationRoute(matchingRoute, destinationPathSegments);
    }
}
