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

public class ReplyHandler extends Handler implements Communicable {

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

    @Override
    public void forward(RouteX message) {
        message = Util.prepareToForward(message);
        Messenger.getInstance().send(message);
    }

    @Override
    public void forMe(RouteX message) {
        JLora.logger.info("RouteX Reply is for me. Send to Messenger.");
        Messenger.getInstance().jobFinished(message);
    }
}
