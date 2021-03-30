package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.util.Util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

/**
 * Organizes the RouteX shipment
 */
public class Messenger {

    public static final Logger logger = Logger.getLogger(Messenger.class.getName());

    /**
     * PropertyChangeSupport, updates with new RouteX.Acknowledge or RouteX.Reply
     */
    private PropertyChangeSupport changes;

    /**
     * Singleton Object
     */
    private static Messenger messenger;

    private Messenger() {
        changes = new PropertyChangeSupport(this);
    }

    public static Messenger getInstance() {
        if (messenger == null) {
            messenger = new Messenger();
        }
        return messenger;
    }

    /**
     * Adds a listener
     * @param l PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    /**
     * Removes a listerner
     * @param l PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener l){
        changes.removePropertyChangeListener(l);
    }

    /**
     * Fires a PropertyChangeEvent, which holds the new RouteX as newValue.
     * @param routeX This object will be set as the newValue variable of the outgoing PropertyChangeEvent
     */
    public void incomingRouteX(RouteX routeX){
        PropertyChangeEvent event = new PropertyChangeEvent(this, routeX.getFlag().name(), new RouteX.Disposable(), routeX);
        changes.firePropertyChange(event);
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
     * Checks retries * 3 times if any incoming RouteX matches this routeX (like: Message ... Acknowledge)
     *
     * @param routeX Any RouteX
     * @param retries How often should this routeX be sent
     */
    public void sendWithWorker(RouteX routeX, int retries) {
        MessageJob job = new MessageJob(routeX, retries);
        MessageWorker worker = new MessageWorker(job);
        addPropertyChangeListener(worker);
        new Thread(worker).start();
    }

    /**
     * Sends an routeX object only once without a worker
     * @param routeX The RouteX object which will be sent
     */
    public void send(RouteX routeX) {
        SerialPortOutput.getInstance().send(routeX);
    }
}
