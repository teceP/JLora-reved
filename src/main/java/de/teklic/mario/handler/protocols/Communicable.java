package de.teklic.mario.handler.protocols;
/*
 *
 * @author Mario Teklic
 */


import de.teklic.mario.model.routex.RouteX;

public interface Communicable {
    /**
     * Send a own and new RouteX out.
     * The RouteX must be an object of the extending class.
     * E.g. RequestHandler only sends RouteX.Request out.
     *
     * @param endNode The receiving node's id
     * @param message The content of the message
     */
    void send(String endNode, String message);

    /**
     * The Handler just forwards the incoming message to the next
     * node or broadcasts the message if no suitable route was found.
     * @param message
     */
    void forward(RouteX message);

    void forMe(RouteX message);
}
