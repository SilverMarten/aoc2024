package aoc._2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

        var testOrderingRules = testLines.stream()
                                         .filter(l -> l.contains("|"))
                                         .map(l -> new Order(Integer.parseInt(l.split("\\|")[0]), Integer.parseInt(l.split("\\|")[1])))
                                         .collect(Collectors.groupingBy(Order::before,
                                                                        Collectors.mapping(Order::after, Collectors.toList())));
        var testPages = testLines.stream()
                                 .filter(l -> l.contains(","))
                                 .map(l -> Arrays.stream(l.split(","))
                                                 .map(Integer::valueOf)
                                                 .toList())
                                 .toList();

        var expectedTestResult = 143;
        var testResult = part1(testPages, testOrderingRules);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        var orderingRules = lines.stream()
                                 .filter(l -> l.contains("|"))
                                 .map(l -> new Order(Integer.parseInt(l.split("\\|")[0]), Integer.parseInt(l.split("\\|")[1])))
                                 .collect(Collectors.groupingBy(Order::before,
                                                                Collectors.mapping(Order::after, Collectors.toList())));

        var pages = lines.stream()
                         .filter(l -> l.contains(","))
                         .map(l -> Arrays.stream(l.split(","))
                                         .map(Integer::valueOf)
                                         .toList())
                         .toList();

        log.info(resultMessage, part1(pages, orderingRules));

        // PART 2
        //        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 123;
        testResult = part2(testPages, testOrderingRules);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(pages, orderingRules));
    }



    /**
     * Determine which updates are already in the correct order. What do you get
     * if you add up the middle page number from those correctly-ordered
     * updates?
     * 
     * @param pages The pages read from the input.
     * @param orderingRules The map of pages and the pages which come after each
     *            page.
     * 
     * @return The sum of the middle pages of the lists which are in order.
     */
    private static long part1(final List<List<Integer>> pages, Map<Integer, List<Integer>> orderingRules) {

        return pages.stream()
                    .filter(p -> pagesInOrder(p, orderingRules))
                    .peek(p -> log.debug(p.toString()))
                    .mapToInt(p -> p.get(p.size() / 2))
                    .sum();

    }



    /**
     * Determine if the pages are in order. The pages are in order if none of
     * the pages which come before it are in the "after" list of the ordering
     * rules.
     * 
     * @param pages The list of pages.
     * @param orderingRules The rules describing which pages must come after the
     *            indexed pages.
     * @return {@code true} if the pages are already in order.
     */
    private static boolean pagesInOrder(List<Integer> pages, Map<Integer, List<Integer>> orderingRules) {

        return IntStream.range(0, pages.size())
                        .allMatch(i -> !CollectionUtils.containsAny(orderingRules.getOrDefault(pages.get(i),
                                                                                               Collections.emptyList()),
                                                                    pages.subList(0, i)));
    }



    /**
     * Find the updates which are not in the correct order. What do you get if
     * you add up the middle page numbers after correctly ordering just those
     * updates?
     * 
     * @param pages The pages read from the input.
     * @param orderingRules The map of pages and the pages which come after each
     *            page.
     * 
     * @return The sum of the middle pages of the lists which are not in order,
     *         once ordered.
     */
    private static int part2(final List<List<Integer>> pages, Map<Integer, List<Integer>> orderingRules) {

        return pages.stream()
                    .filter(p -> !pagesInOrder(p, orderingRules))
                    .peek(p -> log.debug(p.toString()))
                    .map(p -> sortPages(p, orderingRules))
                    .peek(p -> log.debug(p.toString()))
                    .mapToInt(p -> p.get(p.size() / 2))
                    .sum();
    }



    /**
     * Sort the list of pages based on the ordering rules.
     * 
     * @param pages The pages, in any order.
     * @param orderingRules The map of pages and the pages which come after each
     *            page.
     * @return A new list of pages, in the correct order.
     */
    private static List<Integer> sortPages(List<Integer> pages, Map<Integer, List<Integer>> orderingRules) {
        Comparator<Integer> pageOrderComparator = (x, y) -> orderingRules.getOrDefault(x, Collections.emptyList())
                                                                         .contains(y) ? -1 : 1;
        List<Integer> sortedPages = new ArrayList<>(pages);
        sortedPages.sort(pageOrderComparator);

        return sortedPages;
    }



    public record Order(int before, int after) {
    }

}