package com.coreoz.http.routes.router.search;

import com.coreoz.http.routes.HttpRoutes;
import com.coreoz.http.routes.router.HttpRoute;
import com.coreoz.http.routes.router.index.IndexRouteLeaf;
import com.coreoz.http.routes.router.index.IndexedRoutes;

import java.util.Map;

/**
 * A route that has been found in the {@link IndexedRoutes} containing raw index object and raw parameter positions.<br>
 * <br>
 * See also {@link HttpRoutes#toMatchingRoute(RawMatchingRoute)}
 * @param matchingRouteLeaf The index leaf containing the base route and the route patterns values
 * @param parameterByIndex The pattern values used to find the route. For example, for the route
 * @param <T> The type of {@link HttpRoute} stored in the routes index. It can be accessed from the matching route
 *           and contains custom data
 */
public record RawMatchingRoute<T extends HttpRoute>(IndexRouteLeaf<T> matchingRouteLeaf, Map<Integer, String> parameterByIndex) {
    /**
     * @see HttpRoutes#toMatchingRoute(RawMatchingRoute)
     */
    public MatchingRoute<T> toMatchingRoute() {
        return HttpRoutes.toMatchingRoute(this);
    }
}
