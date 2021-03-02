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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.*;
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

        //Read & Write
        setIO(jLora, jLoraModel);

        //Register for update central
        SerialPortInput.getInstance().addObserver(jLora);
        UserInput.getInstance().addObserver(jLora);

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
        File folder = new File(LOG_FOLDER);

        if(!folder.exists()){
            folder.mkdir();
        }

        formatLogger(Initializer.logger, YELLOW);
        setLoggerOutputfile(Initializer.logger, INIT_LOG);

        //JLora
        formatLogger(JLora.logger, WHITE_BRIGHT);
        setLoggerOutputfile(JLora.logger, JLR_LOG);

        //Handlers
        formatLogger(AcknowledgeHandler.logger, GREEN);
        formatLogger(MessageHandler.logger, GREEN_BRIGHT);
        formatLogger(ReplyHandler.logger, YELLOW_BRIGHT);
        formatLogger(RequestHandler.logger, YELLOW);
        formatLogger(ErrorHandler.logger, BLUE);
        setLoggerOutputfile(AcknowledgeHandler.logger, ACK_H_LOG);
        setLoggerOutputfile(MessageHandler.logger, MSG_H_LOG);
        setLoggerOutputfile(ReplyHandler.logger, REPL_H_LOG);
        setLoggerOutputfile(RequestHandler.logger, REQ_H_LOG);
        setLoggerOutputfile(ErrorHandler.logger, ERR_H_LOG);

        //Input
        formatLogger(SerialPortInput.logger, CYAN_BRIGHT);
        formatLogger(UserInput.logger, CYAN_BRIGHT);
        setLoggerOutputfile(SerialPortInput.logger, SER_INP_LOG);
        setLoggerOutputfile(UserInput.logger, USR_INP_LOG);

        //Output
        formatLogger(SerialPortOutput.logger, CYAN);
        formatLogger(UserOutput.logger, CYAN);
        setLoggerOutputfile(SerialPortOutput.logger, SER_OUT_LOG);
        setLoggerOutputfile(UserOutput.logger, USR_OUT_LOG);

        //Messenger
        formatLogger(Messenger.logger, PURPLE);
        formatLogger(MessageWorker.logger, PURPLE);
        setLoggerOutputfile(Messenger.logger, MSG_MSNGR_LOG);
        setLoggerOutputfile(MessageWorker.logger, MSG_WRKR_LOG);

        //Routing Table
        formatLogger(RoutingTable.logger, WHITE);
        setLoggerOutputfile(RoutingTable.logger, ROUT_TBL_LOG);

        //Utils
        formatLogger(MessageEvaluator.logger, WHITE);
        formatLogger(UserService.logger, WHITE);
        formatLogger(Util.logger, WHITE);
        setLoggerOutputfile(MessageEvaluator.logger, MSG_EVLTR_LOG);
        setLoggerOutputfile(UserService.logger, USR_SRVC_LOG);
        setLoggerOutputfile(Util.logger, UTL_LOG);
    }

    private static void formatLogger(Logger logger, String color){
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        CustomFormatter formatter = new CustomFormatter();
        formatter.setColor(color);
        handler.setFormatter(formatter);
        logger.addHandler(handler);
        logger.info(logger.getName() + " got set.");
    }

    private static void setLoggerOutputfile(Logger logger, String filename){
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
