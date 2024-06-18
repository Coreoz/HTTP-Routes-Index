package com.coreoz.http.routes.router;

import com.coreoz.http.routes.router.index.IndexRouteLeaf;
import com.coreoz.http.routes.router.index.IndexedRoutes;

import java.util.List;
import java.util.Map;

public class RouterMocks {

    public static List<HttpRoute<String>> endpointsTest() {
        return endpointsTest;
    }

    private static final List<HttpRoute<String>> endpointsTest = List.of(
            new HttpRoute<String>("1", "GET", "/test/chose", "/test/chose"),
            new HttpRoute<String>("2", "GET", "/test/bidule/chose", "/test/bidule/chose"),
            new HttpRoute<String>("3", "GET", "/test/{truc}/{bidule}", "/test/{truc}/{bidule}"),
            new HttpRoute<String>("4", "GET", "/test/{truc}/machin", "/test/{truc}/machin"),
            new HttpRoute<String>("5", "GET", "/test/{truc}/machin/{chose}", "/test/{chose}/machin/{truc}"),
            new HttpRoute<String>("6", "GET", "/test/{truc}/machin/truc", "/test/{truc}/machin/truc"),
            new HttpRoute<String>("7", "PUT", "/test/chouette", "/test/chouette-found"),
            new HttpRoute<String>("8", "PUT", "/test/{truc}", "/test/{truc}"),
            new HttpRoute<String>("9", "PUT", "/test/machinchouette", "/test/machinchouette-found")
    );

    public static IndexedRoutes<String> choseSegment = new IndexedRoutes<String>(
        new IndexRouteLeaf<>(
            Map.of(),
            new HttpRoute<String>("1", "GET", "/test/chose", "/test/chose")
        ),
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<String> testBiduleChoseSegment = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of(),
                    new HttpRoute<String>("2", "GET", "/test/bidule/chose", "/test/bidule/chose")
            ),
            1L << 62 | 1L << 61 | 1L << 60 | 1L << 59,
            3,
            Map.of(),
            null
    );

    public static IndexedRoutes<String> testTrucBidulePattern = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of("truc", 2, "bidule",3),
                    new HttpRoute<String>("3", "GET", "/test/{truc}/{bidule}", "/test/{truc}/{bidule}")
            ),
            1L << 62 | 1L << 61,
            3,
            Map.of(),
            null
    );

    public static IndexedRoutes<String> testTrucMachinTrucSegment = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of("truc", 2),
                    new HttpRoute<String>("6", "GET", "/test/{truc}/machin/truc", "/test/{truc}/machin/truc")
            ),
            1L << 62 | 1L << 61 | 1L << 59 | 1L << 58,
            4,
            Map.of(),
            null
    );
    public static IndexedRoutes<String> testTrucMachinChosePattern = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of("truc", 2, "chose", 4),
                    new HttpRoute<String>("5", "GET", "/test/{truc}/machin/{chose}", "/test/{chose}/machin/{truc}")
            ),
            1L << 62 | 1L << 61 | 1L << 59,
            4,
            Map.of(),
            null
    );
    public static IndexedRoutes<String> testTrucMachinSegment = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of("truc", 2),
                    new HttpRoute<String>("4", "GET", "/test/{truc}/machin", "/test/{truc}/machin")
            ),
            1L << 62 | 1L << 61 | 1L << 59,
            3,
            Map.of("truc", testTrucMachinTrucSegment),
            testTrucMachinChosePattern
    );

    public static IndexedRoutes<String> biduleSegments = new IndexedRoutes<String>(
            null,
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of("chose", testBiduleChoseSegment),
            null
    );

    public static IndexedRoutes<String> testPattern = new IndexedRoutes<String>(
            null,
            1L << 62 | 1L << 61,
            2,
            Map.of("machin", testTrucMachinSegment),
            testTrucBidulePattern
    );

    public static IndexedRoutes<String> testSegments = new IndexedRoutes<String>(
            null,
            1L << 62 | 1L << 61,
            1,
            Map.of("bidule", biduleSegments, "chose", choseSegment),
            testPattern
    );

    public static IndexedRoutes<String> putTestPattern = new IndexedRoutes<String>(
             new IndexRouteLeaf<String>(
                    Map.of("truc", 2),
                    new HttpRoute<String>("8", "PUT", "/test/{truc}", "/test/{truc}")
            ),
            1L << 62 | 1L << 61,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<String> putChouetteSegment = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of(),
                    new HttpRoute<String>("7", "PUT", "/test/chouette", "/test/chouette-found")
            ),
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<String> putMachinChouetteSegment = new IndexedRoutes<String>(
            new IndexRouteLeaf<String>(
                    Map.of(),
                    new HttpRoute<String>("9", "PUT", "/test/machinchouette", "/test/machinchouette-found")
            ),
            1L << 62 | 1L << 61 | 1L << 60,
            2,
            Map.of(),
            null
    );

    public static IndexedRoutes<String> putTestSegment = new IndexedRoutes<String>(
            null,
            1L << 62 | 1L << 61,
            1,
            Map.of("machinchouette", putMachinChouetteSegment, "chouette", putChouetteSegment),
            putTestPattern
    );

    public static Map<String, IndexedRoutes<String>> indexedRoutesByMethod = Map.of(
            "GET", new IndexedRoutes<String>(
                    null,
                    1L << 62,
                    0,
                    Map.of("test", testSegments),
                    null
            ),
            "PUT", new IndexedRoutes<String>(
                    null,
                    1L << 62,
                    0,
                    Map.of("test", putTestSegment),
                    null
            )
    );
}
