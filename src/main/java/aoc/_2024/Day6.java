package aoc._2024;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
        var testRows = testLines.size();
        var testColumns = testLines.getFirst().length();

        var expectedTestResult = 41;
        var testResult = part1(testMap, testRows, testColumns);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var map = Coordinate.mapCoordinates(lines);
        var rows = lines.size();
        var columns = lines.getFirst().length();

        log.info(resultMessage, part1(map, rows, columns));

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
     * Predict the path of the guard. How many distinct positions will the guard
     * visit before leaving the mapped area?
     * 
     * @param map The map of characters read from the input.
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @return The value calculated for part 1.
     */
    private static long part1(final Map<Coordinate, Character> map, int rows, int columns) {

        // Find the guard in the map
        var guard = map.entrySet()
                       .stream()
                       .filter(e -> e.getValue() != '#')
                       .findAny()
                       .map(e -> new Guard(e.getKey(), Direction.withSymbol(e.getValue())))
                       .orElseThrow(() -> new IllegalStateException("Cannot find the guard."));
        map.remove(guard.getPosition());

        var visited = new HashSet<Coordinate>();

        var rowRange = Range.of(1, rows);
        var columnRange = Range.of(1, columns);

        while (rowRange.contains(guard.getPosition().getRow()) && columnRange.contains(guard.getPosition().getColumn())) {
            visited.add(guard.getPosition());

            var direction = guard.getDirection();
            var newPosition = guard.getPosition().translate(direction, 1);
            if (map.containsKey(newPosition)) {
                guard.setDirection(direction.rotateRight(90));
            } else {
                guard.setPosition(newPosition);
            }

        }
        log.atDebug()
           .setMessage("\n{}")
           .addArgument(Coordinate.printMap(rows, columns, map.keySet(), '#', visited, 'X'))
           .log();

        return visited.size();
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



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