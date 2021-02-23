package de.teklic.mario.model.routex;
/*
 *
 * @author Mario Teklic
 */

import java.util.Arrays;
import java.util.Optional;

public enum RouteFlag {
    MESSAGE(1),
    ACKNOWLEDGE(2),
    REQUEST(3),
    REPLY(4),
    ERROR(5),
    UNREACHABLE(6),
    NO_ROUTEX(-1);

    public final int flag;

    private RouteFlag(int flag){
        this.flag = flag;
    }

    public static Optional<RouteFlag> valueOf(int value){
        return Arrays.stream(values())
                .filter(x -> x.flag == value)
                .findFirst();
    }
}
