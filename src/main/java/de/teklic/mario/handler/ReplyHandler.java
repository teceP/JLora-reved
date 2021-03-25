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
 * The ReplyHandler can handle objects which are an instance of
 * RouteX.Reply.
 */
public class ReplyHandler extends Handler {

    public static final Logger logger = Logger.getLogger(ReplyHandler.class.getName());

    public ReplyHandler(){
        this.setHandlerName(HandlerName.REPLY_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        Util.newRoute(routeX);

        if (Util.isRouteXForMe(routeX)) {
            forMe(routeX);
        }else if(Util.isRouteXForward(routeX)){
            forward(routeX);
        }
    }

    /**
     * If an incoming reply is for me, the possibility exists, that
     * the node is currently waiting for an reply object, because the node
     * send a request earlier.
     * It will be sent to the Messenger.
     * @param message An RouteX Object
     */
    @Override
    public void forMe(RouteX message) {
        logger.info("RouteX Reply is for me. Send to Messenger.");
        Messenger.getInstance().update(message);
    }
}
