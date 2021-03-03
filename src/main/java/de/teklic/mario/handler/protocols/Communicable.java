package de.teklic.mario.handler.protocols;
/*
 *
 * @author Mario Teklic
 */


import de.teklic.mario.model.routex.RouteX;

public interface Communicable {

    /**
     * The Handler just forwards the incoming message to the next
     * node or broadcasts the message if no suitable route was found.
     * @param message An RouteX Object
     */
    void forward(RouteX message);

    /**
     * Handles the specific RouteX object, when it is for the own node.
     * @param message An RouteX Object
     */
    void forMe(RouteX message);
}
