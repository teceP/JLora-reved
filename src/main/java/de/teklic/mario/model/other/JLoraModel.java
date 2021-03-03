package de.teklic.mario.model.other;

import de.teklic.mario.handler.protocols.Handler;
import lombok.Getter;
import lombok.Setter;
import purejavacomm.SerialPort;

import java.util.List;

/**
 * JLoraModel
 */
@Getter
@Setter
public class JLoraModel {

    /**
     * UserInput Thread
     */
    private Thread userInputThread;

    /**
     * SerialPort
     */
    private SerialPort serialPort;

    /**
     * All specific handlers
     */
    private List<Handler> handlers;
}
