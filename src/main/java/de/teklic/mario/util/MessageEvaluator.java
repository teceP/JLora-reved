package de.teklic.mario.util;

import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.model.routex.TokenizedHeader;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MessageEvaluator
 */
public class MessageEvaluator {
    public static final Logger logger = Logger.getLogger(MessageEvaluator.class.getName());

    /**
     * Returns the RouteFlag for a integer value (Route value)
     * @param flag value
     * @return RouteFlag with this flag value
     */
    private static RouteFlag getRouteKind(int flag){
        return RouteFlag.valueOf(flag).get();
    }
    public static final String NO_LR = "no_lr";

    /**
     * Creates a RouteReply based on received information
     * @return RouteX.RouteReply
     */
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

    /**
     * Creates a RouteRequest based on received information
     * @return RouteX.RouteRequest
     */
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

    /**
     * Creates a Error based on received information
     * @return RouteX.Error
     */
    private static RouteX.RouteError createRouteError(String[] infos){
        RouteX.RouteError routeError = new RouteX.RouteError();
        routeError.setFlag(RouteFlag.ERROR);
        routeError.setSource(infos[0]);
        routeError.setTimeToLive(Integer.parseInt(infos[2]));
        routeError.setEndNode(infos[3]);
        routeError.setIncoming(true);
        return routeError;
    }

    /**
     * Creates a Message based on received information
     * @return RouteX.Message
     */
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

    /**
     * Creates a Acknowledge based on received information
     * @return RouteX.Acknowledge
     */
    private static RouteX.Acknowledge createAcknowledge(String[] infos){
        RouteX.Acknowledge acknowledge = new RouteX.Acknowledge();
        acknowledge.setFlag(RouteFlag.ACKNOWLEDGE);
        acknowledge.setSource(infos[0]);
        acknowledge.setTimeToLive(Integer.parseInt(infos[2]));
        acknowledge.setEndNode(infos[3]);
        acknowledge.setPayload(infos[4]);
        acknowledge.setIncoming(true);
        return acknowledge;
    }

    /**
     * Creates a RouteX based on received information
     * @return RouteX with empty tokenized header value/boolean
     */
    private static RouteX handleNotLR(String message){
        RouteX route;
        TokenizedHeader tokenizedHeader = new TokenizedHeader();
        tokenizedHeader.setEmpty(true);
        tokenizedHeader.setPlain(message);
        tokenizedHeader.setOrigin(NO_LR);
        route = new RouteX.Disposable();
        route.setFlag(RouteFlag.NO_ROUTEX);
        route.setTokenizedHeader(tokenizedHeader);
        route.setIncoming(true);
        return route;
    }

    /**
     * Creates a TokenizedHeader based on received information
     * @return TokenizedHeader
     */
    private static TokenizedHeader createHeader(String[] head){
        TokenizedHeader tokenizedHeader = new TokenizedHeader();
        tokenizedHeader.setLr(head[0]);
        tokenizedHeader.setOrigin(head[1]);
        tokenizedHeader.setLength(head[2]);
        return tokenizedHeader;
    }

    /**
     * Removes unnecessary "|" characters which where implemented by other protocol users
     * and which was described in a older version of the used protocol.
     * @param message Received message
     * @return Modified string without unnecessary characters
     */
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

    /**
     * Evaluates, which kind of RouteX object a received String message is
     * @param message Plain input string
     * @return the created RouteX object, based on the message parameter
     */
    public static RouteX evaluate(String message) {
        RouteX route;
        TokenizedHeader tokenizedHeader;

        try{
            if(!message.contains("LR,")){
                logger.info("Does not contains LR.");
                return handleNotLR(message);
            }

            //LR,0139,0F,|0139|3|6|2|0140|
            //-> LR
            //-> 0139
            //-> 0F
            //-> |0139|...|0140|

            String[] head = message.split(",", 4);
            tokenizedHeader = createHeader(head);

            //Remove | at begin and ending
            head[head.length-1] = removeUnnecessary(head[head.length-1]);

            //-> From|Flag|...|To
            String[] tail = head[head.length-1].split("\\|");
            Arrays.asList(tail).stream().forEach(x -> logger.log(Level.FINE, "Tail: " + x));

            if(tail.length < 2){
                logger.info("Tail not long enough. Needs to contain more than 2 informations. Tail length: " + tail.length);
                logger.info("Length: " + tail.length);
                logger.info("Tail: " + (head[head.length-1]));
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
                case MESSAGE:
                    route = createMessage(tail);
                    break;
                case ACKNOWLEDGE:
                    route = createAcknowledge(tail);
                    break;
                default:
                    logger.info("Route flag is not known. Flag was: " + getRouteKind(Integer.parseInt(tail[1])));
                    return handleNotLR(message);
            }
            route.setTokenizedHeader(tokenizedHeader);
            return route;
        }catch (Exception e){
            logger.info("Error while evaluating String '" + message + "'.");
            return handleNotLR(message);
        }
    }
}
