package de.teklic.mario.core;

import de.teklic.mario.io.input.UserInput;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.other.JLoraModel;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.MessageEvaluator;
import de.teklic.mario.util.UserService;
import de.teklic.mario.util.Util;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The central/main entrance point for this software.
 */
public class JLora implements Observer {
    public static final Logger logger = Logger.getLogger(JLora.class.getName());

    /**
     * Holds several information, such as the handler references and the serial port.
     */
    private JLoraModel jLoraModel;

    /**
     * Specifies, if JLora is currently allowed to listen for new messages.
     */
    private boolean listening;

    public JLora() {
    }

    /**
     * Starts the initialization and an endless loop to listen for new events
     * @param addr The nodes address
     */
    public void start(String addr) {
        try {
            this.jLoraModel = Initializer.initialize(this, addr);
            logger.info("Software initialized. Listening for messages now.");
            while (true) {
            }
        } catch (Exception e) {
            logger.info("Failed to initialize software.");
            e.printStackTrace();
        }
    }

    /**
     * Sets if the central is allowed to hear from new events or not
     * @param listen Listen is allowed
     */
    public void setListening(boolean listen){
        listening = listen;
    }

    /**
     * An new event has happend and the event gets evaluated and
     * distributed to the correct handler.
     * @param o Instance of observable
     * @param arg Event (always as String!)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (listening) {
            if (arg instanceof String && arg != null && !((String) arg).isEmpty() || arg instanceof RouteX.Message && arg != null) {
                logger.info("******************NEW PROCESS*************************");
                logger.info("Object class: " + arg.getClass() + ", from " + (o instanceof UserInput ? "UserInput" : "SerialInput"));
                logger.info("[Received Text]: " + arg);

                if (o instanceof UserInput && arg instanceof String) {
                    UserService.getInstance().handle((String) arg);
                } else {
                    RouteX routeX;
                    if (arg instanceof String) {
                        routeX = MessageEvaluator.evaluate((String) arg);
                    } else {
                        routeX = (RouteX.Message) arg;
                    }
                    logger.info("RouteX after evaluation: " + routeX.toString());
                    distributeToHandler(routeX);
                }
            } else {
                logger.info("Error: wont be send to message evaluator.");
                logger.info((arg instanceof String ? "Was instance of String." : "Was no instance of String: " + arg.getClass().getSimpleName()));
                logger.info((arg == null ? "Was not null." : "Was null."));
                logger.info((((String) arg).isEmpty() ? "Was empty." : "Was not empty."));
            }
        }
    }

    /**
     * Distributes to the handlers based on the RouteX instance
     * @param routeX RouteX
     */
    public void distributeToHandler(RouteX routeX) {
        if (routeX instanceof RouteX.Disposable) {
            logger.info("RouteX is an disposable object and will be dropped.");
            return;
        }

        if (hasMessengerRelevance(routeX) && Util.isRouteXForMe(routeX)) {
            Messenger.getInstance().update(routeX);
        } else {
            jLoraModel.getHandlers()
                    .stream()
                    .filter(handler -> routeX.responsibleHandler().equalsIgnoreCase(handler.getHandlerName()))
                    .collect(Collectors.toList())
                    .get(0)
                    .handle(routeX);
        }
    }

    /**
     * Evaluates if this routeX object is releavant for the Messanger Singleton.
     * This can be if the routeX object is an:
     * - RouteX.Acknowledge (answer for a RouteX.Message)
     * - RouteX.RouteReply (answer for a RouteX.RouteRequest)
     *
     * @param routeX
     * @return false if has no
     */
    public boolean hasMessengerRelevance(RouteX routeX) {
        if (routeX instanceof RouteX.Acknowledge || routeX instanceof RouteX.RouteReply) {
            return true;
        }
        return false;
    }
}
