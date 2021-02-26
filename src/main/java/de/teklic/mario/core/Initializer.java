package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.handler.*;
import de.teklic.mario.input.SerialPortListener;
import de.teklic.mario.input.UserInput;
import de.teklic.mario.model.other.JLoraModel;
import de.teklic.mario.output.SerialPortOutput;
import de.teklic.mario.output.UserOutput;
import de.teklic.mario.routingtable.RoutingTable;
import purejavacomm.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.BROADCAST;
import static de.teklic.mario.core.Constant.CONFIG;

public class Initializer {

    public static final Logger logger = Logger.getLogger(Initializer.class.getName());

    public static JLoraModel initialize(JLora jLora, String addr) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException, TooManyListenersException {
        JLoraModel jLoraModel = new JLoraModel();
        logger.info("Set Nodes Address to " + addr);

        Address.getInstance().setAddr(addr);

        logger.info("ADDR: " + Address.getInstance().getAddr());

        //Handlers
        setHandlers(jLoraModel);

        //Serial Port
        jLoraModel.setSerialPort((SerialPort) CommPortIdentifier.getPortIdentifier(Constant.PORT).open(
                JLora.class.getName(), 0));

        //Read & Write
        setIO(jLora, jLoraModel);

        //Port configurations
        setPort(jLoraModel.getSerialPort());

        //Initialize lists
        initLists(jLoraModel);

        //Event Listener (System Input)
        jLoraModel.getSerialPort().addEventListener(SerialPortListener.getInstance());

        //Module configurations
        moduleConfigurations();

        //Restore RoutingTable
        RoutingTable.getInstance().restore(Address.getInstance().getAddr());

        logger.info("+++++ Config finished +++++");

        return jLoraModel;
    }

    private static void setHandlers(JLoraModel jLoraModel){
        jLoraModel.setHandlers(new ArrayList<>());
        jLoraModel.getHandlers().add(new AcknowledgeHandler());
        jLoraModel.getHandlers().add(new ErrorHandler());
        jLoraModel.getHandlers().add(new MessageHandler());
        jLoraModel.getHandlers().add(new ReplyHandler());
        jLoraModel.getHandlers().add(new RequestHandler());
        logger.info("Handlers (" + jLoraModel.getHandlers().size() +") where set.");
    }

    private static void setIO(JLora jLora, JLoraModel jLoraModel) throws IOException {
        //Read
        SerialPortListener.getInstance().setInputScanner(new Scanner(jLoraModel.getSerialPort().getInputStream()));
        UserInput.getInstance().setScanner(new Scanner(System.in));
        jLoraModel.setUserInputThread(new Thread(UserInput.getInstance()));
        jLoraModel.getUserInputThread().start();

        //Write
        SerialPortOutput.getInstance().setPrintWriter(new PrintWriter(jLoraModel.getSerialPort().getOutputStream()));
        UserOutput.getInstance().setPrintStream(System.out);

        //Register for update central
        SerialPortListener.getInstance().addObserver(jLora);
        UserInput.getInstance().addObserver(jLora);

        logger.info("Finished initializing read and write.");
    }

    private static void setPort(SerialPort serialPort) throws UnsupportedCommOperationException {
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        serialPort.notifyOnBreakInterrupt(true);
        serialPort.notifyOnCarrierDetect(true);
        serialPort.notifyOnCTS(true);
        serialPort.notifyOnDataAvailable(true);
        serialPort.notifyOnDSR(true);
        serialPort.notifyOnFramingError(true);
        serialPort.notifyOnOutputEmpty(true);
        serialPort.notifyOnOverrunError(true);
        serialPort.notifyOnParityError(true);
        serialPort.notifyOnRingIndicator(true);

        logger.info("Finished configuring serial port.");
    }

    private static void initLists(JLoraModel jLoraModel){
        jLoraModel.setRequestQueue(Collections.synchronizedList(new ArrayList<>()));
        jLoraModel.setForwardedMessageQueue(new HashMap<>());

        logger.info("Finished initializing lists.");
    }

    private static void moduleConfigurations(){
        SerialPortOutput.getInstance().sendConfig("AT");
        SerialPortOutput.getInstance().sendConfig("AT");
        SerialPortOutput.getInstance().sendConfig("AT+RST");
        SerialPortOutput.getInstance().sendConfig(CONFIG);
        SerialPortOutput.getInstance().sendConfig("AT+ADDR=" + Address.getInstance().getAddr());
        SerialPortOutput.getInstance().sendConfig("AT+DEST=" + BROADCAST);
        SerialPortOutput.getInstance().sendConfig("AT+RX");
        SerialPortOutput.getInstance().sendConfig("AT+SAVE");

        logger.info("Finished sending module configurations.");
    }
}
