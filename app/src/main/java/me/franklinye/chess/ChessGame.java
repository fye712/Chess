package me.franklinye.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franklinye on 11/23/16.
 */

public class ChessGame {
    private ChessBoard board;
    private List<GameMove> moves;
    private String whiteUser;
    private String blackUser;

    public ChessGame() {

    }

    public ChessGame(String whiteUser, String blackUser) {
        moves = new ArrayList<>();
        this.whiteUser = whiteUser;
        this.blackUser = blackUser;
    }

    public void init() {
        board = new ChessBoard();
        board.init();
    }

    public ChessBoard getBoard() {
        return board;
    }

    public List<GameMove> getMoves() {
        return moves;
    }

    public void addMove(GameMove move) {
        moves.add(move);
    }

    public String getWhiteUser() {
        return whiteUser;
    }

    public String getBlackUser() {
        return blackUser;
    }
}
