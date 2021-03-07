package de.teklic.mario.io.input;
/*
 *
 * @author Mario Teklic
 */

public interface Filterable {
    /**
     * If this string is redirected by any filter, the message wont be forwarded to the JLora class
     */
    String SHOULD_NOT = "should_not";

    /**
     * A filter will be prepend before the new message will be forwarded to the JLora class
     * @param msg Any string
     * @return The same string if no action has be made,
     * SHOULD_NOT final String if the String should not be forwarded because of any condition from the filter
     */
    String filter(String msg);
}
