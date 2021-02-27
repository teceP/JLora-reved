package de.teklic.mario.io.input;

import de.teklic.mario.core.Constant;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Logger;

public class SerialPortInput extends Observable implements SerialPortEventListener, Runnable {

    public static final Logger logger = Logger.getLogger(SerialPortInput.class.getName());

    private static SerialPortInput eventListener;

    @Getter
    @Setter
    private Scanner inputScanner;

    private SerialPortInput(){}

    /**
     *
     * @return
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

    @Override
    public void run() {
        while (true){
            if (inputScanner.hasNext()) {
                String msg = inputScanner.nextLine();
                if (!irrelevantMessage(msg)) {
                    setChanged();
                    notifyObservers(msg);
                }
            }
        }
    }

    /**
     * TODO: Nachrichten kommen seit dem letzten, vorletzten commit nicht mehr immer an
     *
     */

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

    }

    public boolean irrelevantMessage(String msg) {
        if (Arrays.asList(Constant.IRRELEVANTS).contains(msg)) {
            return true;
        }
        return false;
    }

    public void exit(){
        inputScanner.close();
    }
}
