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
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.Util;

import static de.teklic.mario.core.Constant.DEFAULT_RETRIES;
import static de.teklic.mario.core.Constant.INITIAL_TTL;
import static de.teklic.mario.routingtable.RoutingTable.NO_NEXT;

public class MessageHandler extends Handler implements Communicable {

    public MessageHandler(){
        this.setHandlerName(HandlerName.MESSAGE_HANDLER);
    }

    @Override
    public void handle(RouteX routeX) {
        JLora.logger.info("MESSAGE HANDLER.");
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
        Util.prepareToForward(message);
        Messenger.getInstance().send(message);
    }

    @Override
    public void forMe(RouteX message) {
        RouteX.Message m = (RouteX.Message) message;
        JLora.logger.info("New Message has reached me: '" + m.getPayload() + "' from node " + m.getSource());
        JLora.logger.info("Sending out acknowledge.");

        RouteX.Acknowledge acknowledge = new RouteX.Acknowledge();
        acknowledge.setFlag(RouteFlag.ACKNOWLEDGE);
        acknowledge.setTimeToLive(9);
        acknowledge.setEndNode(m.getSource());
        acknowledge.setPayload(Util.calcMd5(m));
        Messenger.getInstance().sendWithWorker(m, DEFAULT_RETRIES);
    }

    public void fromMe(RouteX message){
        JLora.logger.info("FROM ME (MESSAGEHANDLER)");
        JLora.logger.info("HasRoute: " + RoutingTable.getInstance().hasRoute(message.getEndNode()));
        JLora.logger.info("endNOde: " + message.getEndNode());

        if(RoutingTable.getInstance().hasRoute(message.getEndNode()) || !message.getEndNode().equalsIgnoreCase(NO_NEXT)){
            Messenger.getInstance().sendWithWorker(message, 3);
        }else{
            RouteX.RouteRequest request = createRequest((RouteX.Message) message);
            Messenger.getInstance().sendWithWorker(request, 3);
        }
    }

    public RouteX.RouteRequest createRequest(RouteX.Message message){
        RouteX.RouteRequest request = new RouteX.RouteRequest();
        request.setFlag(RouteFlag.REQUEST);
        request.setTimeToLive(INITIAL_TTL);
        request.setEndNode(message.getEndNode());
        request.setStoredMessage(message);
        return request;
    }
}
