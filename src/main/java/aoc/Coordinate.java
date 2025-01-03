package aoc;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A coordinate of row and column.
 */
public final class Coordinate implements Comparable<Coordinate> {

    private final int row;

    private final int column;

    private final int hashCode;



    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
        this.hashCode = computeHashCode();
    }



    /**
     * Create a new coordinate by translating this one by the row and column
     * values in the given parameter.
     * 
     * @param by The {@link Coordinate} containing the row and column values to
     *            translate this instance by.
     * @return A new {@link Coordinate} instance representing the translation of
     *         this instance by the given parameters.
     */
    public Coordinate translate(Coordinate by) {
        return Coordinate.of(getRow() + by.getRow(), getColumn() + by.getColumn());
    }



    /**
     * Create a new coordinate by translating this one in the given direction
     * and distance.
     * 
     * @param direction The {@link Direction} in which to translate this
     *            instance.
     * @param distance The distance by which to translate this instance.
     * @return A new {@link Coordinate} instance representing the translation of
     *         this instance by the given parameters.
     */
    public Coordinate translate(Direction direction, int distance) {
        return Coordinate.of(getRow() + direction.getTranslation().getRow() * distance,
                             getColumn() + direction.getTranslation().getColumn() * distance);
    }



    /**
     * Compute the Manhattan distance between this {@link Coordinate} and the
     * other given {@link Coordinate}.
     * 
     * @param other The other coordinate to use to compute the distance.
     * @return The Manhattan distance (the sum of the difference between the
     *         rows and columns) to the other point.
     */
    public int distanceTo(Coordinate other) {
        return Math.abs(this.row - other.row) + Math.abs(this.column - other.column);
    }



    public int getRow() {
        return row;
    }



    public int getColumn() {
        return column;
    }



    public static Coordinate of(int row, int column) {
        return new Coordinate(row, column);
    }



    /**
     * @return The set of adjacent coordinates to this coordinate.
     */
    public Set<Coordinate> findAdjacent() {
        return IntStream.rangeClosed(-1, 1)
                        .mapToObj(x -> IntStream.rangeClosed(-1, 1)
                                                .filter(y -> !(x == 0 && y == 0))
                                                .mapToObj(y -> Coordinate.of(this.row + y, this.column + x)))
                        .flatMap(Function.identity())
                        .collect(Collectors.toSet());
    }



    /**
     * @return The set of orthogonally adjacent coordinates to this coordinate.
     */
    public Set<Coordinate> findOrthogonalAdjacent() {
        return IntStream.rangeClosed(-1, 1)
                        .mapToObj(x -> IntStream.rangeClosed(-1, 1)
                                                .filter(y -> x == 0 ^ y == 0)
                                                .mapToObj(y -> Coordinate.of(this.row + y, this.column + x)))
                        .flatMap(Function.identity())
                        .collect(Collectors.toSet());
    }



    /**
     * Return the pre-generated hashCode for this Coordinate.
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }



    /**
     * Compute the hashCode for this Coordinate so it can be cached by the
     * constructor.
     * 
     * @return the computed hashCode for this Coordinate.
     */
    private int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (column != other.column)
            return false;
        if (row != other.row)
            return false;
        return true;
    }



    @Override
    public String toString() {
        return String.format("(%s, %s)", row, column);
    }



    @Override
    public int compareTo(Coordinate o) {
        int result = Integer.compare(this.row, o.row);

        return result == 0 ? Integer.compare(this.column, o.column) : result;
    }



    /**
     * Create a map of the coordinates of digits.
     * 
     * @param lines The lines to find and map the locations of digits.
     * @return A map of coordinates to the digit found at those coordinates.
     */
    public static Map<Coordinate, Integer> mapDigits(List<String> lines) {

        AtomicInteger row = new AtomicInteger(1);

        Map<Coordinate, Integer> coordinates = new HashMap<>();
        lines.stream().forEachOrdered(line -> {
            IntStream.range(0, line.length())
                     .forEach(i -> {
                         char digit = line.charAt(i);
                         if (Character.isDigit(digit))
                             coordinates.put(new Coordinate(row.get(), i + 1), digit - '0');
                     });

            row.getAndIncrement();
        });

        return coordinates;
    }



    /**
     * Create a map of the coordinates of non-blank characters. The default
     * blank character is a period '.'.
     * 
     * @param lines The lines to find and map the locations of non-blank
     *            characters.
     * @return A map of coordinates to the character found at those coordinates.
     *         The coordinate range starts at 1 and increases.
     */
    public static Map<Coordinate, Character> mapCoordinates(List<String> lines) {
        return mapCoordinates(lines, '.');
    }



    /**
     * Create a map of the coordinates of non-blank characters.
     * 
     * @param lines The lines to find and map the locations of non-blank
     *            characters.
     * @param blankSpace The character to be treated as a blank space.
     * @return A map of coordinates to the character found at those coordinates.
     *         The coordinate range starts at 1 and increases.
     */
    public static Map<Coordinate, Character> mapCoordinates(List<String> lines, char blankSpace) {

        AtomicInteger row = new AtomicInteger(1);

        Map<Coordinate, Character> coordinates = new HashMap<>();
        lines.stream().forEachOrdered(line -> {
            BitSet chars = ArrayUtils.indexesOf(line.toCharArray(), blankSpace);
            // Use the length of the line, since the length of the BitSet is only the last
            // set bit, which might not be the end of the line.
            chars.flip(0, line.length());
            chars.stream()
                 .forEach(c -> coordinates.put(Coordinate.of(row.get(), c + 1), line.charAt(c)));

            row.getAndIncrement();
        });

        return coordinates;
    }



    /**
     * Map a list of strings into a set of coordinates of the locations of # in
     * the strings.
     * 
     * @param lines The lines to find and map the locations of #s.
     * @return The set of coordinates of the locations of #s. The coordinate
     *         range starts at 1 and increases.
     */
    public static Set<Coordinate> findCoordinates(List<String> lines) {
        return findCoordinates(lines, '#');
    }



    /**
     * Map a list of strings into a set of coordinates of the locations of a
     * given character in the strings.
     * 
     * @param lines The lines to find and map the locations of the given
     *            character.
     * @param charToFind The character to find in the strings and return the
     *            coordinates of.
     * @return The set of coordinates of the locations of the given character.
     *         The coordinate range starts at 1 and increases.
     */
    public static Set<Coordinate> findCoordinates(List<String> lines, char charToFind) {
        AtomicInteger row = new AtomicInteger(1);

        Set<Coordinate> coordinates = new HashSet<>();
        for (String line : lines) {
            coordinates.addAll(ArrayUtils.indexesOf(line.toCharArray(), charToFind)
                                         .stream()
                                         .mapToObj(c -> new Coordinate(row.get(), c + 1))
                                         .collect(Collectors.toSet()));
            row.getAndIncrement();
        }

        return coordinates;
    }



    /**
     * Create a printout of the digit map, using '.' for empty spaces.
     * 
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param coordinates The map of coordinates to display the corresponding
     *            digit. The coordinate range is expected to start at 1 and
     *            increase.
     * 
     * @return A string representation of the map.
     */
    public static String printDigitMap(int rows, int columns, Map<Coordinate, Integer> coordinates) {

        int location = columns;

        StringBuilder printout = new StringBuilder(rows * columns + rows);

        while (location < (rows + 1) * columns) {
            printout.append((char) (coordinates.getOrDefault(new Coordinate(location / columns, location % columns + 1),
                                                             '.' - '0') +
                                    '0'));

            if (location % columns == columns - 1)
                printout.append('\n');

            location++;
        }

        return printout.toString();
    }



    /**
     * Create a printout of the map, translating the value to a character with
     * the given function, using '.' for empty spaces.
     * 
     * @param <V> The type of the value in the given map.
     * 
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param coordinates The map of coordinates to display the corresponding
     *            character. The coordinate range is expected to start at 1 and
     *            increase.
     * @param The function to map the given value type to a character.
     * 
     * @return A string representation of the map.
     */
    public static <V> String printMap(int rows, int columns, Map<Coordinate, V> coordinates,
                                      Function<V, Character> characterMapper) {
        return printMap(1, 1, rows, columns, coordinates, characterMapper);
    }



    /**
     * Create a printout of the map, translating the value to a character with
     * the given function, using '.' for empty spaces.
     * 
     * @param <V> The type of the value in the given map.
     * 
     * @param minRow The lowest numbered row in the map.
     * @param minColumn The lowest numbered column in the map.
     * @param maxRow The highest numbered row in the map.
     * @param maxColumn The highest numbered column in the map.
     * @param coordinates The map of coordinates to display the corresponding
     *            character. The coordinate range is expected to start at
     *            {@code minRow}/{@code minColumn} and increase.
     * @param The function to map the given value type to a character.
     * 
     * @return A string representation of the map.
     */
    public static <V> String printMap(int minRow, int minColumn, int maxRow, int maxColumn,
                                      Map<Coordinate, V> coordinates,
                                      Function<V, Character> characterMapper) {
        return printMap(minRow, minColumn, maxRow, maxColumn, coordinates, characterMapper, '.');
    }



    /**
     * Create a printout of the map, translating the value to a character with
     * the given function, using '.' for empty spaces.
     * 
     * @param <V> The type of the value in the given map.
     * 
     * @param minRow The lowest numbered row in the map.
     * @param minColumn The lowest numbered column in the map.
     * @param maxRow The highest numbered row in the map.
     * @param maxColumn The highest numbered column in the map.
     * @param coordinates The map of coordinates to display the corresponding
     *            character. The coordinate range is expected to start at
     *            {@code minRow}/{@code minColumn} and increase.
     * @param The function to map the given value type to a character.
     * 
     * @return A string representation of the map.
     */
    public static <V> String printMap(int minRow, int minColumn, int maxRow, int maxColumn,
                                      Map<Coordinate, V> coordinates,
                                      Function<V, Character> characterMapper,
                                      char defaultCharater) {

        StringBuilder printout = new StringBuilder();

        IntStream.rangeClosed(minRow, maxRow).forEach(r -> {
            IntStream.rangeClosed(minColumn, maxColumn).forEach(c -> {
                Coordinate location = Coordinate.of(r, c);
                Optional<V> charToPrint = Optional.ofNullable(coordinates.get(location));
                printout.append(charToPrint.map(characterMapper).orElse(defaultCharater));
            });
            printout.append('\n');
        });

        return printout.toString();
    }



    /**
     * Create a printout of the map, using '.' for empty spaces.
     * 
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param coordinates The map of coordinates to display the corresponding
     *            character. The coordinate range is expected to start at 1 and
     *            increase.
     * 
     * @return A string representation of the map.
     */
    public static String printMap(int rows, int columns, Map<Coordinate, Character> coordinates) {

        int location = columns;

        StringBuilder printout = new StringBuilder(rows * columns + rows);

        while (location < (rows + 1) * columns) {
            printout.append(coordinates.getOrDefault(new Coordinate(location / columns, location % columns + 1), '.'));

            if (location % columns == columns - 1)
                printout.append('\n');

            location++;
        }

        return printout.toString();
    }



    /**
     * Create a printout of the map, using '#' as the marker and '.' for empty
     * spaces.
     * 
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param coordinates The set of coordinates to display. The coordinate
     *            range is expected to start at 1 and increase.
     * 
     * @return A string representation of the map.
     */
    public static String printMap(int rows, int columns, Set<Coordinate> coordinates) {
        return printMap(rows, columns, coordinates, '#');
    }



    /**
     * Create a printout of the map, using '#' as the marker and '.' for empty
     * spaces.
     * 
     * @param minRow The lowest numbered row in the map.
     * @param minColumn The lowest numbered column in the map.
     * @param maxRow The highest numbered row in the map.
     * @param maxColumn The highest numbered column in the map.
     * @param coordinates The set of coordinates to display. The coordinate
     *            range is expected to start at {@code minRow}/{@code minColumn}
     *            and increase.
     * 
     * @return A string representation of the map.
     */
    public static String printMap(int minRow, int minColumn, int maxRow, int maxColumn, Set<Coordinate> coordinates) {
        return printMap(minRow, minColumn, maxRow, maxColumn, coordinates, '#');
    }



    /**
     * Create a printout of the map.
     * 
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param coordinates The set of coordinates to display. The coordinate
     *            range is expected to start at 1 and increase.
     * @param presentMarker The character to print at the given coordinates.
     * 
     * @return A string representation of the map.
     */
    public static String printMap(int rows, int columns, Set<Coordinate> coordinates, char presentMarker) {
        return printMap(1, 1, rows, columns, coordinates, presentMarker);
    }



    /**
     * Create a printout of the map.
     * 
     * @param minRow The lowest numbered row in the map.
     * @param minColumn The lowest numbered column in the map.
     * @param maxRow The highest numbered row in the map.
     * @param maxColumn The highest numbered column in the map.
     * @param coordinates The set of coordinates to display. The coordinate
     *            range is expected to start at {@code minRow}/{@code minColumn}
     *            and increase.
     * @param presentMarker The character to print at the given coordinates.
     * 
     * @return A string representation of the map.
     */
    public static String printMap(int minRow, int minColumn, int maxRow, int maxColumn,
                                  Set<Coordinate> coordinates, char presentMarker) {

        StringBuilder printout = new StringBuilder();

        IntStream.rangeClosed(minRow, maxRow).forEach(r -> {
            IntStream.rangeClosed(minColumn, maxColumn).forEach(c -> {
                Coordinate location = Coordinate.of(r, c);
                printout.append(coordinates.contains(location) ? presentMarker : ".");
            });
            printout.append('\n');
        });

        return printout.toString();
    }



    /**
     * Create a printout of the map.
     * 
     * @param rows The number of rows in the map.
     * @param columns The number of columns in the map.
     * @param firstCoordinates The first set of coordinates to display. The
     *            coordinate range is expected to start at 1 and increase.
     * @param firstMarker The character to print at the given coordinates in the
     *            first set.
     * @param secondCoordinates The second set of coordinates to display. The
     *            coordinate range is expected to start at 1 and increase.
     * @param secondMarker The character to print at the given coordinates in
     *            the second set.
     * @return A string representation of the map.
     */
    public static String printMap(int rows, int columns,
                                  Set<Coordinate> firstCoordinates, char firstMarker,
                                  Set<Coordinate> secondCoordinates, char secondMarker) {

        int location = columns;

        StringBuilder printout = new StringBuilder(rows * columns + rows);

        while (location < (rows + 1) * columns) {
            Coordinate coordinate = new Coordinate(location / columns, location % columns + 1);
            char marker = '.';
            if (firstCoordinates.contains(coordinate))
                marker = firstMarker;
            else if (secondCoordinates.contains(coordinate))
                marker = secondMarker;
            printout.append(marker);

            if (location % columns == columns - 1)
                printout.append('\n');

            location++;
        }

        return printout.toString();
    }



    /**
     * Create a printout of the map.
     * 
     * @param minRow The lowest numbered row in the map.
     * @param minColumn The lowest numbered column in the map.
     * @param maxRow The highest numbered row in the map.
     * @param maxColumn The highest numbered column in the map.
     * @param firstCoordinates The first set of coordinates to display. The
     *            coordinate range is expected to start at 1 and increase.
     * @param firstMarker The character to print at the given coordinates in the
     *            first set.
     * @param secondCoordinates The second set of coordinates to display. The
     *            coordinate range is expected to start at 1 and increase.
     * @param secondMarker The character to print at the given coordinates in
     *            the second set.
     * @return A string representation of the map.
     */
    public static String printMap(int minRow, int minColumn, int maxRow, int maxColumn,
                                  Set<Coordinate> firstCoordinates, char firstMarker,
                                  Set<Coordinate> secondCoordinates, char secondMarker) {
        var map = Stream.of(firstCoordinates.stream().map(c -> Pair.of(c, firstMarker)),
                            secondCoordinates.stream().map(c -> Pair.of(c, secondMarker)))
                        .flatMap(Function.identity())
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return Coordinate.printMap(minRow, minColumn, maxRow, maxColumn, map, Function.identity());
    }



    /**
     * Find the distinct combinations of pairs of {@link Coordinate}s in the
     * given collection.
     * 
     * @param values The collection of values to pair up.
     * @return The set of pairs of combinations of the given coordinates.
     * 
     * @see https://stackoverflow.com/questions/59354514/generalized-way-to-generate-combinations-in-java-8
     */
    public static Set<CoordinatePair> combinations(Collection<Coordinate> values) {
        return values.stream()
                     .flatMap(v -> values.stream()
                                         .filter(v2 -> v != v2)
                                         .map(v2 -> new CoordinatePair(v, v2)))
                     .collect(Collectors.toSet());
    }



    public record CoordinatePair(Coordinate c1, Coordinate c2) {
    }
}