package de.teklic.mario.io.input;
/*
 *
 * @author Mario Teklic
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class MultiTimeFilter implements Filterable {

    public static final Logger logger = Logger.getLogger(MultiTimeFilter.class.getName());

    private List<MessageItem> messageList;

    private long minTimeDiff = 5000;

    public MultiTimeFilter() {
        messageList = new ArrayList<>();
    }

    @Override
    public String filter(String msg) {
        long currentTime = System.currentTimeMillis();

        //Tidy up: only item with < 5 seconds are in this list
        messageList.removeIf(m -> (currentTime - m.getTimestamp()) > minTimeDiff);

        //If any match, it has < 5 seconds and should not be forwarded
        if(messageList.stream().anyMatch(m -> m.getMsg().equals(msg))){
            return SHOULD_NOT;
        }else{
            //This msg was not found in the list. Will be added to the list
            messageList.add(new MessageItem(msg, currentTime));
        }

        return msg;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class MessageItem {
        private String msg;
        private long timestamp;
    }
}
