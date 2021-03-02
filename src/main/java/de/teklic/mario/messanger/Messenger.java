package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.io.output.SerialPortOutput;
import de.teklic.mario.util.Util;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Messenger {
    public static final Logger logger = Logger.getLogger(Messenger.class.getName());

    /**
     * Parameter "wie oft" wird übergeben.
     * Parameter "delay je loop" wird im executor service geregelt
     * <p>
     * Dies könnte die Oberklasse sein, welche den Executor Service hält.
     * Es gibt dann noch eine Subklasse, die Runnable ist.
     */
    private static Messenger messenger;
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

    public void update(RouteX routeX) {
        Util.newRoute(routeX);
        logger.info("RouteX is for me. Will check for running MessageWorker...");
        jobFinished(routeX);
    }

    public void sendWithWorker(RouteX routeX, int retries) {
        MessageJob job = new MessageJob(routeX, retries);
        MessageWorker worker = new MessageWorker(job);
        new Thread(worker).start();
        workerList.add(worker);
    }

    public void send(RouteX routeX) {
        SerialPortOutput.getInstance().send(routeX);
    }

    public void removeWorker(MessageWorker messageWorker) {
        workerList.removeIf(w -> w.getId().equalsIgnoreCase(messageWorker.getId()));
    }

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
        printWorkerList();
        return false;
    }

    //Wenn imn workerList passendes objekt vorhanden, dann true zurück und aus workerlist raus
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

    public boolean messageJobFinished(RouteX.Acknowledge acknowledge) {
        String acknowledgeHash = acknowledge.getPayload();
        Iterator<MessageWorker> it = workerList.iterator();

        while (it.hasNext()) {
            MessageWorker mw = it.next();
            if (mw.getMessageJob().getRouteX() instanceof RouteX.Message) {
                String messageHash = Util.calcMd5((RouteX.Message) mw.getMessageJob().getRouteX()).substring(0, 5);
                if (acknowledgeHash.equalsIgnoreCase(messageHash)) {
                    it.remove();
                    logger.info("Removes MessageWorker (RouteX.Message) from list according to an matching hash for message payload '" + ((RouteX.Message) mw.getMessageJob().getRouteX()).getPayload() + "'.");
                    return true;
                }
            }
        }
        logger.info("No matching MessageWorker was found for acknowledge: " + acknowledge);
        return false;
    }

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

    public void printWorkerList() {
        workerList.stream().forEach(w -> logger.info("Worker: " + w.getMessageJob().getRouteX() + ", with currently " + w.getMessageJob().getRetries()));
    }
}

