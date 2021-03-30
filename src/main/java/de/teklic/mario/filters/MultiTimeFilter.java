package de.teklic.mario.filters;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Drops all RouteX object which was already received in the last 10 seconds,
 * to avoid a endless loop.
 */
public class MultiTimeFilter implements Filterable {

    public static final Logger logger = Logger.getLogger(MultiTimeFilter.class.getName());

    /**
     * Holds all RouteX objects which was received in the last 10 seconds.
     */
    private List<RouteX> messageList;

    /**
     * Specifies, how low a message will be dropped if it arrives multiple times
     */
    private long minTimeDiff = 10000;

    public MultiTimeFilter() {
        messageList = new ArrayList<>();
    }

    @Override
    public RouteX filter(RouteX msg) {
        long currentTime = System.currentTimeMillis();

        //Tidy up: only item with < 5 seconds are in this list
        messageList.removeIf(m -> (currentTime - m.getTimestamp()) > minTimeDiff);

        msg = proof(msg);

        //If routeX is not a disposable, the message should be forwarded by JLora
        if(!(msg instanceof RouteX.Disposable)){
            //This msg was not found in the list. Will be added to the list
            msg.setTimestamp(currentTime);
            messageList.add(msg);
        }

        return msg;
    }

    /**
     * Proofs, if there is any colliding/matching element in the list
     * @param routeX RouteX
     * @return true if any routeX object matches the incoming new routeX, a new routeX object will be returned
     */
    private RouteX proof(RouteX routeX){
        if(messageList.stream().anyMatch(r ->
                r.getSource().equals(routeX.getSource())
                        && r.getEndNode().equals(routeX.getEndNode())
                        && r.getFlag().flag == routeX.getFlag().flag)){
            logger.info("Disposed by MultiTimeFilter.");
            return new RouteX.Disposable();
        }
        return routeX;
    }
}
