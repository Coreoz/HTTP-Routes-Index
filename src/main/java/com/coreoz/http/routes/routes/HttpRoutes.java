package com.coreoz.http.routes.routes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Provides utility methods to parse routes and paths configuration, e.g. <code>/path/{arg}/details</code>.<br>
 * See {@link ParsedPath} and {@link ParsedRoute} for types details.
 */
public class HttpRoutes {
    public static final String SEGMENT_SEPARATOR = "/";

    /**
     * Parse a complete route. E.g <code>GET /path/{path-arg}/other-path-segment</code>.
     * @param path Route path
     * @param httpMethod Route HTTP method
     * @param attachedData Some data that can be added to the route to ease later usage
     * @return An instance of {@link ParsedRoute}. No exception can be raised as long as <code>path</code> and <code>httpMethod</code> are not null
     * @param <T> The type of <code>attachedData</code>
     */
    public static @NotNull <T> ParsedRoute<T> parseRoute(@NotNull String path, @NotNull String httpMethod, @Nullable T attachedData) {
        return new ParsedRoute<>(
            parsePath(path),
            httpMethod,
            attachedData
        );
    }

    /**
     * Parse a path. E.g <code>/path/{path-arg}/other-path-segment</code>.
     * @param path A route path
     * @return An instance of {@link ParsedPath}. No exception can be raised as long as <code>path</code> is not null
     */
    public static @NotNull ParsedPath parsePath(@NotNull String path) {
        List<ParsedSegment> parsedPathSegments = parsePathAsSegments(path);
        return new ParsedPath(
            parsedPathSegments,
            serializeParsedPath(parsedPathSegments, patternName -> "{}"),
            path
        );
    }

    /**
     * Make a {@link String} for a {@link ParsedPath}.
     * @param parsedPath The parsed path to serialize
     * @param segmentPatternNameMaker The function to serialize segment routePatternIndexes. See {@link SegmentPatternNameMaker}.
     * @return The string representation of the parsedPath
     */
    public static @NotNull String serializeParsedPath(@NotNull ParsedPath parsedPath, @NotNull SegmentPatternNameMaker segmentPatternNameMaker) {
        return serializeParsedPath(parsedPath.segments(), segmentPatternNameMaker);
    }

    /**
     * Make a {@link String} for a list of path {@link ParsedSegment}.<br>
     * This function is the raw version of the {@link #serializeParsedPath(ParsedPath, SegmentPatternNameMaker)}<br>
     * <br>
     * A serialized path always starts with a "/", and a "/" is added between each segment.<br>
     * <br>
     * Some examples:<br>
     * - [] => "/"
     * - ["test"] => "/test"
     * - ["test", "arg" (isPattern), "other" => "/test/" + segmentPatternNameMaker("arg") + "/other"
     * @param parsedPathSegments The path segments to serialize
     * @param segmentPatternNameMaker The function to serialize segment routePatternIndexes. See {@link SegmentPatternNameMaker}.
     * @return The string representation of the parsedPath
     */
    public static @NotNull String serializeParsedPath(
        @NotNull List<ParsedSegment> parsedPathSegments, @NotNull SegmentPatternNameMaker segmentPatternNameMaker
    ) {
        if (parsedPathSegments.isEmpty()) {
            return SEGMENT_SEPARATOR;
        }
        StringBuilder serializingPathSegments = new StringBuilder();
        for (ParsedSegment currentSegment : parsedPathSegments) {
            serializingPathSegments.append(SEGMENT_SEPARATOR);
            if (currentSegment.isPattern()) {
                serializingPathSegments.append(segmentPatternNameMaker.generateSegmentName(currentSegment.name()));
            } else {
                serializingPathSegments.append(currentSegment.name());
            }
        }
        return serializingPathSegments.toString();
    }

    /**
     * Parse the segments of a route path. For instance <code>/users/{id}/addresses</code> will give:
     * <pre>
     * - users (pattern = false)
     * - id (pattern = true)
     * - addresses (pattern = false)
     * </pre>
     * <strong>The path must start with "/"</strong>, else the first letter will be interpreted as a "/": hence it will be skipped.<br>
     * <br>
     * All path are accepted and will not be adapted, some examples of how strange paths will be parsed:<br>
     * - <code>&lt;empty path string&gt;</code> => <code>[]</code><br>
     * - <code>/</code> => <code>[]</code><br>
     * - <code>a</code> => <code>[]</code><br>
     * - <code>abcd</code> => <code>["bcd"]</code><br>
     * - <code>/test//other</code> => <code>["test", "", "other"]</code><br>
     * - <code>/test/{}/other</code> => <code>["test", "" (isPattern), "other"]</code><br>
     * - <code>/test/{unclosed-pattern/other</code> => <code>["test", "{unclosed-pattern", "other"]</code><br>
     * - <code>/test/unclosed-pattern}/other</code> => <code>["test", "unclosed-pattern}", "other"]</code><br>
     * - <code>/test/unclosed{middle}-pattern/other</code> => <code>["test", "unclosed{middle}-pattern", "other"]</code><br>
     * <br>
     * See unit tests in HttpRoutesTest for details.
     */
    public static @NotNull List<ParsedSegment> parsePathAsSegments(@NotNull String path) {
        if (path.length() <= 1) {
            return List.of();
        }
        return Arrays.stream(
            path
                // the first slash needs to be removed, else for the route /a/b split("/") would return ['', 'a', 'b']
                .substring(1)
                .split(SEGMENT_SEPARATOR)
            )
            .map(segment -> {
                boolean isPattern = segment.length() >= 2 && segment.charAt(0) == '{' && segment.charAt(segment.length() - 1) == '}';
                String name = isPattern ?
                    segment.substring(1, segment.length() - 1) :
                    segment;
                return new ParsedSegment(name, isPattern);
            })
            .toList();
    }

    /**
     * Function to generate the name of a pattern path when serializing a {@link ParsedPath}.
     * See {@link #serializeParsedPath(ParsedPath, SegmentPatternNameMaker)} or
     * {@link #serializeParsedPath(List, SegmentPatternNameMaker)}.<br>
     * <br>
     * This function will be called only for segment pattern.
     * Some example for the route <code>/path/{path-arg}/other-path-segment</code>:<br>
     * - For the implementation <code>patternName -> "{}"</code>, <code>/path/{}/other-path-segment</code> will be generated<br>
     * - For the implementation <code>patternName -> "value"</code>, <code>/path/value/other-path-segment</code> will be generated
     */
    @FunctionalInterface
    public interface SegmentPatternNameMaker {
        @NotNull String generateSegmentName(@NotNull String originalName);
    }
}
