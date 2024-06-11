package com.coreoz.http.routes.router.search;

import com.coreoz.http.routes.router.index.IndexedRoutes;

import java.util.ArrayDeque;
import java.util.Map;

public record SearchSegment<T>(IndexedRoutes<T> indexedRoutes, ArrayDeque<String> requestRemainingSegments, Map<Integer, String> params) {
}
