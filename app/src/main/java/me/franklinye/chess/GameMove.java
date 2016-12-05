package me.franklinye.chess;

/**
 * Created by franklinye on 11/27/16.
 */

public class GameMove {

    private ChessGame.Side side;
    private String from;
    private String to;

    public GameMove() {

    }

    public GameMove(ChessGame.Side side, String from, String to) {
        this.side = side;
        this.from = from;
        this.to = to;
    }

    public ChessGame.Side getSide() {
        return side;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
