package de.teklic.mario.io.input;

import de.teklic.mario.core.Constant;
import de.teklic.mario.filters.Filterable;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.util.*;
import java.util.logging.Logger;

import static de.teklic.mario.filters.Filterable.SHOULD_NOT;

/**
 * SerialPortInput-Singleton
 * A input source, which deliveres new content, which the LoRa-Module received.
 */
public class SerialPortInput extends Observable implements SerialPortEventListener, Runnable {

    public static final Logger logger = Logger.getLogger(SerialPortInput.class.getName());

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

    }

    /**
     * @return The SerialPortInput Singleton instance
     */
    public static synchronized SerialPortInput getInstance(){
        if(eventListener == null){
            logger.info("New Event listener gets initialized.");
            eventListener = new SerialPortInput();
        }

        if(eventListener.getInputScanner() == null){
            logger.warning("Input scanner was not set yet!");
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
                    setChanged();
                    notifyObservers(msg);
                }
            }
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        //TODO remove?
    }

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
