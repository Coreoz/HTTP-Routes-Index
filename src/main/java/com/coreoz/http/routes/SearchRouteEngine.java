package com.coreoz.http.routes;

import com.coreoz.http.routes.data.DestinationRoute;
import com.coreoz.http.routes.data.IndexRouteLeaf;
import com.coreoz.http.routes.data.IndexedRoutes;
import com.coreoz.http.routes.data.MatchingRoute;
import com.coreoz.http.routes.data.SearchSegment;
import com.coreoz.http.routes.routes.HttpRoutes;
import com.coreoz.http.routes.routes.ParsedSegment;
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
    public static <T> @NotNull Optional<MatchingRoute<T>> searchRoute(@NotNull IndexedRoutes<T> routesIndex, @NotNull String requestPath) {
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
                    new MatchingRoute<>(
                        indexedRoutes.getLastRoute(),
                        currentRouteOption.params()
                    )
                );
            }

            if (!currentRouteOption.requestRemainingSegments().isEmpty()) {
                String currentRequest = currentRouteOption.requestRemainingSegments().remove();
                if (indexedRoutes.getSegments().get(currentRequest) != null) {
                    segmentOptions.add(toSearchSegment(indexedRoutes.getSegments().get(currentRequest), currentRouteOption));
                }
                if (indexedRoutes.getPattern() != null) {
                    currentRouteOption.params().put(currentRouteOption.indexedRoutes().getDepth() + 1, currentRequest);
                    segmentOptions.add(toSearchSegment(indexedRoutes.getPattern(), currentRouteOption));
                }
                segmentOptions
                    .sort(Comparator.comparingLong((SearchSegment<T> searchSegment) -> searchSegment.indexedRoutes().getRating())
                    .reversed());
            }

        }
        return Optional.empty();
    }

    private static <T> @NotNull SearchSegment<T> toSearchSegment(@NotNull IndexedRoutes<T> indexedRoutes, @NotNull SearchSegment<T> currentSegmentOption) {
        return new SearchSegment<>(
            indexedRoutes,
            currentSegmentOption.requestRemainingSegments().clone(),
            currentSegmentOption.params()
        );
    }

    public static @NotNull DestinationRoute computeDestinationRoute(@NotNull MatchingRoute<?> matchingRoute, @NotNull List<ParsedSegment> destinationPathSegments) {
        IndexRouteLeaf<?> matchingRouteLeaf = matchingRoute.matchingRouteLeaf();
        @NotNull String serializedParsedPath = HttpRoutes.serializeParsedPath(
            destinationPathSegments,
            currentSegmentName -> matchingRoute.params().get(matchingRouteLeaf.routePatternIndexes().get(currentSegmentName))
        );
        return new DestinationRoute(
            matchingRouteLeaf.httpRoute().routeId(),
            serializedParsedPath
        );
    }
}
