package com.coreoz.http.routes.router.search;

import com.coreoz.http.routes.router.HttpRoute;
import com.coreoz.http.routes.router.index.IndexedRoutes;
import com.coreoz.http.routes.HttpRoutes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Handle route search in an {@link IndexedRoutes}. Some examples (URI -> route):<br>
 * <pre>
 * - /users                             -> /users
 * - /users/1234                        -> /users/{id}
 * - /users/1234/addresses              -> /users/{id}/addresses
 * - /users/1234/addresses/5678         -> /users/{id}/addresses/{idAddress}
 * </pre>
 */
public class SearchRouteEngine {
    /**
     * Perform the search in the index.
     * @param routesIndex The route index
     * @param requestPath The path to search (that must start with a slash: "/")
     * @return The optional route that has been found
     */
    public static <T extends HttpRoute> @NotNull Optional<RawMatchingRoute<T>> searchRoute(@NotNull IndexedRoutes<T> routesIndex, @NotNull String requestPath) {
        ArrayDeque<String> requestElements = new ArrayDeque<>(Arrays.asList(requestPath.substring(1).split(HttpRoutes.SEGMENT_SEPARATOR)));
        List<SearchSegment<T>> segmentOptions = new ArrayList<>();
        segmentOptions.add(new SearchSegment<>(
            routesIndex,
            requestElements,
            new HashMap<>()
        ));

        while (!segmentOptions.isEmpty()) {
            SearchSegment<T> currentRouteOption = segmentOptions.remove(0);
            IndexedRoutes<T> indexedRoutes = currentRouteOption.indexedRoutes();

            if (currentRouteOption.requestRemainingSegments().isEmpty() && indexedRoutes.getLastRoute() != null) {
                return Optional.of(
                    new RawMatchingRoute<>(
                        indexedRoutes.getLastRoute(),
                        currentRouteOption.params()
                    )
                );
            }

            if (!currentRouteOption.requestRemainingSegments().isEmpty()) {
                String currentPathSegment = currentRouteOption.requestRemainingSegments().remove();
                if (indexedRoutes.getSegments().get(currentPathSegment) != null) {
                    segmentOptions.add(toSearchSegment(indexedRoutes.getSegments().get(currentPathSegment), currentRouteOption));
                }
                if (indexedRoutes.getPattern() != null) {
                    currentRouteOption.params().put(currentRouteOption.indexedRoutes().getDepth() + 1, currentPathSegment);
                    segmentOptions.add(toSearchSegment(indexedRoutes.getPattern(), currentRouteOption));
                }
                segmentOptions
                    .sort(Comparator.comparingLong((SearchSegment<T> searchSegment) -> searchSegment.indexedRoutes().getRating())
                    .reversed());
            }

        }
        return Optional.empty();
    }

    private static <T extends HttpRoute> @NotNull SearchSegment<T> toSearchSegment(@NotNull IndexedRoutes<T> indexedRoutes, @NotNull SearchSegment<T> currentSegmentOption) {
        return new SearchSegment<>(
            indexedRoutes,
            currentSegmentOption.requestRemainingSegments().clone(),
            currentSegmentOption.params()
        );
    }
}
