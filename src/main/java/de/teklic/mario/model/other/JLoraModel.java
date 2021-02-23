package de.teklic.mario.model.other;

import de.teklic.mario.core.JLora;
import de.teklic.mario.handler.*;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.handler.protocols.HandlerNames;
import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPort;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@Getter
@Setter
public class JLoraModel {
    private PrintWriter printWriter;
    private Scanner scanner;
    private SerialPort serialPort;
    public static String sender_addr;

    private final long REQUEST_TIMEOUT = 30000;

    private List<RouteX.RouteRequest> requestQueue;
    private HashMap<String, RouteX.Message> forwardedMessageQueue;
    private List<Handler> handlers;

    public JLoraModel(){
        handlers = new ArrayList<>();
        handlers.add(new AcknowledgeHandler());
        handlers.add(new ErrorHandler());
        handlers.add(new MessageHandler());
        handlers.add(new ReplyHandler());
        handlers.add(new RequestHandler());
        handlers.add(new UnreachableHandler());
    }

    public Handler getByClass(Class clazz){
        for(Handler h : handlers){
            if(h.getClass() == clazz){
                return h;
            }
        }
        return null;
    }

    public Handler getByName(String handlerName){
        switch(handlerName){
            case HandlerNames.ACKNOWLEDGE_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == AcknowledgeHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerNames.ERROR_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == ErrorHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerNames.MESSAGE_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == MessageHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerNames.MESSAGE_TRANSMITTER_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == UsersMessageHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerNames.REPLY_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == ReplyHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerNames.REQUEST_HANDLER:
                for(Handler h : handlers){
                    if(h.getClass() == RequestHandler.class){
                        return h;
                    }
                }
                break;
            case HandlerNames.UNREACHABLE_HANDLER:
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
