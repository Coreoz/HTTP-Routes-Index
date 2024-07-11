package com.coreoz.http.routes.parsing;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a route that has been parsed. E.g <code>GET /path/{path-arg}/other-path-segment</code>.
 * @param parsedPath See {@link ParsedPath}
 * @param routeDefinition The original route definition
 * @param <T> The type of {@link HttpRouteDefinition} used
 */
public record ParsedRoute<T extends HttpRouteDefinition>(@NotNull ParsedPath parsedPath, @NotNull T routeDefinition) {
}
