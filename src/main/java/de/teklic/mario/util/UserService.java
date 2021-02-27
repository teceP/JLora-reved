package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import de.teklic.mario.core.Address;
import de.teklic.mario.io.input.SerialPortInput;
import de.teklic.mario.io.input.UserInput;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.io.output.UserOutput;
import de.teklic.mario.routingtable.RoutingTable;

import java.util.logging.Logger;

public class UserService {
    public static final Logger logger = Logger.getLogger(UserService.class.getName());
    private static UserService userService;

    private UserService(){}

    public static UserService getInstance(){
        if(userService == null){
            userService = new UserService();
        }
        return userService;
    }

    public void handle(String call){
        switch(call){
            case "table":
                logger.info("Routing Table:");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String str = gson.toJson(JsonParser.parseString(new Gson().toJson(RoutingTable.getInstance().getRouteList())));
                logger.info(str);
                break;
            case "help":
                logger.info("table, msg, help, drop, addr, colors, exit");
                break;
            case "addr":
                logger.info("Nodes Address: " + Address.getInstance().getAddr());
                break;
            case "drop":
                RoutingTable.getInstance().drop();
                break;
            case "exit":
                SerialPortInput.getInstance().exit();
                UserInput.getInstance().exit();
                SerialPortOutput.getInstance().exit();
                UserOutput.getInstance().exit();
                logger.info("Closed all streams. Goodbye.");
                break;
            case "colors":
                logger.info("White Bright: JLora");
                logger.info("White: Utils, Routing Table");
                logger.info("Green: Handlers");
                logger.info("Cyan Bright: Input");
                logger.info("Cyan: Output");
                logger.info("Yellow: Initializer");
                logger.info("Purple: Messenger");
                break;
            default:
                logger.info("Dropping user message (not assignable): " + call);
                break;
        }
    }
}
