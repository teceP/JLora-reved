package de.teklic.mario.handler.protocols;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
/**
 * A Handler can handle a specific instance of an RouteX object.
 * The handler implements the Communicable interface, which guarentees, that a specific handler
 * can work with an incoming RouteX object, even if its not for the own node.
 */
public abstract class Handler implements Communicable{

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
}
