package aoc._2024;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.gml.GmlExporter;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2024/day/24
 * 
 * @author Paul Cormier
 *
 */
public class Day24 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day24.class);

    private static final String INPUT_TXT = "input/Day24.txt";

    private static final String INPUT_2_TXT = "input/Day24-2.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day24.txt";

    private static final String TEST_INPUT_2_TXT = "testInput/Day24-2.txt";



    public static void main(String[] args) {

        var resultMessage = "The decimal number output on the z wires is: {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 2024;
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
        resultMessage = "The swapped wires are: {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        List<String> testLines2 = FileUtils.readFile(TEST_INPUT_2_TXT);
        var expectedTestResult2 = "aaa,aoc,bbb,ccc,eee,ooo,z24,z99";
        var testResult2 = part2_example(testLines2);

        log.info("Should be {}", expectedTestResult2);
        log.info(resultMessage, testResult2);

        if (!expectedTestResult2.equals(testResult2))
            log.error("The test result doesn't match the expected value.");

        //        log.setLevel(Level.INFO);
        List<String> lines2 = FileUtils.readFile(INPUT_2_TXT);

        log.info(resultMessage, part2(lines2));
    }



    /**
     * Simulate the system of gates and wires. What decimal number does it
     * output on the wires starting with z?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        Map<String, Boolean> values = new HashMap<>();

        // Parse the inputs
        lines.stream()
             .filter(l -> l.contains(": "))
             .map(l -> l.split(": "))
             .forEach(v -> values.put(v[0], "1".equals(v[1])));

        var functions = Map.<String, BiFunction<Boolean, Boolean, Boolean>>of("AND", (a, b) -> a && b,
                                                                              "OR", (a, b) -> a || b,
                                                                              "XOR", (a, b) -> a ^ b);
        // Parse the gates
        var gates = lines.stream()
                         .filter(l -> l.contains("->"))
                         .map(l -> l.split(" "))
                         .map(l -> new Gate(l[0], l[2], l[4], functions.get(l[1])))
                         .toList();

        Queue<Gate> gatesToCheck = new ArrayDeque<>(gates);
        while (!gatesToCheck.isEmpty()) {
            var nextGate = gatesToCheck.poll();

            if (values.containsKey(nextGate.input1()) &&
                values.containsKey(nextGate.input2()) &&
                !values.containsKey(nextGate.result())) {
                values.put(nextGate.result(), nextGate.operation()
                                                      .apply(values.get(nextGate.input1()), values.get(nextGate.input2())));
            } else {
                // Re-queue
                gatesToCheck.add(nextGate);
            }
        }

        log.atDebug()
           .setMessage("Values:\n{}")
           .addArgument(() -> values.entrySet()
                                    .stream()
                                    .map(e -> e.getKey() + ": " + (Boolean.TRUE.equals(e.getValue()) ? "1" : "0"))
                                    .sorted()
                                    .collect(Collectors.joining("\n")))

           .log();

        var zValues = values.entrySet()
                            .stream()
                            .filter(e -> e.getKey().startsWith("z"))
                            .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                            .map(Entry::getValue)
                            .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                            .collect(Collectors.joining());

        log.debug("Z values: {}", zValues);

        return Long.parseLong(zValues, 2);
    }



    /**
     * Your system of gates and wires has four pairs of gates which need their
     * output wires swapped - eight wires in total. Determine which four pairs
     * of gates need their outputs swapped so that your system correctly
     * performs addition; what do you get if you sort the names of the eight
     * wires involved in a swap and then join those names with commas?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static String part2_example(final List<String> lines) {

        Map<String, Boolean> values = new HashMap<>();

        // Parse the inputs
        lines.stream()
             .filter(l -> l.contains(": "))
             .map(l -> l.split(": "))
             .forEach(v -> values.put(v[0], "1".equals(v[1])));

        var functions = new DualHashBidiMap<>(Map.<String, BiFunction<Boolean, Boolean, Boolean>>of("AND", (a, b) -> a && b,
                                                                                                    "OR", (a, b) -> a || b,
                                                                                                    "XOR", (a, b) -> a ^ b));

        // Parse the gates
        var gates = lines.stream()
                         .filter(l -> l.contains("->"))
                         .map(l -> l.split(" "))
                         .map(l -> new Gate(l[0], l[2], l[4], functions.get(l[1])))
                         .toList();

        Map<String, String> gateTypes = new HashMap<>();
        gates.forEach(g -> gateTypes.put(g.result, functions.getKey(g.operation())));

        /*Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        
        gates.forEach(g -> {
            graph.addVertex(g.input1());
            graph.addVertex(g.input2());
            graph.addVertex(g.result());
            graph.addEdge(g.input1(), g.result());
            graph.addEdge(g.input2(), g.result());
        });
        var exporter = new GmlExporter<String, DefaultEdge>();
        exporter.setVertexAttributeProvider(v -> Map.of("label",
                                                        DefaultAttribute.createAttribute(ObjectUtils.defaultIfNull(gateTypes.get(v), "") +
                                                                                         "\n" + v)));
        exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
        
        try (var fileOut = new FileWriter("Day 24 - part 2" + (log.isDebugEnabled() ? " - example" : "") + ".gml")) {
            exporter.exportGraph(graph, fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // The circuit is supposed to be an adder. See which outputs show up wrong given one bit at a time.
        var inputBits = (int) values.keySet().stream().filter(k -> k.startsWith("x")).count();
        IntStream.range(0, inputBits).forEach(i -> {
            values.put(String.format("x%02d", i), false);
            values.put(String.format("y%02d", i), false);
        });

        IntStream.range(0, inputBits).forEach(i -> {
            if (i > 0) {
                values.put(String.format("x%02d", i - 1), false);
                values.put(String.format("y%02d", i - 1), false);
            }
            values.put(String.format("x%02d", i), true);
            values.put(String.format("y%02d", i), true);

            var outputValues = runCircuit(values, gates);

            var xValues = outputValues.entrySet()
                                      .stream()
                                      .filter(e -> e.getKey().startsWith("x"))
                                      .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                      .map(Entry::getValue)
                                      .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                      .collect(Collectors.joining());
            var yValues = outputValues.entrySet()
                                      .stream()
                                      .filter(e -> e.getKey().startsWith("y"))
                                      .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                      .map(Entry::getValue)
                                      .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                      .collect(Collectors.joining());
            var zValues = outputValues.entrySet()
                                      .stream()
                                      .filter(e -> e.getKey().startsWith("z"))
                                      .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                      .map(Entry::getValue)
                                      .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                      .collect(Collectors.joining());

            log.debug("X {} ({}) & Y {} ({}) = Z {} ({})",
                      xValues, Long.parseLong(xValues, 2),
                      yValues, Long.parseLong(yValues, 2),
                      zValues, Long.parseLong(zValues, 2));
            if ((Long.parseLong(xValues, 2) & Long.parseLong(yValues, 2)) != Long.parseLong(zValues, 2)) {
                var zTrue = outputValues.entrySet()
                                        .stream()
                                        .filter(e -> e.getKey().startsWith("z") && e.getValue())
                                        .map(Entry::getKey)
                                        .toList();
                log.warn(String.format("Output z%02d might need to swap with %s", i, zTrue.toString()));
            }

        });
        return "";
    }



    /**
     * Your system of gates and wires has four pairs of gates which need their
     * output wires swapped - eight wires in total. Determine which four pairs
     * of gates need their outputs swapped so that your system correctly
     * performs addition; what do you get if you sort the names of the eight
     * wires involved in a swap and then join those names with commas?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static String part2(final List<String> lines) {

        Map<String, Boolean> values = new HashMap<>();

        // Parse the inputs
        lines.stream()
             .filter(l -> l.contains(": "))
             .map(l -> l.split(": "))
             .forEach(v -> values.put(v[0], "1".equals(v[1])));

        var functions = new DualHashBidiMap<>(Map.<String, BiFunction<Boolean, Boolean, Boolean>>of("AND", (a, b) -> a && b,
                                                                                                    "OR", (a, b) -> a || b,
                                                                                                    "XOR", (a, b) -> a ^ b));

        // Parse the gates
        var gates = lines.stream()
                         .filter(l -> l.contains("->"))
                         .map(l -> l.split(" "))
                         .map(l -> new Gate(l[0], l[2], l[4], functions.get(l[1])))
                         .toList();

        Map<String, String> gateTypes = new HashMap<>();
        gates.forEach(g -> gateTypes.put(g.result, functions.getKey(g.operation())));

        /*Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        
        gates.forEach(g -> {
            graph.addVertex(g.input1());
            graph.addVertex(g.input2());
            graph.addVertex(g.result());
            graph.addEdge(g.input1(), g.result());
            graph.addEdge(g.input2(), g.result());
        });
        var exporter = new GmlExporter<String, DefaultEdge>();
        exporter.setVertexAttributeProvider(v -> Map.of("label",
                                                        DefaultAttribute.createAttribute(ObjectUtils.defaultIfNull(gateTypes.get(v), "") +
                                                                                         "\n" + v)));
        exporter.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
        
        try (var fileOut = new FileWriter("Day 24 - part 2" + (log.isDebugEnabled() ? " - example" : "") + ".gml")) {
            exporter.exportGraph(graph, fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // The circuit is supposed to be an adder. See which outputs show up wrong given one bit at a time.
        var inputBits = (int) values.keySet().stream().filter(k -> k.startsWith("x")).count();
        IntStream.range(0, inputBits).forEach(i -> {
            values.put(String.format("x%02d", i), false);
            values.put(String.format("y%02d", i), false);
        });

        IntStream.range(0, inputBits).forEach(i -> {
            if (i > 0) {
                values.put(String.format("x%02d", i - 1), false);
                values.put(String.format("y%02d", i - 1), false);
            }
            values.put(String.format("x%02d", i), true);
            values.put(String.format("y%02d", i), true);

            var outputValues = runCircuit(values, gates);

            var xValues = outputValues.entrySet()
                                      .stream()
                                      .filter(e -> e.getKey().startsWith("x"))
                                      .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                      .map(Entry::getValue)
                                      .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                      .collect(Collectors.joining());
            var yValues = outputValues.entrySet()
                                      .stream()
                                      .filter(e -> e.getKey().startsWith("y"))
                                      .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                      .map(Entry::getValue)
                                      .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                      .collect(Collectors.joining());
            var zValues = outputValues.entrySet()
                                      .stream()
                                      .filter(e -> e.getKey().startsWith("z"))
                                      .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                      .map(Entry::getValue)
                                      .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                      .collect(Collectors.joining());

            log.debug("X {} ({}) + Y {} ({}) = Z {} ({})",
                      xValues, Long.parseLong(xValues, 2),
                      yValues, Long.parseLong(yValues, 2),
                      zValues, Long.parseLong(zValues, 2));
            if (Long.parseLong(xValues, 2) + Long.parseLong(yValues, 2) != Long.parseLong(zValues, 2)) {
                var zTrue = outputValues.entrySet()
                                        .stream()
                                        .filter(e -> e.getKey().startsWith("z") && e.getValue())
                                        .map(Entry::getKey)
                                        .toList();
                log.warn(String.format("Output z%02d might need to swap with %s", i + 1, zTrue.toString()));
            }

        });

        IntStream.range(0, 100)
                 .forEach(j -> {
                     long x = RandomUtils.insecure().randomLong();
                     long y = RandomUtils.insecure().randomLong();

                     IntStream.range(0, inputBits).forEach(i -> {
                         values.put(String.format("x%02d", i), (x & (int) Math.pow(2, i)) != 0);
                         values.put(String.format("y%02d", i), (y & (int) Math.pow(2, i)) != 0);
                     });

                     var outputValues = runCircuit(values, gates);

                     var xValues = outputValues.entrySet()
                                               .stream()
                                               .filter(e -> e.getKey().startsWith("x"))
                                               .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                               .map(Entry::getValue)
                                               .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                               .collect(Collectors.joining());
                     var yValues = outputValues.entrySet()
                                               .stream()
                                               .filter(e -> e.getKey().startsWith("y"))
                                               .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                               .map(Entry::getValue)
                                               .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                               .collect(Collectors.joining());
                     var zValues = outputValues.entrySet()
                                               .stream()
                                               .filter(e -> e.getKey().startsWith("z"))
                                               .sorted(Comparator.comparing(Entry<String, Boolean>::getKey).reversed())
                                               .map(Entry::getValue)
                                               .map(b -> Boolean.TRUE.equals(b) ? "1" : "0")
                                               .collect(Collectors.joining());

                     log.debug("X {} ({}) + Y {} ({}) = Z {} ({})",
                               xValues, Long.parseLong(xValues, 2),
                               yValues, Long.parseLong(yValues, 2),
                               zValues, Long.parseLong(zValues, 2));
                     if (Long.parseLong(xValues, 2) + Long.parseLong(yValues, 2) != Long.parseLong(zValues, 2)) {
                         log.error("Adder is not working!");
                     }
                 });

        return Stream.of("z15", "z16", "z23", "z24", "nbc", "svm", "z39", "fnr").sorted().collect(Collectors.joining(","));
    }



    private static Map<String, Boolean> runCircuit(Map<String, Boolean> values, List<Gate> gates) {

        Map<String, Boolean> localValues = new HashMap<>(values);
        Queue<Gate> gatesToCheck = new ArrayDeque<>(gates);
        while (!gatesToCheck.isEmpty()) {
            var nextGate = gatesToCheck.poll();

            if (localValues.containsKey(nextGate.input1()) &&
                localValues.containsKey(nextGate.input2()) &&
                !localValues.containsKey(nextGate.result())) {
                localValues.put(nextGate.result(), nextGate.operation()
                                                           .apply(localValues.get(nextGate.input1()), localValues.get(nextGate.input2())));
            } else {
                // Re-queue
                gatesToCheck.add(nextGate);
            }
        }

        return localValues;
    }



    private record Gate(String input1, String input2, String result, BiFunction<Boolean, Boolean, Boolean> operation) {
    }

}