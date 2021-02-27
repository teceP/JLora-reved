package de.teklic.mario.io.output;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteX;

import java.io.PrintWriter;

public class SerialPortOutput {
    private static SerialPortOutput serialPortOutput;
    private static PrintWriter printWriter;

    private SerialPortOutput(){}

    public static SerialPortOutput getInstance(){
        if(serialPortOutput == null){
            serialPortOutput = new SerialPortOutput();
        }

        if(printWriter == null){
            JLora.logger.info("PrintWriter (SerialPortOutput) is null! Invoce setPrintWriter first.");
        }

        return serialPortOutput;
    }

    public void setPrintWriter(PrintWriter printWriter){
        this.printWriter = printWriter;
        this.printWriter.flush();
    }

    public void send(RouteX routeX){
        send(routeX.asSendable());
    }

    public void send(String message){
        JLora.logger.info("SerialOutput will send: " + message);
        try {
            int b = message.length();
            printWriter.println("AT+SEND=" + b + "\r\n");
            printWriter.flush();
            printWriter.println(message + "\r\n");
            printWriter.flush();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JLora.logger.info(printWriter.checkError() ? "Has Error in PrintWriter!" : "");
    }

    public void sendConfig(String config){
        JLora.logger.info("SerialOutput sending config: " + config);
        try {
            printWriter.println(config + "\r\n");
            printWriter.flush();
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JLora.logger.info(printWriter.checkError() ? "Has Error in PrintWriter!" : "");
    }
}
