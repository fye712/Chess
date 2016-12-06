package me.franklinye.chess.game;

/**
 * This class represents a position or a spot on the Chess Board
 * Created by franklinye on 11/29/16.
 */

public class Position {
    private int row;
    private int col;

    public Position() {

    }

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getColToChar() {
        return (char) ('a' + col);
    }

    public String toString() {
        return getColToChar() + Integer.toString(row + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (row != position.row) return false;
        return col == position.col;

    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }
}
