package aoc._2024;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/9
 * 
 * @author Paul Cormier
 *
 */
public class Day9 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day9.class);

    private static final String INPUT_TXT = "input/Day9.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day9.txt";

    public static void main(String[] args) {

        var resultMessage = "The filesystem checksum is {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 1928;
        var testResult = part1(testLines.getFirst());

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info(resultMessage, part1(lines.getFirst()));

        // PART 2
        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 2858;
        testResult = part2(testLines.getFirst());

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines.getFirst()));
    }

    /**
     * Compact the amphipod's hard drive using the process he requested. What is
     * the resulting filesystem checksum?
     * 
     * @param line The line read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final String line) {

        final AtomicInteger index = new AtomicInteger(0);
        final List<Integer> memory = new ArrayList<>();
        final List<Integer> freeSpaces = new ArrayList<>();

        // Sort the input into free spaces and memory contents
        line.chars()
            .map(c -> c - '0')
            .forEach(m -> {
                if (index.get() % 2 == 0) {
                    IntStream.range(0, m).forEach(x -> memory.add(index.get() / 2));
                } else {
                    IntStream.range(0, m).forEach(x -> freeSpaces.add(memory.size() + freeSpaces.size()));
                }
                index.incrementAndGet();
            });

        log.debug("Memory: {}", memory);
        log.debug("Free spaces: {}", freeSpaces);

        // Fill the free spaces with the end of the memory content
        index.set(0);
        freeSpaces.stream()
                  .filter(i -> i <= memory.size())
                  .forEach(i -> memory.add(i, memory.removeLast()));

        log.debug("Defragmented memory: {}", memory);

        index.set(0);
        return memory.stream().mapToLong(m -> m * index.getAndIncrement()).sum();

    }

    /**
     * Start over, now compacting the amphipod's hard drive using this new
     * method instead. What is the resulting filesystem checksum?
     * 
     * @param line The line read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final String line) {

        final AtomicInteger inputIndex = new AtomicInteger(0);
        final AtomicInteger memoryIndex = new AtomicInteger(0);

        // Sort the input into free spaces and memory contents
        final List<File> memory = line.chars()
                                      .map(c -> c - '0')
                                      .mapToObj(m -> new File(inputIndex.get() / 2,
                                                              m,
                                                              inputIndex.getAndIncrement() % 2 == 1))
                                      .toList();

        var memoryCopy = new LinkedList<>(memory);
        Supplier<Object> memoryToString = () -> memoryCopy.stream()
                                                          .flatMap(File::bytes)
                                                          .map(b -> b == null ? "." : "" + (char) (b.intValue() + '0'))
                                                          .collect(Collectors.joining());
        log.atDebug()
           .setMessage("Starting memory: {}")
           .addArgument(memoryToString)
           .log();

        // Fill the free spaces with the end of the memory content
        memory.reversed()
              .stream()
              .filter(Predicate.not(File::isEmpty))
              .forEach(file -> {
                  // Find the first empty space big enough, up to the index of the file
                  var fileIndex = memoryCopy.indexOf(file);
                  memoryCopy.subList(0, fileIndex)
                            .stream()
                            .filter(e -> e.isEmpty() && e.size() >= file.size())
                            .findFirst()
                            .ifPresent(empty -> {
                                // Swap it for the file
                                var emptyIndex = memoryCopy.indexOf(empty);
                                // Add the empty space
                                memoryCopy.add(fileIndex, new File(file.id(), file.size(), true));
                                // Remove the file
                                memoryCopy.remove(fileIndex + 1);
                                // Move the file to the old empty space 
                                memoryCopy.add(emptyIndex, new File(file.id(), file.size(), false));
                                // remove the old empty space
                                memoryCopy.remove(emptyIndex + 1);
                                // Pad the empty space if needed
                                if (empty.size() > file.size())
                                    memoryCopy.add(emptyIndex + 1,
                                                   new File(0, empty.size() - file.size(), true));
                            });

                  log.atDebug()
                     .setMessage("Defragmenting memory: {}")
                     .addArgument(memoryToString)
                     .log();
              });

        log.atDebug()
           .setMessage("Defragmented memory: {}")
           .addArgument(memoryToString)
           .log();

        memoryIndex.set(0);
        return memoryCopy.stream()
                         .flatMap(File::bytes)
                         .mapToLong(m -> ObjectUtils.defaultIfNull(m, 0) * memoryIndex.getAndIncrement())
                         .sum();
    }

    private record File(int id, int size, boolean isEmpty) {

        public Stream<Integer> bytes() {
            return IntStream.range(0, size).mapToObj(i -> isEmpty ? null : id);
        }
    }
}