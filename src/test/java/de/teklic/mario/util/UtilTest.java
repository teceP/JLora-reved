package de.teklic.mario.util;

import de.teklic.mario.core.Address;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.teklic.mario.core.Constant.INITIAL_TTL;
import static org.junit.jupiter.api.Assertions.*;

/*
 *
 * @author Mario Teklic
 */

class UtilTest {

    @Test
    void testCalcMd5() {
        RouteX.Message routeX = new RouteX.Message();
        routeX.setSource("0140");
        routeX.setPayload("hallo");
       Assertions.assertEquals("232ad2f6279531527cfff989555c4519", Util.calcMd5(routeX));
    }

    @Test
    void testCalcMd5False() {
        RouteX.Message routeX = new RouteX.Message();
        routeX.setSource("0140");
        routeX.setPayload("hallo");
        Assertions.assertNotEquals("zzzz", Util.calcMd5(routeX));
    }

    @Test
    void isRouteXForMe() {
        Address.getInstance().setAddr("0140");
        RouteX routeX = new RouteX.RouteRequest();
        routeX.setEndNode("0140");
        Assertions.assertTrue(Util.isRouteXForMe(routeX));
    }

    @Test
    void isRouteXForMeFalse() {
        Address.getInstance().setAddr("0130");
        RouteX routeX = new RouteX.RouteRequest();
        routeX.setEndNode("0140");
        Assertions.assertFalse(Util.isRouteXForMe(routeX));
    }

    @Test
    void isRouteXFromMe() {
        Address.getInstance().setAddr("0140");
        RouteX.Message routeX = new RouteX.Message();
        routeX.setSource("0140");
        Assertions.assertTrue(Util.isRouteXFromMe(routeX));
    }

    @Test
    void isRouteXFromMeFalse() {
        Address.getInstance().setAddr("0130");
        RouteX.Message routeX = new RouteX.Message();
        routeX.setSource("0140");
        Assertions.assertFalse(Util.isRouteXFromMe(routeX));
    }

    @Test
    void isRouteXForward() {
        Address.getInstance().setAddr("0130");
        RouteX.Message routeX = new RouteX.Message();
        routeX.setSource("0140");
        routeX.setEndNode("139");
        Assertions.assertTrue(Util.isRouteXForward(routeX));
    }

    @Test
    void isRouteXForwardFalse() {
        Address.getInstance().setAddr("0130");
        RouteX.Message routeX = new RouteX.Message();
        routeX.setSource("0140");
        routeX.setEndNode("130");
        Assertions.assertTrue(Util.isRouteXForward(routeX));
    }

    @Test
    void prepareToForward() {
        RoutingTable.getInstance().add(new RoutingTable.Route("0131", "0140", "0137", 1));
        RouteX.RouteRequest routeX = new RouteX.RouteRequest();
        routeX.setEndNode("0140");
        routeX.setTimeToLive(INITIAL_TTL);
        routeX = (RouteX.RouteRequest) Util.prepareToForward(routeX);
        Assertions.assertTrue(routeX.getTimeToLive() == 4);
        Assertions.assertTrue(routeX.getHops() == 1);
    }

    @Test
    void prepareToForwardNext() {
        RoutingTable.getInstance().add(new RoutingTable.Route("0131", "0140", "0137", 1));
        RouteX.RouteReply routeX = new RouteX.RouteReply();
        routeX.setEndNode("0140");
        routeX = (RouteX.RouteReply) Util.prepareToForward(routeX);
        Assertions.assertTrue(routeX.getNextNode().equals("0137"));
    }
}
