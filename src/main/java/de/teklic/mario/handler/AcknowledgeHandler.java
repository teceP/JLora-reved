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

public class AcknowledgeHandler extends Handler implements Communicable {

    public AcknowledgeHandler(){
        this.setHandlerName(HandlerName.ACKNOWLEDGE_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        if(Util.isRouteXForMe(routeX)){
            forMe(routeX);
        }
    }

    @Override
    public void send(String endNode, String message) {

    }

    @Override
    public void forward(RouteX message) {

    }

    @Override
    public void forMe(RouteX message) {
        JLora.logger.info("RouteX Acknowledge is for me. Send to Messenger.");
        Messenger.getInstance().jobFinished(message);
    }
}
