package com.coreoz.http.routes.data;

import java.util.List;
import java.util.Map;

/**
 *
 * @param patterns
 * @param destinationRouteSegments
 * @param httpEndpoint
 */
public record EndpointParsedData(Map<String, Integer> patterns, List<ParsedSegment> destinationRouteSegments, HttpEndpoint httpEndpoint) {
}
