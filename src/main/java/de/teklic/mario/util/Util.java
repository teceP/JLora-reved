package de.teklic.mario.util;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;
import org.apache.commons.codec.digest.DigestUtils;

public class Util {

    /**
     * Calculates the MD5-Hash with the own address and the payload
     * @param message
     * @return
     */
    public static String calcMd5(RouteX.Message message){
        String sender = message.getSource();
        String payload = message.getPayload();
        return calcMd5(sender, payload);
    }

    public static String calcMd5(String sender, String payload){
        String hash = DigestUtils.md5Hex(sender + payload);
        System.out.println("Calculated hash: " + hash);
        System.out.println("Shortend hash: " + hash.substring(0, 5));
        return DigestUtils.md5Hex(sender + payload);
    }
}
