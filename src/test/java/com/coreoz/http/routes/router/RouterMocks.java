package com.coreoz.http.routes.router;

import com.coreoz.http.routes.router.index.IndexRouteLeaf;
import com.coreoz.http.routes.router.index.IndexedRoutes;

import java.util.List;
import java.util.Map;

public class RouterMocks {

    public static List<MockHttpRoute> endpointsTest() {
        return endpointsTest;
    }

    private static final List<MockHttpRoute> endpointsTest = List.of(
            new MockHttpRoute("1", "GET", "/test/chose", "/test/chose"),
            new MockHttpRoute("2", "GET", "/test/bidule/chose", "/test/bidule/chose"),
            new MockHttpRoute("3", "GET", "/test/{truc}/{bidule}", "/test/{truc}/{bidule}"),
            new MockHttpRoute("4", "GET", "/test/{truc}/machin", "/test/{truc}/machin"),
            new MockHttpRoute("5", "GET", "/test/{truc}/machin/{chose}", "/test/{chose}/machin/{truc}"),
            new MockHttpRoute("6", "GET", "/test/{truc}/machin/truc", "/test/{truc}/machin/truc"),
            new MockHttpRoute("7", "PUT", "/test/chouette", "/test/chouette-found"),
            new MockHttpRoute("8", "PUT", "/test/{truc}", "/test/{truc}"),
            new MockHttpRoute("9", "PUT", "/test/machinchouette", "/test/machinchouette-found")
    );

    public static IndexedRoutes<MockHttpRoute> choseSegment = new IndexedRoutes<>(
        new IndexRouteLeaf<>(
            Map.of(),
            new MockHttpRoute("1", "GET", "/test/chose", "/test/chose")
        ),
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<MockHttpRoute> testBiduleChoseSegment = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of(),
                    new MockHttpRoute("2", "GET", "/test/bidule/chose", "/test/bidule/chose")
            ),
            1L << 62 | 1L << 61 | 1L << 60 | 1L << 59,
            3,
            Map.of(),
            null
    );

    public static IndexedRoutes<MockHttpRoute>testTrucBidulePattern = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of("truc", 2, "bidule",3),
                    new MockHttpRoute("3", "GET", "/test/{truc}/{bidule}", "/test/{truc}/{bidule}")
            ),
            1L << 62 | 1L << 61,
            3,
            Map.of(),
            null
    );

    public static IndexedRoutes<MockHttpRoute>testTrucMachinTrucSegment = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of("truc", 2),
                    new MockHttpRoute("6", "GET", "/test/{truc}/machin/truc", "/test/{truc}/machin/truc")
            ),
            1L << 62 | 1L << 61 | 1L << 59 | 1L << 58,
            4,
            Map.of(),
            null
    );
    public static IndexedRoutes<MockHttpRoute>testTrucMachinChosePattern = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of("truc", 2, "chose", 4),
                    new MockHttpRoute("5", "GET", "/test/{truc}/machin/{chose}", "/test/{chose}/machin/{truc}")
            ),
            1L << 62 | 1L << 61 | 1L << 59,
            4,
            Map.of(),
            null
    );
    public static IndexedRoutes<MockHttpRoute>testTrucMachinSegment = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of("truc", 2),
                    new MockHttpRoute("4", "GET", "/test/{truc}/machin", "/test/{truc}/machin")
            ),
            1L << 62 | 1L << 61 | 1L << 59,
            3,
            Map.of("truc", testTrucMachinTrucSegment),
            testTrucMachinChosePattern
    );

    public static IndexedRoutes<MockHttpRoute>biduleSegments = new IndexedRoutes<>(
            null,
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of("chose", testBiduleChoseSegment),
            null
    );

    public static IndexedRoutes<MockHttpRoute>testPattern = new IndexedRoutes<>(
            null,
            1L << 62 | 1L << 61,
            2,
            Map.of("machin", testTrucMachinSegment),
            testTrucBidulePattern
    );

    public static IndexedRoutes<MockHttpRoute>testSegments = new IndexedRoutes<>(
            null,
            1L << 62 | 1L << 61,
            1,
            Map.of("bidule", biduleSegments, "chose", choseSegment),
            testPattern
    );

    public static IndexedRoutes<MockHttpRoute>putTestPattern = new IndexedRoutes<>(
             new IndexRouteLeaf<>(
                    Map.of("truc", 2),
                    new MockHttpRoute("8", "PUT", "/test/{truc}", "/test/{truc}")
            ),
            1L << 62 | 1L << 61,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<MockHttpRoute>putChouetteSegment = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of(),
                    new MockHttpRoute("7", "PUT", "/test/chouette", "/test/chouette-found")
            ),
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<MockHttpRoute>putMachinChouetteSegment = new IndexedRoutes<>(
            new IndexRouteLeaf<>(
                    Map.of(),
                    new MockHttpRoute("9", "PUT", "/test/machinchouette", "/test/machinchouette-found")
            ),
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<MockHttpRoute>putTestSegment = new IndexedRoutes<>(
            null,
            1L << 62 | 1L << 61,
            1,
            Map.of("machinchouette", putMachinChouetteSegment, "chouette", putChouetteSegment),
            putTestPattern
    );

    public static Map<String, IndexedRoutes<MockHttpRoute>> indexedRoutesByMethod = Map.of(
            "GET", new IndexedRoutes<>(
                    null,
                    1L << 62,
                    0,
                    Map.of("test", testSegments),
                    null
            ),
            "PUT", new IndexedRoutes<>(
                    null,
                    1L << 62,
                    0,
                    Map.of("test", putTestSegment),
                    null
            )
    );
}
