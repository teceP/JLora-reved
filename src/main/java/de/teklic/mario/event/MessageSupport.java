package de.teklic.mario.event;

import java.util.ArrayList;
import java.util.List;

public class MessageSupport {

    private List<MessageListener> listeners = new ArrayList<>();
    private Object source;

    public MessageSupport(Object src){
        this.source = src;
    }

    public void fireMessageParcel(MessageParcel parcel){
        parcel.setSource(source);
        listeners.stream().forEach(l -> l.newMessage(parcel));
    }

    public void addListener(MessageListener ml){
        listeners.add(ml);
    }

    public void removeListener(MessageListener ml){
        listeners.remove(ml);
    }
}
