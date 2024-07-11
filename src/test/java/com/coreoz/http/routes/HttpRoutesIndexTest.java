package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.ParsedRoute;
import com.coreoz.http.routes.router.MockHttpRoute;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static com.coreoz.http.routes.RoutesMocks.makeParsedRoute;

public class HttpRoutesIndexTest {
    @Test
    public void findRoute__verify_than_a_non_existing_route_returns_null() {
        HttpRoutesIndex<MockedHttpRouteDefinition> routesValidator = new HttpRoutesIndex<>();
        Assertions.assertThat(routesValidator.findRoute("/non-exist", "GET")).isNull();
    }

    @Test
    public void findRoute__verify_that_an_existing_route_with_another_http_method_returns_null() {
        HttpRoutesIndex<MockedHttpRouteDefinition> routesValidator = new HttpRoutesIndex<>();
        routesValidator.addRoute(new MockedHttpRouteDefinition("POST", "/non-exist"));
        Assertions.assertThat(routesValidator.findRoute("/non-exist", "GET")).isNull();
    }

    @Test
    public void findRoute__verify_that_an_existing_route_with_the_correct_methods_and_a_renamed_path_returns_route() {
        HttpRoutesIndex<MockedHttpRouteDefinition> routesValidator = new HttpRoutesIndex<>();
        routesValidator.addRoute(new MockedHttpRouteDefinition("PUT", "/exists/{arg}/specific"));
        Assertions.assertThat(routesValidator.findRoute("/exists/{route-arg}/specific", "PUT")).isNotNull();
    }

    @Test
    public void findRoutes__verify_that__the_correct_routes_are_returned() {
        HttpRoutesIndex<MockHttpRoute> routesValidator = new HttpRoutesIndex<>();
        routesValidator.addRoute(new MockHttpRoute("a", "POST", "/route1/{arg}", "a"));
        routesValidator.addRoute(new MockHttpRoute("b", "POST", "/route2/test", "b"));
        routesValidator.addRoute(new MockHttpRoute("c", "GET", "/route1/{arg}", "c"));
        routesValidator.addRoute(new MockHttpRoute("d", "GET", "/route3/{arg}", "d"));
        @NotNull List<ParsedRoute<MockHttpRoute>> routes = routesValidator.findRoutes("/route1/{arg}");
        Assertions.assertThat(routes).hasSize(2);
        Assertions.assertThat(routes.stream().map(parsedRoute -> parsedRoute.routeDefinition().routeId())).containsExactly("a", "c");
    }

    @Test
    public void addRoute__verify_that_an_added_route_can_be_found() {
        HttpRoutesIndex<MockedHttpRouteDefinition> routesValidator = new HttpRoutesIndex<>();
        ParsedRoute<MockedHttpRouteDefinition> addedRoute = routesValidator.addRoute(new MockedHttpRouteDefinition("GET", "/test/{arg-name}"));
        Assertions.assertThat(addedRoute).isNotNull();
        Assertions.assertThat(routesValidator.hasRoute("/test/{arg-name}", "GET")).isTrue();
    }

    @Test
    public void testAddRoute__verify_that_an_added_route_can_be_found() {
        HttpRoutesIndex<MockedHttpRouteDefinition> routesValidator = new HttpRoutesIndex<>();
        ParsedRoute<MockedHttpRouteDefinition> routeToAdd = makeParsedRoute();
        ParsedRoute<MockedHttpRouteDefinition> addedRoute = routesValidator.addRoute(routeToAdd);
        Assertions.assertThat(addedRoute).isEqualTo(routeToAdd);
        Assertions.assertThat(routesValidator.hasRoute("/test/{arg-name}", "GET")).isTrue();
    }

    @Test
    public void testAddRoute__verify_that_when_a_route_already_exists_null_is_returned() {
        HttpRoutesIndex<MockedHttpRouteDefinition> routesValidator = new HttpRoutesIndex<>();
        Assertions.assertThat(routesValidator.addRoute(makeParsedRoute())).isNotNull();
        Assertions.assertThat(routesValidator.addRoute(makeParsedRoute())).isNull();
    }
}
