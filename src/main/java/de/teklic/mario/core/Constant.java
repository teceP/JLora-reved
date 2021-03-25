package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

public interface Constant {
    /**
     * The RaspberryPi-Port for the LoRa-Module
     */
    String PORT = "/dev/ttyS0";

    /**
     * The broadcast address which is used for every outgoing message
     */
    String BROADCAST = "FFFF";

    /**
     * LoRa-Module Config String which consists out of several information.
     * 433500000:
     * 5:
     * 9:
     * 7:
     * 1:
     * 1:
     * 0:
     * 0:
     * 0:
     * 0:
     * 3000:
     * 8:
     * 4:
     */
    String CONFIG = "AT+CFG=433500000,5,9,7,1,1,0,0,0,0,3000,8,4";

    String CONFIG_HEAD = "AT+CFG=433500000,";
    String CONFIG_TAIL = ",9,7,1,1,0,0,0,0,3000,8,4";
    String VOLT = "volt:";

    /**
     * Irrelevant message types, which gets filtered out if they got received.
     * All kind of these messages wont be proceed by the software.
     */
    String[] IRRELEVANTS = {"AT,OK", "AT,SENDED", "SENDING","AT,SENDING", "T,OK", "ERR:CPU_BUSY", "AERR:CPU_BUSY"};

    /**
     * Default timeout between the MessageWorker's retries
     */
    long DEFAULT_TIMEOUT = 5000;

    /**
     * Default retries for a MessageWorker's flow
     */
    int DEFAULT_RETRIES = 3;

    /**
     * Initial TimeToLife parameter for self-created message, request, reply objects
     */
    int INITIAL_TTL = 5;

    /**
     * Logfile folder
     */
    String LOG_FOLDER = "logs";

    /**
     * Logfile name's for each class
     */
    String JLR_LOG = "jlora.log";
    String MTF_LOG = "multi_time_filter.log";
    String INIT_LOG = "initializer.log";
    String ACK_H_LOG = "acknowledge_handler.log";
    String ERR_H_LOG = "error_handler.log";
    String MSG_H_LOG = "message_handler.log";
    String REPL_H_LOG = "reply_handler.log";
    String REQ_H_LOG = "request_handler.log";
    String SER_INP_LOG = "serial_input.log";
    String USR_INP_LOG = "user_input.log";
    String SER_OUT_LOG = "serial_output.log";
    String USR_OUT_LOG = "user_output.log";
    String MSG_WRKR_LOG = "message_worker.log";
    String MSG_MSNGR_LOG = "messenger.log";
    String ROUT_TBL_LOG = "routing_table.log";
    String MSG_EVLTR_LOG = "message_evaluator.log";
    String USR_SRVC_LOG = "user_service.log";
    String UTL_LOG = "util.log";
}
