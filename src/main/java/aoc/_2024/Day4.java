package aoc._2024;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Direction;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/4
 * 
 * @author Paul Cormier
 *
 */
public class Day4 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day4.class);

    private static final String INPUT_TXT = "input/Day4.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day4.txt";



    public static void main(String[] args) {

        var resultMessage = "XMAS occurs a total of {} times.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        // Map the characters
        final var testMap = Coordinate.mapCoordinates(testLines);
        var rows = testLines.size();
        var columns = testLines.getFirst().length();

        log.atDebug()
           .setMessage("Parsed map:\n{}")
           .addArgument(() -> Coordinate.printMap(rows, columns, testMap))
           .log();

        var expectedTestResult = 18;
        var testResult = part1(testMap);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        // Map the characters

        final var map = Coordinate.mapCoordinates(lines);

        log.info(resultMessage, part1(map));

        // PART 2
        resultMessage = "X-MAS occurs a total of {} times.";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 9;
        testResult = part2(testMap);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(map));
    }



    /**
     * Take a look at the little Elf's word search. How many times does XMAS
     * appear?
     * 
     * @param map The mapped characters from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final Map<Coordinate, Character> map) {

        // For each 'X' check the lines around it for the subsequent letters "MAS" and count matches
        return map.entrySet()
                  .stream()
                  .filter(e -> e.getValue().equals('X'))
                  .mapToLong(e -> findXmas(e.getKey(), map))
                  .sum();

    }



    /**
     * Check each direction to see if the correct letters are present.
     * 
     * @param fromX The {@link Coordinate} from which to start.
     * @param map The map of characters, indexed by coordinate.
     * @return The number of directions in which the correct characters appear.
     */
    private static long findXmas(Coordinate fromX, Map<Coordinate, Character> map) {
        return Stream.of(Direction.values())
                     .filter(d -> map.getOrDefault(fromX.translate(d, 1), ' ').equals('M') &&
                                  map.getOrDefault(fromX.translate(d, 2), ' ').equals('A') &&
                                  map.getOrDefault(fromX.translate(d, 3), ' ').equals('S'))
                     .count();
    }



    /**
     * Flip the word search from the instructions back over to the word search
     * side and try again. How many times does an X-MAS appear?
     * 
     * @param map The mapped characters from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final Map<Coordinate, Character> map) {

        // For each 'A' check the lines around it for the subsequent letters "M" and "S" and count matches
        return map.entrySet()
                  .stream()
                  .filter(e -> e.getValue().equals('A'))
                  .filter(e -> isXMas(e.getKey(), map))
                  .count();
    }



    /**
     * Check each direction to see if the correct letters are present.
     * 
     * @param fromA The {@link Coordinate} from which to start.
     * @param map The map of characters, indexed by coordinate.
     * @return Whether there is an X-MAS at the given coordinate or not.
     */
    private static boolean isXMas(Coordinate fromA, Map<Coordinate, Character> map) {
        return Stream.of(Direction.UP_RIGHT, Direction.RIGHT_DOWN, Direction.DOWN_LEFT, Direction.LEFT_UP)
                     .filter(d -> map.getOrDefault(fromA.translate(d, 1), ' ').equals('M') &&
                                  map.getOrDefault(fromA.translate(d.opposite(), 1), ' ').equals('S'))
                     .count() == 2;
    }

}