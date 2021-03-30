package de.teklic.mario.routingtable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.teklic.mario.core.Address;
import de.teklic.mario.model.routex.RouteX;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.teklic.mario.core.Constant.INITIAL_TTL;

/**
 * RoutingTable-Singelton
 */
public class RoutingTable {

    public final static Logger logger = Logger.getLogger(RoutingTable.class.getName());

    /**
     * Singleton instance
     */
    private static RoutingTable routingTable;

    /**
     * RouteList
     */
    private List<Route> routeList;

    /**
     * Storing file for the RoutingList
     */
    public static final String FILE = "routes.json";

    /**
     * Defines the return value, if there was no next (neighbour) was found, or no route for a specific route.
     */
    public static final String NO_NEXT = "no_next";

    private RoutingTable() {
        if (this.routeList == null) {
            this.routeList = Collections.synchronizedList(new ArrayList<>());
        }
    }

    /**
     * Adds a route to the table if it not exist already.
     * @param r Route
     */
    public void add(Route r) {
        if(!hasRoute(r)){
            this.routeList.add(r);
            this.store();
        }
    }

    /**
     * Adds a new Rout, based on a RouteX object.
     * Takes the routeX's source and routeX's and tokenizedHeader's origin as values.
     * @param routeX RouteX
     */
    public void add(RouteX routeX){
        if(routeX instanceof RouteX.Message){
            Route route = new Route(Address.getInstance().getAddr(), routeX.getSource(), routeX.getTokenizedHeader().getOrigin(), (INITIAL_TTL - routeX.getTimeToLive()));
            add(route);
        }
    }

    /**
     * Drops the whole list as creates a new List.
     * Stores the empty list immediately and overrides old records.
     */
    public void drop(){
        routeList = Collections.synchronizedList(new ArrayList<>());
        store();
    }

    /**
     * Checks if there is a route for a given destination address.
     * @param destAddr Destination address, which will be searched for a route
     * @return Returns the nearest and best routes "neighbour" variable. (Check class Route for more information).
     *         Returns "no_next", if there was no route found for this destination.
     */
    public String getNextForDestination(String destAddr){
        Optional<Route> routeOptional = routeList.stream()
                .filter(r -> r.getDestination().equals(destAddr)).collect(Collectors.toList())
                .stream().min(Comparator.comparing(Route::getHops));

        Route route;
        if(routeOptional.isPresent()){
            route = routeOptional.get();
            //logger.info("Next route for destination with address '" + destAddr + "': " + route);
            return route.getNeighbour();
        }else{
            logger.info("No next route found in routingtable.");
            return NO_NEXT;
        }
    }

    /**
     * Must run restore in first contact with this object.
     *
     * @return RoutingTable instance
     */
    public static RoutingTable getInstance() {
        if (routingTable == null) {
            routingTable = new RoutingTable();
        }
        return routingTable;
    }

    /**
     * Returns the RouteList
     * @return the list of Routes
     */
    public List<Route> getRouteList() {
        return this.routeList;
    }

    /**
     * Persists the RouteList
     */
    public void store() {
        try (Writer writer = new FileWriter(FILE)) {
            new Gson().toJson(routeList, writer);
        } catch (IOException e) {
            logger.info("Failed to Store Routelist.");
            e.printStackTrace();
        }
    }

    /**
     * Restores the RouteList for a specific owner.
     * @param owner The owner represents the nodes address.
     */
    public void restore(String owner) {
        try (JsonReader reader = new JsonReader(new FileReader(FILE))) {
            List<Route> tmp = new Gson().fromJson(reader, new TypeToken<ArrayList<Route>>() {
            }.getType());
            if (tmp != null) {
                if (this.routeList == null) {
                    this.routeList = Collections.synchronizedList(new ArrayList<>());
                } else {
                    this.routeList.clear();
                }
                tmp.stream().forEach(r -> {
                    if(r.getOwner().equalsIgnoreCase(owner)){
                        this.routeList.add(r);
                    }
                });
                logger.info("Restored " + routeList.size() + " routes.");
            }
        } catch (FileNotFoundException fnf) {
            logger.info("Restore file does not exist yet. Returning empty list.");
            this.routeList = Collections.synchronizedList(new ArrayList<>());
        } catch (Exception e){
            logger.info("Some error occured while restoring. List will be empty but can be used.");
            this.routeList = Collections.synchronizedList(new ArrayList<>());
            e.printStackTrace();
        }
    }

    /**
     * Removes all nodes which contains this destination
     * @param destination Removes routes for destination address
     */
    public void removeRoute(String destination){
        routeList.removeIf(r -> r.getDestination().equalsIgnoreCase(destination));
    }

    /**
     * Proofs if there is any route for this destination
     * @param destination Destinations address
     * @return true if any route was found, false if no route was found
     */
    public boolean hasRoute(String destination){
        return routeList
                .stream()
                .anyMatch(r -> r.getDestination().equalsIgnoreCase(destination));
    }

    /**
     * Proofs if there is any route for this destination
     * See hasRoute(String destination) for more information
     * @param route Proofs if a route for the destination of the Routes object address exists
     * @return true if has found any route
     */
    public boolean hasRoute(Route route) {
        return hasRoute(route.getDestination());
    }

    /**
     * Route
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Route implements Serializable {

        /**
         * The owner represents the nodes address, which made contact with this node earlier
         */
        private String owner;

        /**
         * Destinations node address
         */
        private String destination;

        /**
         * Neighbours address: Next nodes, on the way to the destination node
         */
        private String neighbour;

        /**
         * Hops needed for this route
         */
        private int hops;

        /**
         * Checks if two Routes are same
         * @param obj Another route
         * @return true if every variable (owner, destination, hops, neighbour) are same
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != Route.class) {
                logger.info("Object is null or Classnames doesnt match.");
                logger.info("Classname (this): " + this.getClass().getName());
                logger.info("Classname (opposite): " + obj.getClass().getName());
                logger.info("Null (opposite): " + (obj == null));
                return false;
            }

            return this.getOwner().equalsIgnoreCase(((Route) obj).getOwner()) &&
                    this.getDestination().equalsIgnoreCase(((Route) obj).getDestination()) &&
                    this.getHops() == (((Route) obj).getHops()) &&
                    this.getNeighbour().equalsIgnoreCase(((Route) obj).getNeighbour());
        }

        @Override
        public String toString() {
            return "Owner: " + this.getOwner() + ", Destination: " + this.getDestination() + ", Neighbour: " + this.getNeighbour() + ", Hops: " + this.getHops();
        }
    }
}
