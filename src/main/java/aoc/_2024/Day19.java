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

        log.info(resultMessage, part1(lines)); // Greater than 272, not 291, 390

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
            var checkingPattern = checking.pattern();

            // The pattern is known
            if (towels.containsKey(checkingPattern)) {
                //                continue;

                if (Boolean.FALSE.equals(towels.get(checkingPattern))) {
                    //                    towels.put(checking.towels().getLast() + checkingPattern, false);
                    //                    return false;
                    //                    continue;
                } else {
                    checking.towels.add(checkingPattern);
                    /*IntStream.rangeClosed(2, checking.towels().size())
                             .forEach(i -> {
                                 var tempTowels = checking.towels();
                                 var tempPattern = tempTowels.subList(tempTowels.size() - i, tempTowels.size())
                                                             .stream()
                                                             .collect(Collectors.joining());
                                 //  checking.towels()
                                 //                        .reversed()
                                 //                        .stream()
                                 //                        .limit(i)
                                 //                        .sorted(Comparator.reverseOrder())
                                 //                        .collect(Collectors.joining());
                                 towels.put(tempPattern, true);
                             });*/
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
                    //                    towels.put(validPattern, true);
                    return true;
                }
            }

            // If the whole pattern can be made, return true
            /*  if (checkingPattern.isEmpty()) {
                checking.towels.add(checkingPattern);
                IntStream.rangeClosed(1, checking.towels().size())
                         .forEach(i -> {
                             var tempPattern = checking.towels().stream().limit(i).collect(Collectors.joining());
                             towels.put(tempPattern, true);
                         });
                var validPattern = checking.towels().stream().collect(Collectors.joining());
                log.atDebug()
                   .setMessage("Pattern {} can be made.")
                   .addArgument(validPattern)
                   .log();
                if (!validPattern.equals(pattern))
                    log.error("The \"valid pattern\" {} doesn't match the given pattern {}.",
                              validPattern, pattern);
            
                maxTowelLength.set(Math.max(maxTowelLength.get(), validPattern.length()));
                towels.put(validPattern, true);
                return true;
            }*/

            // Find the sub sequences which match towels
            var nextToCheck = IntStream.rangeClosed(1, Math.min(maxTowelLength.get(), checkingPattern.length()))
                                       .mapToObj(i -> checkingPattern.substring(0, i))
//                                       .filter(towels::containsKey)
                                       .filter(t->towels.getOrDefault(t, false))
                                       .map(t -> {
                                           var nextTowels = new ArrayList<>(checking.towels());
                                           nextTowels.add(t);
                                           //                         newTowels.add(nextTowels.stream().collect(Collectors.joining()));
                                           //                         var validPattern = nextTowels.stream().collect(Collectors.joining());
                                           //                         maxTowelLength.set(Math.max(maxTowelLength.get(), validPattern.length()));
                                           return new TowelPattern(checkingPattern.substring(t.length()), nextTowels);
                                       })
                                       .filter(t -> towels.getOrDefault(t.pattern(), true))
                                       .toList();
            // Add any new combinations
            //            maxTowelLength = Math.max(maxTowelLength, newTowels.stream().mapToInt(String::length).max().orElse(0));
            //            newTowels.clear();

            if (nextToCheck.isEmpty()) {
                towels.put(checkingPattern, false);
                log.debug("{} doesn't work", checking);
            } else {
                toCheck.add(checking);
                toCheck.addAll(nextToCheck);
                //                var validPattern = checking.towels().stream().collect(Collectors.joining());
                //                maxTowelLength.set(Math.max(maxTowelLength.get(), validPattern.length()));
                //                towels.put(validPattern, true);
            }
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