package planner;

import java.util.*;

/**
 * <p>
 * An immutable class representing a venue in the municipality.
 * </p>
 * 
 * <p>
 * A venue has a name and a capacity. The capacity of the venue is an integer
 * greater than zero that represents the maximum number of people who can attend
 * the venue at the same time for an event. A venue can only host events with a
 * size less than or equal to its capacity.
 * </p>
 * 
 * <p>
 * Hosting an event at a venue generates traffic on some traffic corridors in
 * the municipality.
 * </p>
 * 
 * <p>
 * The amount of traffic that is generated on each corridor is - except for
 * integer truncation - linearly proportional to the size of the event: if an
 * event of maximum size C for the venue would generate traffic X on a corridor,
 * then an event of size K would generate (K*X)/C traffic on that corridor.
 * (Note that the integer division truncates the decimal places of the
 * division.)
 * </p>
 */
public class Venue {
	
	// Line separator for executing machine, for toString method
    private final static String LINE_SEPARATOR = System.getProperty(
            "line.separator");
    // Name of this Venue
	private String name;
	// Capacity of this Venue
	private int capacity;
	// Record of traffic generated by an event at capacity at this Venue
	private Traffic genTraffic;

    /*
     * invariant:
     * 
     * name != null && Venue generated Traffic != null &&
     * capacity of venue > 0 &&
     * all traffic generated at capacity <= Venue capacity
     */

    /**
     * Creates a new venue with the given name, and capacity, that generates the
     * traffic described by parameter capacityTraffic for an event of size
     * capacity at the venue.
     * 
     * @param name
     *            the name of the venue
     * @param capacity
     *            the capacity of the venue
     * @param capacityTraffic
     *            the traffic generated by hosting an event of size capacity at
     *            the venue
     * 
     * @throws NullPointerException
     *             if either name or capacityTraffic are null
     * @throws IllegalArgumentException
     *             if capacity is less than or equal to zero.
     * @throws InvalidTrafficException
     *             if the traffic on any corridor described by capacityTraffic
     *             is greater than the capacity of the venue (i.e. you can't
     *             generate more traffic for a corridor than you have people at
     *             the venue.)
     */
    public Venue(String name, int capacity, Traffic capacityTraffic) {
    	if (name == null) {
        	throw new NullPointerException("Name must not be null.");
        }
        if (capacityTraffic == null) {
        	throw new NullPointerException("capacityTraffic must not be null.");
        }
        if (capacity <= 0) {
        	throw new IllegalArgumentException(
        			"Capacity must be greater than zero.");
        }
        for (Corridor c : capacityTraffic.getCorridorsWithTraffic()) {
        	if (capacityTraffic.getTraffic(c) > capacity) {
        		throw new InvalidTrafficException("Corridor traffic cannot "
        				+ "exceed capacity of venue.");
        	}
        }
        // This Venue's name, max capacity, and generated traffic at capacity
        this.name = name;
        this.capacity = capacity;
        this.genTraffic = new Traffic(capacityTraffic);
    }

    /**
     * Returns the name of the venue.
     * 
     * @return the name of the venue
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the capacity of the venue.
     * 
     * @return the capacity of the venue
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Returns true if the size of the event is less than or equal to the
     * capacity of the venue, and false otherwise.
     * 
     * @param event
     *            the event whose compatibility with this venue will be checked
     * @return true iff the capacity of the venue is greater than or equal to
     *         the size of the event.
     * @throws NullPointerException
     *             if event is null
     */
    public boolean canHost(Event event) {
    	if (event == null) {
    		throw new NullPointerException("Event cannot be null.");
    	}
        return event.getSize() <= getCapacity();
    }

    /**
     * <p>
     * Returns the amount of traffic that would be generated by hosting the
     * given event at this venue.
     * </p>
     * 
     * <p>
     * For each corridor c, the traffic generated by the event on that corridor
     * is defined to be the integer ((K * X) / C), where K is the size of the
     * event, C is the capacity of this venue and X is the traffic generated by
     * an event of size C at this venue on corridor c.
     * </p>
     * 
     * <p>
     * (This definition means that the amount of traffic that is generated on
     * each corridor is - except for the integer truncation - linearly
     * proportional to the size of the event.)
     * </p>
     * 
     * @param event
     *            the event for which the traffic will be generated
     * @return the traffic generated by hosting the given event at this venue
     * @throws NullPointerException
     *             if event is null
     * @throws IllegalArgumentException
     *             if the size of the event exceeds the capacity of the venue
     */
    public Traffic getTraffic(Event event) {
    	if (event == null) {
    		throw new NullPointerException("Event cannot be null.");
    	}
    	if (event.getSize() > getCapacity()) {
    		throw new IllegalArgumentException("Event size cannot exceed "
    				+ "Venue capacity");
    	}
    	// Record generated event traffic by Corridor
        Traffic eventTraffic = new Traffic();
        for (Corridor c : genTraffic.getCorridorsWithTraffic()) {
        	int result = (event.getSize() * genTraffic.getTraffic(c)) 
                        / getCapacity();
        	eventTraffic.updateTraffic(c, result);
        }
        return eventTraffic;
    }

    /**
     * The string representation of a venue is a string of the form <br>
     * <br>
     * 
     * "NAME (CAPACITY)" + LINE_SEPARATOR + "CAPACITYTRAFFIC"<br>
     * <br>
     * 
     * where NAME is the name of the venue, CAPACITY is the capacity of the
     * venue, LINE_SEPARATOR is the line separator retrieved in a
     * machine-independent way by calling System.getProperty("line.separator"),
     * and CAPACITYTRAFFIC is the toString() representation of the Traffic
     * object describing the traffic generated by hosting an event of size
     * capacity at the venue.
     */
    @Override
    public String toString() {
        return getName() + " (" + getCapacity() + ")" + LINE_SEPARATOR 
        		+ genTraffic.toString();
    }

    /**
     * Returns true if and only if the given object
     * 
     * (i) is an instance of the class Venue
     * 
     * (ii) with a name that is equal to this venue's name (according to the
     * equals method of the String class),
     * 
     * (iii) a capacity that is equal to this venue's capacity and
     * 
     * (iv) generates the same traffic as this venue for an event of maximum
     * size (capacity) at the venue. (The amount of traffic generated is the
     * same if it is the same according to the sameTraffic() method of the
     * Traffic class).
     * 
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Venue)) {
        	return false;
        }
        Venue v = (Venue) object;
        return this.getName().equals(v.getName())
                && this.getCapacity() == v.getCapacity()
                && this.genTraffic.sameTraffic(v.genTraffic);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        // Determine Hash Code with formula result = prime * result + property
        result = result * PRIME + getName().hashCode();
        result = result * PRIME + getCapacity();
        for (Corridor c : genTraffic.getCorridorsWithTraffic()) {
        	result = result * PRIME + c.hashCode();
        }
        return result;
    }

    /**
     * <p>
     * Determines whether this class is internally consistent (i.e. it satisfies
     * its class invariant).
     * </p>
     * 
     * <p>
     * NOTE: This method is only intended for testing purposes.
     * </p>
     * 
     * @return true if this class is internally consistent, and false otherwise.
     */
    public boolean checkInvariant() {
        return getName() != null && genTraffic != null
                && getCapacity() > 0 && checkTraffic();
    }
    
    /**
     * Checks that all traffic generated at capacity is not greater than
     * Venue capacity.
     * 
     * @return true if all traffic is less than capacity, false otherwise.
     */
    private boolean checkTraffic() {
    	for (Corridor c : genTraffic.getCorridorsWithTraffic()) {
    		if (genTraffic.getTraffic(c) > getCapacity()) {
    			return false;
    		}
    	}
    	return true;
    }

}
