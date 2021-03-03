package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import lombok.Getter;
import lombok.Setter;

/**
 * A MessageJob holds the RouteX object and the information about, how often this job should
 * be tried, before it gets discarded or an RouteX.Error will be sent out.
 */
@Getter
@Setter
public class MessageJob {

    /**
     * RouteX object
     */
    private RouteX routeX;

    /**
     * Retries, till discard or doing other actions
     */
    private int retries;

    public MessageJob(RouteX routeX, int retries){
        this.routeX = routeX;
        this.retries = retries;
    }
}
