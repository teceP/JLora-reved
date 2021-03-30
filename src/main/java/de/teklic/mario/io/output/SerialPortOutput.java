package de.teklic.mario.io.output;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;

import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Logger;

/**
 * SerialPortOutput-Singleton
 * Outputchannel for LoRa-Module communication
 */
public class SerialPortOutput {
    public static final Logger logger = Logger.getLogger(SerialPortOutput.class.getName());

    /**
     * Singleton instance
     */
    private static SerialPortOutput serialPortOutput;

    /**
     * PrintWriter which has a reference to the serial output
     */
    private static PrintWriter printWriter;

    private SerialPortOutput() {}

    /**
     * @return the SerialPortOutput instance
     */
    public static SerialPortOutput getInstance() {
        if (serialPortOutput == null) {
            serialPortOutput = new SerialPortOutput();
        }
        return serialPortOutput;
    }

    /**
     * Sets the PrintWriter and flush's it
     * @param printWriter PrintWriter
     */
    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
        this.printWriter.flush();
    }

    /**
     * Sends a RouteX out to the Serial Output.
     * @param routeX RouteX
     */
    public void send(RouteX routeX) {
        send(routeX.asSendable());
    }

    /**
     * Sends a String out to the Serial Output.
     * Waits a random time between 0 and 1000 milliseconds before sending out,
     * to avoid clashes with other nodes, which sends on the same frequency.
     * @param message String
     */
    public void send(String message) {
        int randomTimeout = new Random().nextInt( 1000);
        try {
            Thread.sleep(randomTimeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int b = message.length();
        logger.info("########## AT+SEND=" + b + " ##########");
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("########## " + message + " ##########");
        printWriter.println("AT+SEND=" + b + System.lineSeparator());
        printWriter.flush();
        printWriter.println(message + System.lineSeparator());
        printWriter.flush();
        logger.info(printWriter.checkError() ? "Has Error in PrintWriter!" : "");
    }

    /**
     * Sends a config String to the LoRa-Module.
     * @param config A config String such as "AT+CFG=..."
     */
    public void sendConfig(String config) {
        logger.info("SerialOutput sending config: " + config);
        try {
            printWriter.println(config + System.lineSeparator());
            printWriter.flush();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(printWriter.checkError() ? "Has Error in PrintWriter!" : "");
    }

    /**
     * Closes the PrintWriter Stream
     */
    public void exit(){
        printWriter.close();
    }
}
