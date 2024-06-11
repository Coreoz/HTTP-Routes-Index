HTTP Routes Index
=================
Provide in-memory indexing and search features for HTTP routes following the OpenAPI format, e.g. `/users/{userId}/orders/{orderId}`. So route paths can contain path arguments using brackets.

Installation
------------
With Maven:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>http-routes-index</artifactId>
  <version>1.0.0</version>
</dependency>
```

Indexing routes to avoid duplication
------------------------------------
To check for routes duplication and recognize that:
- `/users/{userId}/orders/{orderId}` and `/users/{idUser}/orders/{idOrder}` are the same route
- `/users/{userId}/orders/{orderId}` and `/users/special-user/orders/special-order` are two different routes

The `HttpRoutesIndex` should be used.

Sample usage:
```java
HttpRoutesIndex<String> routesIndex = new HttpRoutesIndex<>();
routesIndex.addRoute("/users/{userId}/orders/{orderId}", "GET", "my custom data");
if (routesIndex.hasRoute("/users/{idUser}/orders/{idOrder}", "GET")) {
    // route already exists
}
```

Indexing multiple routes:
```java
List<CustomRouteType> existingCustomRoutes = // Get custom routes
HttpRoutesIndex<CustomRouteType> routesIndex = existingCustomRoutes
  .stream()
   // Convert custom route to ParsedRoute
  .map(route -> HttpRoutes.parseRoute(route.getPath(), route.getMethod(), route))
  .collect(HttpRoutesValidator.collector());
```

Router (index & search)
-----------------------
This feature enables to:
- Index routes likes `/users/{userId}` and `/users/{userId}/orders/{orderId}`
- Search the matching route for a path like `/users/123` and return the indexed route

This feature can be used to implement a HTTP Proxy or an HTTP Server.

Capabilities of this router are limited to provide good performance: it will take about the same time to search a route in a 10 routes index or in a 10 000 routes index.

Sample usage:

```java
// First create an iterable of HttpRoute: the last constructor argument enables to add custom data that can be recovered after the router search resolution
List<HttpRoute<String>> routes = List.of(new HttpRoute<>("a", "/users/{userId}", "GET", "custom-data"));
// Then create the router that indexes routes, note that it is also possible to add routes afterward with the method HttpRouter.addRoute()
HttpRouter<String> router = new HttpRouter<>(routes);
// Search a route
MatchingRoute<String> searchedRoute = router
    .search("GET", "/users/123")
    // Using the matching route object is easier to manipulate, but it results in the creation of a new HashMap and a MatchingRoute object
    // If significant load is expected, it might be best to avoid using this method to reduce the work of the garbage collector
    .toMatchingRoute();
// searchedRoute.httpRoute() contains the base route
// searchedRoute.parameterValues() contains the route parameters values
```
