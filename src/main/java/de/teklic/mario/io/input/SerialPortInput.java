package de.teklic.mario.io.input;

import de.teklic.mario.core.Constant;
import de.teklic.mario.event.MessageListener;
import de.teklic.mario.event.MessageParcel;
import de.teklic.mario.event.MessageSupport;
import de.teklic.mario.filters.Filterable;
import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Logger;

import static de.teklic.mario.filters.Filterable.SHOULD_NOT;

/**
 * SerialPortInput-Singleton
 * A input source, which deliveres new content, which the LoRa-Module received.
 */
public class SerialPortInput implements SerialPortEventListener, Runnable {

    public static final Logger logger = Logger.getLogger(SerialPortInput.class.getName());

    private MessageSupport messageSupport;

    /**
     * SerialPortInput from LoRa-Module
     */
    private static SerialPortInput eventListener;

    /**
     * Scanner for SerialPortInput
     */
    @Getter
    @Setter
    private Scanner inputScanner;

    private SerialPortInput(){
        messageSupport = new MessageSupport(this);
    }

    /**
     * @return The SerialPortInput Singleton instance
     */
    public static synchronized SerialPortInput getInstance(){
        if(eventListener == null){
            logger.info("New Event listener gets initialized.");
            eventListener = new SerialPortInput();
        }

        return eventListener;
    }

    /**
     * Runs the SerialPortInput in a Thread and constantly reads new received data.
     * Notifies the JLora instance, if there are new data available.
     */
    @Override
    public void run() {
        while (true){
            if (inputScanner.hasNext()) {
                String msg = inputScanner.nextLine();
                if (!msg.equalsIgnoreCase(SHOULD_NOT) && !irrelevantMessage(msg)) {
                    messageSupport.fireMessageParcel(new MessageParcel(msg));
                }
            }
        }
    }

    /**
     * Adds an PropertyChangeListener which gets notified, when new data has arrived.
     * @param ml The Listener
     */
    public void addMessageListener(MessageListener ml){
        messageSupport.addListener(ml);
    }

    /**
     * Removes a PropertyChangeListener
     * @param ml The Listener
     */
    public void removeMessageListener(MessageListener ml){
        messageSupport.removeListener(ml);
    }

    /**
     * Not implemented. Also dont needs to get implemented.
     * Data gets received by Serial.port.inputstream.
     * @param serialPortEvent
     */
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {}

    /**
     * Filters a message for unnecessary data.
     * @param msg A received data as String.
     * @return true if unnecessary, false if useful
     */
    public boolean irrelevantMessage(String msg) {
        if (Arrays.asList(Constant.IRRELEVANTS).contains(msg)) {
            return true;
        }
        return false;
    }

    /**
     * Closes the Input Scanner Stream.
     */
    public void exit(){
        inputScanner.close();
    }
}
