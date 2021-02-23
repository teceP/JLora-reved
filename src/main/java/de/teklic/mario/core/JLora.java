package de.teklic.mario.core;

import de.teklic.mario.input.SerialPortListener;
import de.teklic.mario.input.UserInput;
import de.teklic.mario.model.other.JLoraModel;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.util.MessageEvaluator;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

public class JLora implements Observer {
    public static final Logger logger = Logger.getLogger(JLora.class.getName());

    private JLoraModel jLoraModel;

    public JLora(){
        jLoraModel = new JLoraModel();
        SerialPortListener.getInstance().addObserver(this);
        UserInput.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof String && arg != null && !((String) arg).isEmpty()){
            if(o instanceof UserInput){
                UserInput.getInstance().deleteObserver(this);
                //do stuff
                //note: check if endnode is in routingtable

                /**
                 * if(RoutingTable.getInstance().getNextForDestination(dest).equalsIgnoreCase(NO_NEXT)){
                 *             System.out.println("Needs to send route request, because not next node for destination " + dest +" was found...");
                 *             jLora.ownRequest(dest);
                 *             System.out.println("Try again when route has been added.");
                 *         }else {
                 *
                 *   }
                 */

                UserInput.getInstance().addObserver(this);
            }else if(o instanceof SerialPortListener){
                RouteX routeX = MessageEvaluator.evaluate((String) arg);
                logger.info("RouteX after evaluation: " + routeX.toString());
                distributeToHandler(routeX);
            }
        }else{
            logger.info("Error: wont be send to message evaluator.");
            logger.info((arg instanceof String ? "Was instance of String.":"Was no instance of String: " + arg.getClass().getSimpleName()));
            logger.info((arg == null ? "Was not null.":"Was null."));
            logger.info((((String) arg).isEmpty() ? "Was empty.":"Was not empty."));
        }
    }

    public void distributeToHandler(RouteX routeX){

    }
}
