package de.teklic.mario.io.input;
/*
 *
 * @author Mario Teklic
 */

public interface Filterable {
    String SHOULD_NOT = "should_not";
    String filter(String msg);
}
