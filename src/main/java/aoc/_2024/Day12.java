package aoc._2024;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.teeing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/12
 * 
 * @author Paul Cormier
 *
 */
public class Day12 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day12.class);

    private static final String INPUT_TXT = "input/Day12.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day12.txt";



    public static void main(String[] args) {

        var resultMessage = "The total price of fencing all regions on the map is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        var testMap = Coordinate.mapCoordinates(testLines);

        var expectedTestResult = 1930;
        var testResult = part1(testMap);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var map = Coordinate.mapCoordinates(lines);

        log.info(resultMessage, part1(map));

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
     * What is the total price of fencing all regions on your map?
     * 
     * @param map The map read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final Map<Coordinate, Character> map) {

        // Group into regions
        AtomicInteger regionId = new AtomicInteger();
        Map<Coordinate, Region> regionMap = new HashMap<>();
        map.forEach((c, p) -> {
            if (!regionMap.containsKey(c)) {
                // Map the new region
                var region = new Region(regionId.getAndIncrement(), p);
                regionMap.put(c, region);

                // Map its neighbours
                Deque<Coordinate> neighbours = c.findOrthogonalAdjacent()
                                                .stream()
                                                .filter(n -> p.equals(map.get(n)))
                                                .filter(Predicate.not(regionMap::containsKey))
                                                .collect(toCollection(ArrayDeque::new));
                while (!neighbours.isEmpty()) {
                    var neighbour = neighbours.pop();
                    regionMap.putIfAbsent(neighbour, region);
                    neighbour.findOrthogonalAdjacent()
                             .stream()
                             .filter(n -> p.equals(map.get(n)))
                             .filter(Predicate.not(regionMap::containsKey))
                             .forEach(neighbours::push);
                }
            }
        });

        /*Map<Region, List<Coordinate>> regionMap = new HashMap<>();
        map.forEach((c, p) -> {
            var existingRegions = regionMap.entrySet()
                                           .stream()
                                           .filter(e -> e.getKey().plantType() == p)
                                           .toList();
            AtomicBoolean foundRegion = new AtomicBoolean(false);
            existingRegions.forEach(e -> {
                if (CollectionUtils.containsAny(c.findOrthogonalAdjacent(), e.getValue())) {
                    e.getValue().add(c);
                    foundRegion.set(true);
                }
            });
            if (!foundRegion.get())
                regionMap.compute(new Region(regionId.getAndIncrement(), p), (k, v) -> new ArrayList<>()).add(c);
        });*/
        log.debug("Region map: {}", regionMap);

        /*return map.entrySet()
                  .stream()
                  .collect(groupingBy(Entry::getValue,
                                      teeing(counting(),
                                             mapping(e -> Day12.countEdges(e, map),
                                                     summingInt(Integer::intValue)),
                                             (a, p) -> a * p)))
                  .entrySet()
                  .stream()
                  .peek(e -> log.debug("A region of {} plants with price {}.", e.getKey(), e.getValue()))
                  .map(Entry::getValue)
                  .mapToLong(Long::longValue)
                  .sum();*/

        return regionMap.entrySet()
                        .stream()
                        .collect(groupingBy(Entry::getValue, mapping(Entry::getKey, toList())))
                        .entrySet()
                        .stream()
                        .mapToLong(e -> {
                            var price = e.getValue()
                                         .stream()
                                         .collect(teeing(counting(),
                                                         mapping(c -> countEdges(c, map),
                                                                 summingInt(Integer::intValue)),
                                                         (a, p) -> a * p));
                            log.debug("A region of {} plants with price {}.", e.getKey().plantType(), price);
                            return price;
                        })
                        .sum();

    }



    private static int countEdges(Coordinate gardenPlot, Map<Coordinate, Character> map) {
        var plantType = map.get(gardenPlot);
        return (int) gardenPlot.findOrthogonalAdjacent()
                               .stream()
                               .map(map::get)
                               .filter(c -> !plantType.equals(c))
                               .count();
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



    private record Region(int id, char plantType) {
    }
}