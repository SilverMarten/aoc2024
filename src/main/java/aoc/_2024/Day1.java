package aoc._2024;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/1
 * 
 * @author Paul Cormier
 *
 */
public class Day1 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day1.class);

    private static final String INPUT_TXT = "input/Day1.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day1.txt";

    public static void main(String[] args) {

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        int expectedTestResult = 11;
        int part1TestResult = part1(testLines);
        log.info("The total distance between the lists is: {} (should be {})", part1TestResult, expectedTestResult);

        if (part1TestResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info("The total distance between the lists is: {}", part1(lines));

        // PART 2
        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 1_234_567_890;
        int part2TestResult = part2(testLines);
        log.info("{} (should be {})", part2TestResult, expectedTestResult);

        if (part2TestResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info("{}", part2(lines));
    }

    /**
     * Your actual left and right lists contain many location IDs. What is the total distance between your lists?
     */
    private static int part1(final List<String> lines) {
        List<Integer> leftList = lines.stream()
                                      .map(l -> l.split(" +")[0])
                                      .map(Integer::valueOf).sorted()
                                      .collect(Collectors.toList());
        List<Integer> rightList = lines.stream()
                                       .map(l -> l.split(" +")[1])
                                       .map(Integer::valueOf).sorted()
                                       .collect(Collectors.toList());

        int sum = IntStream.range(0, leftList.size())
                           .map(i -> Math.abs(leftList.get(i) - rightList.get(i)))
                           .sum();

        return sum;
    }

    private static int part2(final List<String> lines) {

        return -1;
    }

}