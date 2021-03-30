package de.teklic.mario.filters;

import de.teklic.mario.model.routex.RouteX;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 *
 * @author Mario Teklic
 */

class MultiTimeFilterTest {
    @Test
    void filter() {
        RouteX.RouteRequest req = new RouteX.RouteRequest();
        req.setSource("0140");
        req.setEndNode("0140");

        MultiTimeFilter filter = new MultiTimeFilter();

        /**
         * Would return new RouteX.Disposable, if gets filtered out
         */
        RouteX obj = filter.filter(req);

        Assertions.assertTrue(obj instanceof RouteX.RouteRequest);
        Assertions.assertFalse(obj instanceof RouteX.Disposable);

        RouteX obj2 = filter.filter(req);

        Assertions.assertTrue(obj2 instanceof RouteX.Disposable);
        Assertions.assertFalse(obj2 instanceof RouteX.RouteRequest);
    }

    @Test
    void proof() {
    }
}
