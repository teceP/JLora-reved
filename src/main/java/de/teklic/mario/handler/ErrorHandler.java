package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.Util;

import java.util.logging.Logger;

/**
 * The ErrorHandler can handle objects which are an instance of
 * RouteX.Error.
 */
public class ErrorHandler extends Handler {

    public static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());

    public ErrorHandler(){
        this.setHandlerName(HandlerName.ERROR_HANDLER);
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
     * An incoming error can occur, when another node could not reach me.
     * The RoutingTable gets cleaned for this unreachable node.
     * @param message An RouteX Object
     */
    @Override
    public void forMe(RouteX message) {
        logger.info("Error is for me. Cleaning routing table.");
    }
}
