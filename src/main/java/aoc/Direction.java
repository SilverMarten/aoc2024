package aoc;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Direction {

    RIGHT('>', 'R', Coordinate.of(0, 1)),
    RIGHT_DOWN('↘', 'C', Coordinate.of(1, 1)),
    DOWN('v', 'D', Coordinate.of(1, 0)),
    DOWN_LEFT('↙', 'Z', Coordinate.of(1, -1)),
    LEFT('<', 'L', Coordinate.of(0, -1)),
    LEFT_UP('↖', 'Q', Coordinate.of(-1, -1)),
    UP('^', 'U', Coordinate.of(-1, 0)),
    UP_RIGHT('↗', 'E', Coordinate.of(-1, 1)),
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



    public Direction opposite() {
        return oppositeOf(this);
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
