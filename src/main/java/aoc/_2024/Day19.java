package aoc._2024;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        resultMessage = "There are {} ways the towels in this example could be arranged into the desired designs.";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 16;
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

        Map<String, Boolean> towels = new HashMap<>();
        Stream.of(lines.getFirst().split(", ")).forEach(t -> towels.put(t, true));

        List<String> patterns = lines.subList(2, lines.size());

        return patterns.stream().filter(p -> canBeMade(p, towels)).count();
    }



    private static boolean canBeMade(String pattern, Map<String, Boolean> towels) {

        List<TowelPattern> toCheck = new LinkedList<>();
        toCheck.add(new TowelPattern(pattern, new ArrayList<>()));

        AtomicInteger maxTowelLength = new AtomicInteger(towels.keySet().stream().mapToInt(String::length).max().orElseThrow());
        while (!toCheck.isEmpty()) {
            var checking = toCheck.removeLast();
            var checkingPattern = checking.pattern();

            // The pattern is known
            if (Boolean.TRUE.equals(towels.getOrDefault(checkingPattern, false))) {
                checking.towels.add(checkingPattern);
                IntStream.rangeClosed(2, checking.towels().size())
                         .forEach(i -> {
                             var tempTowels = checking.towels();
                             var tempPattern = tempTowels.subList(tempTowels.size() - i, tempTowels.size())
                                                         .stream()
                                                         .collect(Collectors.joining());
                             towels.put(tempPattern, true);
                         });
                var validPattern = checking.towels().stream().collect(Collectors.joining());
                log.atDebug()
                   .setMessage("Pattern {} can be made. {}")
                   .addArgument(validPattern)
                   .addArgument(checking)
                   .log();
                if (!validPattern.equals(pattern))
                    log.error("The \"valid pattern\" {} doesn't match the given pattern {}.",
                              validPattern, pattern);

                maxTowelLength.set(Math.max(maxTowelLength.get(), validPattern.length()));
                towels.put(validPattern, true);
                return true;
            }

            // Find the sub sequences which match towels
            var nextToCheck = IntStream.rangeClosed(1, Math.min(maxTowelLength.get(), checkingPattern.length()))
                                       .mapToObj(i -> checkingPattern.substring(0, i))
                                       //                                       .filter(towels::containsKey)
                                       .filter(t -> towels.getOrDefault(t, false))
                                       .map(t -> {
                                           var nextTowels = new ArrayList<>(checking.towels());
                                           nextTowels.add(t);
                                           return new TowelPattern(checkingPattern.substring(t.length()), nextTowels);
                                       })
                                       .filter(t -> towels.getOrDefault(t.pattern(), true))
                                       .toList();

            if (nextToCheck.isEmpty()) {
                towels.put(checkingPattern, false);
                log.trace("{} doesn't work", checking);
            } else {
                toCheck.add(checking);
                toCheck.addAll(nextToCheck);
            }
        }

        return false;
    }



    private static long numberOfCombinations(String pattern, Map<String, Boolean> towels) {

        Map<String, Long> numberOfCombinations = new HashMap<>();
//                towels.keySet().stream().forEach(t -> numberOfCombinations.put(t, 1L));

        List<TowelPattern> toCheck = new LinkedList<>();
        toCheck.add(new TowelPattern(pattern, new ArrayList<>()));

        AtomicInteger maxTowelLength = new AtomicInteger(towels.keySet().stream().mapToInt(String::length).max().orElseThrow());
        while (!toCheck.isEmpty()) {
            var checking = toCheck.removeLast();
            var checkingPattern = checking.pattern();

            // The pattern is known
            /*
            if (towels.containsKey(checkingPattern)) {
                checking.towels.add(checkingPattern);
                //                IntStream.rangeClosed(2, checking.towels().size())
                //                         .forEach(i -> {
                //                             var tempTowels = checking.towels();
                //                             var tempPattern = tempTowels.subList(tempTowels.size() - i, tempTowels.size())
                //                                                         .stream()
                //                                                         .collect(Collectors.joining());
                //                             towels.put(tempPattern, true);
                //                         });
                var validPattern = checking.towels().stream().collect(Collectors.joining());
                log.atDebug()
                   .setMessage("Pattern {} can be made. {}")
                   .addArgument(validPattern)
                   .addArgument(checking)
                   .log();
                if (!validPattern.equals(pattern))
                    log.error("The \"valid pattern\" {} doesn't match the given pattern {}.",
                              validPattern, pattern);
            
                maxTowelLength.set(Math.max(maxTowelLength.get(), validPattern.length()));
                towels.put(validPattern, true);
                continue;
            }*/

            if (checkingPattern.isEmpty()) {
                numberOfCombinations.putIfAbsent(checking.towels().getLast(), 1L);
                continue;
            }

            // Find the sub sequences which match towels
            var nextToCheck = IntStream.rangeClosed(1, Math.min(maxTowelLength.get(), checkingPattern.length()))
                                       .mapToObj(i -> checkingPattern.substring(0, i))
                                       //                                       .filter(t -> towels.getOrDefault(t, false))
                                       .filter(towels::containsKey)
                                       .map(t -> {
                                           var nextTowels = new ArrayList<>(checking.towels());
                                           nextTowels.add(t);
                                           return new TowelPattern(checkingPattern.substring(t.length()), nextTowels);
                                       })
                                       //                                       .filter(t -> towels.getOrDefault(t.pattern(), true))
                                       .toList();

            if (nextToCheck.isEmpty()) {
                //                towels.put(checkingPattern, false);
                numberOfCombinations.put(checkingPattern, 0L);
                log.trace("{} doesn't work", checking);
            } else if (nextToCheck.stream().map(TowelPattern::pattern).allMatch(numberOfCombinations::containsKey)) {
                var sum = nextToCheck.stream().map(TowelPattern::pattern).mapToLong(numberOfCombinations::get).sum();
                numberOfCombinations.put(checkingPattern, sum);
                log.debug("{} can be made {} ways: {}", pattern, sum, nextToCheck);
            } else if (nextToCheck.stream().map(TowelPattern::pattern).allMatch(String::isEmpty)) {
                numberOfCombinations.put(checkingPattern, 1L);
            } else {
                toCheck.add(checking);
                toCheck.removeIf(t -> numberOfCombinations.containsKey(t.pattern()));
                toCheck.addAll(nextToCheck);
            }
        }

        var combinations = numberOfCombinations.getOrDefault(pattern, 0L);
        log.debug("{} can be made {} different ways.", pattern, combinations);
        return combinations;
    }



    /**
     * They'll let you into the onsen as soon as you have the list. What do you
     * get if you add up the number of different ways you could make each
     * design?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        Map<String, Boolean> towels = new HashMap<>();
        Stream.of(lines.getFirst().split(", ")).forEach(t -> towels.put(t, true));

        List<String> patterns = lines.subList(2, lines.size());

        return patterns.stream().map(p -> numberOfCombinations(p, towels)).mapToLong(Long::longValue).sum();
    }



    private record TowelPattern(String pattern, List<String> towels) {
    }
}