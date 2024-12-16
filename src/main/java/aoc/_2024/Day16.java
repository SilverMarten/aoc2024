package aoc._2024;

import static aoc.Direction.ORTHOGONAL_DIRECTIONS;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Direction;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/16
 * 
 * @author Paul Cormier
 *
 */
public class Day16 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day16.class);

    private static final String INPUT_TXT = "input/Day16.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day16.txt";

    private static final String TEST_INPUT_2_TXT = "testInput/Day16-2.txt";



    public static void main(String[] args) {

        var resultMessage = "The lowest score is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 7036;
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        // Read the test file
        List<String> testLines2 = FileUtils.readFile(TEST_INPUT_2_TXT);

        var expectedTestResult2 = 11048;
        var testResult2 = part1(testLines2);

        log.info("Should be {}", expectedTestResult2);
        log.info(resultMessage, testResult2);

        if (testResult2 != expectedTestResult2)
            log.error("The second test result doesn't match the expected value.");

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
     * Analyze your map carefully. What is the lowest score a Reindeer could
     * possibly get?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {
        var map = Coordinate.mapCoordinates(lines);
        var rows = lines.size();
        var columns = lines.getFirst().length();

        log.atDebug()
           .setMessage("Map:\n{}")
           .addArgument(() -> Coordinate.printMap(rows, columns, map))
           .log();

        // Find start and end
        var start = map.entrySet().stream().filter(e -> e.getValue() == 'S').map(Entry::getKey).findAny().orElseThrow();
        map.remove(start);
        var end = map.entrySet().stream().filter(e -> e.getValue() == 'E').map(Entry::getKey).findAny().orElseThrow();
        map.remove(end);

        //        Set<Coordinate> visited = new HashSet<>();
        //        visited.add(start);
        Queue<Path> pathsToCheck = new ArrayDeque<>();
        pathsToCheck.add(new Path(Arrays.asList(new Step(start, Direction.RIGHT)), Set.of(start)));

        List<Path> successfulPaths = new ArrayList<>();
        long lowestScore = Long.MAX_VALUE;

        Map<Step, Long> scores = new HashMap<>();

        int pathsToCheckLog = 0;
        while (!pathsToCheck.isEmpty()) {
            if (Math.log(pathsToCheck.size()) > pathsToCheckLog) {
                log.debug("Paths to check: {}", pathsToCheck.size());
                pathsToCheckLog = (int) Math.ceil(Math.log(pathsToCheck.size()));
            }

            var path = pathsToCheck.poll();
            var score = scorePath(path.steps());
            if (score >= lowestScore)
                continue;

            var lastStep = path.steps().getLast();
            if (scores.merge(lastStep, score, Math::min) < score)
                continue;

            // Is it at the end?
            if (lastStep.position().equals(end)) {
                successfulPaths.add(path);
                lowestScore = score;
                log.info("New low score: {}", score);
            }

            // Can it continue?
            ORTHOGONAL_DIRECTIONS.stream()
                                 .map(d -> new Step(lastStep.position().translate(d, 1), d))
                                 .filter(s -> !(s.direction().equals(lastStep.direction().opposite()) ||
                                                path.positions().contains(s.position()) ||
                                                map.containsKey(s.position())))
                                 .forEach(s -> {
                                     var newSteps = new ArrayList<Step>(path.steps());
                                     newSteps.add(s);
                                     var newPositions = new HashSet<>(path.positions());
                                     newPositions.add(s.position());
                                     pathsToCheck.add(new Path(newSteps, newPositions));
                                 });
        }

        if (log.isDebugEnabled()) {
            successfulPaths.stream()
                           .map(Path::steps)
                           .forEach(steps -> {
                               var tempMap = new HashMap<>(map);
                               steps.forEach(s -> tempMap.put(s.position(), s.direction().getSymbol()));
                               log.debug("Path ({}):\n{}", scorePath(steps), Coordinate.printMap(rows, columns, tempMap));
                           });
        }
        return lowestScore;
    }



    /**
     * Compute the score for the given path.
     * 
     * @param steps The steps in the path.
     * @return The score for this path.
     */
    private static long scorePath(List<Step> steps) {

        AtomicInteger turns = new AtomicInteger(0);
        AtomicReference<Direction> lastDirection = new AtomicReference<>(steps.getFirst().direction());
        steps.forEach(s -> {
            if (!lastDirection.get().equals(s.direction())) {
                lastDirection.set(s.direction());
                turns.incrementAndGet();
            }
        });

        return turns.get() * 1000 + steps.size() - 1L;
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



    private record Step(Coordinate position, Direction direction) {
    }

    private record Path(List<Step> steps, Set<Coordinate> positions) {
    }

}