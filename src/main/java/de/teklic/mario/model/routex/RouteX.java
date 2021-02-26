package de.teklic.mario.model.routex;

import de.teklic.mario.handler.protocols.HandlerName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class RouteX {
    private String source;
    private int timeToLive;
    private TokenizedHeader tokenizedHeader;
    private de.teklic.mario.model.routex.RouteFlag flag;
    private String endNode;

    /**
     * Determines if this RouteX objects is based on an incoming request/reply/error/unreachable
     */
    private boolean incoming;

    public RouteX(de.teklic.mario.model.routex.RouteFlag flag, String source, int timeToLive, TokenizedHeader tokenizedHeader, boolean incoming, String endNode) {
        this.flag = flag;
        this.source = source;
        this.timeToLive = timeToLive;
        this.tokenizedHeader = tokenizedHeader;
        this.incoming = incoming;
        this.endNode = endNode;
    }

    public abstract String asSendable();
    public abstract String responsibleHandler();

    @Override
    public String toString() {
        return "Source: " + this.getSource() +
                "\nTimeToLive: " + getTimeToLive() +
                "\nTokenizedHeader: " + getTokenizedHeader().toString() +
                "\nFlag: " + this.getFlag().name();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Message extends RouteX {
        private String nextNode;
        private String payload;
        private int tries;
        private boolean ack;
        private String hash;

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
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nDestination: " + getEndNode() +
                    "\nNextNode: " + nextNode +
                    "\nPayload: " + payload +
                    "\nHash: " + hash +
                    "\nTokenizedHeader: " + getTokenizedHeader() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

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
            return  "\nSource: " + getSource() +
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nPayload: " + payload +
                    "\nDestination: " + getEndNode() +
                    "\nTokenizedHeader: " + getTokenizedHeader() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

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
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nEndNode: " + getEndNode() +
                    "\nHops: " + hops +
                    "\nTokenizedHeader: " + getTokenizedHeader() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

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
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nEndNode: " + getEndNode() +
                    "\nNextNode: " + nextNode +
                    "\nHops: " + hops +
                    "\nTokenizedHeader: " + getTokenizedHeader().toString() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

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
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nBrokenNode: " + getEndNode() +
                    "\nTokenizedHeader: " + getTokenizedHeader().toString() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteUnreachable extends RouteX {

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getEndNode() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.UNREACHABLE_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nUnreachableNode: " + getEndNode() +
                    "\nTokenizedHeader: " + getTokenizedHeader().toString() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

    public static class Disposable extends RouteX{

        @Override
        public String asSendable() {
            return null;
        }

        @Override
        public String responsibleHandler() {
            return null;
        }
    }
}
