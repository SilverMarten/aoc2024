package aoc;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Direction {

    // The order of the enumerations is representative of their order going clockwise.
    RIGHT('>', 'R', Coordinate.of(0, 1)),
    RIGHT_DOWN('\u2198', 'C', Coordinate.of(1, 1)),
    DOWN('v', 'D', Coordinate.of(1, 0)),
    DOWN_LEFT('\u2199', 'Z', Coordinate.of(1, -1)),
    LEFT('<', 'L', Coordinate.of(0, -1)),
    LEFT_UP('\u2196', 'Q', Coordinate.of(-1, -1)),
    UP('^', 'U', Coordinate.of(-1, 0)),
    UP_RIGHT('\u2197', 'E', Coordinate.of(-1, 1)),
    ;



    private final char symbol;

    private final char letter;

    private final Coordinate translation;

    private static final Map<Character, Direction> symbolMap = Collections.unmodifiableMap(Stream.of(Direction.values())
                                                                                                 .collect(Collectors.toMap(d -> d.symbol,
                                                                                                                           d -> d)));

    private static final Map<Character, Direction> letterMap = Collections.unmodifiableMap(Stream.of(Direction.values())
                                                                                                 .collect(Collectors.toMap(d -> d.letter,
                                                                                                                           d -> d)));

    public static final EnumSet<Direction> ORTHOGONAL_DIRECTIONS = EnumSet.of(UP, DOWN, LEFT, RIGHT);



    private Direction(char symbol, char letter, Coordinate translation) {
        this.symbol = symbol;
        this.letter = letter;
        this.translation = translation;
    }



    public static Direction withSymbol(char symbol) {
        return symbolMap.get(symbol);
    }



    public static Direction withLetter(char letter) {
        return letterMap.get(letter);
    }



    public static Direction oppositeOf(Direction direction) {
        return switch (direction) {
            case UP -> DOWN;
            case UP_RIGHT -> DOWN_LEFT;
            case RIGHT -> LEFT;
            case RIGHT_DOWN -> LEFT_UP;
            case DOWN -> UP;
            case DOWN_LEFT -> UP_RIGHT;
            case LEFT -> RIGHT;
            case LEFT_UP -> RIGHT_DOWN;

            default -> throw new IllegalArgumentException();
        };
    }



    /**
     * @return The opposite to the current direction. Up becomes down, down left
     *         becomes up right, etc.
     */
    public Direction opposite() {
        return oppositeOf(this);
    }



    /**
     * @return The next direction to the right (clockwise).
     */
    public Direction rotateRight() {
        return Direction.values()[(this.ordinal() + 1) % Direction.values().length];
    }



    /**
     * @param degrees The number of degrees to rotate.
     * @return The next direction to the right (clockwise), based on the number
     *         of degrees.
     */
    public Direction rotateRight(int degrees) {
        var steps = (degrees / 360.) * Direction.values().length;
        return Direction.values()[(this.ordinal() + (int) steps) % Direction.values().length];
    }



    /**
     * @return The next direction to the left (anti-clockwise).
     */
    public Direction rotateLeft() {
        return Direction.values()[(this.ordinal() + 9) % Direction.values().length];
    }



    /**
     * @param degrees The number of degrees to rotate.
     * @return The next direction to the left (anti-clockwise), based on the
     *         number of degrees.
     */
    public Direction rotateLeft(int degrees) {
        var steps = (degrees / 360.) * Direction.values().length;
        return Direction.values()[(this.ordinal() + 10 - (int) steps) % Direction.values().length];
    }



    public char getSymbol() {
        return symbol;
    }



    public char getLetter() {
        return letter;
    }



    public Coordinate getTranslation() {
        return translation;
    }

}
