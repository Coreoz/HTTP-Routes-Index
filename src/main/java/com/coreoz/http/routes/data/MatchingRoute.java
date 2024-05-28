package com.coreoz.http.routes.data;

import java.util.Map;

public record MatchingRoute(EndpointParsedData matchingEndpoint, Map<Integer, String> params) {
}
