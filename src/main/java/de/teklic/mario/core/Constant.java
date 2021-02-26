package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

public interface Constant {
    String PORT = "/dev/ttyS0";
    String BROADCAST = "FFFF";
    String CONFIG = "AT+CFG=433500000,5,9,7,1,1,0,0,0,0,3000,8,4";
    String[] IRRELEVANTS = {"AT,OK", "AT,SENDING", "T,OK", "ERR:CPU_BUSY", "AERR:CPU_BUSY"};
    long DEFAULT_TIMEOUT = 5000;
    int DEFAULT_RETRIES = 3;
    int INITIAL_TTL = 9;
}
