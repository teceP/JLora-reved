package de.teklic.mario.model.routex;

import de.teklic.mario.handler.protocols.HandlerName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RouteX abstract class, which contains all specific kind of object types.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class RouteX {

    /**
     * Source node
     */
    private String source;

    /**
     * Time to live, till expiration
     */
    private int timeToLive;

    /**
     * Tokenized Header
     */
    private TokenizedHeader tokenizedHeader;

    /**
     * RouteFlag
     */
    private de.teklic.mario.model.routex.RouteFlag flag;

    /**
     * EndNode
     */
    private String endNode;

    /**
     * Determines if this RouteX objects is based on an incoming request/reply/error/unreachable
     */
    private boolean incoming;

    /**
     * Arriving timestamp
     */
    private long timestamp;

    public RouteX(de.teklic.mario.model.routex.RouteFlag flag, String source, int timeToLive, TokenizedHeader tokenizedHeader, boolean incoming, String endNode) {
        this.flag = flag;
        this.source = source;
        this.timeToLive = timeToLive;
        this.tokenizedHeader = tokenizedHeader;
        this.incoming = incoming;
        this.endNode = endNode;
    }

    /**
     * Returns this object as a sendable string, which matches the protocol
     */
    public abstract String asSendable();

    /**
     * A String which determines which Handler is responsible for this object
     */
    public abstract String responsibleHandler();

    @Override
    public String toString() {
        return "Source: " + this.getSource() +
                ", TimeToLive: " + getTimeToLive() +
                ", Flag: " + this.getFlag().name();
    }

    /**
     * Specific implementation for an Message object
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Message extends RouteX {
        private String nextNode;
        private String payload;
        private int tries;
        private boolean ack;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getEndNode() + "|" + getNextNode() + "|" + getPayload() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.MESSAGE_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    ", TimeToLive: " + getTimeToLive() +
                    ", Destination: " + getEndNode() +
                    ", NextNode: " + nextNode +
                    ", Payload: " + payload +
                    ", Flag: " + this.getFlag().name();
        }
    }

    /**
     * Specific implementation for an Acknowledge object
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Acknowledge extends RouteX {
        private String payload;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getEndNode() + "|" + getPayload() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.ACKNOWLEDGE_HANDLER;
        }

        @Override
        public String toString() {
            return  ", Source: " + getSource() +
                    ", TimeToLive: " + getTimeToLive() +
                    ", Payload/Hash: " + payload +
                    ", Destination: " + getEndNode() +
                    ", Flag: " + this.getFlag().name();
        }
    }

    /**
     * Specific implementation for an Request object
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteRequest extends RouteX {
        private int hops;
        private long date;
        private RouteX.Message storedMessage;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getHops() + "|" + getEndNode() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.REQUEST_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    ", TimeToLive: " + getTimeToLive() +
                    ", EndNode: " + getEndNode() +
                    ", Hops: " + hops +
                    ", Flag: " + this.getFlag().name();
        }
    }

    /**
     * Specific implementation for an Reply object
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteReply extends RouteX {
        private String nextNode;
        private int hops;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getHops() + "|" + getEndNode() + "|" + getNextNode() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.REPLY_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    ", TimeToLive: " + getTimeToLive() +
                    ", EndNode: " + getEndNode() +
                    ", NextNode: " + nextNode +
                    ", Hops: " + hops +
                    ", Flag: " + this.getFlag().name();
        }
    }

    /**
     * Specific implementation for an Error object
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteError extends RouteX {

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getEndNode() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.ERROR_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    ", TimeToLive: " + getTimeToLive() +
                    ", BrokenNode: " + getEndNode() +
                    ", Flag: " + this.getFlag().name();
        }
    }

    /**
     * Specific implementation for an Disposable object.
     * This kind of objects get immediately dropped by JLora
     */
    public static class Disposable extends RouteX{

        @Override
        public String asSendable() {
            return null;
        }

        @Override
        public String responsibleHandler() {
            return null;
        }

        @Override
        public String toString() {
            return "Disposable object.";
        }
    }
}
