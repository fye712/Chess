package me.franklinye.chess;

/**
 * Created by franklinye on 11/27/16.
 */

public class GameMove {

    private ChessGame.Side side;
    private String move;

    public GameMove() {

    }

    public GameMove(ChessGame.Side side, String move) {
        this.side = side;
        this.move = move;
    }

    public ChessGame.Side getSide() {
        return side;
    }

    public String getMove() {
        return move;
    }
}
