package aoc._2024;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/22
 * 
 * @author Paul Cormier
 *
 */
public class Day22 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day22.class);

    private static final String INPUT_TXT = "input/Day22.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day22.txt";



    public static void main(String[] args) {

        var resultMessage = "The sum of the 2000th secret number generated by each buyer is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 37_327_623L;
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
     * For each buyer, simulate the creation of 2000 new secret numbers. What is
     * the sum of the 2000th secret number generated by each buyer?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        return lines.stream()
                    .mapToLong(Long::parseLong)
                    .map(s -> {
                        AtomicLong secret = new AtomicLong(s);
                        IntStream.rangeClosed(1, 2000)
                                 .forEach(i -> secret.set(nextSecret(secret.get())));

                        return secret.get();
                    })
                    .sum();
    }



    /**
     * In particular, each buyer's secret number evolves into the next secret
     * number in the sequence via the following process:
     * <ul>
     * <li>Calculate the result of multiplying the secret number by 64. Then,
     * mix this result into the secret number. Finally, prune the secret
     * number.</li>
     * <li>Calculate the result of dividing the secret number by 32. Round the
     * result down to the nearest integer. Then, mix this result into the secret
     * number. Finally, prune the secret number.</li>
     * <li>Calculate the result of multiplying the secret number by 2048. Then,
     * mix this result into the secret number. Finally, prune the secret
     * number.</li>
     * </ul>
     * Each step of the above process involves mixing and pruning:
     * 
     * <ul>
     * <li>To mix a value into the secret number, calculate the bitwise XOR of
     * the given value and the secret number. Then, the secret number becomes
     * the result of that operation. (If the secret number is 42 and you were to
     * mix 15 into the secret number, the secret number would become 37.)</li>
     * <li>To prune the secret number, calculate the value of the secret number
     * modulo 16777216. Then, the secret number becomes the result of that
     * operation. (If the secret number is 100000000 and you were to prune the
     * secret number, the secret number would become 16113920.)</li>
     * </ul>
     * 
     * @param secret
     * @return
     */
    private static long nextSecret(long secret) {

        var result = prune(mix(secret, secret * 64));

        result = prune(mix(result, result / 32));

        result = prune(mix(result, result * 2048));

        return result;
    }



    /**
     * To mix a value into the secret number, calculate the bitwise XOR of the
     * given value and the secret number. Then, the secret number becomes the
     * result of that operation. (If the secret number is 42 and you were to mix
     * 15 into the secret number, the secret number would become 37.)
     * 
     * @param secret The secret into which to mix the value.
     * @param value The value to mix into the secret.
     * @return The mixed value.
     */
    private static long mix(long secret, long value) {
        return secret ^ value;
    }



    /**
     * To prune the secret number, calculate the value of the secret number
     * modulo 16777216. Then, the secret number becomes the result of that
     * operation. (If the secret number is 100000000 and you were to prune the
     * secret number, the secret number would become 16113920.)
     * 
     * @param secret The secret to prune.
     * @return The pruned secret.
     */
    private static long prune(long secret) {
        return secret % 16_777_216;
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