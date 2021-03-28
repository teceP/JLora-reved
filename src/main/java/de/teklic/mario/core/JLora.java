package de.teklic.mario.core;

import de.teklic.mario.filters.Filterable;
import de.teklic.mario.io.input.UserInput;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.other.JLoraModel;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.MessageEvaluator;
import de.teklic.mario.util.UserService;
import de.teklic.mario.util.Util;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The central/main entrance point for this software.
 */
public class JLora implements PropertyChangeListener {
    public static final Logger logger = Logger.getLogger(JLora.class.getName());

    /**
     * Should show logging output
     */
    @Getter
    @Setter
    private boolean hideLoggerOutput;

    /**
     * Holds several information, such as the handler references and the serial port.
     */
    private JLoraModel jLoraModel;

    /**
     * Specifies, if JLora is currently allowed to listen for new messages.
     */
    @Setter
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
     * An new event has happend and the event gets evaluated and
     * distributed to the correct handler.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (listening) {
            Object newObject = evt.getNewValue();
            if (newObject instanceof String && newObject != null && !((String) newObject).isEmpty() || newObject instanceof RouteX.Message && newObject != null) {
                logger.info("******************NEW PROCESS*************************");
                logger.info("Object class: " + newObject.getClass().getSimpleName() + ", from " + (evt.getSource() instanceof UserInput ? "UserInput" : "SerialInput"));
                logger.info("[Received Text]: " + newObject);

                if (evt.getSource() instanceof UserInput && newObject instanceof String) {
                    UserService.getInstance().handle((String) newObject);
                } else {
                    RouteX routeX;
                    if (newObject instanceof String) {
                        routeX = MessageEvaluator.evaluate((String) newObject);
                        for (Filterable f : jLoraModel.getFilters()) {
                            routeX = f.filter(routeX);
                        }
                    } else {
                        routeX = (RouteX.Message) newObject;
                    }
                    logger.info("RouteX after evaluation: " + routeX.toString());
                    distributeToHandler(routeX);
                }
            } else {
                logger.info("Error: wont be send to message evaluator, due to not matching processing conditions.");
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

        jLoraModel.getHandlers()
                .stream()
                .filter(handler -> routeX.responsibleHandler().equalsIgnoreCase(handler.getHandlerName()))
                .collect(Collectors.toList())
                .get(0)
                .handle(routeX);

    }
}
