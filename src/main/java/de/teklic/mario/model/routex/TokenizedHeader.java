package de.teklic.mario.model.routex;

import lombok.Getter;
import lombok.Setter;

/*
 *
 * @author Mario Teklic
 */

@Getter
@Setter
public class TokenizedHeader {
    private String lr;
    private String origin;
    private int length;
    private boolean empty;
    private String plain;

    /**
     *
     * @param lr
     * @param origin
     * @param length in Hex as String
     */
    public TokenizedHeader(String lr, String origin, String length, String plain){
        this.lr = lr;
        this.origin = origin;
        this.length = Integer.parseInt(length, 16);
        this.plain = plain;
    }

    public TokenizedHeader(boolean empty){
        this.empty = empty;
        this.lr = "";
        this.origin = "";
        this.plain = "";
    }

    public TokenizedHeader() {

    }

    @Override
    public String toString() {
        if(empty){
            return "[TokenizedHeader]: Plain: " + this.plain;
        }else{
            return "[TokenizedHeader]: LR:" + this.lr  + ", Origin Address: " + this.origin + ", Length: " + this.length + ", Plain: " + this.plain;
        }
    }

    public void setLength(String length){
        this.length = Integer.parseInt(length, 16);
    }
}
