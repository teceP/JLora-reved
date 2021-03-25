package de.teklic.mario.io.input;

import de.teklic.mario.core.Address;
import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.UserService;
import lombok.Setter;
import purejavacomm.SerialPortEventListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.INITIAL_TTL;

/**
 * UserInput-Singleton
 * Scans the standard user input from command line with System.in
 */
public class UserInput implements Runnable {

    public static final Logger logger = Logger.getLogger(UserInput.class.getName());

    private PropertyChangeSupport changes;

    /**
     * Singleton instance
     */
    private static UserInput userInput;

    /**
     * Input scanner
     */
    @Setter
    private Scanner scanner;

    /**
     * Creates a new instance with System.in as input source
     */
    private UserInput(){
        this.scanner = new Scanner(System.in);
        changes = new PropertyChangeSupport(this);
    }

    /**
     * @return UserInput instance with already set initialized scanner.
     */
    public static UserInput getInstance(){
        if(userInput == null){
            userInput = new UserInput();
        }
        return userInput;
    }

    /**
     * Runs in a separate thread and constantly scans for new user input.
     * Will distinguish between a "ServerCall" and the situation where the user wants to send
     * a message to another node.
     */
    @Override
    public void run() {
        while(scanner.hasNext()){
            String next = scanner.nextLine();
            if(isServiceCall(next)){
                UserService.getInstance().handle(next);
            }else{
                RouteX.Message message = createMessage();
                logger.info("New User Message created.");
                changes.firePropertyChange(new PropertyChangeEvent(this, "userInput", new RouteX.Disposable(), message));
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l){
        changes.removePropertyChangeListener(l);
    }

    /**
     * Flow to create a new RouteX.Message object, based on own users input
     * @return RouteX.Message
     */
    public RouteX.Message createMessage(){
        String dest = "";
        String msg = "";

        while(!isInteger(dest) && notMe(dest)){
            logger.info("Destination Address: ");
            dest = scanner.nextLine();
        }

        logger.info("Insert message: ");
        msg = scanner.nextLine();
        msg = replaceUmlauts(msg);

        RouteX.Message message = new RouteX.Message();
        message.setFlag(RouteFlag.MESSAGE);
        message.setSource(Address.getInstance().getAddr());
        message.setTimeToLive(INITIAL_TTL);
        message.setEndNode(dest);
        message.setNextNode(RoutingTable.getInstance().getNextForDestination(dest));
        message.setPayload(msg);

        return message;
    }

    /**
     * Replaces all german umlauts in a String into ASCII conform format.
     * Example: "Hören" -> "Hoeren"
     * @param message with umlauts
     * @return message without umlauts
     */
    public String replaceUmlauts(String message){
        return message.replace("Ü", "Ue")
                .replace("Ö", "Oe")
                .replace("Ä", "Ae")
                .replace("ü", "ue")
                .replace("ö", "oe")
                .replace("ä", "ae");
    }

    /**
     * Proofs if a String only consists out of numbers
     * @param str String
     * @return true if String matches method description
     */
    public boolean isInteger(String str){
        try{
            Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean notMe(String str){
        return !str.equals(Address.getInstance().getAddr());
    }

    /**
     * Decides if a string is a ServiceCall or "msg".
     * @param message
     * @return true if the string is not "msg".
     */
    public boolean isServiceCall(String message){
        return !message.equalsIgnoreCase("msg");
    }

    /**
     * Closes the scanner
     */
    public void exit(){
        scanner.close();
    }
}
