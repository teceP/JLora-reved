package de.teklic.mario.core;

import de.teklic.mario.handler.*;
import de.teklic.mario.handler.protocols.HandlerName;
import de.teklic.mario.input.SerialPortListener;
import de.teklic.mario.input.UserInput;
import de.teklic.mario.model.other.JLoraModel;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.MessageEvaluator;
import de.teklic.mario.util.UserService;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JLora extends Observable implements Observer {
    public static final Logger logger = Logger.getLogger(JLora.class.getName());

    private JLoraModel jLoraModel;

    public JLora(){
        try {
            this.jLoraModel = Initializer.initialize(this);
            logger.info("Software initialized. Listening for messages now.");
            while(true){}
        } catch (Exception e) {
            logger.info("Failed to initialize software.");
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof String && arg != null && !((String) arg).isEmpty() || arg instanceof RouteX.Message && arg != null){
            System.out.println("******************NEW PROCESS*************************");
            logger.info("Object class: " + arg.getClass() +  ", from " + (o instanceof UserInput ? "UserInput" : "SerialInput"));
            System.out.println("[Received Text]: " + arg);

            if(o instanceof UserInput && arg instanceof String){
                UserService.getInstance().handle((String) arg);
            }else{
                RouteX routeX;
                if(arg instanceof String){
                    routeX = MessageEvaluator.evaluate((String) arg);
                }else{
                    routeX = (RouteX.Message) arg;
                }
                logger.info("RouteX after evaluation: " + routeX.toString());
                distributeToHandler(routeX);
            }
        }else{
            logger.info("Error: wont be send to message evaluator.");
            logger.info((arg instanceof String ? "Was instance of String.":"Was no instance of String: " + arg.getClass().getSimpleName()));
            logger.info((arg == null ? "Was not null.":"Was null."));
            logger.info((((String) arg).isEmpty() ? "Was empty.":"Was not empty."));
        }
    }

    public void distributeToHandler(RouteX routeX){
        if(hasMessengerRelevance(routeX)){
            setChanged();
            notifyObservers(routeX);
        }else{
            jLoraModel.getHandlers()
                    .stream()
                    .filter(handler -> routeX.responsibleHandler().equalsIgnoreCase(handler.getHandlerName()))
                    .collect(Collectors.toList())
                    .get(0)
                    .handle(routeX);
        }
    }

    public boolean hasMessengerRelevance(RouteX routeX){
        return true;
    }
}
