package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteX;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Messenger implements Observer {
    /**
     * Parameter "wie oft" wird übergeben.
     * Parameter "delay je loop" wird im executor service geregelt
     *
     * Dies könnte die Oberklasse sein, welche den Executor Service hält.
     * Es gibt dann noch eine Subklasse, die Runnable ist.
     */
    private static Messenger messenger;
    private ScheduledExecutorService executorService;
    private List<MessageWorker> workerList;

    private Messenger(){
        workerList = new ArrayList<>();
        executorService = Executors.newScheduledThreadPool(10);
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

        if(list.size() > 1){
            JLora.logger.info("Found multiple MessageWorkers: " + list.size());
            return false;
        }

        return false;
    }

    public void jobFinished(MessageWorker worker){
        workerList.removeIf(w -> w.getId().equalsIgnoreCase(worker.getId()));
    }

    //Wenn imn workerList passendes objekt vorhanden, dann true zurück und aus workerlist raus
    public void jobFinished(RouteX routeX){
        //Testen anhand einer speziellen id?
        //Id anstatt UUID generieren z.B. konkatenieren von bestimmten Daten

        //TODO
        if(routeX instanceof RouteX.Acknowledge){
            //When acknowledge arrives, according to a message from before

        }

        if(routeX instanceof RouteX.RouteReply) {
            //When reply arrives, according to a request from before

        }
    }

    public String generateId(RouteX routeX){
        String id;

        //TODO
        //An incoming and outgoing must match with String.equalsIgnoreCase
        //Example1: Zielnummer + x von Acknowledge, Startnummer + x von Message (Würde schon reichen!)
        //Example2: Zielnummer + x von Reply,       Startnummer + x von Request (Würde auch schon reichen.)

        //An incoming routeX
        if(routeX instanceof RouteX.Acknowledge){

        }

        if(routeX instanceof RouteX.RouteReply) {

        }

        //An outgoing routeX
        if(routeX instanceof RouteX.Message){

        }

        if(routeX instanceof RouteX.RouteRequest) {

        }

        return "";
    }


}

