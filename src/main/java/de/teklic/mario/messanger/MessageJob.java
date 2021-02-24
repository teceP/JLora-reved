package de.teklic.mario.messanger;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;

import java.util.UUID;

public class MessageJob {
    private RouteX routeX;
    private int retries;
    private String id;

    public MessageJob(RouteX routeX, int retries){
        id = UUID.randomUUID().toString();
        this.routeX = routeX;
        this.retries = retries;
    }
}
