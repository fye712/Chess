package me.franklinye.chess;

/**
 * Created by franklinye on 11/23/16.
 */

public abstract class ChessPiece {
    public enum Side {
        WHITE, BLACK;
    }

    private String name;
    private Side side;

    public ChessPiece(String name, Side side) {
        this.name = name;
        this.side = side;
    }

    public abstract boolean isValidMove(ChessBoard board);

    public String getName() {
        return this.name;
    }

    public Side getSide() {
        return this.side;
    }
}
