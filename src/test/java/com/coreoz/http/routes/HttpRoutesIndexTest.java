package com.coreoz.http.routes;

import com.coreoz.http.routes.parsing.ParsedRoute;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static com.coreoz.http.routes.RoutesMocks.makeParsedRoute;

public class HttpRoutesIndexTest {
    @Test
    public void findRoute__verify_than_a_non_existing_route_returns_null() {
        HttpRoutesIndex<Void> routesValidator = new HttpRoutesIndex<>();
        Assertions.assertThat(routesValidator.findRoute("/non-exist", "GET")).isNull();
    }

    @Test
    public void findRoute__verify_that_an_existing_route_with_another_http_method_returns_null() {
        HttpRoutesIndex<Void> routesValidator = new HttpRoutesIndex<>();
        routesValidator.addRoute("/non-exist", "POST", null);
        Assertions.assertThat(routesValidator.findRoute("/non-exist", "GET")).isNull();
    }

    @Test
    public void findRoute__verify_that_an_existing_route_with_the_correct_methods_and_a_renamed_path_returns_route() {
        HttpRoutesIndex<Void> routesValidator = new HttpRoutesIndex<>();
        routesValidator.addRoute("/exists/{arg}/specific", "PUT", null);
        Assertions.assertThat(routesValidator.findRoute("/exists/{route-arg}/specific", "PUT")).isNotNull();
    }

    @Test
    public void findRoutes__verify_that__the_correct_routes_are_returned() {
        HttpRoutesIndex<String> routesValidator = new HttpRoutesIndex<>();
        routesValidator.addRoute("/route1/{arg}", "POST", "a");
        routesValidator.addRoute("/route2/test", "POST", "b");
        routesValidator.addRoute("/route1/{arg}", "GET", "c");
        routesValidator.addRoute("/route3/{arg}", "GET", "d");
        @NotNull List<ParsedRoute<String>> routes = routesValidator.findRoutes("/route1/{arg}");
        Assertions.assertThat(routes).hasSize(2);
        Assertions.assertThat(routes.stream().map(ParsedRoute::attachedData)).containsExactly("a", "c");
    }

    @Test
    public void addRoute__verify_that_an_added_route_can_be_found() {
        HttpRoutesIndex<Void> routesValidator = new HttpRoutesIndex<>();
        ParsedRoute<Void> addedRoute = routesValidator.addRoute("/test/{arg-name}", "GET", null);
        Assertions.assertThat(addedRoute).isNotNull();
        Assertions.assertThat(routesValidator.hasRoute("/test/{arg-name}", "GET")).isTrue();
    }

    @Test
    public void testAddRoute__verify_that_an_added_route_can_be_found() {
        HttpRoutesIndex<String> routesValidator = new HttpRoutesIndex<>();
        ParsedRoute<String> routeToAdd = makeParsedRoute();
        ParsedRoute<String> addedRoute = routesValidator.addRoute(routeToAdd);
        Assertions.assertThat(addedRoute).isEqualTo(routeToAdd);
        Assertions.assertThat(routesValidator.hasRoute("/test/{arg-name}", "GET")).isTrue();
    }

    @Test
    public void testAddRoute__verify_that_when_a_route_already_exists_null_is_returned() {
        HttpRoutesIndex<String> routesValidator = new HttpRoutesIndex<>();
        Assertions.assertThat(routesValidator.addRoute(makeParsedRoute())).isNotNull();
        Assertions.assertThat(routesValidator.addRoute(makeParsedRoute())).isNull();
    }
}
