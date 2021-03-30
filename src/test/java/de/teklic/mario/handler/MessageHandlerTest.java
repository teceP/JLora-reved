package de.teklic.mario.handler;

import de.teklic.mario.core.Address;
import de.teklic.mario.model.routex.RouteX;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 *
 * @author Mario Teklic
 */

class MessageHandlerTest {

    /**
     * Create request first and get route before sending out message
     */
    @Test
    void createRequest() {
        Address.getInstance().setAddr("0140");
        RouteX.Message message = new RouteX.Message();
        message.setSource(Address.getInstance().getAddr());
        message.setPayload("hello world");
        message.setEndNode("0130");

        RouteX.RouteRequest req = new MessageHandler().createRequest(message);
        Assertions.assertTrue(req.getSource().equals(Address.getInstance().getAddr()));
        Assertions.assertTrue(req.getEndNode().equals("0130"));
        Assertions.assertTrue(req.getStoredMessage() != null && req.getStoredMessage().getPayload().equals(message.getPayload()));
    }
}
