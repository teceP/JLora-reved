package de.teklic.mario.io.input;

import de.teklic.mario.core.Address;
import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.UserService;
import lombok.Setter;

import java.util.Observable;
import java.util.Scanner;

public class UserInput extends Observable implements Runnable {

    private static UserInput userInput;
    @Setter
    private Scanner scanner;

    private UserInput(){
        this.scanner = new Scanner(System.in);
    }

    public static UserInput getInstance(){
        if(userInput == null){
            JLora.logger.info("UserInput or Scanner object was null. Create new instance.");
            userInput = new UserInput();
        }
        return userInput;
    }

    @Override
    public void run() {
        while(scanner.hasNext()){
            String next = scanner.nextLine();
            if(isServiceCall(next)){
                UserService.getInstance().handle(next);
            }else{
                RouteX.Message message = createMessage();
                JLora.logger.info("New User Message created: " + message);
                setChanged();
                notifyObservers(message);
            }
        }
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
        message.setSource(Address.getInstance().getAddr());
        message.setTimeToLive(9);
        message.setEndNode(dest);
        message.setNextNode(RoutingTable.getInstance().getNextForDestination(dest));
        message.setPayload(msg);

        System.out.println("Message was created: " + message.toString());

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

    public boolean isServiceCall(String message){
        return !message.equalsIgnoreCase("msg");
    }
}
