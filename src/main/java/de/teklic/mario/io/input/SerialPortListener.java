package de.teklic.mario.io.input;

import de.teklic.mario.core.JLora;
import de.teklic.mario.core.Constant;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;

public class SerialPortListener extends Observable implements SerialPortEventListener, Runnable {
    private static SerialPortListener eventListener;

    @Getter
    @Setter
    private Scanner inputScanner;

    private SerialPortListener(){}

    /**
     *
     * @return
     */
    public static synchronized SerialPortListener getInstance(){
        if(eventListener == null){
            JLora.logger.info("New Event listener gets initialized.");
            eventListener = new SerialPortListener();
        }

        if(eventListener.getInputScanner() == null){
            JLora.logger.warning("Input scanner was not set yet!");
        }

        return eventListener;
    }

    @Override
    public void run() {
        while (true){
            if (inputScanner.hasNext()) {
                String msg = inputScanner.nextLine();
                if (!irrelevantMessage(msg)) {
                    JLora.logger.info("------------->>> IRRELEVANT: " + msg);
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
}
