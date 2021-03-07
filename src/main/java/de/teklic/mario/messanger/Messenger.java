package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.util.Util;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Messenger {

    public static final Logger logger = Logger.getLogger(Messenger.class.getName());

    /**
     * Singleton Object
     */
    private static Messenger messenger;

    /**
     * List of all current workers
     */
    private List<MessageWorker> workerList;

    private Messenger() {
        workerList = new ArrayList<>();
    }

    public static Messenger getInstance() {
        if (messenger == null) {
            messenger = new Messenger();
        }
        return messenger;
    }

    /**
     * Checks if this incoming routeX object has any connection to a running worker
     * @param routeX Incoming routeX
     */
    public void update(RouteX routeX) {
        Util.newRoute(routeX);
        logger.info("RouteX is for me. Will check for running MessageWorker...");
        jobFinished(routeX);
    }

    /**
     * Sends a routeX with a worker in another thread.
     * Checks retries * 3 times if any incoming RouteX matches this routeX (like: Message -> Acknowledge)
     *
     * @param routeX Any RouteX
     * @param retries How often should this routeX be sent
     */
    public void sendWithWorker(RouteX routeX, int retries) {
        MessageJob job = new MessageJob(routeX, retries);
        MessageWorker worker = new MessageWorker(job);
        new Thread(worker).start();
        workerList.add(worker);
    }

    /**
     * Sends an routeX object only once without a worker
     * @param routeX
     */
    public void send(RouteX routeX) {
        SerialPortOutput.getInstance().send(routeX);
    }

    /**
     * Removes a MessageWorker
     * @param messageWorker
     */
    public void removeWorker(MessageWorker messageWorker) {
        workerList.removeIf(w -> w.getId().equalsIgnoreCase(messageWorker.getId()));
    }

    /**
     * Validates if a job has been finished
     * @param worker Worker
     * @return true if finished
     */
    public boolean isJobFinished(MessageWorker worker) {
        List<Object> list = workerList
                .stream()
                .map(MessageWorker::getId)
                .distinct()
                .filter(w -> w.equalsIgnoreCase(worker.getId()))
                .collect(Collectors.toList());

        if (list.size() == 0) {
            return true;
        }

        logger.info("Job " + worker.getMessageJob().getRouteX().getFlag() + " to " + worker.getMessageJob().getRouteX().getEndNode() + " not finished.");
        return false;
    }

    /**
     * Validates if this is an answer for a running MessageWorker like an Acknowledge for a Message
     */
    public boolean jobFinished(RouteX routeX) {

        //When acknowledge arrives, according to a message from before
        if (routeX instanceof RouteX.Acknowledge) {
            return messageJobFinished((RouteX.Acknowledge) routeX);
        }

        if (routeX instanceof RouteX.RouteReply) {
            return requestJobFinished((RouteX.RouteReply) routeX);
        }

        return false;
    }

    /**
     * Validates if any of the Workers has a connection to this acknowledge
     * @param acknowledge
     * @return true if a running MessageWorker is affected to this Acknowledge
     */
    public boolean messageJobFinished(RouteX.Acknowledge acknowledge) {
        String acknowledgeHash = acknowledge.getPayload();
        Iterator<MessageWorker> it = workerList.iterator();

        while (it.hasNext()) {
            MessageWorker mw = it.next();
            if (mw.getMessageJob().getRouteX() instanceof RouteX.Message) {
                String messageHash = Util.calcMd5((RouteX.Message) mw.getMessageJob().getRouteX()).substring(0, 6);
                if (acknowledgeHash.equalsIgnoreCase(messageHash)) {
                    it.remove();
                    logger.info("Removes MessageWorker (RouteX.Message) from list according to an matching hash for message payload '" + ((RouteX.Message) mw.getMessageJob().getRouteX()).getPayload() + "'.");
                    return true;
                }
            }
        }
        logger.info("No matching MessageWorker was found for acknowledge: " + acknowledge);
        printWorkerList();
        return false;
    }

    /**
     * Validates if any of the Workers has a connection to this reply
     * @param reply
     * @return true if a running MessageWorker is affected to this Reply
     */
    public boolean requestJobFinished(RouteX.RouteReply reply) {
        String replierNode = reply.getSource();
        Iterator<MessageWorker> it = workerList.iterator();

        while (it.hasNext()) {
            MessageWorker mw = it.next();
            if (mw.getMessageJob().getRouteX() instanceof RouteX.RouteRequest) {
                String requestedNode = (mw.getMessageJob().getRouteX()).getEndNode();
                if (replierNode.equalsIgnoreCase(requestedNode)) {
                    it.remove();
                    logger.info("Removes MessageWorker (RouteX.Request) from list according to an matching requested/replier node.");
                    return true;
                }
            }
        }
        logger.info("No matching MessageWorker was found for reply: " + reply);
        return false;
    }

    /**
     * Prints all workers
     */
    public void printWorkerList() {
        System.out.println("+++++++++++ WORKERLIST +++++++++++");
        workerList.stream().forEach(w -> {
            if(w.getMessageJob().getRouteX() instanceof RouteX.Message){
            logger.info("Worker: " + w.getMessageJob().getRouteX() + ", with awaited hash " + Util.calcMd5((RouteX.Message) w.getMessageJob().getRouteX()).substring(0, 6));
        }
        });
        System.out.println("++++++++ WORKERLIST END ++++++++");
    }
}
