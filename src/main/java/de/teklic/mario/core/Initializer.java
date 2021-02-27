package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.handler.*;
import de.teklic.mario.io.input.SerialPortInput;
import de.teklic.mario.io.input.UserInput;
import de.teklic.mario.messanger.MessageWorker;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.other.JLoraModel;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.io.output.UserOutput;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.CustomFormatter;
import de.teklic.mario.util.MessageEvaluator;
import de.teklic.mario.util.UserService;
import de.teklic.mario.util.Util;
import purejavacomm.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.BROADCAST;
import static de.teklic.mario.core.Constant.CONFIG;
import static de.teklic.mario.util.CustomFormatter.*;

public class Initializer {

    public static final Logger logger = Logger.getLogger(Initializer.class.getName());

    public static JLoraModel initialize(JLora jLora, String addr) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException, TooManyListenersException {
        JLoraModel jLoraModel = new JLoraModel();

        //Configure loggers
        configureLoggers();

        //Nodes Address
        Address.getInstance().setAddr(addr);
        logger.info("Nodes Address: " + addr);

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
        jLoraModel.getSerialPort().addEventListener(SerialPortInput.getInstance());

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
        SerialPortInput.getInstance().setInputScanner(new Scanner(jLoraModel.getSerialPort().getInputStream()));
        new Thread(SerialPortInput.getInstance()).start();
        UserInput.getInstance().setScanner(new Scanner(System.in));
        jLoraModel.setUserInputThread(new Thread(UserInput.getInstance()));
        jLoraModel.getUserInputThread().start();

        //Write
        SerialPortOutput.getInstance().setPrintWriter(new PrintWriter(jLoraModel.getSerialPort().getOutputStream()));
        UserOutput.getInstance().setPrintStream(System.out);

        //Register for update central
        SerialPortInput.getInstance().addObserver(jLora);
        UserInput.getInstance().addObserver(jLora);

        logger.info("Finished initializing read and write.");
    }

    private static void setPort(SerialPort serialPort) throws UnsupportedCommOperationException {
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        serialPort.disableReceiveTimeout();
        serialPort.disableReceiveFraming();
        serialPort.disableReceiveThreshold();

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

    private static void configureLoggers(){
        configureLogger(Initializer.logger, YELLOW);

        //JLora
        configureLogger(JLora.logger, CYAN_BOLD_BRIGHT);

        //Handlers
        configureLogger(AcknowledgeHandler.logger, GREEN);
        configureLogger(ErrorHandler.logger, GREEN);
        configureLogger(MessageHandler.logger, GREEN);
        configureLogger(ReplyHandler.logger, GREEN);
        configureLogger(RequestHandler.logger, GREEN);

        //Input
        configureLogger(SerialPortInput.logger, BLUE);
        configureLogger(UserInput.logger, BLUE);

        //Output
        configureLogger(SerialPortOutput.logger, CYAN);
        configureLogger(UserOutput.logger, CYAN);

        //Messenger
        configureLogger(Messenger.logger, PURPLE);
        configureLogger(MessageWorker.logger, PURPLE);

        //Routing Table
        configureLogger(RoutingTable.logger, WHITE);

        //Utils
        configureLogger(MessageEvaluator.logger, WHITE);
        configureLogger(UserService.logger, WHITE);
        configureLogger(Util.logger, WHITE);
    }

    private static void configureLogger(Logger logger, String color){
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        CustomFormatter formatter = new CustomFormatter();
        formatter.setColor(color);
        handler.setFormatter(formatter);
        Initializer.logger.addHandler(handler);
        logger.info(logger.getName() + " got set.");
    }
}
