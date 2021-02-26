package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */


import de.teklic.mario.handler.protocols.Communicable;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.Util;

public class MessageHandler extends Handler implements Communicable {

    public MessageHandler(){
        this.setHandlerName(HandlerName.MESSAGE_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        if(Util.isRouteXForMe(routeX)){
            forMe(routeX);
        }else if(Util.isRouteXForward(routeX)){
            forward(routeX);
        }
    }

    @Override
    public void send(String endNode, String message) {

    }

    @Override
    public void forward(RouteX message) {
        Util.prepareToForward(message);
        Messenger.getInstance().send(message);
    }

    @Override
    public void forMe(RouteX message) {

    }
}
