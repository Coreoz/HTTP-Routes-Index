package com.coreoz.http.routes.router.index;

import com.coreoz.http.routes.router.MockHttpRoute;
import com.coreoz.http.routes.router.RouterMocks;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SearchRouteIndexerTest {

    @Test
    public void indexEndpoints__check_that_indexation_function_order_is_correct() {
        Map<String, IndexedRoutes<MockHttpRoute>> indexedEndPoint = SearchRouteIndexer.indexRoutes(RouterMocks.endpointsTest());
        Assertions.assertThat(indexedEndPoint).containsExactlyInAnyOrderEntriesOf(RouterMocks.indexedRoutesByMethod);
    }

    @Test
    public void addEndpointToIndex__check_that_adding_endpoint_returns_added_endpoint() {
        Map<String, IndexedRoutes<MockHttpRoute>> index = new HashMap<>();
        IndexRouteLeaf<MockHttpRoute> addedEndpoint = SearchRouteIndexer.addRouteToIndex(index, new MockHttpRoute("1", "GET", "/test", "/test"));
        Assertions.assertThat(addedEndpoint).isNotNull();
        Assertions.assertThat(addedEndpoint.httpRoute().routeId()).isEqualTo("1");
        Assertions.assertThat(index).hasSize(1);
    }

    @Test
    public void addEndpointToIndex__check_that_adding_an_existing_endpoint_returns_existing_endpoint() {
        Map<String, IndexedRoutes<MockHttpRoute>> index = new HashMap<>();
        SearchRouteIndexer.addRouteToIndex(index, new MockHttpRoute("1", "GET", "/test", "/test"));
        IndexRouteLeaf<MockHttpRoute> existingEndpoint = SearchRouteIndexer.addRouteToIndex(index, new MockHttpRoute("2", "GET", "/test", "/test"));

        Assertions.assertThat(existingEndpoint).isNotNull();
        Assertions.assertThat(existingEndpoint.httpRoute().routeId()).isEqualTo("1");
        Assertions.assertThat(index).hasSize(1);
    }
}
