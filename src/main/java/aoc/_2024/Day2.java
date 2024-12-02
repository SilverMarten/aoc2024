package aoc._2024;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.Range;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/2
 * 
 * @author Paul Cormier
 *
 */
public class Day2 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day2.class);

    private static final String INPUT_TXT = "input/Day2.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day2.txt";

    public static void main(String[] args) {

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        long expectedTestResult = 2;
        long part1TestResult = part1(testLines);
        log.info("{} reports are safe (should be {})", part1TestResult, expectedTestResult);

        if (part1TestResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("{} reports are safe", part1(lines));

        // PART 2
        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 1_234_567_890L;
        long part2TestResult = part2(testLines);
        log.info("{} (should be {})", part2TestResult, expectedTestResult);

        if (part2TestResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info("{}", part2(lines));
    }

    /**
     * Analyze the unusual data from the engineers. How many reports are safe?
     */
    private static long part1(final List<String> lines) {
        return lines.stream()
                    .map(l -> Stream.of(l.split(" "))
                                    .map(Integer::valueOf)
                                    .collect(Collectors.toList()))
                    .map(Day2::safe)
                    .filter(Boolean::booleanValue)
                    .count();
    }

    private static boolean safe(List<Integer> l) {
        log.debug("{}", l);
        Range<Integer> safeRange = Range.of(1, 3);
        boolean increasing = l.get(1) > l.get(0);
        return IntStream.range(0, l.size() - 1)
                        .allMatch(i -> safeRange.contains(Math.abs(l.get(i) - l.get(i + 1)))
                                       && !(increasing ^ l.get(i + 1) > l.get(i)));
    }

    private static long part2(final List<String> lines) {

        return -1;
    }

}