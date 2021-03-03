package de.teklic.mario.handler.protocols;

/**
 * Final Handlernames. Each handler can and must have one handlername.
 */
public interface HandlerName {
    String ACKNOWLEDGE_HANDLER = "acknowledge_handler";
    String ERROR_HANDLER = "error_handler";
    String MESSAGE_HANDLER = "message_handler";
    String REPLY_HANDLER = "reply_handler";
    String REQUEST_HANDLER = "request_handler";
}
