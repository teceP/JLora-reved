package de.teklic.mario.util;

import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.model.routex.TokenizedHeader;

import java.util.Arrays;
import java.util.logging.Level;

public class MessageEvaluator {

    private static RouteFlag getRouteKind(int flag){
        return RouteFlag.valueOf(flag).get();
    }
    public static final String NO_LR = "no_lr";

    private static RouteX.RouteReply createRouteReply(String[] infos){
        RouteX.RouteReply routeReply = new RouteX.RouteReply();
        routeReply.setFlag(RouteFlag.REPLY);
        routeReply.setSource(infos[0]);
        routeReply.setTimeToLive(Integer.parseInt(infos[2]));
        routeReply.setHops(Integer.parseInt(infos[3]));
        routeReply.setEndNode(infos[4]);
        routeReply.setNextNode(infos[5]);
        routeReply.setIncoming(true);
        return routeReply;
    }

    private static RouteX.RouteRequest createRouteRequest(String[] infos){
        RouteX.RouteRequest routeRequest = new RouteX.RouteRequest();
        routeRequest.setFlag(RouteFlag.REQUEST);
        routeRequest.setSource(infos[0]);
        routeRequest.setTimeToLive(Integer.parseInt(infos[2]));
        routeRequest.setHops(Integer.parseInt(infos[3]));
        routeRequest.setEndNode(infos[4]);
        routeRequest.setIncoming(true);
        return routeRequest;
    }

    private static RouteX.RouteError createRouteError(String[] infos){
        RouteX.RouteError routeError = new RouteX.RouteError();
        routeError.setFlag(RouteFlag.ERROR);
        routeError.setSource(infos[0]);
        routeError.setTimeToLive(Integer.parseInt(infos[2]));
        routeError.setEndNode(infos[3]);
        routeError.setIncoming(true);
        return routeError;
    }

    private static RouteX.RouteUnreachable createRouteUnreacheable(String[] infos){
        RouteX.RouteUnreachable routeUnreachable = new RouteX.RouteUnreachable();
        routeUnreachable.setFlag(RouteFlag.UNREACHABLE);
        routeUnreachable.setSource(infos[0]);
        routeUnreachable.setTimeToLive(Integer.parseInt(infos[2]));
        routeUnreachable.setEndNode(infos[3]);
        routeUnreachable.setIncoming(true);
        return routeUnreachable;
    }

    private static RouteX.Message createMessage(String[] infos){
        RouteX.Message message = new RouteX.Message();
        message.setFlag(RouteFlag.MESSAGE);
        message.setSource(infos[0]);
        message.setTimeToLive(Integer.parseInt(infos[2]));
        message.setEndNode(infos[3]);
        message.setNextNode(infos[4]);
        message.setPayload(infos[5]);
        return message;
    }

    private static RouteX.Acknowledge createAcknowledge(String[] infos){
        RouteX.Acknowledge acknowledge = new RouteX.Acknowledge();
        acknowledge.setFlag(RouteFlag.ACKNOWLEDGE);
        acknowledge.setTimeToLive(Integer.parseInt(infos[1]));
        acknowledge.setEndNode(infos[2]);
        acknowledge.setPayload(infos[3]);
        acknowledge.setIncoming(true);
        return acknowledge;
    }

    private static RouteX handleNotLR(String message){
        RouteX route;
        TokenizedHeader tokenizedHeader = new TokenizedHeader();
        //logger.log(Level.WARNING, "Not a Peer Message.");
        tokenizedHeader.setEmpty(true);
        tokenizedHeader.setPlain(message);
        tokenizedHeader.setOrigin(NO_LR);
        route = new RouteX.Disposable();
        route.setFlag(RouteFlag.NO_ROUTEX);
        route.setTokenizedHeader(tokenizedHeader);
        route.setIncoming(true);
        return route;
    }

    private static TokenizedHeader createHeader(String[] head){
        TokenizedHeader tokenizedHeader = new TokenizedHeader();
        tokenizedHeader.setLr(head[0]);
        tokenizedHeader.setOrigin(head[1]);
        tokenizedHeader.setLength(head[2]);
        return tokenizedHeader;
    }

    private static String removeUnnecessary(String message){
        StringBuilder builder = new StringBuilder(message);
        if(message.length() > 2){
            if(message.charAt(message.length()-1) == '|'){
                builder.deleteCharAt(message.length()-1);
            }

            if(message.charAt(0) == '|'){
                builder.deleteCharAt(0);
            }
        }

        return builder.toString();
    }

    public static RouteX evaluate(String message) {
        RouteX route;
        TokenizedHeader tokenizedHeader;

        try{
            if(!message.contains("LR,")){
                System.out.println("Does not contains LR.");
                return handleNotLR(message);
            }

            //LR,0139,0F,|0139|3|6|2|0140|
            //-> LR
            //-> 0139
            //-> 0F
            //-> |0139|...|0140|

            String[] head = message.split(",");
            tokenizedHeader = createHeader(head);

            //Remove | at begin and ending
            head[head.length-1] = removeUnnecessary(head[head.length-1]);

            //-> From|Flag|...|To
            String[] tail = head[head.length-1].split("\\|");
            Arrays.asList(tail).stream().forEach(x -> JLora.logger.log(Level.FINE, "Tail: " + x));

            if(tail.length < 2){
                System.out.println("Tail not long enough. Needs to contain more than 2 informations." + tail.length);
                System.out.println("Length: " + tail.length);
                System.out.println("Tail: " + (head[head.length-1]));
                return handleNotLR(message);
            }
            switch (getRouteKind(Integer.parseInt(tail[1]))) {
                case ERROR:
                    route = createRouteError(tail);
                    break;
                case REPLY:
                    route = createRouteReply(tail);
                    break;
                case REQUEST:
                    route = createRouteRequest(tail);
                    break;
                case UNREACHABLE:
                    route = createRouteUnreacheable(tail);
                    break;
                case MESSAGE:
                    route = createMessage(tail);
                    break;
                case ACKNOWLEDGE:
                    route = createAcknowledge(tail);
                default:
                    System.out.println("Route flag is not known. Flag was: " + getRouteKind(Integer.parseInt(tail[1])));
                    return handleNotLR(message);
            }
            route.setTokenizedHeader(tokenizedHeader);
            return route;
        }catch (Exception e){
            e.printStackTrace();
            return handleNotLR(message);
        }
    }
}
