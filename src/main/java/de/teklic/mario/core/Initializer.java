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
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.CustomFormatter;
import de.teklic.mario.util.MessageEvaluator;
import de.teklic.mario.util.UserService;
import de.teklic.mario.util.Util;
import purejavacomm.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.*;
import static de.teklic.mario.util.CustomFormatter.*;

/**
 * Initializes all instances, classes, variables which are used in JLora.
 */
public class Initializer {

    public static final Logger logger = Logger.getLogger(Initializer.class.getName());

    /**
     * Makes following initializing:
     *  - configure loggers
     *  - connects the observers with the observables.
     *  - sets the nodes address
     *  - sets all handlers
     *  - sets the serial port
     *  - creates input and output
     *  - restores the routing table
     * @param jLora An instance of JLora
     * @param addr The nodes address
     * @return A fully initialized JLora instance
     * @throws NoSuchPortException SerialPort
     * @throws PortInUseException SerialPort
     * @throws IOException SerialPort
     * @throws UnsupportedCommOperationException SerialPort
     * @throws TooManyListenersException SerialPort
     */
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

        //Port configurations
        setPort(jLoraModel.getSerialPort());

        //Read & Write
        setIO(jLora, jLoraModel);

        //Event Listener (System Input)
        jLoraModel.getSerialPort().addEventListener(SerialPortInput.getInstance());

        //Module configurations
        moduleConfigurations();

        //Restore RoutingTable
        RoutingTable.getInstance().restore(Address.getInstance().getAddr());

        logger.info("+++++ Config finished +++++");

        jLora.setListening(true);

        return jLoraModel;
    }

    /**
     * Creates new handlers and adds all to the jlora-Model
     * @param jLoraModel JLoraModel
     */
    private static void setHandlers(JLoraModel jLoraModel){
        jLoraModel.setHandlers(new ArrayList<>());
        jLoraModel.getHandlers().add(new AcknowledgeHandler());
        jLoraModel.getHandlers().add(new ErrorHandler());
        jLoraModel.getHandlers().add(new MessageHandler());
        jLoraModel.getHandlers().add(new ReplyHandler());
        jLoraModel.getHandlers().add(new RequestHandler());
        logger.info("Handlers (" + jLoraModel.getHandlers().size() +") where set.");
    }

    /**
     * Sets the inputs and outputs, based on the SerialPort and System.in and System.out
     *
     * @param jLora JLora
     * @param jLoraModel JLoraModel
     * @throws IOException
     */
    private static void setIO(JLora jLora, JLoraModel jLoraModel) throws IOException {
        //Read
        SerialPortInput.getInstance().setInputScanner(new Scanner(jLoraModel.getSerialPort().getInputStream()));
        new Thread(SerialPortInput.getInstance()).start();
        UserInput.getInstance().setScanner(new Scanner(System.in));
        jLoraModel.setUserInputThread(new Thread(UserInput.getInstance()));
        jLoraModel.getUserInputThread().start();

        //Write
        SerialPortOutput.getInstance().setPrintWriter(new PrintWriter(jLoraModel.getSerialPort().getOutputStream()));

        //Register for update central
        SerialPortInput.getInstance().addObserver(jLora);
        UserInput.getInstance().addObserver(jLora);

        logger.info("Finished initializing read and write.");
    }

    /**
     * Configures the Port with standard values and disables few options.
     * @param serialPort
     * @throws UnsupportedCommOperationException
     */
    private static void setPort(SerialPort serialPort) throws UnsupportedCommOperationException {
        serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        serialPort.disableReceiveTimeout();
        serialPort.disableReceiveFraming();
        serialPort.disableReceiveThreshold();

        logger.info("Finished configuring serial port.");
    }

    /**
     * Configures the LoRa Module with nodes address and sets the destination to broadcast.
     * Also sets the transmission power.
     */
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

    /**
     * Configure all loggers
     */
    private static void configureLoggers(){
        File folder = new File(LOG_FOLDER);

        if(!folder.exists()){
            folder.mkdir();
        }

        formatLogger(Initializer.logger, YELLOW);
        setLoggerOutputs(Initializer.logger, INIT_LOG);

        //JLora
        formatLogger(JLora.logger, WHITE_BRIGHT);
        setLoggerOutputs(JLora.logger, JLR_LOG);

        //Handlers
        formatLogger(AcknowledgeHandler.logger, GREEN);
        formatLogger(MessageHandler.logger, GREEN_BRIGHT);
        formatLogger(ReplyHandler.logger, YELLOW_BRIGHT);
        formatLogger(RequestHandler.logger, YELLOW);
        formatLogger(ErrorHandler.logger, BLUE);
        setLoggerOutputs(AcknowledgeHandler.logger, ACK_H_LOG);
        setLoggerOutputs(MessageHandler.logger, MSG_H_LOG);
        setLoggerOutputs(ReplyHandler.logger, REPL_H_LOG);
        setLoggerOutputs(RequestHandler.logger, REQ_H_LOG);
        setLoggerOutputs(ErrorHandler.logger, ERR_H_LOG);

        //Input
        formatLogger(SerialPortInput.logger, CYAN_BRIGHT);
        formatLogger(UserInput.logger, CYAN_BRIGHT);
        setLoggerOutputs(SerialPortInput.logger, SER_INP_LOG);
        setLoggerOutputs(UserInput.logger, USR_INP_LOG);

        //Output
        formatLogger(SerialPortOutput.logger, CYAN);
        setLoggerOutputs(SerialPortOutput.logger, SER_OUT_LOG);

        //Messenger
        formatLogger(Messenger.logger, PURPLE);
        formatLogger(MessageWorker.logger, PURPLE);
        setLoggerOutputs(Messenger.logger, MSG_MSNGR_LOG);
        setLoggerOutputs(MessageWorker.logger, MSG_WRKR_LOG);

        //Routing Table
        formatLogger(RoutingTable.logger, WHITE);
        setLoggerOutputs(RoutingTable.logger, ROUT_TBL_LOG);

        //Utils
        formatLogger(MessageEvaluator.logger, WHITE);
        formatLogger(UserService.logger, WHITE);
        formatLogger(Util.logger, WHITE);
        setLoggerOutputs(MessageEvaluator.logger, MSG_EVLTR_LOG);
        setLoggerOutputs(UserService.logger, USR_SRVC_LOG);
        setLoggerOutputs(Util.logger, UTL_LOG);
    }

    /**
     * Formats the logger: changes the color output and takes the CustomFormatter as main handler
     * @param logger Logger
     * @param color Color String in ANSI Code. There are several final String in the CustomFormatter which can be used.
     */
    private static void formatLogger(Logger logger, String color){
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        CustomFormatter formatter = new CustomFormatter();
        formatter.setColor(color);
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        logger.info(logger.getName() + " got set.");
    }

    /**
     * Adds a FileHandler to the logger.
     * All LogRecords which the logger creates,
     * will be store in the logs folder under the given filename.
     * @param logger Logger
     * @param filename Filename
     */
    private static void setLoggerOutputs(Logger logger, String filename){
        String pathname = LOG_FOLDER + "/" + filename;
        File f = new File(pathname);
        try{
            if(!f.exists()){
                f.createNewFile();
            }
            FileHandler fileHandler = new FileHandler(pathname);
            fileHandler.setFormatter(logger.getHandlers()[0].getFormatter());
            logger.addHandler(fileHandler);
            logger.info("Outputfile has been set.");
        }catch (Exception e){
            logger.info("Logger Outputfile could not be created for '" + logger.getName() + "' and file '" + filename + "' due to " + e.getClass().getSimpleName() + ".");
        }
    }
}
