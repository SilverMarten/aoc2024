package aoc._2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        var resultMessage = "Aave after blinking 25 times, you have {} stones.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 55_312;
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
     * Consider the arrangement of stones in front of you. How many stones will
     * you have after blinking 25 times?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        var stones = Stream.of(lines.getFirst().split(" ")).map(Long::valueOf).collect(Collectors.toCollection(ArrayList::new));

        log.debug("Initial arragement: {}", stones);

        //        Map<Long, List<List<Long>>> blinkMap = new HashMap<>();

        IntStream.rangeClosed(1, 25).forEach(i -> {
            var temp = stones.stream().map(Day11::getNextValue).flatMap(List::stream).toList();
            stones.clear();
            stones.addAll(temp);

            if (i <= 6)
                log.debug("After {} blinks: {}", i, stones);
        });

        return stones.size();
    }



    private static List<Long> getNextValue(long stone) {
        if (stone == 0)
            return Arrays.asList(1L);

        int digits = (int) Math.log10(stone) + 1;
        if (digits % 2 == 0)
            return Arrays.asList(stone / ((int) Math.pow(10, digits / 2.)),
                                 stone % ((int) Math.pow(10, digits / 2.)));

        return Arrays.asList(stone * 2024);
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