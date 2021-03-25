package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.handler.protocols.Communicable;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.Util;

import java.util.logging.Logger;

/**
 * The AcknowledgeHandler can handle objects which are an instance of
 * RouteX.Acknowledge.
 */
public class AcknowledgeHandler extends Handler {

    public static final Logger logger = Logger.getLogger(AcknowledgeHandler.class.getName());

    public AcknowledgeHandler(){
        this.setHandlerName(HandlerName.ACKNOWLEDGE_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        if(Util.isRouteXForMe(routeX)){
            forMe(routeX);
        }else if(Util.isRouteXForward(routeX)){
            forward(routeX);
        }
    }

    /**
     * If an incoming acknowledge is for me, the possibility exists, that
     * the node is currently waiting for an acknowledge object, because the node
     * send a message earlier.
     * It will be sent to the Messenger.
     * @param message An RouteX Object
     */
    @Override
    public void forMe(RouteX message) {
        logger.info("RouteX Acknowledge is for me. Send to Messenger.");
        Messenger.getInstance().update(message);
    }
}
