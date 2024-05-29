package com.coreoz.http.routes.data;

import java.util.Map;

public record MatchingRoute<T>(IndexRouteLeaf<T> matchingRouteLeaf, Map<Integer, String> params) {
}
