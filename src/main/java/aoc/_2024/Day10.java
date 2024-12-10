package aoc._2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/10
 * 
 * @author Paul Cormier
 *
 */
public class Day10 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day10.class);

    private static final String INPUT_TXT = "input/Day10.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day10.txt";



    public static void main(String[] args) {

        var resultMessage = "The sum of the scores of all trailheads on the topographic map is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        var testMap = Coordinate.mapDigits(testLines);

        var expectedTestResult = 36;
        var testResult = part1(testMap);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var map = Coordinate.mapDigits(lines);

        log.info(resultMessage, part1(map));

        // PART 2
        resultMessage = "The sum of the ratings of all trailheads is: {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 81;
        testResult = part2(testMap);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(map));
    }



    /**
     * What is the sum of the scores of all trailheads on your topographic map?
     * 
     * @param map The map read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final Map<Coordinate, Integer> map) {

        List<List<Coordinate>> trails = findTrails(map);

        // Find distinct starts and ends
        return trails.stream()
                     .collect(Collectors.groupingBy(List::getFirst, Collectors.mapping(List::getLast, Collectors.toSet())))
                     .values()
                     .stream()
                     .mapToInt(Collection::size)
                     .sum();

    }



    /**
     * Compute all trails along the map, starting from 0, where there is only an
     * increase of 1 each orthogonal step.
     * 
     * @param map The topographic map.
     * @return A list of all of the trails found on the map.
     */
    private static List<List<Coordinate>> findTrails(final Map<Coordinate, Integer> map) {
        List<List<Coordinate>> trails = map.entrySet()
                                           .stream()
                                           .filter(e -> e.getValue() == 0)
                                           .map(Entry::getKey)
                                           .map(t -> {
                                               var list = new ArrayList<Coordinate>();
                                               list.add(t);
                                               return list;
                                           })
                                           .collect(Collectors.toCollection(ArrayList::new));

        log.info("There are {} trailheads.", trails.size());

        // Check around for the next steps
        IntStream.rangeClosed(1, 9).forEach(i -> {
            // Create new trails if the paths diverge
            var temp = trails.stream()
                             .flatMap(trail -> trail.getLast()
                                                    .findOrthogonalAdjacent()
                                                    .stream()
                                                    .filter(c -> map.getOrDefault(c, 0) == i)
                                                    .map(c -> ListUtils.union(trail, Arrays.asList(c))))
                             .toList();
            trails.clear();
            trails.addAll(temp);
        });
        return trails;
    }



    /**
     * What is the sum of the ratings of all trailheads?
     * 
     * @param map The map read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final Map<Coordinate, Integer> map) {

        return findTrails(map).size();
    }

}