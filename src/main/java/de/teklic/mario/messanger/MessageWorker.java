package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.Address;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.routingtable.RoutingTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.DEFAULT_TIMEOUT;
import static de.teklic.mario.core.Constant.INITIAL_TTL;

@Getter
@Setter
public class MessageWorker implements Runnable{

    public static final Logger logger = Logger.getLogger(MessageWorker.class.getName());
    private MessageJob messageJob;
    private ScheduledFuture<?> future;
    private String id;

    public MessageWorker(MessageJob messageJob){
        this.messageJob = messageJob;
        id = UUID.randomUUID().toString();
    }

    //TODO timeouts
    @Override
    public void run() {
        for(int i = 0; i < messageJob.getRetries(); i++){
            logger.info("TRY " + i + ":");
            SerialPortOutput.getInstance().send(messageJob.getRouteX());
            if(sleepAndCheck(3)){
                return;
            }
        }
        logger.info("Message Job has been finished by retries.");
        onFailedPostExecutions();
    }

    public boolean sleepAndCheck(Integer times){
        for(int i = 0; i < times; i++){
            try {
                Thread.sleep(DEFAULT_TIMEOUT/3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Messenger.getInstance().isJobFinished(this)){
                logger.info("Message Job has been finished by condition.");
                onSuccessfulPostExecutions();
                return true;
            }
        }
        return false;
    }

    public void onSuccessfulPostExecutions(){
        if(messageJob.getRouteX() instanceof RouteX.RouteRequest){
            if(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage() != null){
                ((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage().setNextNode(RoutingTable.getInstance().getNextForDestination(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage().getEndNode()));
                Messenger.getInstance().sendWithWorker(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage(), 3);
                logger.info("Sending message after received new route information.");
            }
        }
    }

    public void onFailedPostExecutions(){
        logger.info("Message Job has been sent " + messageJob.getRetries() + " times. MessageWorker stops now. Removing from worker list now. Sending Error.");
        Messenger.getInstance().removeWorker(this);
        RoutingTable.getInstance().removeRoute(messageJob.getRouteX().getEndNode());
        sendError();
    }

    public void sendError(){
        if(messageJob.getRouteX() instanceof RouteX.Message){
            RouteX.RouteError error = new RouteX.RouteError();
            error.setSource(Address.getInstance().getAddr());
            error.setFlag(RouteFlag.ERROR);
            error.setTimeToLive(INITIAL_TTL);
            error.setEndNode(messageJob.getRouteX().getEndNode());
            Messenger.getInstance().send(error);
        }
    }
}

//TODO benutzert will was senden, test erst ob es route gibt.
//wenn nicht, route request.. neues attribute in routeRequest: saved RouteX.Message, um später, wenn route gefunden neu zu senden
//wenn doch, senden
