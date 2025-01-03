package aoc._2024;

import static aoc._2024.Computer.REGISTER_A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private static final String TEST_INPUT_TXT_2 = "testInput/Day17-2.txt";



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
        resultMessage = "The lowest positive initial value for register A that causes the program to output a copy of itself is: {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        List<String> testLines2 = FileUtils.readFile(TEST_INPUT_TXT_2);
        var expectedTestResult2 = 117_440;
        var testResult2 = part2(testLines2);

        log.info("Should be {}", expectedTestResult2);
        log.info(resultMessage, testResult2);

        if (testResult2 != expectedTestResult2)
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
             .forEach(l -> state.registers().put(l.split(inputDelimiter)[0], Long.valueOf(l.split(inputDelimiter)[1])));

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
     * What is the lowest positive initial value for register A that causes the
     * program to output a copy of itself?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        var inputDelimiter = ": ";

        // Parse the program
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

        // Try adding digits to the input as long as its matching the output
        int digit = 0;
        List<Long> bases = new ArrayList<>();
        List<Long> nextBases = new ArrayList<>();
        bases.add(0L);
        var programSize = program.size();
        while (digit < programSize && !bases.isEmpty()) {
            var base = bases.removeLast();
            var localDigit = digit;
            IntStream.rangeClosed(0, 7)
                     .forEach(i -> {
                         var input = base * 8 + i;
                         var output = runComputerWithInput(input, program);
                         var outputSize = output.size();
                         if (outputSize > localDigit &&
                             output.equals(program.subList(programSize - localDigit - 1, programSize))) {
                             nextBases.add(input);
                         }
                     });
            if (bases.isEmpty()) {
                digit++;
                bases.addAll(nextBases);
                nextBases.clear();
            }
        }
        log.debug("Found options: {}", bases);

        // Double check
        var finalInput = bases.stream()
                              .mapToLong(Long::longValue)
                              .filter(i -> runComputerWithInput(i, program).equals(program))
                              .min()
                              .orElse(-1);

        var finalOutput = runComputerWithInput(finalInput, program);
        if (finalOutput.equals(program))
            log.debug("Success!");

        // Begin a REPL loop taking numbers from input
        /*
        Scanner scanner = new Scanner(System.in);
        long input = 0;
        while (input >= 0) {
            input = Long.parseLong(scanner.nextLine(), 8);
            runComputerWithInput(input, program);
        }
        scanner.close();
        */

        return finalInput;
    }



    /**
     * A helpful method to run the given program on the computer with a given A
     * register starting value.
     * 
     * @param aRegister The value to initialize the A register with.
     * @param program The program to run.
     * @return The output of running the program.
     */
    private static List<Integer> runComputerWithInput(long aRegister, List<Integer> program) {
        Computer state = new Computer();
        state.registers().put(REGISTER_A, aRegister);
        state.run(program);
        log.debug("Input of {}o gives output of {}", Long.toOctalString(aRegister), state.output());
        return state.output();
    }



    /**
     * This worked for the test input, but the number for the real input was
     * greater than {@link Integer#MAX_VALUE}. It took 11 minutes to scan
     * through them all.
     * <p>
     * What is the lowest positive initial value for register A that causes the
     * program to output a copy of itself?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2_x(final List<String> lines) {

        var inputDelimiter = ": ";

        // Parse the program
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

        // Try values for A until the output matches the program.
        Computer state = new Computer();
        var testInput = Long.parseLong("6522275", 8);
        state.registers().put(REGISTER_A, testInput);
        state.run(program);
        log.debug("Input of {}o gives output of {}", Long.toOctalString(testInput), state.output());
        state.reset();

        long initialA = 0;
        var outputsMatch = false;

        while (!outputsMatch && initialA < 100 && initialA >= 0) {
            //            if (initialA % 10_000 == 0)
            //                log.debug("Trying {}", initialA);

            state.reset();
            state.registers().put(REGISTER_A, initialA);
            var canContinue = true;
            outputsMatch = true;
            do {
                canContinue = state.step(program);
                var output = state.output();
                //                outputsMatch = output.size() <= program.size() &&
                //                               program.subList(0, output.size()).equals(state.output());
            } while (canContinue && outputsMatch);
            outputsMatch = program.equals(state.output());

            log.debug("Initial value {} ({}o {}b) resulting output: {}",
                      initialA, Long.toOctalString(initialA), Long.toBinaryString(initialA), state.output());

            //            initialA+=8*8*x++;
            //            initialA+=8;
            initialA++;
        }

        log.debug("Final state:\n{}", state);

        return initialA;
    }



    /**
     * The sample program for part two converts the initial A value into an
     * octal number by dividing by 8 and outputting the mod 8 value.
     * 
     * @param lines The input lines which have the program in them.
     * @return The lowest positive integer value which produces the program
     *         itself as an output.
     */
    private static long part2_sample2_reverseEngineered(final List<String> lines) {

        var inputDelimiter = ": ";

        // Parse the program
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

        //        var initialA = IntStream.range(0, program.size())
        //                                .mapToLong(i -> (long) (Math.pow(8, i + 1)) * program.get(i))
        //                                .sum();

        var initialA = Long.parseLong(program.reversed().stream().map(Object::toString).collect(Collectors.joining()), 8) * 8;

        // Try it
        Computer state = new Computer();
        state.registers().put(REGISTER_A, initialA);
        state.run(program);

        log.debug("Final state:\n{}", state);
        if (!state.output().equals(program))
            log.error("The inital input of {} resulted in an output of {} instead of the input program.", initialA, state.output());

        return initialA;

    }

}