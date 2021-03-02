package de.teklic.mario.io.output;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;

import java.io.PrintWriter;
import java.util.logging.Logger;

public class SerialPortOutput {
    public static final Logger logger = Logger.getLogger(SerialPortOutput.class.getName());

    private static SerialPortOutput serialPortOutput;
    private static PrintWriter printWriter;

    private SerialPortOutput() {
    }

    public static SerialPortOutput getInstance() {
        if (serialPortOutput == null) {
            serialPortOutput = new SerialPortOutput();
        }

        if (printWriter == null) {
            logger.info("PrintWriter (SerialPortOutput) is null! Invoce setPrintWriter first.");
        }

        return serialPortOutput;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
        this.printWriter.flush();
    }

    public void send(RouteX routeX) {
        send(routeX.asSendable());
    }

    public void send(String message) {
        logger.info("SerialOutput will send: " + message);
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

    public void exit(){
        printWriter.close();
    }
}
