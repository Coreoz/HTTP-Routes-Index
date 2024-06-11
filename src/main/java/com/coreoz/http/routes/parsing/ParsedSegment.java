package com.coreoz.http.routes.parsing;

/**
 * Represents a path segment.<br>
 * E.g. <code>users</code> in the <code>/users/{userId}</code> route
 * @param name The name of the segment
 * @param isPattern Indicate whether the route segment is a route pattern or note. For exemple the <code>userId</code> segment is a route pattern in the <code>/users/{userId}</code> route
 */
public record ParsedSegment(String name, boolean isPattern) {
}
