package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageJob {
    private RouteX routeX;
    private int retries;

    public MessageJob(RouteX routeX, int retries){
        this.routeX = routeX;
        this.retries = retries;
    }
}
