package com.coreoz.http.routes.router.index;

import com.coreoz.http.routes.HttpRoutes;
import com.coreoz.http.routes.parsing.ParsedSegment;
import com.coreoz.http.routes.router.HttpRoute;
import com.coreoz.http.routes.router.search.SearchRouteEngine;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle route indexing in an {@link IndexedRoutes}. Some examples of routes:<br>
 * <pre>
 * - /users
 * - /users/{id}
 * - /users/{id}/addresses
 * - /users/{id}/addresses/{idAddress}
 * </pre>
 * {@link SearchRouteEngine} enables route search in the {@link IndexedRoutes}.
 */
public class SearchRouteIndexer {
    private static final Integer MAX_LONG_OFFSET_FOR_POSITIVE_NUMBERS = 62;

    /**
     * Add a new route to the routes index tree.<br>
     * <br>
     * Returns the new route added to the tree or the existing route that is already present in the tree.
     */
    public static <T extends HttpRoute> @NotNull IndexRouteLeaf<T> addRouteToIndex(@NotNull Map<String, IndexedRoutes<T>> indexedRoutes, @NotNull T route) {
        IndexedRoutes<T> rootIndex = indexedRoutes.computeIfAbsent(route.method(), method -> new IndexedRoutes<>(
            null,
            1L << MAX_LONG_OFFSET_FOR_POSITIVE_NUMBERS,
            0,
            new HashMap<>(),
            null
        ));

        List<ParsedSegment> pathSegments = HttpRoutes.parsePathAsSegments(route.path());
        // initialise patternIndexes map
        Map<String, Integer> patternIndexes = new HashMap<>();

        IndexedRoutes<T> currentIndex = rootIndex;
        for (int segmentIndex = 1; segmentIndex <= pathSegments.size(); segmentIndex++) {
            ParsedSegment parsedSegmentToAdd = pathSegments.get(segmentIndex - 1);
            if (parsedSegmentToAdd.isPattern()) {
                currentIndex = computePatternIndex(currentIndex, parsedSegmentToAdd.name(), segmentIndex, patternIndexes);
            } else {
                currentIndex = computeSegmentIndex(currentIndex, parsedSegmentToAdd.name(), segmentIndex);
            }

            // final stop condition
            if (segmentIndex == pathSegments.size()) {
                if (currentIndex.getLastRoute() != null) {
                    // When a route already exists, e.g.
                    // /test/{bidule}/truc and /test/{machin}/truc
                    // There is already an existing route for the current route
                    // => The new route is not added and the existing route is returned
                    return currentIndex.getLastRoute();
                }
                IndexRouteLeaf<T> newRouteLeaf = new IndexRouteLeaf<>(
                    patternIndexes,
                    route
                );
                currentIndex.setLastRoute(newRouteLeaf);
                return newRouteLeaf;
            }
        }

        throw new RuntimeException("The route " + route + " could not be added, this is a bug");
    }

    private static <T extends HttpRoute> @NotNull IndexedRoutes<T> computeSegmentIndex(@NotNull IndexedRoutes<T> currentIndex, @NotNull String segmentName, int segmentIndex) {
        return currentIndex.getSegments().computeIfAbsent(segmentName, segmentNameToAdd -> new IndexedRoutes<>(
            null,
            currentIndex.getRating() | 1L << (MAX_LONG_OFFSET_FOR_POSITIVE_NUMBERS - segmentIndex),
            segmentIndex,
            new HashMap<>(),
            null
        ));
    }

    private static <T extends HttpRoute> @NotNull IndexedRoutes<T> computePatternIndex(
        @NotNull IndexedRoutes<T> currentIndex, @NotNull String segmentName, int segmentIndex, @NotNull Map<String, Integer> patterns
    ) {
        patterns.put(segmentName, segmentIndex);
        if (currentIndex.getPattern() == null) {
            IndexedRoutes<T> pattern = new IndexedRoutes<>(
                null,
                currentIndex.getRating(),
                segmentIndex,
                new HashMap<>(),
                null
            );
            currentIndex.setPattern(pattern);
            return pattern;
        }
        return currentIndex.getPattern();
    }

    /**
     * Main indexation method
     */
    public static <T extends HttpRoute> @NotNull Map<String, IndexedRoutes<T>> indexRoutes(@NotNull Iterable<T> routes) {
        // 1. Build the route index
        Map<String, IndexedRoutes<T>> routesIndex = new HashMap<>();
        // 2. Loop over all routes to add them in the index
        for (T route : routes) {
            addRouteToIndex(routesIndex, route);
        }
        // 3. Returns the route index
        return routesIndex;
    }
}
