package com.coreoz.http.routes.data;

import java.util.ArrayDeque;
import java.util.Map;

public record SearchSegment(IndexedEndpoints indexedEndpoints, ArrayDeque<String> requestRemainingSegments, Map<Integer, String> params) {
}
