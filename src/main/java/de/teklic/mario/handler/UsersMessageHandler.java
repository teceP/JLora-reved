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
import static de.teklic.mario.model.other.JLoraModel.sender_addr;

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

    public RouteX.Message createMessage(){
        String dest = "";
        String msg = "";

        while(!isInteger(dest)){
            System.out.print("Destination Address: ");
            dest = scanner.nextLine();
        }


            System.out.print("Insert message: ");
            msg = scanner.nextLine();

            RouteX.Message message = new RouteX.Message();
            message.setFlag(RouteFlag.MESSAGE);
            message.setSource(sender_addr);
            message.setTimeToLive(9);
            message.setDestination(dest);
            message.setNextNode(RoutingTable.getInstance().getNextForDestination(dest));
            message.setPayload(msg);

            System.out.println("Message was created: " + message.toString());
            //Todo: Message queue handling
            //jLora.putToForwardedMessageQueue(jLora.calcMd5(message), message);
            //jLora.sendMessage(BROADCAST, message.asSendable());

        return message;
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
