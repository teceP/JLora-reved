package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.event.MessageListener;
import de.teklic.mario.event.MessageParcel;
import de.teklic.mario.event.MessageSupport;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.util.Util;

import java.util.logging.Logger;

public class Messenger {

    public static final Logger logger = Logger.getLogger(Messenger.class.getName());

    private MessageSupport messageSupport;

    /**
     * Singleton Object
     */
    private static Messenger messenger;

    private Messenger() {
        messageSupport = new MessageSupport(this);
    }

    public static Messenger getInstance() {
        if (messenger == null) {
            messenger = new Messenger();
        }
        return messenger;
    }

    public void addMessageListener(MessageListener ml){
        messageSupport.addListener(ml);
    }

    public void removeMessageListener(MessageListener ml){
        messageSupport.removeListener(ml);
    }

    public void incomingRouteX(RouteX routeX){
        MessageParcel messageParcel = new MessageParcel(routeX);
        messageSupport.fireMessageParcel(messageParcel);
    }

    /**
     * Checks if this incoming routeX object has any connection to a running worker
     * @param routeX Incoming routeX
     */
    public void update(RouteX routeX) {
        Util.newRoute(routeX);
        logger.info("RouteX is for me. Will send to MessageWorkers...");
        incomingRouteX(routeX);
    }

    /**
     * Sends a routeX with a worker in another thread.
     * Checks retries * 3 times if any incoming RouteX matches this routeX (like: Message -> Acknowledge)
     *
     * @param routeX Any RouteX
     * @param retries How often should this routeX be sent
     */
    public void sendWithWorker(RouteX routeX, int retries) {
        MessageJob job = new MessageJob(routeX, retries);
        MessageWorker worker = new MessageWorker(job);
        addMessageListener(worker);
        new Thread(worker).start();
    }

    /**
     * Sends an routeX object only once without a worker
     * @param routeX
     */
    public void send(RouteX routeX) {
        SerialPortOutput.getInstance().send(routeX);
    }
}
