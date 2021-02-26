package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import org.apache.commons.codec.digest.DigestUtils;

import static de.teklic.mario.model.other.JLoraModel.SENDER_ADDR;
import static de.teklic.mario.routingtable.RoutingTable.NO_NEXT;

public class Util {

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
        System.out.println("Calculated hash: " + hash);
        System.out.println("Shortend hash: " + hash.substring(0, 5));
        return DigestUtils.md5Hex(sender + payload);
    }

    public static boolean isRouteXForMe(RouteX routeX){
        return routeX.getEndNode().equalsIgnoreCase(SENDER_ADDR);
    }

    public static boolean isRouteXSend(RouteX routeX){
        return routeX.getSource().equalsIgnoreCase(SENDER_ADDR);
    }

    public static boolean isRouteXForward(RouteX routeX){
        return !isRouteXForMe(routeX) && !isRouteXSend(routeX);
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
