package me.franklinye.chess;

/**
 * Created by franklinye on 11/27/16.
 */

public class GameMove {
    private ChessPiece.Side side;
    private String move;

    public GameMove() {

    }

    public GameMove(ChessPiece.Side side, String move) {
        this.side = side;
        this.move = move;
    }

    public ChessPiece.Side getSide() {
        return side;
    }

    public String getMove() {
        return move;
    }
}
