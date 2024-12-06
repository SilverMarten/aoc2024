package aoc._2024;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Range;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Direction;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/6
 * 
 * @author Paul Cormier
 *
 */
public class Day6 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day6.class);

    private static final String INPUT_TXT = "input/Day6.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day6.txt";



    public static void main(String[] args) {

        var resultMessage = "The guard will visit {} distinct positions on the map.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        var testMap = Coordinate.mapCoordinates(testLines);
        // Find the guard in the map
        var testGuard = testMap.entrySet()
                               .stream()
                               .filter(e -> e.getValue() != '#')
                               .findAny()
                               .map(e -> new Guard(e.getKey(), Direction.withSymbol(e.getValue())))
                               .orElseThrow(() -> new IllegalStateException("Cannot find the guard."));
        testMap.remove(testGuard.getPosition());
        var testRows = testLines.size();
        var testColumns = testLines.getFirst().length();

        var expectedTestResult = 41;
        var testResult = part1(testMap, testGuard, testRows, testColumns);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var map = Coordinate.mapCoordinates(lines);
        // Find the guard in the map
        var guard = map.entrySet()
                       .stream()
                       .filter(e -> e.getValue() != '#')
                       .findAny()
                       .map(e -> new Guard(e.getKey(), Direction.withSymbol(e.getValue())))
                       .orElseThrow(() -> new IllegalStateException("Cannot find the guard."));
        map.remove(guard.getPosition());
        var rows = lines.size();
        var columns = lines.getFirst().length();

        log.info(resultMessage, part1(map, guard, rows, columns));

        // PART 2
        resultMessage = "There are {} different positions you could choose";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 6;
        testResult = part2(testMap, testGuard, testRows, testColumns);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(map, guard, rows, columns));
    }



    /**
     * Predict the path of the guard. How many distinct positions will the guard
     * visit before leaving the mapped area?
     * 
     * @param map The map of characters read from the input.
     * @param guard The guard, found at their starting position.
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @return The value calculated for part 1.
     */
    private static long part1(final Map<Coordinate, Character> map, final Guard guard, int rows, int columns) {

        var movingGuard = new Guard(guard.getPosition(), guard.getDirection());
        var visited = new HashSet<Coordinate>();

        var rowRange = Range.of(1, rows);
        var columnRange = Range.of(1, columns);

        while (rowRange.contains(movingGuard.getPosition().getRow()) && columnRange.contains(movingGuard.getPosition().getColumn())) {
            visited.add(movingGuard.getPosition());

            var direction = movingGuard.getDirection();
            var newPosition = movingGuard.getPosition().translate(direction, 1);
            if (map.containsKey(newPosition)) {
                movingGuard.setDirection(direction.rotateRight(90));
            } else {
                movingGuard.setPosition(newPosition);
            }

        }
        log.atDebug()
           .setMessage("\n{}")
           .addArgument(Coordinate.printMap(rows, columns, map.keySet(), '#', visited, 'X'))
           .log();

        return visited.size();
    }



    /**
     * You need to get the guard stuck in a loop by adding a single new
     * obstruction. How many different positions could you choose for this
     * obstruction?
     * 
     * @param map The map of characters read from the input.
     * @param guard The guard, found at their starting position.
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @return The value calculated for part 2.
     */
    private static long part2(final Map<Coordinate, Character> map, final Guard guard, int rows, int columns) {

        var movingGuard = new Guard(guard.getPosition(), guard.getDirection());
        var visited = new HashMap<Coordinate, Set<Direction>>();

        var rowRange = Range.of(1, rows);
        var columnRange = Range.of(1, columns);

        // Find the unobstructed path
        while (rowRange.contains(movingGuard.getPosition().getRow()) && columnRange.contains(movingGuard.getPosition().getColumn())) {
            var direction = movingGuard.getDirection();
            visited.computeIfAbsent(movingGuard.getPosition(), p -> new HashSet<Direction>()).add(direction);

            var newPosition = movingGuard.getPosition().translate(direction, 1);
            if (map.containsKey(newPosition)) {
                movingGuard.setDirection(direction.rotateRight(90));
            } else {
                movingGuard.setPosition(newPosition);
            }

        }
        log.atDebug()
           .setMessage("\n{}")
           .addArgument(Coordinate.printMap(rows, columns, map.keySet(), '#', visited.keySet(), 'X'))
           .log();

        // Check everywhere, except for the start, to see if adding an obstruction would create a loop
        var loopPositions = new HashSet<Coordinate>();
        visited.keySet()
               .stream()
               .filter(l -> !l.equals(guard.getPosition()))
               .forEach(obstruction -> {
                   var movingGuard2 = new Guard(guard.getPosition(), guard.getDirection());
                   var visited2 = new HashMap<Coordinate, Set<Direction>>();

                   while (rowRange.contains(movingGuard2.getPosition().getRow()) &&
                          columnRange.contains(movingGuard2.getPosition().getColumn())) {
                       var direction = movingGuard2.getDirection();
                       if (!visited2.computeIfAbsent(movingGuard2.getPosition(), p -> new HashSet<Direction>()).add(direction)) {
                           loopPositions.add(obstruction);

                           break;
                       }

                       var newPosition = movingGuard2.getPosition().translate(direction, 1);
                       if (map.containsKey(newPosition) || obstruction.equals(newPosition)) {
                           movingGuard2.setDirection(direction.rotateRight(90));
                       } else {
                           movingGuard2.setPosition(newPosition);
                       }

                   }

               });

        log.debug("Loops occur with obstructions at: {}", loopPositions);

        return loopPositions.size();
    }



    /**
     * A mutable representation of the position and direction of the guard.
     */
    private static class Guard {

        private Coordinate position;

        private Direction direction;



        Guard(Coordinate position, Direction direction) {
            this.setPosition(position);
            this.setDirection(direction);
        }



        public Coordinate getPosition() {
            return position;
        }



        public void setPosition(Coordinate position) {
            this.position = position;
        }



        public Direction getDirection() {
            return direction;
        }



        public void setDirection(Direction direction) {
            this.direction = direction;
        }
    }
}