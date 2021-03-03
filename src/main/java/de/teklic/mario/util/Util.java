package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.Address;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.logging.Logger;

import static de.teklic.mario.routingtable.RoutingTable.NO_NEXT;

/**
 * Utilities which are used across the JLora software
 */
public class Util {
    public static final Logger logger = Logger.getLogger(Util.class.getName());

    /**
     * A RouteX object gets examined, eventually parsed into a Route object and
     * added to the RoutingTable
     * @param routeX
     * @return true if a new route was added, false if not because no Route could be created or this route already exists.
     */
    public static boolean newRoute(RouteX routeX){
        RoutingTable.Route route = determineRoute(routeX);
        return Util.addRoute(route);
    }

    /**
     * Determines a Route out of a RouteX object
     * @param routeX RouteX
     * @return Route object, based on the RouteX object
     */
    private static RoutingTable.Route determineRoute(RouteX routeX){
        RoutingTable.Route route = null;

        if(routeX instanceof RouteX.RouteReply){
            route = new RoutingTable.Route(Address.getInstance().getAddr(), routeX.getSource(), routeX.getTokenizedHeader().getOrigin(), ((RouteX.RouteReply) routeX).getHops());
        }else if(routeX instanceof RouteX.RouteRequest){
            route = new RoutingTable.Route(Address.getInstance().getAddr(), routeX.getSource(), routeX.getTokenizedHeader().getOrigin(), ((RouteX.RouteRequest) routeX).getHops());
        }else{
            logger.info("Add Route: No route created. RouteX object was not an instance of Reply or Request. Was "+ routeX.getFlag());
        }

        return route;
    }

    /**
     * Adds a Route if this route is not already present in the RoutingTable
     * @param route Route
     * @return true if route was added
     */
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
     * Calculates the MD5-Hash with the own address and the payload out of an RouteX object
     * @param message
     * @return
     */
    public static String calcMd5(RouteX.Message message){
        String sender = message.getSource();
        String payload = message.getPayload();
        return calcMd5(sender, payload);
    }

    /**
     * Calculates the MD5-Hash with the own address and the payload
     * @param sender Sender/Own address
     * @param payload Payload
     * @return MD5 Hash as String
     */
    public static String calcMd5(String sender, String payload){
        String hash = DigestUtils.md5Hex(sender + payload);
        logger.info("Calculated hash: " + hash);
        return DigestUtils.md5Hex(sender + payload);
    }

    /**
     * Determines if a RouteX is FOR me
     * @param routeX RouteX
     * @return true if is for me
     */
    public static boolean isRouteXForMe(RouteX routeX){
        return routeX.getEndNode().equalsIgnoreCase(Address.getInstance().getAddr());
    }

    /**
     * Determines if a RouteX is FROM me
     * @param routeX RouteX
     * @return true if is from me
     */
    public static boolean isRouteXFromMe(RouteX routeX){
        return routeX.getSource().equalsIgnoreCase(Address.getInstance().getAddr());
    }

    /**
     * Determines if a RouteX object must be forwarded
     * @param routeX RouteX
     * @return true if must be forwarded
     */
    public static boolean isRouteXForward(RouteX routeX){
        return !isRouteXForMe(routeX) && !isRouteXFromMe(routeX);
    }

    /**
     * Prepares a RouteX object to be forwarded.
     * Sets down the TimeToLive, if not 0!
     * Count up the hops if has this variable.
     *
     * @param routeX RouteX
     * @return the modified RouteX object
     */
    public static RouteX prepareToForward(RouteX routeX){
        boolean knowsNext = !RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()).equalsIgnoreCase(NO_NEXT);

        if(routeX.getTimeToLive() > 0){
            routeX.setTimeToLive(routeX.getTimeToLive() - 1);
        }

        if(routeX instanceof RouteX.RouteReply) {
            ((RouteX.RouteReply) routeX).setHops(((RouteX.RouteReply) routeX).getHops() + 1);
            if (knowsNext) {
                logger.info("Changed next node to " + RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()) + ".");
                ((RouteX.RouteReply) routeX).setNextNode(RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()));
            }
        }

        if(routeX instanceof RouteX.Message) {
            if (knowsNext) {
                logger.info("Changed next node to " + RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()) + ".");
                ((RouteX.Message) routeX).setNextNode(RoutingTable.getInstance().getNextForDestination(routeX.getEndNode()));
            }
        }

        if(routeX instanceof RouteX.RouteRequest){
            ((RouteX.RouteRequest) routeX).setHops(((RouteX.RouteRequest) routeX).getHops() + 1);
        }

        return routeX;
    }
}
