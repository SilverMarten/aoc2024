package aoc._2024;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/5
 * 
 * @author Paul Cormier
 *
 */
public class Day5 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day5.class);

    private static final String INPUT_TXT = "input/Day5.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day5.txt";



    public static void main(String[] args) {

        var resultMessage = "The sum of the middle page numbers is {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 143;
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
     * Determine which updates are already in the correct order. What do you get
     * if you add up the middle page number from those correctly-ordered
     * updates?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {
        var orderingRules = lines.stream()
                                 .filter(l -> l.contains("|"))
                                 .map(l -> new Order(Integer.valueOf(l.split("\\|")[0]), Integer.valueOf(l.split("\\|")[1])))
                                 .collect(Collectors.groupingBy(Order::before,
                                                                Collectors.mapping(Order::after, Collectors.toList())));

        return lines.stream()
                    .filter(l -> l.contains(","))
                    .map(l -> Arrays.stream(l.split(","))
                                    .map(Integer::valueOf)
                                    .toList())
                    .filter(p -> pagesInOrder(p, orderingRules))
                    .peek(p -> log.debug(p.toString()))
                    .mapToInt(p -> p.get(p.size() / 2))
                    .sum();

    }



    private static boolean pagesInOrder(List<Integer> pages, Map<Integer, List<Integer>> orderingRules) {
        // The pages are in order if none of the pages which come before it 
        // are in the "after" list of the ordering rules.

        return IntStream.range(0, pages.size())
                        .allMatch(i -> !CollectionUtils.containsAny(orderingRules.getOrDefault(pages.get(i),
                                                                                               Collections.emptyList()),
                                                                    pages.subList(0, i)));
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static int part2(final List<String> lines) {

        return -1;
    }



    public record Order(int before, int after) {
    }

}