package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.output.SerialPortOutput;
import de.teklic.mario.util.Util;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.teklic.mario.model.other.JLoraModel.SENDER_ADDR;

public class Messenger implements Observer {
    /**
     * Parameter "wie oft" wird übergeben.
     * Parameter "delay je loop" wird im executor service geregelt
     *
     * Dies könnte die Oberklasse sein, welche den Executor Service hält.
     * Es gibt dann noch eine Subklasse, die Runnable ist.
     */
    private static Messenger messenger;
    private List<MessageWorker> workerList;

    private Messenger(){
        workerList = new ArrayList<>();
    }

    public static Messenger getInstance(){
        if(messenger == null){
            messenger = new Messenger();
        }
        return messenger;
    }

    @Override
    public void update(Observable o, Object arg) {
        RouteX routeX = (RouteX) arg;
        if(routeX != null && Util.isRouteXForMe(routeX)){
            JLora.logger.info("RouteX is for me. Will check for running MessageWorker...");
            jobFinished(routeX);
        }
    }

    /**
     *         //TODO
     * Use Case 1: A message gets sended.
     *     Es muss 3 mal gesendet werden. Der Hash vom meiner Adresse + Payload muss gespeichert werden, um später abgegleicht zu werden.
     *
     *         //TODO
     * Use Case 2: Requests:
     *      Wenn Reply reingekommen ist, kann der entsprechende worker vom request aus der liste genommen werden
     *
     * @param routeX
     * @param retries
     */
    public void sendWithWorker(RouteX routeX, int retries){
        MessageJob job = new MessageJob(routeX, retries);
        MessageWorker worker = new MessageWorker(job);
        new Thread(worker).start();
        workerList.add(worker);
    }

    public void send(RouteX routeX){
        SerialPortOutput.getInstance().send(routeX);
    }

    public void removeWorker(MessageWorker messageWorker){
        workerList.removeIf(w -> w.getId().equalsIgnoreCase(messageWorker.getId()));
    }

    public boolean isJobFinished(MessageWorker worker){
        List<Object> list = workerList
                .stream()
                .map(MessageWorker::getId)
                .distinct()
                .filter(w -> w.equalsIgnoreCase(worker.getId()))
                .collect(Collectors.toList());

        if(list.size() == 0){
            return true;
        }

        if(list.size() == 1){
            JLora.logger.info("Found one MessageWorker.");
            return false;
        }

        if(list.size() > 1){
            JLora.logger.info("Failure: Found multiple MessageWorkers: " + list.size());
            return false;
        }

        return false;
    }

    //Wenn imn workerList passendes objekt vorhanden, dann true zurück und aus workerlist raus
    public boolean jobFinished(RouteX routeX){

        //When acknowledge arrives, according to a message from before
        if(routeX instanceof RouteX.Acknowledge){
            return messageJobFinished((RouteX.Acknowledge) routeX);
        }

        if(routeX instanceof RouteX.RouteReply) {
            return requestJobFinished((RouteX.RouteReply) routeX);
        }

        return false;
    }

    public boolean messageJobFinished(RouteX.Acknowledge acknowledge){
        String acknowledgeHash = acknowledge.getPayload();
        Iterator<MessageWorker> it = workerList.iterator();

        while(it.hasNext()){
            MessageWorker mw = it.next();
            if(mw.getMessageJob().getRouteX() instanceof RouteX.Message){
                String messageHash = Util.calcMd5((RouteX.Message) mw.getMessageJob().getRouteX());
                if(acknowledgeHash.equalsIgnoreCase(messageHash)){
                    it.remove();
                    JLora.logger.info("Removes MessageWorker (RouteX.Message) from list according to an matching hash for message payload '" + ((RouteX.Message) mw.getMessageJob().getRouteX()).getPayload() + "'.");
                    return true;
                }
            }
        }
        JLora.logger.info("No matching MessageWorker was found for acknowledge: " + acknowledge);
        return false;
    }

    public boolean requestJobFinished(RouteX.RouteReply reply){
        String replierNode = reply.getSource();
        Iterator<MessageWorker> it = workerList.iterator();

        while (it.hasNext()){
            MessageWorker mw = it.next();
            if(mw.getMessageJob().getRouteX() instanceof RouteX.RouteRequest){
                String requestedNode = ((RouteX.RouteRequest) mw.getMessageJob().getRouteX()).getEndNode();
                if(replierNode.equalsIgnoreCase(requestedNode)){
                    it.remove();
                    JLora.logger.info("Removes MessageWorker (RouteX.Request) from list according to an matching requested/replier node.");
                    return true;
                }
            }
        }
        JLora.logger.info("No matching MessageWorker was found for reply: " + reply);
        return false;
    }

    public void printWorkerList(){
        workerList.stream().forEach(w -> JLora.logger.info("Worker: " + w.getMessageJob().getRouteX() + ", with currently " + w.getMessageJob().getRetries()));
    }

        //TODO
        //An incoming and outgoing must match with String.equalsIgnoreCase
        //Example1: Zielnummer + x von Acknowledge, Startnummer + x von Message (Würde schon reichen!) -> Dafür gibt es diese md5 hash berechnung.
        //Example2: Zielnummer + x von Reply,       Startnummer + x von Request (Würde auch schon reichen.) -> -
        //
        //
        // Message  ->  Message     ->  Message
        //                                     |
        //                                     V
        // Acknowledge <- Acknowledge <- Acknowledge
        //
}

