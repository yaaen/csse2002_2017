package planner;

import java.io.*;
import java.util.*;

/**
 * Provides a method to read in a list of venues from a text file.
 */
public class VenueReader {
    /**
     * <p>
     * Reads a text file called fileName that describes the venues in a
     * municipality, and returns a list containing each of the venues read from
     * the file, in the order that they appear in the file.
     * </p>
     * 
     * <p>
     * The file contains zero or more descriptions of different venues. (I.e. a
     * file containing zero venues contains zero lines; a file containing one
     * venue contains exactly one description of a venue and no other lines or
     * information; a file containing multiple venues contains each description
     * of a venue, one after the other with no other information or lines in the
     * file.)
     * </p>
     * 
     * <p>
     * A description of a venue consists of exactly (i) one line consisting of
     * the name of the venue followed by (ii) one line containing a positive
     * integer denoting the capacity of the venue followed by (iii) a
     * description of the traffic generated by hosting an event of maximum size
     * at the venue, followed by (iv) an empty line.
     * </p>
     * 
     * <p>
     * For (i) the venue name is the entire string on the first line of the
     * venue description (i.e. it may contain white space characters etc.). The
     * only constraint on the venue name is that it may not be equal to the
     * empty string ("").
     * </p>
     * 
     * <p>
     * For (ii) the second line of a venue description may not contain leading
     * or trailing whitespace characters, it may only contain a positive integer
     * denoting the venue capacity.
     * </p>
     * 
     * <p>
     * For (iii) the traffic is described by one line for each corridor that
     * will have traffic from the venue when it hosts an event of maximum size.
     * Each line is a string of the form <br>
     * <br>
     * "START, END, CAPACITY: TRAFFIC"<br>
     * <br>
     * where START and END are different non-empty strings denoting the name of
     * the start location of the corridor and the end location of the corridor,
     * respectively; CAPACITY is a positive integer denoting the capacity of the
     * corridor; and TRAFFIC is a positive integer denoting the amount of
     * traffic from the venue that will use the corridor when the venue hosts
     * the largest event that it can. The strings denoting the start and end
     * locations of the corridor may contain any characters other than a comma
     * (',') or semicolon (':'). Both CAPACITY and TRAFFIC should be positive
     * integers with no additional leading or trailing whitespace. For example,
     * <br>
     * <br>
     * "St. Lucia, Royal Queensland Show - EKKA, 120: 60"<br>
     * <br>
     * represents a traffic corridor from "St. Lucia" to "Royal Queensland Show
     * - EKKA" with a maximum capacity of 120, that will have 60 people from the
     * venue using it when the venue hosts an event of maximum size. <br>
     * <br>
     * Note that the start, end and capacity of a corridor are separated by the
     * string ", ". The corridor and its traffic are separated by ": ". <br>
     * <br>
     * The corridors and their respective traffic may appear in any order (i.e.
     * the corridors aren't necessarily sorted in any way.) Each corridor may
     * only appear once in the traffic description for a venue (i.e. there is
     * only one line for each corridor), and the traffic on that corridor should
     * be less than or equal to the capacity of the venue, and less than or
     * equal to the capacity of the corridor.
     * </p>
     * 
     * <p>
     * For (iv) an empty line is a line with no characters at all (i.e. the
     * contents of the line is the empty string "").
     * </p>
     * 
     * <p>
     * Two equivalent venues shouldn't appear twice in the file.
     * </p>
     * 
     * <p>
     * If a FormatException is thrown, it will have a meaningful message that
     * accurately describes the problem with the input file format, including
     * the line of the file where the problem was detected.
     * </p>
     * 
     * @param fileName
     *            the name of the file to read from.
     * @return a list of the venues from the file, in the order in which they
     *         appear in the file.
     * @throws IOException
     *             if there is an error reading from the input file.
     * @throws FormatException
     *             if there is an error with the input format (e.g. there is
     *             more than one venue description in the file that describes
     *             the same venue, or the file format is not as specified above
     *             in any other way.) The FormatExceptions thrown should have a
     *             meaningful message that accurately describes the problem with
     *             the input file format, including the line of the file where
     *             the problem was detected.
     */
    public static List<Venue> read(String fileName) throws IOException,
            FormatException {
        // Arraylist to store Venues
        List<Venue> venues = new ArrayList<>();
        // Name of venue
        String venueName = "";
        // Venue's max capacity
        int venueCapacity = 0;
        // List of Corridors formed from file read
        List<Corridor> corridors = new ArrayList<>();
        // List of integers corresponding to parsed traffic values
        List<Integer> traffic = new ArrayList<>();

        // Running line counter
        int lineCount = 0;
        // Venue information line count
        int venueLineCount = 0;
        /* Flag for end of venue information, true when at empty line after
        venue information indicating venue information has ended. */
        boolean venueEnd = false;

        // Read File
        try (Scanner scan = new Scanner(new FileReader(fileName))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                lineCount++;
                /* If this line is empty and we are not at start of new venue
                information, create Venue and move on to next venue(s). */
                if ( (venueLineCount > 1) && line.isEmpty() ) {
                    venueEnd = true;
                    // Create Venue Object
                    Venue venue = createVenue(venueName, venueCapacity,
                            corridors, traffic);
                    if (venues.contains(venue)) {
                        throw new FormatException("Error on line " + lineCount
                                + ": duplicate venue detected");
                    }
                    venues.add(venue);

                    // Clean up for next venue
                    venueLineCount = 0;
                    corridors.clear();
                    traffic.clear();
                }
                else {
                    venueLineCount++;
                    // Line is the name of venue
                    if (venueLineCount == 1) {
                        venueEnd = false;
                        if (line.isEmpty()) {
                            throw new FormatException("Error on line " +
                                    lineCount + ": Venue name cannot be \"\"");
                        }
                        venueName = line;
                    }
                    // Line is the venue's capacity
                    else if (venueLineCount == 2) {
                        try {
                            venueCapacity = Integer.parseInt(line);
                        } catch (NumberFormatException e) {
                            throw new FormatException("Error on line " +
                                    lineCount + ": Venue capacity invalid. " +
                                    "Must be an integer with no " +
                                    "leading/trailing whitespace.");
                        }
                        if (venueCapacity < 0) {
                            throw new FormatException("Error on line " +
                                    lineCount + ": Venue capacity must be " +
                                    "positive integer.");
                        }
                    }
                    // Lines record Corridor and traffic information
                    else if (line.contains(",")) {
                        /* Split string by comma or colon with no more than 1
                        white space as per formatting */
                        String[] lineSplit = line.split("([,:])\\s?");
                        // Check that corridor information is correct
                        checkCorridor(lineSplit, venueCapacity, lineCount);

                        // Add corridors and traffic value
                        Corridor c = createCorridor(lineSplit[0], lineSplit[1],
                                Integer.parseInt(lineSplit[2]));
                        if (corridors.contains(c)) {
                            throw new FormatException("Error on line " +
                                    lineCount + ": same corridor appears " +
                                    "more than once in traffic.");
                        }
                        corridors.add(c);
                        traffic.add(Integer.parseInt(lineSplit[3]));
                    }
                    /* Else venue is not terminating with empty line or
                    erroneous information after corridors */
                    else {
                        throw new FormatException("Error on line " + lineCount +
                                ": Unexpected information encountered OR " +
                                "venue not terminating with empty line.");
                    }
                }
            }
            // Last venue must have empty line to indicate end of venue
            if (!venueEnd && !venues.isEmpty()) {
                throw new FormatException("Error on line " + lineCount +
                        ": Empty line expected to complete venue.");
            }
        }
        return venues;
    }

    /**
     * <p>Creates a new Venue object, taking in data pre-constructed from
     * VenueReader.</p>
     *
     * <p>This method will run if and only if all Venue information for one
     * particular venue as provided for VenueReader has been read and
     * parsed.</p>
     *
     * @param venueName
     *          the String formatted name of this Venue
     * @param venueCapacity
     *          the positive integer representing Venue Capacity
     * @param corridors
     *          a list of Corridor objects associated with this Venue
     * @param traffic
     *          a list of the amount of traffic generated through events at
     *          this Venue.
     * @ensure  venueName != "" && venueCapacity >= 0 &&
     *          each traffic <= corridor.getCapacity()
     * @return  New Venue Object
     */
    private static Venue createVenue(String venueName, int venueCapacity,
                                     List<Corridor> corridors,
                                     List<Integer> traffic) {
        Traffic venueTraffic = new Traffic();
        /* Build Traffic object with corridors and traffic values for Venue
        for allocation to new venue object. */
        for (int i = 0; i < corridors.size(); i++) {
            venueTraffic.updateTraffic(corridors.get(i), traffic.get(i));
        }
        return new Venue(venueName, venueCapacity, venueTraffic);
    }
    /**
     * <p>Creates a Corridor object with a start and end location, and maximum
     * capacity.</p>
     *
     * @param a
     *          a string representing location start name
     * @param b
     *          a string representing location end name
     * @param capacity
     *          positive integer representing corridor max capacity
     * @ensure  capacity >= 0 && a != null && b != null
     * @return  New Corridor object
     */
    private static Corridor createCorridor(String a, String b, int capacity) {
        Location start = new Location(a);
        Location end = new Location(b);
        return new Corridor(start, end, capacity);
    }

    /**
     * <p>Checks corridor information line for correct information, otherwise
     * throws an explicit FormatException to indicate cause of error.</p>
     *
     * @param line
     *          A String Array containing corridor and traffic information
     * @param venueCapacity
     *          Capacity of the venue associated with corridors and traffic
     * @param lineCount
     *          The current line number of corridor information being checked
     * @ensure  line is non-empty && line != null && venueCapacity >= 0
     *
     */
    private static void checkCorridor(String[] line, int venueCapacity,
                                         int lineCount) throws FormatException {
        String corridorStart = line[0];
        String corridorEnd = line[1];
        int corridorCapacity;
        int generatedTraffic;

        // Check if missing traffic value
        if (3 >= line.length) {
            throw new FormatException("Error on line " + lineCount +
                    ": traffic value is missing.");
        }

        // Ensure corridor capacity and traffic generated can be parsed
        try {
            corridorCapacity = Integer.parseInt(line[2]);
            generatedTraffic = Integer.parseInt(line[3]);
        } catch (NumberFormatException e) {
            throw new FormatException("Error on line " + lineCount +
                    ": Capacity and Traffic values must be integers without " +
                    "additional leading or trailing whitespace.");
        }

        // Check Corridor start and end names
        if (corridorStart.isEmpty()) {
            throw new FormatException("Error on line " + lineCount +
                    ": Corridor Start location must not be \"\"");
        }
        if (corridorEnd.isEmpty()) {
            throw new FormatException("Error on line " + lineCount +
                    ": Corridor End location must not be \"\"");
        }

        // Check that (traffic <= venueCapacity && traffic <= corridorCapacity)
        if (generatedTraffic > venueCapacity) {
            throw new FormatException("Error on line " + lineCount +
                    ": Generated traffic must not exceed Venue's capacity.");
        }
        if (generatedTraffic > corridorCapacity) {
            throw new FormatException("Error on line " + lineCount +
                    ": Generated traffic must not exceed Corridor capacity.");
        }

        // Check values are positive
        if (generatedTraffic < 0) {
            throw new FormatException("Error on line " + lineCount +
                    ": Generated traffic must be a positive integer.");
        }
        if (corridorCapacity < 0) {
            throw new FormatException("Error on line " + lineCount +
                    ": Corridor capacity must be a positive integer.");
        }
    }
}