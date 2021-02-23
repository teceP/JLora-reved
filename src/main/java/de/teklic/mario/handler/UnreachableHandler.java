package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.handler.protocols.Communicable;
import de.teklic.mario.handler.protocols.Handler;
import de.teklic.mario.model.routex.RouteX;

public class UnreachableHandler extends Handler implements Communicable {

    @Override
    public void handle(RouteX routeX) {

    }

    @Override
    public void send(String endNode, String message) {

    }

    @Override
    public void forward(RouteX message) {

    }
}
