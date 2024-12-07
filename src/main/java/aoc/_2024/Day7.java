package aoc._2024;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/7
 * 
 * @author Paul Cormier
 *
 */
public class Day7 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day7.class);

    private static final String INPUT_TXT = "input/Day7.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day7.txt";

    public static void main(String[] args) {

        var resultMessage = "The sum of the correct statements is {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 3749;
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
     * Determine which equations could possibly be true. What is their total calibration result?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        return lines.stream()
                    .map(line -> line.split(": "))
                    .map(p -> new Line(Long.parseLong(p[0]),
                                       Arrays.stream(p[1].split(" "))
                                             .map(Long::valueOf)
                                             .toList()))
                    .filter(Day7::linePossiblyTrue)
                    .mapToLong(Line::total)
                    .sum();

    }

    /**
     * Given the values for a line, determine if there's a combination of addition or multiplication that would result
     * in its total.
     * 
     * @param line The {@link Line} describing the target total and the numbers to work with.
     * @return {@code true} if there's a combination that results in the target total, {@code false} otherwise.
     */
    private static boolean linePossiblyTrue(final Line line) {

        Set<Long> totals = new HashSet<>();

        var numbers = line.numbers().iterator();
        totals.add(numbers.next());

        while (numbers.hasNext() && !totals.isEmpty()) {
            var next = numbers.next();
            // Compute the next set of numbers, filtering out any greater than the target total
            var newTotals = totals.stream()
                                  .flatMap(n -> Stream.of(n + next, n * next))
                                  .filter(n -> n <= line.total())
                                  .toList();

            totals.clear();
            totals.addAll(newTotals);
        }

        return totals.contains(line.total());
    }

    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }

    record Line(long total, List<Long> numbers) {
    }

}