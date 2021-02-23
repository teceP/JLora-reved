package de.teklic.mario.handler.protocols;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;

@Getter
public abstract class Handler {

    public abstract void handle(RouteX routeX);
}
