package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

public interface Constant {
    String PORT = "/dev/ttyS0";
    String BROADCAST = "FFFF";
    String CONFIG = "AT+CFG=433500000,5,9,7,1,1,0,0,0,0,3000,8,4";
    String[] IRRELEVANTS = {"AT,OK", "AT,SENDED", "AT,SENDING", "T,OK", "ERR:CPU_BUSY", "AERR:CPU_BUSY"};
    long DEFAULT_TIMEOUT = 5000;
    int DEFAULT_RETRIES = 3;
    int INITIAL_TTL = 9;

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
}
