package com.coreoz.http.routes;

import com.coreoz.http.routes.router.HttpRoute;
import com.coreoz.http.routes.router.search.SearchRouteEngine;
import com.coreoz.http.routes.router.index.SearchRouteIndexer;
import com.coreoz.http.routes.router.index.IndexedRoutes;
import com.coreoz.http.routes.router.search.RawMatchingRoute;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handle index and search operations for HTTP routing.<br>
 * This should be used:<br>
 * 1. To index the available routes using constructor, and if necessary using {@link #addRoute(HttpRoute)}<br>
 * 2. To search for a route for a method and a path using {@link #searchRoute(String, String)}<br>
 * <br>
 * In case of route rewriting, a destination path can be computed using {@link HttpRoutes#computeDestinationRoute(RawMatchingRoute, List)}
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
    public @NotNull Optional<RawMatchingRoute<T>> searchRoute(@NotNull String method, @NotNull String path) {
        IndexedRoutes<T> methodIndex = routerIndex.get(method);
        if (methodIndex == null) {
            return Optional.empty();
        }
        return SearchRouteEngine.searchRoute(methodIndex, path);
    }
}
