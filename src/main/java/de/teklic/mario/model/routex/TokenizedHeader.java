package de.teklic.mario.model.routex;

import lombok.Getter;
import lombok.Setter;

/*
 *
 * @author Mario Teklic
 */

/**
 * The TokenizedHeader object consists out of the LoRa-Module receiving information.
 * It represents only the first part like following:
 * A normal message gets received like "LR,01AD,412,payload".
 * The TokenizedHeader is only the part to the payload.
 * For debug purposes, the complete received String is also saved.
 */
@Getter
@Setter
public class TokenizedHeader {

    /**
     * LR
     */
    private String lr;

    /**
     * Origin nodes address
     */
    private String origin;

    /**
     * Length of message
     */
    private int length;

    /**
     * Empty must be true, if a RouteX object gets initialized and stays empty, to avoid errors.
     * (Outdated: not in use anymore.)
     */
    private boolean empty;

    /**
     * Plain received String
     */
    private String plain;

    public TokenizedHeader(boolean empty){
        this.empty = empty;
        this.lr = "";
        this.origin = "";
        this.plain = "";
    }

    public TokenizedHeader() {}

    @Override
    public String toString() {
        if(empty){
            return "[TokenizedHeader]: Plain: " + this.plain;
        }else{
            return "[TokenizedHeader]: LR:" + this.lr  + ", Origin Address: " + this.origin + ", Length: " + this.length + ", Plain: " + this.plain;
        }
    }

    /**
     * Sets the length to hex number.
     * @param length as String
     */
    public void setLength(String length){
        this.length = Integer.parseInt(length, 16);
    }
}
