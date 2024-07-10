package com.coreoz.http.routes.router.search;

import com.coreoz.http.routes.HttpRoutes;
import com.coreoz.http.routes.parsing.DestinationRoute;
import com.coreoz.http.routes.router.MockHttpRoute;
import com.coreoz.http.routes.router.RouterMocks;
import com.coreoz.http.routes.router.index.IndexedRoutes;
import com.coreoz.http.routes.router.index.SearchRouteIndexer;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

public class SearchRouteEngineTest {
    @Test
    public void searchGatewayRoute___check_that_if_no_corresponding_route_returns_empty() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoints = RouterMocks.indexedRoutesByMethod;
        @NotNull Optional<RawMatchingRoute<MockHttpRoute>> resultRoute = SearchRouteEngine.searchRoute(indexedEndPoints.get("GET"), "/ddddddd");
        Assertions.assertThat(resultRoute).isEmpty();
    }

    @Test
    public void searchGatewayRoute___check_that_mapping_without_param_correct() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoints = RouterMocks.indexedRoutesByMethod;
        RawMatchingRoute<MockHttpRoute> resultRoute = SearchRouteEngine.searchRoute(indexedEndPoints.get("GET"), "/test/chose").orElse(null);
        Assertions.assertThat(resultRoute).isNotNull();
        Assertions.assertThat(resultRoute.matchingRouteLeaf().httpRoute().routeId()).isEqualTo("1");
    }

    @Test
    public void searchGatewayRoute___check_that_mapping_with_path_param_correct() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoints = RouterMocks.indexedRoutesByMethod;
        // gateway route : /test/{truc}/{bidule}
        DestinationRoute resultRoute = SearchRouteEngine
            .searchRoute(indexedEndPoints.get("GET"), "/test/param/machin")
            .map(SearchRouteEngineTest::toDestinationRoute)
            .orElse(null);
        Assertions.assertThat(resultRoute).isNotNull();
        Assertions.assertThat(resultRoute.destinationPath()).isEqualTo("/test/param/machin");
    }

    @Test
    public void searchGatewayRoute___check_that_mapping_with_path_param_wrong_order_correct() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoints = RouterMocks.indexedRoutesByMethod;
        // gateway route :/test/{truc}/machin/{chose}
        // provider route : /test/{chose}/machin/{truc}
        DestinationRoute resultRoute = SearchRouteEngine
            .searchRoute(indexedEndPoints.get("GET"), "/test/bidule/machin/aaaa")
            .map(SearchRouteEngineTest::toDestinationRoute)
            .orElse(null);
        Assertions.assertThat(resultRoute).isNotNull();
        Assertions.assertThat(resultRoute.destinationPath()).isEqualTo("/test/aaaa/machin/bidule");
    }

    @Test
    public void searchGatewayRoute___check_that_returns_fail_if_one_parameter_missing() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoints = RouterMocks.indexedRoutesByMethod;
        // gateway route : /test/{truc}/{bidule}
        @NotNull Optional<RawMatchingRoute<MockHttpRoute>> resultRoute = SearchRouteEngine.searchRoute(indexedEndPoints.get("GET"), "/test/param");

        Assertions.assertThat(resultRoute).isEmpty();
    }

    @Test
    public void searchGatewayRoute__check_that_route_with_exact_name_matches() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoint = SearchRouteIndexer.indexRoutes(RouterMocks.endpointsTest());
        RawMatchingRoute<MockHttpRoute> resultRoute = SearchRouteEngine.searchRoute(indexedEndPoint.get("PUT"), "/test/machinchouette").orElse(null);
        RawMatchingRoute<MockHttpRoute> resultRoute2 = SearchRouteEngine.searchRoute(indexedEndPoint.get("PUT"), "/test/chouette").orElse(null);

        Assertions.assertThat(resultRoute).isNotNull();
        Assertions.assertThat(resultRoute2).isNotNull();
        Assertions.assertThat(resultRoute.matchingRouteLeaf().httpRoute().routeId()).isEqualTo("9");
        Assertions.assertThat(resultRoute2.matchingRouteLeaf().httpRoute().routeId()).isEqualTo("7");
    }

    @Test
    public void searchGatewayRoute__check_that_route_with_non_exact_name_matches() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoint = SearchRouteIndexer.indexRoutes(RouterMocks.endpointsTest());
        DestinationRoute resultRoute = SearchRouteEngine
            .searchRoute(indexedEndPoint.get("PUT"), "/test/wildcard-route")
            .map(SearchRouteEngineTest::toDestinationRoute)
            .orElse(null);

        Assertions.assertThat(resultRoute).isNotNull();
        Assertions.assertThat(resultRoute.destinationPath()).isEqualTo("/test/wildcard-route");
    }

    private static DestinationRoute toDestinationRoute(RawMatchingRoute<MockHttpRoute> matchingRoute) {
        return HttpRoutes.computeDestinationRoute(
            matchingRoute,
            HttpRoutes.parsePathAsSegments(matchingRoute
                .matchingRouteLeaf()
                .httpRoute()
                .upstreamPath()
            )
        );
    }
}
