package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.Address;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.model.routex.TokenizedHeader;
import de.teklic.mario.util.Util;

import java.util.logging.Logger;

/**
 * The RequestHandler can handle objects which are an instance of
 * RouteX.Request.
 */
public class RequestHandler extends Handler {

    public static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(){
        this.setHandlerName(HandlerName.REQUEST_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        Util.newRoute(routeX);

        if(Util.isRouteXForMe(routeX)){
            forMe(routeX);
        }else if(Util.isRouteXForward(routeX)){
            forward(routeX);
        }
    }

    /**
     * Sends a reply to the source node of the request.
     * @param message
     */
    @Override
    public void forMe(RouteX message) {
        logger.info("Request is for me. Sending out reply.");
        if(message.getTimeToLive() >= 0){
            RouteX.RouteReply outgoingReply = new RouteX.RouteReply();
            outgoingReply.setIncoming(false);
            outgoingReply.setSource(Address.getInstance().getAddr());
            outgoingReply.setFlag(RouteFlag.REPLY);
            outgoingReply.setTimeToLive(9);
            outgoingReply.setHops(0);
            outgoingReply.setEndNode(message.getSource());
            outgoingReply.setNextNode(message.getTokenizedHeader().getOrigin());
            outgoingReply.setTokenizedHeader(new TokenizedHeader(true));
            logger.info("Outgoing reply after an incoming request which was for me: " + outgoingReply);
            logger.info("Outgoing reply as sendable: " + outgoingReply.asSendable());
            Messenger.getInstance().send(outgoingReply);
        }else{
            logger.info("Request was dropped because time to life is on " + message.getTimeToLive() + ". Minimum is 0.");
        }
    }
}
