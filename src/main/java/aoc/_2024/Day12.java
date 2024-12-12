package aoc._2024;

import static aoc.Direction.DOWN;
import static aoc.Direction.LEFT;
import static aoc.Direction.RIGHT;
import static aoc.Direction.UP;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        var testRows = testLines.size();
        var testColumns = testLines.getFirst().length();

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
        var rows = lines.size();
        var columns = lines.getFirst().length();

        log.info(resultMessage, part1(map));

        // PART 2
        //        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 1206;
        testResult = part2(testMap, testRows, testColumns);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(map, rows, columns));
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

        log.debug("Region map: {}", regionMap);

        // For each contiguous region, multiply the area by the perimeter.
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



    /**
     * Count the number of adjacent garden plots to the garden plot at the given
     * location with a different plant type.
     * 
     * @param gardenPlot The location of the garden plot to count non-matching
     *            neighbours.
     * @param map The map of garden plots.
     * @return The number of orthogonally adjacent neighbours with a different
     *         plant type.
     */
    private static int countEdges(Coordinate gardenPlot, Map<Coordinate, Character> map) {
        var plantType = map.get(gardenPlot);
        return (int) gardenPlot.findOrthogonalAdjacent()
                               .stream()
                               .map(map::get)
                               .filter(c -> !plantType.equals(c))
                               .count();
    }



    /**
     * What is the new total price of fencing all regions on your map?
     * 
     * @param map The map read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final Map<Coordinate, Character> map, int rows, int columns) {

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

        // Map out the sides of the regions
        Map<Region, Integer> sidesMap = new HashMap<>();

        Stream.of(UP, DOWN)
              .forEach(d -> {
                  IntStream.rangeClosed(1, rows)
                           .forEach(r -> {
                               IntStream.rangeClosed(1, columns)
                                        .forEach(c -> {
                                            // Is there an edge?
                                            var location = Coordinate.of(r, c);
                                            var plantType = map.get(location);
                                            var locationUpOrDown = location.translate(d, 1);
                                            if (!plantType.equals(map.get(locationUpOrDown))) {
                                                // Does the edge end?
                                                var plantTypeRight = map.get(location.translate(RIGHT, 1));
                                                if (!plantType.equals(plantTypeRight) ||
                                                    plantTypeRight.equals(map.get(locationUpOrDown.translate(RIGHT, 1)))) {
                                                    sidesMap.merge(regionMap.get(location), 1, Math::addExact);
                                                }
                                            }
                                        });
                           });
              });
        Stream.of(LEFT, RIGHT)
              .forEach(d -> {
                  IntStream.rangeClosed(1, columns)
                           .forEach(r -> {
                               IntStream.rangeClosed(1, rows)
                                        .forEach(c -> {
                                            // Is there an edge?
                                            var location = Coordinate.of(r, c);
                                            var plantType = map.get(location);
                                            var locationLeftOrRight = location.translate(d, 1);
                                            if (!plantType.equals(map.get(locationLeftOrRight))) {
                                                // Does the edge end?
                                                var plantTypeRight = map.get(location.translate(DOWN, 1));
                                                if (!plantType.equals(plantTypeRight) ||
                                                    plantTypeRight.equals(map.get(locationLeftOrRight.translate(DOWN, 1)))) {
                                                    sidesMap.merge(regionMap.get(location), 1, Math::addExact);
                                                }
                                            }
                                        });
                           });
              });

        // For each contiguous region, multiply the area by the number of sides.
        return regionMap.entrySet()
                        .stream()
                        .collect(groupingBy(Entry::getValue, mapping(Entry::getKey, toList())))
                        .entrySet()
                        .stream()
                        .mapToLong(e -> {
                            var area = e.getValue().size();
                            var sides = sidesMap.getOrDefault(e.getKey(), -1);
                            var price = area * sides;
                            log.debug("A region of {} plants with price {} * {} = {}.", e.getKey().plantType(), area, sides, price);
                            return price;
                        })
                        .sum();

    }



    private record Region(int id, char plantType) {
    }
}