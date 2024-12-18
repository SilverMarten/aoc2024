package aoc._2024;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/19
 * 
 * @author Paul Cormier
 *
 */
public class Day19 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day19.class);

    private static final String INPUT_TXT = "input/Day19.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day19.txt";



    public static void main(String[] args) {

        var resultMessage = "{} designs are possible.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 6;
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        //        log.setLevel(Level.INFO);

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
     * To get into the onsen as soon as possible, consult your list of towel
     * patterns and desired designs carefully. How many designs are possible?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        //        Set<String> towels = new TreeSet<>(Comparator.comparing(String::length).thenComparing(Function.identity()).reversed());
        //        Set<String> towels = new HashSet<>();
        //        towels.addAll(Set.of(lines.getFirst().split(", ")));
        Map<String, Boolean> towels = new HashMap<>();
        Stream.of(lines.getFirst().split(", ")).forEach(t -> towels.put(t, true));

        List<String> patterns = lines.subList(2, lines.size());

        return patterns.stream().filter(p -> canBeMade(p, towels)).count();
    }



    private static boolean canBeMade(String pattern, Map<String, Boolean> towels) {

        List<TowelPattern> toCheck = new LinkedList<>();
        toCheck.add(new TowelPattern(pattern, new ArrayList<>()));

        AtomicInteger maxTowelLength = new AtomicInteger(towels.keySet().stream().mapToInt(String::length).max().orElseThrow());
        //        List<String> newTowels = new ArrayList<>();
        while (!toCheck.isEmpty()) {
            var checking = toCheck.removeLast();

            // If the whole pattern can be made, return true
            if (checking.pattern().isEmpty() || towels.getOrDefault(checking.pattern(), false)) {
                log.atDebug()
                   .setMessage("Pattern {} can be made.")
                   .addArgument(() -> checking.pattern() + checking.towels().stream().collect(Collectors.joining()))
                   .log();

                return true;
            }

            if (!towels.getOrDefault(checking.pattern(), true)) {
                // The pattern is known to be impossible
                return false;
            }

            // Find the sub sequences which match towels
            IntStream.rangeClosed(1, Math.min(maxTowelLength.get(), checking.pattern().length()))
                     .mapToObj(i -> checking.pattern().substring(0, i))
                     .map(t -> {
                         var nextTowels = new ArrayList<>(checking.towels());
                         nextTowels.add(t);
                         //                         newTowels.add(nextTowels.stream().collect(Collectors.joining()));
                         var validPattern = nextTowels.stream().collect(Collectors.joining());
                         maxTowelLength.set(Math.max(maxTowelLength.get(), validPattern.length()));
                         towels.put(validPattern, true);
                         return new TowelPattern(checking.pattern().substring(t.length()), nextTowels);
                     })
                     .forEach(toCheck::add);
            // Add any new combinations
            //            maxTowelLength = Math.max(maxTowelLength, newTowels.stream().mapToInt(String::length).max().orElse(0));
            //            newTowels.clear();
        }

        return false;
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



    private record TowelPattern(String pattern, List<String> towels) {
    }
}