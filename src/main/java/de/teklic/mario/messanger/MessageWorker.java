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
import de.teklic.mario.util.Util;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
public class MessageWorker implements Runnable, PropertyChangeListener {

    public static final Logger logger = Logger.getLogger(MessageWorker.class.getName());

    /**
     * MessageJob
     */
    private MessageJob messageJob;

    /**
     * If this is true, the message could not be delivered to the endNode.
     */
    private boolean inactive;

    private boolean finished;

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
        for(int i = 0; i < messageJob.getRetries(); i++){
            if(finished){
                return;
            }
            logger.info("Try " + (i+1) + " ...");
            SerialPortOutput.getInstance().send(messageJob.getRouteX());
            waitAndListen(3);
        }
        logger.info("RouteX could not be delivered.");
        if(!finished){
            onFailedPostExecutions();
        }
    }

    /**
     * Checks if cancellation condition have met.
     * Example: An acknowledge has been registered after an message was sent out.
     * @param times Retries to check for condition.
     * @return true if MessageWorker can stop.
     * @return false if MessageWorker should try again.
     */
    public void waitAndListen(Integer times){
        for(int i = 0; i < times; i++){
            try {
                Thread.sleep(DEFAULT_TIMEOUT/10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Bases on the MessageJob's RouteX instance, other work will be done.
     *
     * Example: A Message must be sent to 0134. No Route for 0134 is stored.
     * Make a RouteX.Request first. After a reply was registered, fire a new RouteX.Message Job to the Messenger.
     * For this situation, the initial RouteX.Message must and can be stored inside of a RouteX.Request object.
     */
    private void onSuccessfulPostExecutions(){
        logger.info("onSuccessfulPostExecutions");
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
    private void onFailedPostExecutions(){
        logger.info("Message Job has been sent " + messageJob.getRetries() + " times. MessageWorker stops now. Setting worker to inactive state. Sending Error.");
        logger.info("onFailedPostExecutions: setting inactive. gets notified, if routeX gets received later ...");
        this.setInactive(true);
        this.setFinished(false);
        RoutingTable.getInstance().removeRoute(messageJob.getRouteX().getEndNode());
        sendError();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // if not finished OR not finished AND inactive
        if(!finished){

            RouteX r = (RouteX) evt.getNewValue();
            int flag = r.getFlag().flag;
            if(flag == RouteFlag.ACKNOWLEDGE.flag || flag == RouteFlag.REPLY.flag){
                if(r.getFlag().flag == RouteFlag.ACKNOWLEDGE.flag && messageJob.getRouteX().getFlag().flag == RouteFlag.MESSAGE.flag){
                    finished = checkAckIfFinished((RouteX.Acknowledge) r);
                }else if(r.getFlag().flag == RouteFlag.REPLY.flag && messageJob.getRouteX().getFlag().flag == RouteFlag.REQUEST.flag){
                    finished = checkReplyIfFinished((RouteX.RouteReply) r);
                }
            }else{
                logger.info("PropertyChangeEvent does not contains Acknowledge or Repy. Was: " + r.getFlag());
            }

            if(inactive && finished){
                logger.info("Job has been finished, after MessageWorker was inactive!");
            }

            if(finished){
                setInactive(true);
                Messenger.getInstance().removePropertyChangeListener(this);
                onSuccessfulPostExecutions();
            }
        }
    }

    public boolean checkAckIfFinished(RouteX.Acknowledge acknowledge){
        String awaitedHash = Util.calcMd5((RouteX.Message) messageJob.getRouteX()).substring(0, 6);
        if (acknowledge.getPayload().equalsIgnoreCase(awaitedHash)) {
            logger.info("Message Job has finished by conditions!");
            return true;
        }
        return false;
    }

    public boolean checkReplyIfFinished(RouteX.RouteReply reply){
        String requestedNode = messageJob.getRouteX().getEndNode();
        if (reply.getSource().equalsIgnoreCase(requestedNode)) {
            logger.info("Request Job has finished by conditions!");
            return true;
        }

        return false;
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
