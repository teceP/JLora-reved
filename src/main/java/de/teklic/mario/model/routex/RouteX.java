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

    /**
     * Determines if this RouteX objects is based on an incoming request/reply/error/unreachable
     */
    private boolean incoming;

    public RouteX(de.teklic.mario.model.routex.RouteFlag flag, String source, int timeToLive, TokenizedHeader tokenizedHeader, boolean incoming) {
        this.flag = flag;
        this.source = source;
        this.timeToLive = timeToLive;
        this.tokenizedHeader = tokenizedHeader;
        this.incoming = incoming;
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
        private String destination;
        private String nextNode;
        private String payload;
        private int tries;
        private boolean ack;
        private String hash;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getDestination() + "|" + getNextNode() + "|" + getPayload() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.MESSAGE_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nDestination: " + destination +
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
        private String destination;
        private String payload;

        @Override
        public String asSendable(){
            return "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getDestination() + "|" + getPayload() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.ACKNOWLEDGE_HANDLER;
        }

        @Override
        public String toString() {
            return  "\nTimeToLive: " + getTimeToLive() +
                    "\nPayload: " + payload +
                    "\nDestination: " + destination +
                    "\nTokenizedHeader: " + getTokenizedHeader() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteRequest extends RouteX {
        private String endNode;
        private int hops;
        private long date;

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
                    "\nEndNode: " + endNode +
                    "\nHops: " + hops +
                    "\nTokenizedHeader: " + getTokenizedHeader() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteReply extends RouteX {
        private String endNode, nextNode;
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
                    "\nEndNode: " + endNode +
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
        private String brokenNode;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getBrokenNode() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.ERROR_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nBrokenNode: " + brokenNode +
                    "\nTokenizedHeader: " + getTokenizedHeader().toString() +
                    "\nFlag: " + this.getFlag().name();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RouteUnreachable extends RouteX {
        private String unreachableNode;

        @Override
        public String asSendable(){
            return "|" + getSource() + "|" + getFlag().flag + "|" + getTimeToLive() + "|" + getUnreachableNode() + "|";
        }

        @Override
        public String responsibleHandler() {
            return HandlerName.UNREACHABLE_HANDLER;
        }

        @Override
        public String toString() {
            return "Source: " + this.getSource() +
                    "\nTimeToLive: " + getTimeToLive() +
                    "\nUnreachableNode: " + unreachableNode +
                    "\nTokenizedHeader: " + getTokenizedHeader().toString() +
                    "\nFlag: " + this.getFlag().name();
        }
    }
}
