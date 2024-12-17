package aoc._2024;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/17
 * 
 * @author Paul Cormier
 *
 */
public class Day17 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day17.class);

    private static final String INPUT_TXT = "input/Day17.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day17.txt";



    public static void main(String[] args) {

        var resultMessage = "The output is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = List.of(4, 6, 3, 5, 6, 3, 5, 2, 1, 0);
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (!expectedTestResult.equals(testResult))
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        List<Integer> part1Result = part1(lines);
        log.info(resultMessage, part1Result.stream().map(Object::toString).collect(Collectors.joining(",")));

        // PART 2
        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        //        expectedTestResult = 1_234_567_890;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (!expectedTestResult.equals(testResult))
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Using the information provided by the debugger, initialize the registers
     * to the given values, then run the program. Once it halts, what do you get
     * if you use commas to join the values it output into a single string?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static List<Integer> part1(final List<String> lines) {

        var inputDelimiter = ": ";
        // Setup the computer
        Computer state = new Computer();
        lines.stream()
             .filter(l -> l.startsWith("Register"))
             .forEach(l -> state.registers().put(l.split(inputDelimiter)[0], Integer.valueOf(l.split(inputDelimiter)[1])));

        log.debug("Initial state:\n{}", state);

        // Run the program
        var program = lines.stream()
                           .filter(l -> l.startsWith("Program:"))
                           .map(l -> l.split(inputDelimiter)[1].split(","))
                           .flatMap(Arrays::stream)
                           .map(Integer::valueOf)
                           .toList();
        log.atDebug()
           .setMessage("Program: {}")
           .addArgument(() -> program.stream().map(Object::toString).collect(Collectors.joining(",")))
           .log();

        state.run(program);

        // Return the output
        return state.output();
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static List<Integer> part2(final List<String> lines) {

        return Collections.emptyList();
    }

}