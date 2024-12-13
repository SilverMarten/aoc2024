package aoc._2024;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/13
 * 
 * @author Paul Cormier
 *
 */
public class Day13 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day13.class);

    private static final String INPUT_TXT = "input/Day13.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day13.txt";



    public static void main(String[] args) {

        var resultMessage = "The fewest tokens you would have to spend to win all possible prizes is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 480L;
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
        //        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        // Not given, but should be
        expectedTestResult = 875_318_608_908L;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Figure out how to win as many prizes as possible. What is the fewest
     * tokens you would have to spend to win all possible prizes?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        Set<Machine> machines = new HashSet<>();
        Coordinate buttonA = null;
        Coordinate buttonB = null;
        for (var line : lines) {
            if (line.isBlank())
                continue;

            var halves = line.split(":");
            var coordinate = new Coordinate(Integer.parseInt(StringUtils.getDigits(halves[1].split(",")[0])),
                                            Integer.parseInt(StringUtils.getDigits(halves[1].split(",")[1])));
            switch (halves[0]) {
                case "Button A" -> buttonA = coordinate;
                case "Button B" -> buttonB = coordinate;
                case "Prize" -> machines.add(new Machine(buttonA, buttonB, coordinate));
                default -> throw new IllegalArgumentException(halves[0] + " is not expected.");
            }
        }
        String machineFormat = """
            Button A: X+%d, Y+%d
            Button B: X+%d, Y+%d
            Prize: X=%d, Y=%d

            """;

        log.atDebug()
           .setMessage("Machines:\n{}")
           .addArgument(() -> machines.stream()
                                      .map(m -> String.format(machineFormat,
                                                              m.buttonA().x(), m.buttonA().y(),
                                                              m.buttonB().x(), m.buttonB().y(),
                                                              m.prize().x(), m.prize().y()))
                                      .collect(Collectors.joining()))
           .log();

        return machines.stream()
                       .map(Day13::canBeReached)
                       .filter(Optional::isPresent)
                       .map(Optional::get)
                       .mapToLong(b -> b.buttonA() * 3 + b.buttonB() * 1)
                       .sum();

    }



    /**
     * Determine if the prize can be reached in a whole number of button pushes.
     * 
     * @param machine The {@link Machine} configuration to test.
     * @return An optional wrapping the {@link ButtonPresses} required to get to
     *         the prize. An empty optional if it is not possible.
     */
    private static Optional<ButtonPresses> canBeReached(Machine machine) {

        var buttonA = machine.buttonA();
        var buttonB = machine.buttonB();
        var prize = machine.prize();

        double aPresses;
        double bPresses;

        // Given the equations:
        // prize.x = buttonA.x * aPresses + buttonB.x * bPresses
        // prize.y = buttonA.y * aPresses + buttonB.y * bPresses

        // Rearrange to:
        bPresses = (double) (buttonA.x * prize.y - buttonA.y * prize.x) / (buttonA.x * buttonB.y - buttonA.y * buttonB.x);
        aPresses = (prize.x - buttonB.x * bPresses) / buttonA.x;

        log.debug("Button A presses: {}, Button B presses: {}", aPresses, bPresses);
        if (aPresses % 1 == 0 && bPresses % 1 == 0) {
            return Optional.of(new ButtonPresses((long) aPresses, (long) bPresses));
        }
        return Optional.empty();
    }



    /**
     * Using the corrected prize coordinates, figure out how to win as many
     * prizes as possible. What is the fewest tokens you would have to spend to
     * win all possible prizes?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        var extra = 10_000_000_000_000L;

        List<Machine> machines = new ArrayList<>();
        Coordinate buttonA = null;
        Coordinate buttonB = null;
        for (var line : lines) {
            if (line.isBlank())
                continue;

            var halves = line.split(":");
            var coordinate = new Coordinate(Integer.parseInt(StringUtils.getDigits(halves[1].split(",")[0])),
                                            Integer.parseInt(StringUtils.getDigits(halves[1].split(",")[1])));
            switch (halves[0]) {
                case "Button A" -> buttonA = coordinate;
                case "Button B" -> buttonB = coordinate;
                case "Prize" -> machines.add(new Machine(buttonA, buttonB,
                                                         new Coordinate(coordinate.x + extra, coordinate.y + extra)));
                default -> throw new IllegalArgumentException(halves[0] + " is not expected.");
            }
        }
        String machineFormat = """
            Button A: X+%d, Y+%d
            Button B: X+%d, Y+%d
            Prize: X=%d, Y=%d

            """;

        return machines.stream()
                       .peek(m -> log.atDebug()
                                     .setMessage("\n{}")
                                     .addArgument(() -> String.format(machineFormat,
                                                                      m.buttonA().x(), m.buttonA().y(),
                                                                      m.buttonB().x(), m.buttonB().y(),
                                                                      m.prize().x(), m.prize().y()))
                                     .log())
                       .map(Day13::canBeReached)
                       .filter(Optional::isPresent)
                       .map(Optional::get)
                       .peek(m -> log.debug("Prize can be reached!"))
                       .mapToLong(b -> b.buttonA() * 3 + b.buttonB() * 1)
                       .sum();
    }



    private record Coordinate(long x, long y) {
    }

    private record Machine(Coordinate buttonA, Coordinate buttonB, Coordinate prize) {
    }

    private record ButtonPresses(long buttonA, long buttonB) {
    }

}