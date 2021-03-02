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

public class RoutingTable {

    public final static Logger logger = Logger.getLogger(RoutingTable.class.getName());

    private static RoutingTable routingTable;
    private List<Route> routeList;
    public static final String FILE = "routes.json";
    public static final String NO_NEXT = "no_next";

    private RoutingTable() {
        if (this.routeList == null) {
            this.routeList = Collections.synchronizedList(new ArrayList<>());
        }
    }

    public void add(Route r) {
        if(!hasRoute(r)){
            this.routeList.add(r);
            this.store();
        }
    }

    public void add(RouteX routeX){
        if(routeX instanceof RouteX.Message){
            Route route = new Route(Address.getInstance().getAddr(), routeX.getSource(), routeX.getTokenizedHeader().getOrigin(), (INITIAL_TTL - routeX.getTimeToLive()));
            add(route);
        }
    }

    public void drop(){
        routeList = Collections.synchronizedList(new ArrayList<>());
        store();
    }

    public String getNextForDestination(String destAddr){
        logger.info("Routes from routing table (size " + routeList.size() + "):");

        Optional<Route> routeOptional = routeList.stream()
                .filter(r -> r.getDestination().equals(destAddr)).collect(Collectors.toList())
                .stream().min(Comparator.comparing(Route::getHops));

        Route route;
        if(routeOptional.isPresent()){
            route = routeOptional.get();
            logger.info("Next route for destination with address '" + destAddr + "': " + route);
            return route.getNeighbour();
        }else{
            logger.info("No next route found in routingtable.");
            return NO_NEXT;
        }
    }

    /**
     * Must run restore in first contact with this object.
     *
     * @return
     */
    public static RoutingTable getInstance() {
        if (routingTable == null) {
            routingTable = new RoutingTable();
        }
        return routingTable;
    }

    public List<Route> getRouteList() {
        return this.routeList;
    }

    public void store() {
        try (Writer writer = new FileWriter(FILE)) {
            new Gson().toJson(routeList, writer);
        } catch (IOException e) {
            logger.info("Failed to Store Routelist.");
            e.printStackTrace();
        }
    }

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
     * @param destination
     */
    public void removeRoute(String destination){
        routeList.removeIf(r -> r.getDestination().equalsIgnoreCase(destination));
    }

    public boolean hasRoute(String destination){
        return routeList
                .stream()
                .anyMatch(r -> r.getDestination().equalsIgnoreCase(destination));
    }

    public boolean hasRoute(Route route) {
        return hasRoute(route.getDestination());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Route implements Serializable {
        private String owner;
        private String destination;
        private String neighbour;
        private int hops;

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
