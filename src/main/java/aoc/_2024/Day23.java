package aoc._2024;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/23
 * 
 * @author Paul Cormier
 *
 */
public class Day23 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day23.class);

    private static final String INPUT_TXT = "input/Day23.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day23.txt";



    public static void main(String[] args) {

        var resultMessage = "There are {} computer groups containing at least one computer who's name starts with 't'.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 7;
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
        resultMessage = "The password is: {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        var expectedTestResult2 = "co,de,ka,ta";
        var testResult2 = part2(testLines);

        log.info("Should be {}", expectedTestResult2);
        log.info(resultMessage, testResult2);

        if (!expectedTestResult2.equals(testResult2))
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        var password = part2(lines);
        log.info(resultMessage, password);
    }



    /**
     * Find all the sets of three inter-connected computers. How many contain at
     * least one computer with a name that starts with t?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {
        Map<String, Computer> computers = new HashMap<>();

        lines.stream()
             .map(l -> l.split("-"))
             .forEach(c -> {
                 var c1 = computers.computeIfAbsent(c[0], Computer::new);
                 var c2 = computers.computeIfAbsent(c[1], Computer::new);
                 c1.addNeighbour(c2);
             });

        var groups = computers.values()
                              .stream()
                              .flatMap(c1 -> c1.neighbours()
                                               .stream()
                                               .flatMap(c2 -> c2.neighbours()
                                                                .stream()
                                                                .filter(c3 -> c3.neighbours().contains(c1))
                                                                .map(c3 -> Set.of(c1, c2, c3))))
                              .collect(Collectors.toSet());

        log.atDebug()
           .setMessage("Groups:\n{}")
           .addArgument(() -> groups.stream().map(l -> l.stream()
                                                        .map(Computer::name)
                                                        .collect(Collectors.joining(",")))
                                    .collect(Collectors.joining("\n")))
           .log();

        return groups.stream()
                     .filter(l -> l.stream().map(Computer::name).anyMatch(n -> n.startsWith("t")))
                     .count();
    }



    /**
     * What is the password to get into the LAN party?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static String part2(final List<String> lines) {
        Map<String, Computer> computers = new HashMap<>();

        lines.stream()
             .map(l -> l.split("-"))
             .forEach(c -> {
                 var c1 = computers.computeIfAbsent(c[0], Computer::new);
                 var c2 = computers.computeIfAbsent(c[1], Computer::new);
                 c1.addNeighbour(c2);
             });

        log.atDebug()
           .setMessage("Computers:\n{}")
           .addArgument(() -> computers.values()
                                       .stream()
                                       .sorted(Comparator.comparing(Computer::name))
                                       .map(Computer::toString)
                                       .collect(Collectors.joining("\n")))
           .log();

        return "";
    }



    private record Computer(String name, Set<Computer> neighbours) {

        public Computer(String name) {
            this(name, new HashSet<>());
        }



        public void addNeighbour(Computer neighbour) {
            this.neighbours.add(neighbour);
            neighbour.neighbours.add(this);
        }



        @Override
        public final int hashCode() {
            return Objects.hashCode(name);
        }



        @Override
        public final String toString() {
            return String.format("%s (%s)", name, neighbours.stream().map(Computer::name).collect(Collectors.joining(", ")));
        }
    }
}