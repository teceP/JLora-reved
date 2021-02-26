package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.core.JLora;
import de.teklic.mario.output.SerialPortOutput;
import de.teklic.mario.routingtable.RoutingTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static de.teklic.mario.core.Constant.TIMEOUT;

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
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JLora.logger.info("Message Job has been finished by retries.");
            if(Messenger.getInstance().isJobFinished(this)){
                JLora.logger.info("Message Job has been finished by condition.");
                return;
            }
        }
        JLora.logger.info("Message Job has been sent " + messageJob.getRetries() + " times. MessageWorker stops now. Removing from worker list now.");
        Messenger.getInstance().removeWorker(this);
        //Remove node + send RouteError
        //TODO both
    }
}
