package com.coreoz.http.routes.data;

import java.util.ArrayDeque;
import java.util.Map;

public record SearchSegment<T>(IndexedRoutes<T> indexedRoutes, ArrayDeque<String> requestRemainingSegments, Map<Integer, String> params) {
}
