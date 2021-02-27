package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.Address;
import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.logging.Logger;

import static de.teklic.mario.routingtable.RoutingTable.NO_NEXT;

public class Util {
    public static final Logger logger = Logger.getLogger(Util.class.getName());

    public static boolean newRoute(RouteX routeX){
        RoutingTable.Route route = determineRoute(routeX);
        return Util.addRoute(route);
    }

    private static RoutingTable.Route determineRoute(RouteX routeX){
        RoutingTable.Route route = null;

        if(routeX instanceof RouteX.RouteReply){
            route = new RoutingTable.Route(Address.getInstance().getAddr(), routeX.getSource(), routeX.getTokenizedHeader().getOrigin(), ((RouteX.RouteReply) routeX).getHops());
        }else if(routeX instanceof RouteX.RouteRequest){
            route = new RoutingTable.Route(Address.getInstance().getAddr(), routeX.getSource(), routeX.getTokenizedHeader().getOrigin(), ((RouteX.RouteRequest) routeX).getHops());
        }else{
            logger.info("Add Route: No route created. RouteX object was not an instance of Reply or Request.");
        }

        return route;
    }

    private static boolean addRoute(RoutingTable.Route route) {
        if (route != null && !RoutingTable.getInstance().hasRoute(route)) {
            RoutingTable.getInstance().add(route);
            logger.info("New Route (neighbour) was added on index " + (RoutingTable.getInstance().getRouteList().size() - 1) + ": " + route);
            return true;
        }

        logger.info("No new route was added!");
        return false;
    }

    /**
     * Calculates the MD5-Hash with the own address and the payload
     * @param message
     * @return
     */
    public static String calcMd5(RouteX.Message message){
        String sender = message.getSource();
        String payload = message.getPayload();
        return calcMd5(sender, payload);
    }

    public static String calcMd5(String sender, String payload){
        String hash = DigestUtils.md5Hex(sender + payload);
        logger.info("Calculated hash: " + hash);
        return DigestUtils.md5Hex(sender + payload);
    }

    public static boolean isRouteXForMe(RouteX routeX){
        return routeX.getEndNode().equalsIgnoreCase(Address.getInstance().getAddr());
    }

    public static boolean isRouteXFromMe(RouteX routeX){
        return routeX.getSource().equalsIgnoreCase(Address.getInstance().getAddr());
    }

    public static boolean isRouteXForward(RouteX routeX){
        return !isRouteXForMe(routeX) && !isRouteXFromMe(routeX);
    }

    public static RouteX prepareToForward(RouteX routeX){
        boolean knowsNext = !RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()).equalsIgnoreCase(NO_NEXT);

        routeX.setTimeToLive(routeX.getTimeToLive() - 1);

        if(routeX instanceof RouteX.RouteReply) {
            ((RouteX.RouteReply) routeX).setHops(((RouteX.RouteReply) routeX).getHops() + 1);
            if (knowsNext) {
                JLora.logger.info("Changed next node to " + RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()) + ".");
                ((RouteX.RouteReply) routeX).setNextNode(RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()));
            }
        }

        if(routeX instanceof RouteX.Message) {
            if (knowsNext) {
                JLora.logger.info("Changed next node to " + RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()) + ".");
                ((RouteX.Message) routeX).setNextNode(RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()));
            }
        }

        if(routeX instanceof RouteX.RouteRequest){
            ((RouteX.RouteRequest) routeX).setHops(((RouteX.RouteRequest) routeX).getHops() + 1);
        }

        return routeX;
    }
}
