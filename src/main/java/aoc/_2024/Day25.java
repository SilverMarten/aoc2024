package aoc._2024;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.SetUtils;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/25
 * 
 * @author Paul Cormier
 *
 */
public class Day25 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day25.class);

    private static final String INPUT_TXT = "input/Day25.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day25.txt";



    public static void main(String[] args) {

        var resultMessage = "There are {} unique pairs that fit.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 3;
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info(resultMessage, part1(lines));

        // PART 2
        // No part 2 for day 25!
    }



    /**
     * Analyze your lock and key schematics. How many unique lock/key pairs fit
     * together without overlapping in any column?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        var rows = 8;
        var columns = 5;

        List<Set<Coordinate>> keys = new ArrayList<>();
        List<Set<Coordinate>> locks = new ArrayList<>();

        IntStream.rangeClosed(0, lines.size() / rows)
                 .mapToObj(i -> lines.subList(i * rows,
                                              Math.min(i * rows + rows, lines.size())))
                 .map(Coordinate::findCoordinates)
                 .forEach(kl -> {
                     if (kl.contains(Coordinate.of(1, 1)))
                         locks.add(kl);
                     else
                         keys.add(kl);
                 });

        log.atDebug()
           .setMessage("Keys:\n{}")
           .addArgument(() -> keys.stream().map(k -> Coordinate.printMap(rows - 1, columns, k)).collect(Collectors.joining("\n\n")))
           .log();
        log.atDebug()
           .setMessage("Locks:\n{}")
           .addArgument(() -> locks.stream().map(k -> Coordinate.printMap(rows - 1, columns, k)).collect(Collectors.joining("\n\n")))
           .log();

        // Just look for non overlapping coordinate sets
        return keys.stream()
                   .flatMap(k -> locks.stream().filter(l -> SetUtils.intersection(k, l).isEmpty()))
                   .count();

    }



}