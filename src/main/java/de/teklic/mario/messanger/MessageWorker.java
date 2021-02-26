package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.Address;
import de.teklic.mario.core.JLora;
import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.output.SerialPortOutput;
import de.teklic.mario.routingtable.RoutingTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static de.teklic.mario.core.Constant.DEFAULT_TIMEOUT;
import static de.teklic.mario.core.Constant.INITIAL_TTL;

@Getter
@Setter
public class MessageWorker implements Runnable{

    private MessageJob messageJob;
    private ScheduledFuture<?> future;
    private String id;

    public MessageWorker(MessageJob messageJob){
        this.messageJob = messageJob;
        id = UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        for(int i = 0; i < messageJob.getRetries(); i++){
            SerialPortOutput.getInstance().send(messageJob.getRouteX());
            try {
                Thread.sleep(DEFAULT_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JLora.logger.info("Message Job has been finished by retries.");
            if(Messenger.getInstance().isJobFinished(this)){
                JLora.logger.info("Message Job has been finished by condition.");
                onSuccessfulPostExecutions();
                return;
            }
        }
        onFailedPostExecutions();
    }

    public void onSuccessfulPostExecutions(){
        if(messageJob.getRouteX() instanceof RouteX.RouteRequest){
            Messenger.getInstance().sendWithWorker(((RouteX.RouteRequest) messageJob.getRouteX()).getStoredMessage(), 3);
        }
    }

    public void onFailedPostExecutions(){
        JLora.logger.info("Message Job has been sent " + messageJob.getRetries() + " times. MessageWorker stops now. Removing from worker list now.");
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
