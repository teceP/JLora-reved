package de.teklic.mario.model.other;

import de.teklic.mario.core.JLora;
import de.teklic.mario.handler.*;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPort;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class JLoraModel {
    private Thread userInputThread;
    private SerialPort serialPort;
    public static String SENDER_ADDR;
    private final long REQUEST_TIMEOUT = 30000;

    private List<RouteX.RouteRequest> requestQueue;
    private HashMap<String, RouteX.Message> forwardedMessageQueue;
    private List<Handler> handlers;

    public Handler getHandlerByClass(Class clazz){
        for(Handler h : handlers){
            if(h.getClass() == clazz){
                return h;
            }
        }
        return null;
    }

    public Handler getHandlerByName(String handlerName){
        switch(handlerName){
            case HandlerName.ACKNOWLEDGE_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == AcknowledgeHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerName.ERROR_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == ErrorHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerName.MESSAGE_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == MessageHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerName.MESSAGE_TRANSMITTER_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == UsersMessageHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerName.REPLY_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == ReplyHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerName.REQUEST_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == RequestHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerName.UNREACHABLE_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == UnreachableHandler.class){
                        return h;
                    }
                }
                break;
            default:
                JLora.logger.info("Handler by name was not successful for name '" + handlerName + "'.");
        }
        return null;
    }
}
