package de.teklic.mario.input;

import de.teklic.mario.core.JLora;
import de.teklic.mario.core.Constant;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;

public class SerialPortListener extends Observable implements SerialPortEventListener {
    private static SerialPortListener eventListener;

    @Getter
    @Setter
    private Scanner inputScanner;

    private SerialPortListener(){}

    /**
     *
     * @return
     */
    public static SerialPortListener getInstance(){
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
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (inputScanner.hasNext()) {
            String msg = inputScanner.nextLine();
            if (!irrelevantMessage(msg)) {
                setChanged();
                notifyObservers(msg);
            }
        }
    }

    public boolean irrelevantMessage(String msg) {
        if (Arrays.asList(Constant.IRRELEVANTS).contains(msg)) {
            return true;
        }
        return false;
    }
}
