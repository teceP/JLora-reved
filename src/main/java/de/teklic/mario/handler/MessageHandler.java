package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */


import de.teklic.mario.core.Address;
import de.teklic.mario.core.JLora;
import de.teklic.mario.handler.protocols.Communicable;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.Util;

import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.DEFAULT_RETRIES;
import static de.teklic.mario.core.Constant.INITIAL_TTL;

/**
 * The MessageHandler can handle objects which are an instance of
 * RouteX.Message.
 */
public class MessageHandler extends Handler {

    public static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    public MessageHandler(){
        this.setHandlerName(HandlerName.MESSAGE_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        if(Util.isRouteXForMe(routeX)){
            forMe(routeX);
        }else if(Util.isRouteXForward(routeX)){
            forward(routeX);
        }else if(Util.isRouteXFromMe(routeX)){
            fromMe(routeX);
        }
    }

    @Override
    public void forward(RouteX message) {
        RoutingTable.getInstance().add(message);
        if(message.getTimeToLive() > 0) {
            Util.prepareToForward(message);
        }
        Messenger.getInstance().send(message);
    }

    /**
     * An incoming Message has to be acknowledged.
     * An Acknowledge will be sent out to the source node of the Message object (the initial creator).
     * @param message An RouteX Object
     */
    @Override
    public void forMe(RouteX message) {
        RoutingTable.getInstance().add(message);
        RouteX.Message m = (RouteX.Message) message;
        logger.info("New Message has reached me: '" + m.getPayload() + "' from node " + m.getSource());
        logger.info("Sending out acknowledge.");

        RouteX.Acknowledge acknowledge = new RouteX.Acknowledge();
        acknowledge.setSource(Address.getInstance().getAddr());
        acknowledge.setFlag(RouteFlag.ACKNOWLEDGE);
        acknowledge.setTimeToLive(9);
        acknowledge.setEndNode(m.getSource());
        acknowledge.setPayload(Util.calcMd5(m).substring(0, 6));
        Messenger.getInstance().send(acknowledge);
    }

    /**
     * A Message gets sent out, if a Route to the destination node exists in the Routing table.
     * If not, a request gets sent out first. When a route was found, the Messenger sends out
     * automatically the Message object to the destination.
     * @param message
     */
    public void fromMe(RouteX message){
        RouteX.Message msg = (RouteX.Message) message;
        if(RoutingTable.getInstance().hasRoute(msg.getEndNode())){
            msg.setNextNode(RoutingTable.getInstance().getNextForDestination(msg.getEndNode()));
            Messenger.getInstance().sendWithWorker(msg, DEFAULT_RETRIES);
        }else{
            logger.info("Will send out request before sending message.");
            RouteX.RouteRequest request = createRequest(msg);
            Messenger.getInstance().sendWithWorker(request, DEFAULT_RETRIES);
        }
    }

    /**
     * Creates an Request, based on the information out of a Message object.
     * @param message Message object with a concrete destination.
     * @return Route Request with Message information.
     */
    public RouteX.RouteRequest createRequest(RouteX.Message message){
        RouteX.RouteRequest request = new RouteX.RouteRequest();
        request.setSource(Address.getInstance().getAddr());
        request.setFlag(RouteFlag.REQUEST);
        request.setTimeToLive(INITIAL_TTL);
        request.setEndNode(message.getEndNode());
        request.setStoredMessage(message);
        return request;
    }
}
