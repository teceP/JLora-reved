package de.teklic.mario.filters;
/*
 *
 * @author Mario Teklic
 */

import de.teklic.mario.model.routex.RouteX;

/**
 * Needs to be implemented by a applied Filter
 */
public interface Filterable {
    /**
     * A filter will be prepend before the new message will be forwarded to the JLora class.
     * The filter can modify the object or return another RouteX object.
     *
     * @param msg Any RouteX
     * @return Modified or new RouteX
     */
    RouteX filter(RouteX msg);
}
