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
import de.teklic.mario.model.routex.TokenizedHeader;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.Util;

import static de.teklic.mario.core.Constant.BROADCAST;
import static de.teklic.mario.core.Constant.DEFAULT_RETRIES;
import static de.teklic.mario.model.other.JLoraModel.SENDER_ADDR;

public class RequestHandler extends Handler implements Communicable {

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

    @Override
    public void forward(RouteX message) {
        message = Util.prepareToForward(message);
        Messenger.getInstance().send(message);
    }

    /**
     * Sends a reply to the source node of the request.
     * @param message
     */
    @Override
    public void forMe(RouteX message) {
        JLora.logger.info("Request is for me. Sending out reply.");
        if(message.getTimeToLive() >= 0){
            RouteX.RouteReply outgoingReply = new RouteX.RouteReply();
            outgoingReply.setIncoming(false);
            outgoingReply.setSource(SENDER_ADDR);
            outgoingReply.setFlag(RouteFlag.REPLY);
            outgoingReply.setTimeToLive(9);
            outgoingReply.setHops(0);
            outgoingReply.setEndNode(message.getSource());
            outgoingReply.setNextNode(message.getTokenizedHeader().getOrigin());
            outgoingReply.setTokenizedHeader(new TokenizedHeader(true));
            JLora.logger.info("Outgoing reply after an incoming reply which was for me: " + outgoingReply);
            JLora.logger.info("Outgoing reply as sendable: " + outgoingReply.asSendable());
            Messenger.getInstance().sendWithWorker(outgoingReply, DEFAULT_RETRIES);
        }else{
            JLora.logger.info("Request was dropped because time to life is on " + message.getTimeToLive() + ". Minimum is 0.");
        }
    }
}
