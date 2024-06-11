package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.ParsedPath;
import com.coreoz.http.routes.parsing.ParsedRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Route definitions index to verify whether another route definition is present or not in the Index.<br>
 * This is usually used for validation purpose to verify that a route has not already been declared.
 * @param <T> If needed, the type of data that is being attached to a route
 */
public class HttpRoutesIndex<T> {
    private final Map<String, List<ParsedRoute<T>>> existingRoutes = new HashMap<>();

    /**
     * Verify if a route exists for a path and an HTTP method
     * @param path The path to search route
     * @param httpMethod The HTTP method to search route
     * @return True if a route exists, else false
     */
    public boolean hasRoute(@NotNull String path, @NotNull String httpMethod) {
        return findRoute(path, httpMethod) != null;
    }

    /**
     * Verify if a route exists for a path
     * @param path The path to search route
     * @return True if a route exists, else false
     */
    public boolean hasRoute(@NotNull String path) {
        return !findRoutes(path).isEmpty();
    }

    /**
     * Find the route matching the path and the HTTP method passed as parameter
     * @param path The path to search route
     * @return The routes matching the path, or null if none are found
     */
    public @Nullable ParsedRoute<T> findRoute(@NotNull String path, @NotNull String httpMethod) {
        return findRoute(findRoutes(path), httpMethod).orElse(null);
    }

    /**
     * Find all the routes matching the path, one route will be returned for each existing HTTP method matching the path.
     * @param path The path to search route
     * @return The routes matching the path
     */
    public @NotNull List<ParsedRoute<T>> findRoutes(@NotNull String path) {
        return findRoutes(HttpRoutes.parsePath(path));
    }

    /**
     * Find all the routes matching the path, one route will be returned for each existing HTTP method matching the path.
     * @param path The path to search route
     * @return The routes matching the path
     */
    public @NotNull List<ParsedRoute<T>> findRoutes(@NotNull ParsedPath path) {
        return existingRoutes.getOrDefault(path.genericPath(), List.of());
    }

    /**
     * Add a route to the existing routes
     * @param path The path of the new route
     * @param httpMethod The HTTP method of the new route: GET, POST, PUT, etc.
     * @param attachedData Some optional data to be attached to the route, for further processing later
     * @return The corresponding {@link ParsedRoute} that has been added if no route existed for the path and http method.
     * If a route already exists, null is returned and the existing route is not changed
     */
    public @Nullable ParsedRoute<T> addRoute(@NotNull String path, @NotNull String httpMethod, @Nullable T attachedData) {
        return addRoute(HttpRoutes.parseRoute(path, httpMethod, attachedData));
    }

    /**
     * Add a route to the existing routes
     * @param route The route to add
     * @return The route passed as parameter if the route didn't exist yet, or null if the route already exists:
     * so the route passed as parameter is not added
     */
    public @Nullable ParsedRoute<T> addRoute(@NotNull ParsedRoute<T> route) {
        List<ParsedRoute<T>> availableRoutes = existingRoutes.get(route.parsedPath().genericPath());
        if (availableRoutes == null) {
            List<ParsedRoute<T>> routes = new ArrayList<>();
            routes.add(route);
            existingRoutes.put(route.parsedPath().genericPath(), routes);
            return route;
        }
        if (findRoute(availableRoutes, route.httpMethod()).isPresent()) {
            return null;
        }
        availableRoutes.add(route);
        return route;
    }

    private @NotNull Optional<ParsedRoute<T>> findRoute(@NotNull List<ParsedRoute<T>> availableRoutes, @NotNull String httpMethod) {
        return availableRoutes.stream().filter(route -> route.httpMethod().equals(httpMethod)).findFirst();
    }

    /**
     * Creates a {@link Collector} to use on a {@link java.util.stream.Stream} of {@link ParsedRoute} to reduce a Stream to a {@link HttpRoutesIndex}
     * @return The corresponding {@link HttpRoutesIndex}
     * @param <T> The type of the resulted {@link HttpRoutesIndex}
     */
    public static <T> Collector<ParsedRoute<T>, HttpRoutesIndex<T>, HttpRoutesIndex<T>> collector() {
        return Collector.of(
            HttpRoutesIndex::new,
            HttpRoutesIndex::addRoute,
            (a, b) -> {
                throw new RuntimeException("Parallel stream collection is not supported");
            },
            Function.identity(),
            Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH
        );
    }
}
