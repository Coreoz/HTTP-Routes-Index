package com.coreoz.http.routes.router.index;

import com.coreoz.http.routes.router.HttpRoute;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * A routes index (or node in the index tree).
 * A route index is partitioned by HTTP method (GET, POST, etc.),
 * so a routes index contains only routes for the same HTTP method.<br>
 * See {@link SearchRouteIndexer} for usage.
 */
@AllArgsConstructor
@Data
public class IndexedRoutes<T extends HttpRoute> {
    private IndexRouteLeaf<T> lastRoute;
    private long rating;
    private int depth;
    private Map<String, IndexedRoutes<T>> segments;
    private IndexedRoutes<T> pattern;
}
