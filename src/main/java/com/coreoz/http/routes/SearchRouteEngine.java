package com.coreoz.http.routes;

import com.coreoz.http.routes.data.DestinationRoute;
import com.coreoz.http.routes.data.EndpointParsedData;
import com.coreoz.http.routes.data.IndexedEndpoints;
import com.coreoz.http.routes.data.MatchingRoute;
import com.coreoz.http.routes.data.SearchSegment;
import com.coreoz.http.routes.routes.HttpRoutes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Handle route search in an {@link IndexedEndpoints}. Some examples (URI -> route):<br>
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
     * @param indexEndpoints The route index
     * @param requestPath The path to search (that must start with a slash: "/")
     * @return The optional route that has been found
     */
    public static @NotNull Optional<MatchingRoute> searchRoute(@NotNull IndexedEndpoints indexEndpoints, @NotNull String requestPath) {
        ArrayDeque<String> requestElements = new ArrayDeque<>(Arrays.asList(requestPath.substring(1).split(HttpRoutes.SEGMENT_SEPARATOR)));
        List<SearchSegment> segmentOptions = new ArrayList<>();
        segmentOptions.add(new SearchSegment(
            indexEndpoints,
            requestElements,
            new HashMap<>()
        ));

        while (!segmentOptions.isEmpty()) {
            SearchSegment currentEndpointsOption = segmentOptions.remove(0);
            IndexedEndpoints indexedEndpoints = currentEndpointsOption.indexedEndpoints();

            if (currentEndpointsOption.requestRemainingSegments().isEmpty() && indexedEndpoints.getLastEndpoint() != null) {
                return Optional.of(
                    new MatchingRoute(
                        indexedEndpoints.getLastEndpoint(),
                        currentEndpointsOption.params()
                    )
                );
            }

            if (!currentEndpointsOption.requestRemainingSegments().isEmpty()) {
                String currentRequest = currentEndpointsOption.requestRemainingSegments().remove();
                if (indexedEndpoints.getSegments().get(currentRequest) != null) {
                    segmentOptions.add(toSearchSegment(indexedEndpoints.getSegments().get(currentRequest), currentEndpointsOption));
                }
                if (indexedEndpoints.getPattern() != null) {
                    currentEndpointsOption.params().put(currentEndpointsOption.indexedEndpoints().getDepth() + 1, currentRequest);
                    segmentOptions.add(toSearchSegment(indexedEndpoints.getPattern(), currentEndpointsOption));
                }
                segmentOptions
                    .sort(Comparator.comparingLong((SearchSegment searchSegment) -> searchSegment.indexedEndpoints().getRating())
                    .reversed());
            }

        }
        return Optional.empty();
    }

    private static @NotNull SearchSegment toSearchSegment(@NotNull IndexedEndpoints indexedEndpoints, @NotNull SearchSegment currentSegmentOption) {
        return new SearchSegment(
            indexedEndpoints,
            currentSegmentOption.requestRemainingSegments().clone(),
            currentSegmentOption.params()
        );
    }

    public static @NotNull DestinationRoute computeDestinationRoute(@NotNull MatchingRoute matchingRoute, @Nullable String destinationBaseUrl) {
        EndpointParsedData matchingEndpoint = matchingRoute.matchingEndpoint();
        @NotNull String serializedParsedPath = HttpRoutes.serializeParsedPath(
            matchingEndpoint.destinationRouteSegments(),
            currentSegmentName -> matchingRoute.params().get(matchingEndpoint.patterns().get(currentSegmentName))
        );
        return new DestinationRoute(
            matchingEndpoint.httpEndpoint().routeId(),
            destinationBaseUrl == null ? serializedParsedPath : (destinationBaseUrl + serializedParsedPath)
        );
    }
}
