package de.teklic.mario.messanger;

import de.teklic.mario.model.routex.RouteFlag;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;

import static org.junit.jupiter.api.Assertions.*;

/*
 *
 * @author Mario Teklic
 */

class MessageWorkerTest {

    @Test
    void checkAckIfFinished() {
        RouteX.Message routeXmsg = new RouteX.Message();
        routeXmsg.setSource("140");
        routeXmsg.setPayload("hello");
        routeXmsg.setEndNode("130");
        MessageWorker messageWorker = new MessageWorker(new MessageJob(routeXmsg, 3));

        RouteX.Acknowledge routeXAck = new RouteX.Acknowledge();
        routeXAck.setSource("130");
        routeXAck.setPayload(Util.calcMd5(routeXmsg).substring(0, 6));
        routeXAck.setEndNode("140");
        messageWorker.propertyChange(new PropertyChangeEvent(Messenger.class, "messenger", new RouteX.Disposable(), routeXAck));

        Assertions.assertTrue(messageWorker.isFinished());
    }

    @Test
    void checkAckIfFinishedElseMsg() {
        RouteX.Message routeXmsg = new RouteX.Message();
        routeXmsg.setSource("140");
        routeXmsg.setPayload("hello");
        routeXmsg.setEndNode("130");
        MessageWorker messageWorker = new MessageWorker(new MessageJob(routeXmsg, 3));

        RouteX.Message anotherMsg = new RouteX.Message();
        anotherMsg.setSource("140");
        anotherMsg.setPayload("moin");
        anotherMsg.setEndNode("130");

        RouteX.Acknowledge routeXAck = new RouteX.Acknowledge();
        routeXAck.setSource("130");
        routeXAck.setPayload(Util.calcMd5(anotherMsg).substring(0, 6));
        routeXAck.setEndNode("140");
        messageWorker.propertyChange(new PropertyChangeEvent(Messenger.class, "messenger", new RouteX.Disposable(), routeXAck));

        Assertions.assertFalse(messageWorker.isFinished());
    }

    @Test
    void checkAckIfFinishedWrongHas() {
        RouteX.Message routeXmsg = new RouteX.Message();
        routeXmsg.setSource("140");
        routeXmsg.setPayload("hello");
        routeXmsg.setEndNode("130");
        MessageWorker messageWorker = new MessageWorker(new MessageJob(routeXmsg, 3));

        RouteX.Acknowledge routeXAck = new RouteX.Acknowledge();
        routeXAck.setSource("130");
        routeXAck.setPayload("wrongHash");
        routeXAck.setEndNode("140");
        messageWorker.propertyChange(new PropertyChangeEvent(Messenger.class, "messenger", new RouteX.Disposable(), routeXAck));

        Assertions.assertFalse(messageWorker.isFinished());
    }

    @Test
    void checkReplyIfFinished() {
        RouteX.RouteRequest routeXReq = new RouteX.RouteRequest();
        routeXReq.setSource("140");
        routeXReq.setEndNode("130");
        MessageWorker messageWorker = new MessageWorker(new MessageJob(routeXReq, 3));

        RouteX.RouteReply routeXReply = new RouteX.RouteReply();
        routeXReply.setSource("130");
        routeXReply.setEndNode("140");
        messageWorker.propertyChange(new PropertyChangeEvent(Messenger.class, "messenger", new RouteX.Disposable(), routeXReply));

        Assertions.assertTrue(messageWorker.isFinished());
    }
}
