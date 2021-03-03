package de.teklic.mario.model.routex;
/*
 *
 * @author Mario Teklic
 */

import java.util.Arrays;
import java.util.Optional;

/**
 * RouteFlags
 */
public enum RouteFlag {
    MESSAGE(1),
    ACKNOWLEDGE(2),
    REQUEST(3),
    REPLY(4),
    ERROR(5),
    NO_ROUTEX(-1);

    public final int flag;

    RouteFlag(int flag){
        this.flag = flag;
    }

    /**
     * Returns a RouteFlag, based on the flag-number
     * @param value flag-number
     * @return RouteFlag, which is reserved for this flag-number
     */
    public static Optional<RouteFlag> valueOf(int value){
        return Arrays.stream(values())
                .filter(x -> x.flag == value)
                .findFirst();
    }
}
