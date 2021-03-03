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
import java.util.logging.Logger;

import static de.teklic.mario.core.Constant.DEFAULT_TIMEOUT;
import static de.teklic.mario.core.Constant.INITIAL_TTL;

/**
 * A MessageWorker is responsible for a MessageJob.
 * It reruns a Job, based on the MessageJobs information.
 * It also checks for conditions if a job has finished
 */
@Getter
@Setter
public class MessageWorker implements Runnable{

    public static final Logger logger = Logger.getLogger(MessageWorker.class.getName());

    /**
     * MessageJob
     */
    private MessageJob messageJob;

    /**
     * A random ID recognition and matching
     */
    private String id;

    public MessageWorker(MessageJob messageJob){
        this.messageJob = messageJob;
        id = UUID.randomUUID().toString();
    }

    /**
     * Runs in an extra thread and tries to send out a RouteX, based on the MessageJobs information.
     * Checks several times (retries * 3) if condition matches. If so, the MessageWorker will stop his work.
     * If not and all retries have been tried, the worker stops and makes other actions, based on which kind of
     * RouteX instance it is.
     */
    @Override
    public void run() {
        for(int i = 0; i < messageJob.getRetries()-1; i++){
            logger.info("TRY " + (i+1) + ":");
            SerialPortOutput.getInstance().send(messageJob.getRouteX());
            if(sleepAndCheck(3)){
                return;
            }
        }
        logger.info("Message Job has been finished by retries.");
        onFailedPostExecutions();
    }

    /**
     * Checks if cancellation condition have met.
     * Example: An acknowledge has been registered after an message was sent out.
     * @param times Retries to check for condition.
     * @return true if MessageWorker can stop.
     * @return false if MessageWorker should try again.
     */
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

    /**
     * Bases on the MessageJob's RouteX instance, other work will be done.
     *
     * Example: A Message must be sent to 0134. No Route for 0134 is stored.
     * Make a RouteX.Request first. After a reply was registered, fire a new RouteX.Message Job to the Messenger.
     * For this situation, the initial RouteX.Message must and can be stored inside of a RouteX.Request object.
     */
    public void onSuccessfulPostExecutions(){
        if(messageJob.getRouteX() instanceof RouteX.RouteRequest){
            if(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage() != null){
                ((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage().setNextNode(RoutingTable.getInstance().getNextForDestination(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage().getEndNode()));
                Messenger.getInstance().sendWithWorker(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage(), 3);
                logger.info("Sending message after received new route information.");
            }
        }
    }

    /*
     * If a job fails, the worker and also the route has to be removed.
     * After that, an error gets sent out.
     */
    public void onFailedPostExecutions(){
        logger.info("Message Job has been sent " + messageJob.getRetries() + " times. MessageWorker stops now. Removing from worker list now. Sending Error.");
        Messenger.getInstance().removeWorker(this);
        RoutingTable.getInstance().removeRoute(messageJob.getRouteX().getEndNode());
        sendError();
    }

    /**
     * An error message gets created and gets sent out.
     * The Error is based on the MessageJobs information.
     */
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