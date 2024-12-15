package aoc._2024;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Direction;
import aoc.FileUtils;
import aoc.MovableObject;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/15
 * 
 * @author Paul Cormier
 *
 */
public class Day15 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day15.class);

    private static final String INPUT_TXT = "input/Day15.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day15.txt";



    public static void main(String[] args) {

        var resultMessage = "The sum of all boxes' GPS coordinates is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var testMapLines = testLines.stream()
                                    .filter(l -> l.startsWith("#"))
                                    .toList();
        var testMap = Coordinate.mapCoordinates(testMapLines);
        var testRows = testMapLines.size();
        var testColumns = testMapLines.getFirst().length();
        var testInstructions = testLines.stream()
                                        .filter(l -> !l.startsWith("#"))
                                        .collect(Collectors.joining());

        var expectedTestResult = 10_092;
        var testResult = part1(testMap, testRows, testColumns, testInstructions);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var mapLines = lines.stream()
                            .filter(l -> l.startsWith("#"))
                            .toList();
        var map = Coordinate.mapCoordinates(mapLines);
        var rows = mapLines.size();
        var columns = mapLines.getFirst().length();
        var instructions = lines.stream()
                                .filter(l -> !l.startsWith("#"))
                                .collect(Collectors.joining());

        log.info(resultMessage, part1(map, rows, columns, instructions));

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
     * Predict the motion of the robot and boxes in the warehouse. After the
     * robot is finished moving, what is the sum of all boxes' GPS coordinates?
     * 
     * @param map The map read from the input.
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param instructions The instructions to follow.
     * 
     * @return The value calculated for part 1.
     */
    private static long part1(Map<Coordinate, Character> map, int rows, int columns, String instructions) {

        log.atDebug()
           .setMessage("Initial state:\n{}")
           .addArgument(Coordinate.printMap(rows, columns, map))
           .log();

        // Find the robot
        var robot = new MovableObject(Coordinate.of(0, 0), '@');

        Map<Coordinate, MovableObject> warehouse = new HashMap<>();

        // Map the rest of the objects
        map.entrySet()
           .forEach(e -> {
               switch (e.getValue()) {
                   case '@' -> {
                       robot.setPosition(e.getKey());
                       warehouse.put(e.getKey(), robot);
                   }
                   case '#' -> warehouse.put(e.getKey(), MovableObject.immovableObject(e.getKey(), '#'));
                   case 'O' -> warehouse.put(e.getKey(), new MovableObject(e.getKey(), 'O'));
                   default -> log.error("Unexpected entry: {}", e);
               }
           });

        // Follow the instructions and move the robot
        instructions.chars()
                    .mapToObj(c -> (char) c)
                    .map(Direction::withSymbol)
                    .filter(Objects::nonNull)
                    .forEach(d -> {
                        warehouse.remove(robot.getPosition());
                        if (robot.canMove(d, warehouse)) {
                            robot.move(d);
                            var neighbour = warehouse.remove(robot.getPosition());
                            warehouse.put(robot.getPosition(), robot);
                            // Move the neighbours
                            while (neighbour != null) {
                                neighbour.move(d);
                                var nextNeighbour = warehouse.remove(neighbour.getPosition());
                                warehouse.put(neighbour.getPosition(), neighbour);
                                neighbour = nextNeighbour;
                            }
                        }
                    });

        // Final state
        log.atDebug()
           .setMessage("Final state:\n{}")
           .addArgument(Coordinate.printMap(rows, columns, warehouse, MovableObject::getCharacter))
           .log();

        return warehouse.entrySet()
                        .stream()
                        .filter(e -> e.getValue().getCharacter() == 'O')
                        .mapToInt(e -> (e.getKey().getRow() - 1) * 100 + (e.getKey().getColumn() - 1))
                        .sum();
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }

}