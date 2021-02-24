package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */


import de.teklic.mario.handler.protocols.Communicable;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;

import java.util.Scanner;
import static de.teklic.mario.model.other.JLoraModel.SENDER_ADDR;

public class UsersMessageHandler extends Handler implements Communicable, Runnable {

    private Scanner scanner;

    @Override
    public void run() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void handle(RouteX routeX) {

    }

    @Override
    public void send(String endNode, String message) {

    }

    @Override
    public void forward(RouteX message) {

    }

    public boolean isInteger(String str){
        try{
            Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
