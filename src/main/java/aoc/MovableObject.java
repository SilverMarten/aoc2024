package aoc;

import java.util.Map;

/**
 * Model an object, at a given position, which can be moved and can check the
 * position map to determine if all the movable objects in that direction can be
 * moved.
 */
public class MovableObject {

    /** The position of this object. */
    private Coordinate position;

    /** How to display this object. */
    private char character;



    /**
     * Create a {@link MovableObject} that can be moved.
     * 
     * @param position The position of this {@link MovableObject}.
     * @param character The character to display for this object.
     */
    public MovableObject(Coordinate position, char character) {
        this.position = position;
        this.character = character;
    }



    public Coordinate getPosition() {
        return position;
    }



    public void setPosition(Coordinate position) {
        this.position = position;
    }



    public char getCharacter() {
        return character;
    }



    public void setCharacter(char character) {
        this.character = character;
    }



    /**
     * Move this object in the direction given.
     * 
     * @param direction The direction in which to move.
     */
    public void move(Direction direction) {
        this.position = this.position.translate(direction, 1);
    }



    /**
     * Check the map for {@link MovableObject}s in the given direction, and
     * whether they can all be moved.
     * 
     * @param direction The {@link Direction} in which to move.
     * @param map The map of {@link MovableObject}s.
     * @return {@code true} if this object is free to move in the given
     *         direction.
     */
    public boolean canMove(Direction direction, Map<Coordinate, MovableObject> map) {
        MovableObject neighbour = map.get(this.position.translate(direction, 1));
        if (neighbour != null) {
            return neighbour.canMove(direction, map);
        }
        return true;
    }



    /**
     * Create a {@link MovableObject} that, in fact, cannot be moved.
     * 
     * @param position The position of this {@link MovableObject}.
     * @param character The character to display for this object.
     * @return A {@link MovableObject} that cannot be moved.
     */
    public static MovableObject immovableObject(Coordinate position, char character) {
        return new MovableObject(position, character) {

            @Override
            public void move(Direction direction) {
                // Does not move
            }



            @Override
            public boolean canMove(Direction direction, Map<Coordinate, MovableObject> map) {
                // Cannot move
                return false;
            }
        };
    }

}
