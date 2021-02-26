package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import de.teklic.mario.core.Address;
import de.teklic.mario.core.JLora;
import de.teklic.mario.routingtable.RoutingTable;

public class UserService {
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
                System.out.println("Routing Table:");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String str = gson.toJson(JsonParser.parseString(new Gson().toJson(RoutingTable.getInstance().getRouteList())));
                System.out.println(str);
                break;
            case "help":
                System.out.println("table, msg, help, drop, addr");
                break;
            case "addr":
                System.out.println("Nodes Address: " + Address.getInstance().getAddr());
                break;
            case "drop":
                RoutingTable.getInstance().drop();
                break;
            default:
                JLora.logger.info("Dropping user message (not assignable): " + call);
                break;
        }
    }
}
