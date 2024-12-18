package aoc._2024;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/18
 * 
 * @author Paul Cormier
 *
 */
public class Day18 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day18.class);

    private static final String INPUT_TXT = "input/Day18.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day18.txt";



    public static void main(String[] args) {

        var resultMessage = "The minimum number of steps to reach the end is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        List<Coordinate> testMemory = testLines.stream()
                                               .map(l -> l.split(","))
                                               .map(l -> Coordinate.of(Integer.parseInt(l[1]), Integer.parseInt(l[0])))
                                               .toList();
        var testSize = 6;
        var testLimit = 12;

        var expectedTestResult = 22;
        var testResult = part1(testMemory, testSize, testLimit);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        List<Coordinate> memory = lines.stream()
                                       .map(l -> l.split(","))
                                       .map(l -> Coordinate.of(Integer.parseInt(l[1]), Integer.parseInt(l[0])))
                                       .toList();
        var size = 70;
        var limit = 1024;

        log.info(resultMessage, part1(memory, size, limit));

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
     * Simulate the first kilobyte (1024 bytes) falling onto your memory space.
     * Afterward, what is the minimum number of steps needed to reach the exit?
     * 
     * @param memory The coordinates of the memory read from the input.
     * @param size The size of the space in which you can move.
     * @param limit The number of bytes to place.
     * 
     * @return The shortest path from (0,0) to (size, size).
     */
    private static long part1(List<Coordinate> memory, int size, int limit) {

        var corruptedSpaces = memory.stream().limit(limit).collect(Collectors.toSet());
        log.debug("Corrupted memory: {}", corruptedSpaces);
        log.atDebug()
           .setMessage("Memory after {} bytes:\n{}")
           .addArgument(limit)
           .addArgument(() -> Coordinate.printMap(0, 0, size, size, corruptedSpaces))
           .log();

        var validRange = Range.of(0, size);
        var end = Coordinate.of(size, size);
        Map<Coordinate, Integer> minDistanceToEnd = new HashMap<>();
        minDistanceToEnd.put(end, 0);

        // Start from the end, and compute the minimum distance to reach it
        List<Coordinate> spacesToCheck = new ArrayList<>();
        Set<Coordinate> nextSpacesToCheck = new HashSet<>();
        spacesToCheck.add(end);

        while (!spacesToCheck.isEmpty()) {
            var space = spacesToCheck.removeLast();
            // Compute the min distance from this space
            var distance = space.findOrthogonalAdjacent()
                                .stream()
                                .map(minDistanceToEnd::get)
                                .filter(Objects::nonNull)
                                .mapToInt(Integer::intValue)
                                .min()
                                .orElse(-1) +
                           1;
            minDistanceToEnd.put(space, distance);

            // Find spaces to check next
            space.findOrthogonalAdjacent()
                 .stream()
                 .filter(c -> validRange.contains(c.getRow()) && validRange.contains(c.getColumn()))
                 .filter(not(corruptedSpaces::contains))
                 .filter(not(minDistanceToEnd::containsKey))
                 .forEach(nextSpacesToCheck::add);

            if (spacesToCheck.isEmpty()) {
                spacesToCheck.addAll(nextSpacesToCheck);
                nextSpacesToCheck.clear();
            }
        }

        log.atDebug()
           .setMessage("Distance to end:\n{}")
           .addArgument(() -> Coordinate.printMap(0, 0, size, size,
                                                  minDistanceToEnd,
                                                  d -> Integer.toString(d, 36).transform(s -> s.charAt(s.length() - 1)),
                                                  '#'))
           .log();

        return minDistanceToEnd.get(Coordinate.of(0, 0));
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }

}