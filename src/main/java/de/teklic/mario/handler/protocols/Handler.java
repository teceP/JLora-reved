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
public abstract class Handler {
    private String handlerName;
    public abstract void handle(RouteX routeX);
}
