package de.teklic.mario.event;

import de.teklic.mario.model.routex.RouteX;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageParcel {
    private Object source;
    private Object newObject;

    public MessageParcel(Object newObject){
        this.newObject = newObject;
    }
}
