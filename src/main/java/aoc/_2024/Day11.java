package aoc._2024;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/11
 * 
 * @author Paul Cormier
 *
 */
public class Day11 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day11.class);

    private static final String INPUT_TXT = "input/Day11.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day11.txt";



    public static void main(String[] args) {

        var resultMessage = "After blinking 25 times, you have {} stones.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 55_312L;
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
        resultMessage = "After blinking 75 times, you have {} stones.";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        // There was no test result, but it was in fact 65,601,038,650,482
        expectedTestResult = 65_601_038_650_482L;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Consider the arrangement of stones in front of you. How many stones will
     * you have after blinking 25 times?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        var stones = Stream.of(lines.getFirst().split(" ")).map(Long::valueOf).collect(Collectors.toCollection(ArrayList::new));

        log.debug("Initial arragement: {}", stones);

        IntStream.rangeClosed(1, 25).forEach(i -> {
            var temp = stones.stream().map(Day11::getNextValues).flatMap(List::stream).toList();
            stones.clear();
            stones.addAll(temp);

            if (i <= 6)
                log.debug("After {} blinks: {}", i, stones);
        });

        return stones.size();
    }



    /**
     * Compute the next values for a given stone.
     * 
     * @param stone The number on the stone.
     * @return A list of the next values after blinking once.
     */
    private static List<Long> getNextValues(long stone) {
        if (stone == 0)
            return Arrays.asList(1L);

        int digits = (int) Math.log10(stone) + 1;
        if (digits % 2 == 0)
            return Arrays.asList(stone / ((int) Math.pow(10, digits / 2.)),
                                 stone % ((int) Math.pow(10, digits / 2.)));

        return Arrays.asList(stone * 2024);
    }



    /**
     * How many stones would you have after blinking a total of 75 times?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        var stones = Stream.of(lines.getFirst().split(" ")).map(Long::valueOf).collect(Collectors.toCollection(ArrayList::new));

        log.debug("Initial arragement: {}", stones);

        var blinks = 75;

        // Create a map of known results (number of stones given a starting number and a number of blinks)
        Map<Result, Long> blinkMap = new HashMap<>();

        // Begin a stack of unknown results
        Deque<Result> stack = new ArrayDeque<>();
        stones.stream().forEach(s -> stack.push(new Result(s, blinks)));

        // Find the needed results
        while (!stack.isEmpty()) {
            var result = stack.pop();

            List<Result> nextValues = getNextValues(result.stone()).stream().map(s -> new Result(s, result.blinks() - 1)).toList();

            if (result.blinks() == 1) {
                // If it's the terminal blink, compute it
                blinkMap.put(result, (long) nextValues.size());
            } else if (nextValues.stream().allMatch(blinkMap::containsKey)) {
                // If we have the number of stones for the next value(s) 
                blinkMap.put(result, nextValues.stream().mapToLong(blinkMap::get).sum());
            } else {
                // We don't have enough information yet, put the results back on the stack
                stack.push(result);
                nextValues.stream().filter(Predicate.not(blinkMap::containsKey)).forEach(stack::push);
            }
        }

        // The map should now contain the initially required results
        return stones.stream().mapToLong(s -> blinkMap.get(new Result(s, blinks))).sum();
    }



    private record Result(long stone, long blinks) {
    }
}