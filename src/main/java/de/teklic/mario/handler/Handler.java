package de.teklic.mario.handler;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.handler.protocols.Communicable;
import de.teklic.mario.messanger.Messenger;
import de.teklic.mario.model.routex.RouteX;
import de.teklic.mario.routingtable.RoutingTable;
import de.teklic.mario.util.Util;
import lombok.Getter;
import lombok.Setter;

import static de.teklic.mario.routingtable.RoutingTable.NO_NEXT;
import static de.teklic.mario.routingtable.RoutingTable.logger;


@Getter
@Setter
/**
 * A Handler can handle a specific instance of an RouteX object.
 * The handler implements the Communicable interface, which guarentees, that a specific handler
 * can work with an incoming RouteX object, even if its not for the own node.
 */
public abstract class Handler implements Communicable {

    /**
     * Represents the handler. This information is needed when the correct handler must be chosen.
     * The Handler name is the same as the class names, but in lower case and with underscores.
     */
    private String handlerName;

    /**
     * A handler can handle incoming RouteX objects.
     * It is needed to be a specific handler-object to differentiate by specific RouteX objects.
     * @param routeX
     */
    public abstract void handle(RouteX routeX);

    @Override
    public void forward(RouteX message) {
        RoutingTable.getInstance().add(message);

        if(message instanceof RouteX.Message){
            if(RoutingTable.getInstance().getNextForDestination(message.getEndNode()).equalsIgnoreCase(NO_NEXT)){
                //Drop if has no next node for destination x
                logger.info("Dropped RouteX.Message due to no NextNode was found for EndNode " + message.getEndNode() + ".");
                return;
            }
        }

        if(message.getTimeToLive() > 0) {
            Util.prepareToForward(message);
            Messenger.getInstance().send(message);
        }
    }
}
