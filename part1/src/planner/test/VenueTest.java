package planner.test;

import planner.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

/**
 * Basic tests for the {@link Venue} implementation class.
 * 
 * A more extensive test suite will be performed for assessment of your code,
 * but this should get you started writing your own unit tests.
 */
public class VenueTest {

    // Correct line separator for executing machine (used in toString method)
    private final static String LINE_SEPARATOR = System.getProperty(
            "line.separator");
    // locations to test with
    private Location[] locations;
    // corridors to test with
    private Corridor[] corridors;
    // traffic objects to test with;
    private Traffic[] trafficRecords;
    // event objects to test with
    private Event[] events;

    /**
     * This method is run by JUnit before each test to initialise instance
     * variables locations and corridors.
     */
    @Before
    public void setUp() {
        // locations to test with
        locations = new Location[6];
        locations[0] = new Location("l0");
        locations[1] = new Location("l1");
        locations[2] = new Location("l2");

        // corridors to test with
        corridors = new Corridor[4];
        corridors[0] = new Corridor(locations[0], locations[1], 100);
        corridors[1] = new Corridor(locations[1], locations[2], 200);
        corridors[2] = new Corridor(locations[2], locations[0], 200);

        // events to test with
        events = new Event[3];
        events[0] = new Event("Adele", 200);
        events[1] = new Event("Bieber", 100);
        events[2] = new Event("Foo Fighters", 50);

        // traffic records to test with
        trafficRecords = new Traffic[6];

        trafficRecords[0] = new Traffic();
        trafficRecords[0].updateTraffic(corridors[0], 100);
        trafficRecords[0].updateTraffic(corridors[1], 200);

        trafficRecords[1] = new Traffic();
        trafficRecords[1].updateTraffic(corridors[0], 50);
        trafficRecords[1].updateTraffic(corridors[1], 100);

        trafficRecords[2] = new Traffic();
        trafficRecords[2].updateTraffic(corridors[0], 25);
        trafficRecords[2].updateTraffic(corridors[1], 50);
        
        // Test Sameness
        trafficRecords[3] = new Traffic();
        trafficRecords[3].updateTraffic(corridors[0], 0);
        
        trafficRecords[4] = new Traffic();
        trafficRecords[4].updateTraffic(corridors[1], 0);
        
        trafficRecords[5] = new Traffic();
        trafficRecords[5].updateTraffic(corridors[2], 100);;
        
    }

    /**
     * Test of the construction of a typical venue.
     */
    @Test
    public void testTypicalVenue() {
        // venue parameters
        String name = "Suncorp Stadium";
        int capacity = 100;
        Traffic capacityTraffic = trafficRecords[1];

        // the venue under test
        Venue venue = new Venue(name, capacity, capacityTraffic);

        // check the name of the venue
        Assert.assertEquals(name, venue.getName());
        // check the capacity of the venue
        Assert.assertEquals(capacity, venue.getCapacity());
        // check which events the venue can host
        Assert.assertFalse(venue.canHost(events[0]));
        Assert.assertTrue(venue.canHost(events[1]));
        Assert.assertTrue(venue.canHost(events[2]));

        // check the traffic generated by an event of maximum size
        Traffic expectedTraffic = capacityTraffic;
        Traffic actualTraffic = venue.getTraffic(events[1]);
        Assert.assertTrue(expectedTraffic.sameTraffic(actualTraffic));

        // check the traffic generated by an event of half the maximum size
        expectedTraffic = trafficRecords[2];
        actualTraffic = venue.getTraffic(events[2]);
        Assert.assertTrue(expectedTraffic.sameTraffic(actualTraffic));

        // check the string representation
        String expectedString = "Suncorp Stadium (100)" + LINE_SEPARATOR
                + "Corridor l0 to l1 (100): 50" + LINE_SEPARATOR
                + "Corridor l1 to l2 (200): 100" + LINE_SEPARATOR;
        String actualString = venue.toString();
        Assert.assertEquals(expectedString, actualString);

        // check that the invariant holds
        Assert.assertTrue(venue.checkInvariant());
    }

    /**
     * Test that the appropriate exception is thrown if a venue is created with
     * a capacity traffic that is too big.
     */
    @Test(expected = InvalidTrafficException.class)
    public void testInvalidTrafficParameter() {
        // venue parameters
        String name = "Suncorp Stadium";
        int capacity = 100;
        Traffic capacityTraffic = trafficRecords[0];

        // the venue under test
        Venue venue = new Venue(name, capacity, capacityTraffic);
    }

    /** Basic check of the equals method */
    @Test
    public void testEquals() {
        Venue[] venues = new Venue[5];
        venues[0] = new Venue(new String("Suncorp Stadium"), 100,
                trafficRecords[1]);
        venues[1] = new Venue(new String("Suncorp Stadium"), 100, new Traffic(
                trafficRecords[1]));
        venues[2] = new Venue(new String("The Gabba"), 200, trafficRecords[0]);

        // test equal venues
        Assert.assertTrue(venues[0].equals(venues[1]));
        // equal venues should have equal hash-codes
        Assert.assertEquals(venues[0].hashCode(), venues[1].hashCode());

        // test unequal venues
        Assert.assertFalse(venues[0].equals(venues[2]));
    }
    
    @Test
    public void testZeroCorridors() {
    	Venue[] venues = new Venue[3];
    	venues[0] = new Venue(new String("Suncorp Stadium"), 100, 
    			trafficRecords[3]);
    	venues[1] = new Venue(new String("Suncorp Stadium"), 100, 
    			trafficRecords[4]);
    	venues[2] = new Venue(new String("Suncorp Stadium"), 100, 
    			trafficRecords[5]);
    	
    	// test equals
    	Assert.assertTrue(venues[0].equals(venues[1]));
    	Assert.assertTrue(venues[1].equals(venues[1]));
    	Assert.assertFalse(venues[0].equals(venues[2]));
    	
    	// test hashcode
    	Assert.assertEquals(venues[0].hashCode(), venues[1].hashCode());
    	Assert.assertNotEquals(venues[0].hashCode(), venues[2].hashCode());
    }
}
