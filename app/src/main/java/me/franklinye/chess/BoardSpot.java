package me.franklinye.chess;

/**
 * Created by franklinye on 11/23/16.
 */

public class BoardSpot {
    private int x;
    private int y;
    private ChessPiece piece;

    public BoardSpot() {

    }

    public BoardSpot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ChessPiece getPiece() {
        return piece;
    }
}
