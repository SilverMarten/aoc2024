package aoc._2024;

import static aoc.Direction.DOWN;
import static aoc.Direction.LEFT;
import static aoc.Direction.RIGHT;
import static aoc.Direction.UP;
import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.Direction;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/21
 * 
 * @author Paul Cormier
 *
 */
public class Day21 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day21.class);

    private static final String INPUT_TXT = "input/Day21.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day21.txt";



    public static void main(String[] args) {

        var resultMessage = "The sum of the complexities of the codes is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 126_384;
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info(resultMessage, part1(lines)); // Lower than 296200

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
     * Find the fewest number of button presses you'll need to perform in order
     * to cause the robot in front of the door to type each code. What is the
     * sum of the complexities of the five codes on your list?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        return lines.stream()
                    .mapToLong(Day21::computeComplexity)
                    .sum();
    }



    private static long computeComplexity(String code) {

        List<Character> moves = new ArrayList<>();
        var numericPad = new NumericKeypad();
        var directionalPad1 = new DirectionalKeypad();
        var directionalPad2 = new DirectionalKeypad();

        log.debug("Code: {}", code);

        code.chars().forEach(c -> {
            log.debug("Numberic pad: {}\tDirectional pad 1: {}\tDirectional pad 2: {}",
                      numericPad.getCurrentChar(), directionalPad1.getCurrentChar(), directionalPad2.getCurrentChar());

            // Get the moves for the numeric pad
            var numericPadPath = numericPad.moveToButton((char) c, ' ')
                                           .stream()
                                           .map(Direction::getSymbol)
                                           .collect(Collectors.toCollection(ArrayList::new));
            // Press the button
            numericPadPath.add('A');

            log.debug("Numeric pad move to the {}: {}", (char) c, numericPadPath);

            // Get the moves for the first directional pad
            var directionalPad1Path = numericPadPath.stream()
                                                    .map(d -> directionalPad1.moveToButton(d, numericPad.getCurrentChar()))
                                                    .flatMap(l -> {
                                                        var chars = l.stream()
                                                                     .map(Direction::getSymbol)
                                                                     .collect(Collectors.toCollection(ArrayList::new));
                                                        // Press the button
                                                        chars.add('A');
                                                        return chars.stream();
                                                    })
                                                    .toList();

            log.debug("Directional pad 1 move to the {}: {}", (char) c, directionalPad1Path);

            // Get the moves for the second directional pad
            var directionalPad2Path = directionalPad1Path.stream()
                                                         .map(d -> directionalPad2.moveToButton(d, directionalPad1.getCurrentChar()))
                                                         .flatMap(l -> {
                                                             var chars = l.stream()
                                                                          .map(Direction::getSymbol)
                                                                          .collect(Collectors.toCollection(ArrayList::new));
                                                             // Press the button
                                                             chars.add('A');
                                                             return chars.stream();
                                                         })
                                                         .toList();

            log.debug("Directional pad 2 move to the {}: {}", (char) c, directionalPad2Path);

            moves.addAll(directionalPad2Path);
        });

        var numericCode = Long.parseLong(StringUtils.getDigits(code));
        var numberOfMoves = moves.size();
        log.atDebug()
           .setMessage("{} ({} * {}): {}")
           .addArgument(code)
           .addArgument(numberOfMoves)
           .addArgument(numericCode)
           .addArgument(() -> moves.stream().map(Object::toString).collect(Collectors.joining()))
           .log();

        return numericCode * numberOfMoves;
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



    /**
     * An abstract representation of a keypad. It is initialized to point to the
     * 'A' key.
     */
    private abstract static class Keypad {

        protected BidiMap<Coordinate, Character> buttons;

        protected Coordinate currentButton;



        /**
         * Initialize the {@link Keypad} with the given buttons. The current
         * button will point to the 'A' key.
         * 
         * @param buttons The map of button locations and characters.
         */
        protected Keypad(Map<Coordinate, Character> buttons) {
            this.buttons = new DualHashBidiMap<>(buttons);
            currentButton = this.buttons.getKey('A');
        }



        /**
         * Find the shortest path from the current button to the button with the
         * target character.
         * 
         * @param character The target character.
         * @return The list of directions to the target button.
         */
        public abstract List<Direction> moveToButton(char character, char optimalStartChar);



        public Coordinate getCurrentButton() {
            return this.currentButton;
        }



        public char getCurrentChar() {
            return this.buttons.get(this.currentButton);
        }
    }

    private static class NumericKeypad extends Keypad {

        public NumericKeypad() {
            super(Map.ofEntries(entry(Coordinate.of(1, 1), '7'), entry(Coordinate.of(1, 2), '8'), entry(Coordinate.of(1, 3), '9'),
                                entry(Coordinate.of(2, 1), '4'), entry(Coordinate.of(2, 2), '5'), entry(Coordinate.of(2, 3), '6'),
                                entry(Coordinate.of(3, 1), '1'), entry(Coordinate.of(3, 2), '2'), entry(Coordinate.of(3, 3), '3'),
                                entry(Coordinate.of(4, 2), '0'), entry(Coordinate.of(4, 3), 'A')));
        }



        @Override
        public List<Direction> moveToButton(char character, char optimalStartChar) {
            var targetLocation = this.buttons.getKey(character);

            int distanceUp = this.currentButton.getRow() - targetLocation.getRow();
            int distanceLeft = this.currentButton.getColumn() - targetLocation.getColumn();

            List<Direction> directions = new ArrayList<>();
            if (distanceUp > 0) {
                if (distanceLeft > 0) {
                    IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
                } else {
                    IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
                    IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                }

            } else {
                if (distanceLeft > 0) {
                    IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                } else {
                    IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                    IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                }
            }

            this.currentButton = targetLocation;

            return directions;
        }

    }

    private static class DirectionalKeypad extends Keypad {

        public DirectionalKeypad() {
            super(Map.of(Coordinate.of(1, 2), UP.getSymbol(), Coordinate.of(1, 3), 'A',
                         Coordinate.of(2, 1), LEFT.getSymbol(), Coordinate.of(2, 2), DOWN.getSymbol(), Coordinate.of(2, 3),
                         RIGHT.getSymbol()));
        }



        @Override
        public List<Direction> moveToButton(char character, char optimalStartChar) {
            Direction optimalStartDirection = Direction.withSymbol(optimalStartChar);
            Coordinate targetLocation = this.buttons.getKey(character);
            char targetChar = buttons.get(targetLocation);
            char currentChar = buttons.get(this.currentButton);

            int distanceUp = this.currentButton.getRow() - targetLocation.getRow();
            int distanceLeft = this.currentButton.getColumn() - targetLocation.getColumn();

            List<Direction> directions = new ArrayList<>();

            // Avoid the empty space when going to/from the '<' button
            if (targetChar == '<') {
                // Go down before left
                IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
            } else if (currentChar == '<') {
                // Go right before up
                IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
            } else {

                if (distanceUp > 0) {
                    if (distanceLeft > 0) {
                        IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
                        IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    } else {
                        IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                        IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
                    }

                } else {
                    if (distanceLeft > 0) {
                        IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                        IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                    } else {
                        IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                        IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                    }
                }
                /*
                // If the optimalStartDirection is a direction that is needed, start with it
                
                if (distanceUp > 0) {
                    //                    if (optimalStartDirection == UP) {
                    //                        IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
                    //
                    //                        if (distanceLeft > 0)
                    //                            IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    //                        else
                    //                            IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                    //                    } else {
                    if (distanceLeft > 0)
                        IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    else
                        IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                
                    IntStream.range(0, distanceUp).forEach(i -> directions.add(UP));
                    //                    }
                } else {
                    //                    if (optimalStartDirection == DOWN) {
                    IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                
                    if (distanceLeft > 0)
                        IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    else
                        IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                    //                    } else {
                    //                        if (distanceLeft > 0)
                    //                            IntStream.range(0, distanceLeft).forEach(i -> directions.add(LEFT));
                    //                        else
                    //                            IntStream.range(0, -distanceLeft).forEach(i -> directions.add(RIGHT));
                    //
                    //                        IntStream.range(0, -distanceUp).forEach(i -> directions.add(DOWN));
                    //                    }
                }
                */
            }

            directions.stream().forEach(d -> {
                this.currentButton = this.currentButton.translate(d, 1);
                if (!buttons.containsKey(this.currentButton))
                    throw new IllegalStateException(String.format("The move from %s to %s crossed over a blank space!",
                                                                  currentChar, character));
            });

            return directions;
        }
    }

}