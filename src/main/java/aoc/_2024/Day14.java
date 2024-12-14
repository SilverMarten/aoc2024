package aoc._2024;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Range;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/14
 * 
 * @author Paul Cormier
 *
 */
public class Day14 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day14.class);

    private static final String INPUT_TXT = "input/Day14.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day14.txt";



    public static void main(String[] args) {

        var resultMessage = "The safety factor is {} after exactly 100 seconds have elapsed.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);
        var testRows = 7;
        var testColumns = 11;

        var expectedTestResult = 12;
        var testResult = part1(testLines, testRows, testColumns);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        var rows = 103;
        var columns = 101;

        log.info(resultMessage, part1(lines, rows, columns));

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
     * Predict the motion of the robots in your list within a space which is
     * {@code rows} tiles wide and {@code columns} tiles tall. What will the
     * safety factor be after exactly 100 seconds have elapsed?
     * 
     * @param lines The lines read from the input.
     * @param rows The number of rows in the space.
     * @param columns The number of columns in the space.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines, int rows, int columns) {

        List<Robot> robots = lines.stream()
                                  .map(Robot::new)
                                  .toList();

        log.atDebug()
           .setMessage("Robots:\n{}")
           .addArgument(() -> robots.stream().map(Robot::toString).collect(Collectors.joining("\n")))
           .log();

        // Run the robots 100 times
        robots.forEach(r -> {
            var position = r.getPosition();
            var velocity = r.getVelocity();
            position = position.translate(Coordinate.of(velocity.getRow() * 100, velocity.getColumn() * 100));
            position = Coordinate.of((position.getRow() % rows + rows) % rows,
                                     (position.getColumn() % columns + columns) % columns);
            r.setPosition(position);
        });

        // Determine the number of robots in each quadrant
        var positionMap = robots.stream()
                                .collect(Collectors.groupingBy(Robot::getPosition,
                                                               Collectors.counting()));

        log.atDebug()
           .setMessage("Robot positions:{}\n{}")
           .addArgument(positionMap)
           .addArgument(() -> Coordinate.printMap(0, 0, rows - 1, columns - 1, positionMap,
                                                  i -> i < 10 ? i.toString().charAt(0) : 'X'))
           .log();

        var topRange = Range.of(0, rows / 2 - 1);
        var bottomRange = Range.of(rows / 2 + 1, rows);
        var leftRange = Range.of(0, columns / 2 - 1);
        var rightRange = Range.of(columns / 2 + 1, columns);

        var quadrantCounts = robots.stream()
                                   .collect(Collectors.groupingBy(r -> {
                                       var position = r.getPosition();
                                       if (topRange.contains(position.getRow())) {
                                           if (leftRange.contains(position.getColumn()))
                                               return 1;
                                           if (rightRange.contains(position.getColumn()))
                                               return 2;
                                       }
                                       if (bottomRange.contains(position.getRow())) {
                                           if (leftRange.contains(position.getColumn()))
                                               return 3;
                                           if (rightRange.contains(position.getColumn()))
                                               return 4;
                                       }
                                       return 0;
                                   }, Collectors.counting()));

        log.atDebug()
           .setMessage("The quadrants contain {} robots.")
           .addArgument(() -> IntStream.rangeClosed(1, 4)
                                       .mapToObj(i -> quadrantCounts.getOrDefault(i, 0L).toString())
                                       .collect(Collectors.joining(", ")))
           .log();

        return IntStream.rangeClosed(1, 4)
                        .mapToLong(i -> quadrantCounts.getOrDefault(i, 0L))
                        .reduce(1L, Math::multiplyExact);
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
     * Representation of a Robot having position and velocity.
     */
    private static final class Robot {

        private Coordinate position;

        private Coordinate velocity;



        public Robot(String definition) {
            var positionAndVelocity = definition.split("[ =]");
            var positionArray = positionAndVelocity[1].split(",");
            var velocityArray = positionAndVelocity[3].split(",");

            this.position = new Coordinate(Integer.parseInt(positionArray[1]), Integer.parseInt(positionArray[0]));
            this.velocity = new Coordinate(Integer.parseInt(velocityArray[1]), Integer.parseInt(velocityArray[0]));
        }



        public Coordinate getPosition() {
            return position;
        }



        public void setPosition(Coordinate position) {
            this.position = position;
        }



        public Coordinate getVelocity() {
            return velocity;
        }



        public void setVelocity(Coordinate velocity) {
            this.velocity = velocity;
        }



        @Override
        public String toString() {
            return String.format("p=%s v=%s", this.position, this.velocity);
        }
    }

}