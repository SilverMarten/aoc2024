package aoc._2024;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Range;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Coordinate.CoordinatePair;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/8
 * 
 * @author Paul Cormier
 *
 */
public class Day8 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day8.class);

    private static final String INPUT_TXT = "input/Day8.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day8.txt";

    public static void main(String[] args) {

        var resultMessage = "{} locations within the bounds of the map contain an antinode";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 14;
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

        expectedTestResult = 34;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }

    /**
     * Calculate the impact of the signal. How many unique locations within the bounds of the map contain an antinode?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {
        var antennaMap = Coordinate.mapCoordinates(lines)
                                   .entrySet()
                                   .stream()
                                   .collect(Collectors.groupingBy(Entry::getValue,
                                                                  Collectors.mapping(Entry::getKey,
                                                                                     Collectors.toList())));

        var rowRange = Range.of(1, lines.size());
        var columnRange = Range.of(1, lines.getFirst().length());

        Set<Coordinate> antinodes = antennaMap.entrySet()
                                              .stream()
                                              .map(e -> findAntinodes(e.getValue()))
                                              .flatMap(Collection::stream)
                                              .filter(c -> rowRange.contains(c.getRow())
                                                           && columnRange.contains(c.getColumn()))
                                              .collect(Collectors.toSet());

        return antinodes.size();
    }

    /**
     * Find all the antinodes of the given collection of antennae.
     * 
     * @param antennae The collection of antennae to find all the antinodes of.
     * @return All the antinodes of the given collection of antennae.
     */
    private static Set<Coordinate> findAntinodes(List<Coordinate> antennae) {

        return Coordinate.combinations(antennae)
                         .stream()
                         .peek(p -> log.debug("Antennae {}, {}", p.c1(), p.c2()))
                         .map(Day8::findAntinodes)
                         .peek(c -> log.debug("Antinodes {}", c))
                         .flatMap(Collection::stream)
                         .collect(Collectors.toSet());

    }

    /**
     * Find the antinodes of the given coordinate pair.
     * 
     * @param antennae The pair of antennae to find the antinodes of.
     * @return The antinodes of the given coordinate pair.
     */
    private static Set<Coordinate> findAntinodes(CoordinatePair antennae) {
        Set<Coordinate> antinodes = new HashSet<>();

        antinodes.add(antennae.c1()
                              .translate(Coordinate.of(-2 * (antennae.c1().getRow() - antennae.c2().getRow()),
                                                       -2 * (antennae.c1().getColumn() - antennae.c2().getColumn()))));
        antinodes.add(antennae.c2()
                              .translate(Coordinate.of(-2 * (antennae.c2().getRow() - antennae.c1().getRow()),
                                                       -2 * (antennae.c2().getColumn() - antennae.c1().getColumn()))));

        return antinodes;
    }

    /**
     * Calculate the impact of the signal using this updated model.
     * How many unique locations within the bounds of the map contain an antinode?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        var antennaMap = Coordinate.mapCoordinates(lines)
                                   .entrySet()
                                   .stream()
                                   .collect(Collectors.groupingBy(Entry::getValue,
                                                                  Collectors.mapping(Entry::getKey,
                                                                                     Collectors.toList())));

        var rowRange = Range.of(1, lines.size());
        var columnRange = Range.of(1, lines.getFirst().length());

        Set<Coordinate> antinodes = antennaMap.entrySet()
                                              .stream()
                                              .map(e -> findAntinodes2(e.getValue(), rowRange, columnRange))
                                              .flatMap(Collection::stream)
                                              .filter(c -> rowRange.contains(c.getRow())
                                                           && columnRange.contains(c.getColumn()))
                                              .collect(Collectors.toSet());

        return antinodes.size();
    }

    /**
     * Find all the antinodes of the given collection of antennae within the given range.
     * 
     * @param antennae The collection of antennae to find all the antinodes of.
     * @param rowRange The range of row values to search within.
     * @param columnRange The range of column values to search within.
     * @return All the antinodes of the given collection of antennae within the given range.
     */
    private static Set<Coordinate> findAntinodes2(List<Coordinate> antennae,
                                                  Range<Integer> rowRange,
                                                  Range<Integer> columnRange) {

        return Coordinate.combinations(antennae)
                         .stream()
                         .peek(p -> log.debug("Antennae {}, {}", p.c1(), p.c2()))
                         .map(p -> findAntinodes2(p, rowRange, columnRange))
                         .peek(c -> log.debug("Antinodes {}", c))
                         .flatMap(Collection::stream)
                         .collect(Collectors.toSet());

    }

    /**
     * Find all the antinodes of the given coordinate pair within the given range.
     * 
     * @param antennae The pair of antennae to find the antinodes of.
     * @return The all antinodes of the given coordinate pair within the given range.
     */
    private static Set<Coordinate> findAntinodes2(CoordinatePair antennae,
                                                  Range<Integer> rowRange,
                                                  Range<Integer> columnRange) {
        Set<Coordinate> antinodes = new HashSet<>();
        boolean eitherInRange = true;
        int i = 1;
        while (eitherInRange) {

            Coordinate antinode1 = antennae.c1()
                                           .translate(Coordinate.of(-i * (antennae.c1().getRow()
                                                                          - antennae.c2().getRow()),
                                                                    -i * (antennae.c1().getColumn()
                                                                          - antennae.c2().getColumn())));
            boolean antinode1InRange = rowRange.contains(antinode1.getRow())
                                       && columnRange.contains(antinode1.getColumn());
            if (antinode1InRange)
                antinodes.add(antinode1);

            Coordinate antinode2 = antennae.c2()
                                           .translate(Coordinate.of(-i * (antennae.c2().getRow()
                                                                          - antennae.c1().getRow()),
                                                                    -i * (antennae.c2().getColumn()
                                                                          - antennae.c1().getColumn())));
            boolean antinode2InRange = rowRange.contains(antinode2.getRow())
                                       && columnRange.contains(antinode2.getColumn());
            if (antinode2InRange)
                antinodes.add(antinode2);

            eitherInRange = antinode1InRange || antinode2InRange;
            i++;
        }

        return antinodes;
    }
}