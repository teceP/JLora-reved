package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import de.teklic.mario.core.Address;
import de.teklic.mario.core.Initializer;
import de.teklic.mario.core.JLora;
import de.teklic.mario.handler.*;
import de.teklic.mario.io.input.SerialPortInput;
import de.teklic.mario.io.input.UserInput;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.messanger.MessageWorker;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.routingtable.RoutingTable;

import java.util.logging.Level;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.*;
import static de.teklic.mario.util.CustomFormatter.*;
import static de.teklic.mario.util.CustomFormatter.WHITE;

/**
 * UserService-Singleton
 */
public class UserService {
    public static final Logger logger = Logger.getLogger(UserService.class.getName());

    public int voltage = 5;

    /**
     * Singleton instance
     */
    private static UserService userService;

    private UserService() {
    }

    /**
     * @return UserService singleton instance
     */
    public static UserService getInstance() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    /**
     * Handles a message and tries to print out information for the user.
     *
     * @param call String which must match any of the service strings.
     */
    public void handle(String call) {
        switch (call) {
            case "table":
                logger.info("Routing Table:");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String str = gson.toJson(JsonParser.parseString(new Gson().toJson(RoutingTable.getInstance().getRouteList())));
                logger.info(str);
                break;
            case "help":
                logger.info("table, msg, help, drop, addr, colors, volt:xx (xx>5 && xx<20), nolog, exit");
                break;
            case "addr":
                logger.info("Nodes Address: " + Address.getInstance().getAddr());
                break;
            case "drop":
                RoutingTable.getInstance().drop();
                logger.info("Routing table has been droped.");
                break;
            case "exit":
                SerialPortInput.getInstance().exit();
                UserInput.getInstance().exit();
                SerialPortOutput.getInstance().exit();
                System.out.println("Closed all streams. Goodbye.");
                System.exit(0);
                break;
            case "dolog":
                loggerState(true);
                logger.info("Loggeroutput was enabled.");
            case "nolog":
                logger.info("Loggeroutput was disabled.");
                loggerState(false);
                break;
            case "colors":
                logger.info("White Bright: JLora");
                logger.info("White: Utils, Routing Table");
                logger.info("Green: Acknowledge Handler");
                logger.info("Green Bright: Message Handler");
                logger.info("Yellow: Reply, Initializer");
                logger.info("Yellow Bright: Request");
                logger.info("Blue: Error Handler");
                logger.info("Cyan Bright: Input");
                logger.info("Cyan: Output");
                logger.info("Purple: Messenger");
                break;
            case "voltage":
                logger.info("Current voltage level: " + voltage + ".");
            default:
                if (!voltage(call)) {
                    logger.info("Dropping user message (not assignable): " + call);
                }
                break;
        }
    }

    public boolean voltage(String s) {
        try {
            if (s.length() > 5 && s.startsWith(VOLT)) {
                String volt = s.split(":", 0)[1];
                int i = Integer.parseInt(volt);
                if (i > 5 && i < 20) {
                    SerialPortOutput.getInstance().sendConfig(CONFIG_HEAD + i + CONFIG_TAIL);
                    voltage = i;
                }
                logger.info("Set output voltage to " + i + ".");
                return true;
            }
        } catch (Exception e) {
            logger.info("Could not parse voltage info.");
        }
        return false;
    }

    /**
     * Enables or deactivates all terminal logger outputs.
     * @param enable if enable is true, the Log Level of all loggers
     */
    public void loggerState(boolean enable){
        Level lvl;

        if(enable){
            lvl = Level.ALL;
        }else{
            lvl = Level.OFF;
        }

        Initializer.logger.setLevel(lvl);

        //JLora
        JLora.logger.setLevel(lvl);

        //Handlers
        AcknowledgeHandler.logger.setLevel(lvl);
        MessageHandler.logger.setLevel(lvl);
        ReplyHandler.logger.setLevel(lvl);
        RequestHandler.logger.setLevel(lvl);
        ErrorHandler.logger.setLevel(lvl);

        //Input
        SerialPortInput.logger.setLevel(lvl);
        UserInput.logger.setLevel(lvl);

        //Output
        SerialPortOutput.logger.setLevel(lvl);

        //Messenger
        Messenger.logger.setLevel(lvl);
        MessageWorker.logger.setLevel(lvl);

        //Routing Table
        RoutingTable.logger.setLevel(lvl);

        //Util
        MessageEvaluator.logger.setLevel(lvl);
        //UserService.logger.setLevel(lvl); -> should not be deactivated.
        Util.logger.setLevel(lvl);
    }
}
