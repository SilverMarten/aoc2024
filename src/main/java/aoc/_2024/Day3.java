package aoc._2024;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/3
 * 
 * @author Paul Cormier
 *
 */
public class Day3 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day3.class);

    private static final String INPUT_TXT = "input/Day3.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day3.txt";



    public static void main(String[] args) {

        var resultMessage = "Adding up the result of each instruction produces {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 161;
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
     * Scan the corrupted memory for uncorrupted mul instructions. What do you
     * get if you add up all of the results of the multiplications?
     * 
     * @param lines The lines read from the input.
     * @return The sum of the valid {@code mul}.
     */
    private static int part1(final List<String> lines) {

        Pattern regex = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)");

        List<String> validExpressions = lines.stream()
                                             .map(regex::matcher)
                                             .flatMap(Matcher::results)
                                             .map(MatchResult::group)
                                             .toList();

        log.debug("Valid expressions found: {}", validExpressions);

        return validExpressions.stream()
                               .mapToInt(Day3::processInstruction)
                               .sum();
    }



    /**
     * Parse the numbers out of the given instruction and multiply them
     * together.
     * 
     * @param instruction An instruction containing separated numbers to be
     *            multiplied together.
     * @return The product of the numbers.
     */
    private static int processInstruction(String instruction) {
        return Pattern.compile("\\d+")
                      .matcher(instruction)
                      .results()
                      .map(MatchResult::group)
                      .mapToInt(Integer::parseInt)
                      .reduce(1, Math::multiplyExact);
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static int part2(final List<String> lines) {

        return -1;
    }

}