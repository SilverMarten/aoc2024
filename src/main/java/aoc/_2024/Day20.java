package aoc._2024;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Direction;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/20
 * 
 * @author Paul Cormier
 *
 */
public class Day20 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day20.class);

    private static final String INPUT_TXT = "input/Day20.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day20.txt";



    public static void main(String[] args) {

        var resultMessage = "{} cheats would save you at least {} picoseconds.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        var testMap = Coordinate.mapCoordinates(testLines, '#');
        var testRows = testLines.size();
        var testColumns = testLines.getFirst().length();
        var testThreshold = 2;

        var expectedTestResult = 44;
        var testResult = part1(testMap, testRows, testColumns, testThreshold);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult, testThreshold);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var map = Coordinate.mapCoordinates(lines, '#');
        var rows = lines.size();
        var columns = lines.getFirst().length();
        var threshold = 100;

        log.info(resultMessage, part1(map, rows, columns, threshold), threshold);

        // PART 2
        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 1_234_567_890;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * You aren't sure what the conditions of the racetrack will be like, so to
     * give yourself as many options as possible, you'll need a list of the best
     * cheats. How many cheats would save you at least {@code threshold}
     * picoseconds?
     * 
     * @param map The map read from the input.
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param threshold The minimum number of units saved to report on.
     * @return The value calculated for part 1.
     */
    private static long part1(final Map<Coordinate, Character> map, int rows, int columns, int threshold) {

        Map<Coordinate, Location> locations = new HashMap<>();

        // Start from the end and record the distance from the end that each location is
        var endPosition = map.entrySet().stream().filter(e -> e.getValue() == 'E').map(Entry::getKey).findAny().orElseThrow();

        var location = new Location(endPosition, null, 0);
        var end = location;
        do {
            locations.put(location.position(), location);
            var previousPosition = Optional.ofNullable(location.next()).map(Location::position).orElse(null);
            var nextPosition = location.position()
                                       .findOrthogonalAdjacent()
                                       .stream()
                                       .filter(c -> !c.equals(previousPosition) && map.containsKey(c))
                                       .findAny()
                                       .orElseThrow();
            location = new Location(nextPosition, location, location.distanceFromEnd() + 1);
        } while (map.get(location.position()) != 'S');
        var start = location;

        log.atDebug()
           .setMessage("Path to the end: {}")
           .addArgument(() -> {
               var path = new ArrayList<Coordinate>();
               var pathLocation = start;
               do {
                   path.add(pathLocation.position());
                   pathLocation = pathLocation.next;
               } while (pathLocation != end);
               return path;
           })
           .log();

        // Go along the path and see if skipping by two in a direction would result
        // in a savings above the threshold
        Collection<Integer> cheatSavings = new ArrayList<>();
        while (location.next() != null) {
            var position = location.position();
            var distanceFromEnd = location.distanceFromEnd();
            var twoAway = location.next().next();
            Direction.ORTHOGONAL_DIRECTIONS.stream()
                                           .map(d -> position.translate(d, 2))
                                           .filter(locations::containsKey)
                                           .map(locations::get)
                                           .filter(l -> !l.equals(twoAway))
                                           .mapToInt(l -> distanceFromEnd - l.distanceFromEnd() - 2)
                                           .filter(s -> s >= threshold)
                                           .forEach(cheatSavings::add);
            location = location.next();
        }

        log.atDebug()
           .setMessage("The total number of cheats (grouped by the amount of time they save) are as follows:\n{}")
           .addArgument(() -> cheatSavings.stream()
                                          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                          .entrySet()
                                          .stream()
                                          .sorted(Comparator.comparing(Entry::getKey))
                                          .map(e -> String.format(" - There are %d cheats that save %d picoseconds.",
                                                                  e.getValue(), e.getKey()))
                                          .collect(Collectors.joining("\n")))
           .log();

        return cheatSavings.size();
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



    private record Location(Coordinate position, Location next, int distanceFromEnd) {
    }

}