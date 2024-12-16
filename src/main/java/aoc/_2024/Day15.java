package aoc._2024;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
        log.setLevel(Level.TRACE);

        expectedTestResult = 9021;
        testResult = part2(testMap, testRows, testColumns, testInstructions);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(map, rows, columns, instructions));
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
     * Predict the motion of the robot and boxes in this new, scaled-up
     * warehouse. What is the sum of all boxes' final GPS coordinates?
     * 
     * @param map The map read from the input.
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param instructions The instructions to follow.
     * @return The value calculated for part 2.
     */
    private static long part2(Map<Coordinate, Character> map, int rows, int columns, String instructions) {

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
               var translatedLocation = e.getKey().translate(Direction.RIGHT, e.getKey().getColumn() - 1);
               switch (e.getValue()) {
                   case '@' -> {
                       robot.setPosition(translatedLocation);
                       warehouse.put(translatedLocation, robot);
                   }
                   case '#' -> {
                       warehouse.put(translatedLocation,
                                     MovableObject.immovableObject(translatedLocation, '#'));
                       warehouse.put(translatedLocation.translate(Direction.RIGHT, 1),
                                     MovableObject.immovableObject(translatedLocation.translate(Direction.RIGHT, 1), '#'));
                   }
                   case 'O' -> {
                       var leftSide = new WideBox(translatedLocation, '[');
                       var rightSide = new WideBox(translatedLocation.translate(Direction.RIGHT, 1), ']');
                       leftSide.pair(rightSide);
                       warehouse.put(translatedLocation, leftSide);
                       warehouse.put(translatedLocation.translate(Direction.RIGHT, 1), rightSide);
                   }
                   default -> log.error("Unexpected entry: {}", e);
               }
           });

        log.atDebug()
           .setMessage("Wide state:\n{}")
           .addArgument(Coordinate.printMap(rows, columns * 2, warehouse, MovableObject::getCharacter))
           .log();

        AtomicInteger counter = new AtomicInteger();

        // Follow the instructions and move the robot
        instructions.chars()
                    .mapToObj(c -> (char) c)
                    .map(Direction::withSymbol)
                    .filter(Objects::nonNull)
                    .forEach(d -> {
                        counter.incrementAndGet();
                        warehouse.remove(robot.getPosition());
                        if (robot.canMove(d, warehouse)) {
                            robot.move(d);
                            var neighbour = warehouse.remove(robot.getPosition());
                            if (d == Direction.LEFT || d == Direction.RIGHT) {
                                // Move the neighbours
                                while (neighbour != null) {
                                    neighbour.move(d);
                                    var nextNeighbour = warehouse.remove(neighbour.getPosition());
                                    warehouse.put(neighbour.getPosition(), neighbour);
                                    neighbour = nextNeighbour;
                                }
                            } else {
                                Queue<MovableObject> needsToMove = new ArrayDeque<>();
                                Set<MovableObject> hasMoved = new HashSet<>();
                                if (neighbour != null) {
                                    needsToMove.add(neighbour);
                                    var otherNeighbour = ((WideBox) neighbour).getOtherSide();
                                    needsToMove.add(otherNeighbour);
                                    warehouse.remove(otherNeighbour.getPosition());
                                    while (!needsToMove.isEmpty()) {
                                        var toMove = needsToMove.poll();
                                        if (!hasMoved.contains(toMove)) {
                                            toMove.move(d);
                                            var nextToMove = warehouse.remove(toMove.getPosition());
                                            warehouse.put(toMove.getPosition(), toMove);

                                            if (!(nextToMove == null || hasMoved.contains(nextToMove) || needsToMove.contains(nextToMove)))
                                                needsToMove.add(nextToMove);

                                            var otherToMove = ((WideBox) toMove).getOtherSide();
                                            if (!(otherToMove == null || hasMoved.contains(otherToMove) ||
                                                  needsToMove.contains(otherToMove))) {
                                                needsToMove.add(otherToMove);
                                                // Make sure to remove it from the warehouse map
                                                warehouse.remove(otherToMove.getPosition());
                                            }

                                            hasMoved.add(toMove);
                                        }
                                    }
                                }
                            }
                        }
                        warehouse.put(robot.getPosition(), robot);
                    });

        // Final state
        log.atDebug()
           .setMessage("Final state:\n{}")
           .addArgument(Coordinate.printMap(rows, columns * 2, warehouse, MovableObject::getCharacter))
           .log();

        return warehouse.entrySet()
                        .stream()
                        .filter(e -> e.getValue().getCharacter() == '[')
                        .mapToInt(e -> (e.getKey().getRow() - 1) * 100 + (e.getKey().getColumn() - 1))
                        .sum();
    }



    private static class WideBox extends MovableObject {

        private WideBox otherSide;



        public WideBox(Coordinate position, char character) {
            super(position, character);
        }



        public void pair(WideBox otherSide) {
            this.otherSide = otherSide;
            otherSide.setOtherSide(this);
        }



        public WideBox getOtherSide() {
            return otherSide;
        }



        public void setOtherSide(WideBox otherSide) {
            this.otherSide = otherSide;
        }



        @Override
        public boolean canMove(Direction direction, Map<Coordinate, MovableObject> map) {
            // Both sides must be able to move up or down
            if (direction == Direction.UP || direction == Direction.DOWN) {
                var neighbour = map.get(this.getPosition().translate(direction, 1));
                var otherNeighbour = map.get(this.getOtherSide().getPosition().translate(direction, 1));
                log.trace("{} and {} can move {} if {} and {} can move.",
                          this.getPosition(), this.getOtherSide().getPosition(), direction,
                          Optional.ofNullable(neighbour).map(MovableObject::getPosition).orElse(null),
                          Optional.ofNullable(otherNeighbour).map(MovableObject::getPosition).orElse(null));

                var neighbourClear = Optional.ofNullable(neighbour).map(n -> n.canMove(direction, map)).orElse(true);
                var otherNeighbourClear = Optional.ofNullable(otherNeighbour).map(n -> n.canMove(direction, map)).orElse(true);

                return neighbourClear && otherNeighbourClear;
            } else {
                MovableObject neighbour = map.get(this.getPosition().translate(direction, 1));
                if (neighbour != null) {
                    log.trace("{} can move {} if {} can move.", this.getPosition(), direction, neighbour.getPosition());
                    return neighbour.canMove(direction, map);
                }
                return true;
            }
        }



        @Override
        public String toString() {
            return String.format("%s at %s", this.getCharacter(), this.getPosition());
        }

    }
}