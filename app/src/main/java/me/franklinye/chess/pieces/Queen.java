package me.franklinye.chess.pieces;

import me.franklinye.chess.ChessBoard;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessPiece;
import me.franklinye.chess.Position;

/**
 * Created by franklinye on 11/29/16.
 */

public class Queen extends ChessPiece {
    public Queen(ChessGame.Side side) {
        super(Type.QUEEN, side);
    }

    public Queen() {

    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        if (Rook.rookMovement(current, dest, board)) {
            return true;
        }

        if (Bishop.bishopMovement(current, dest, board)) {
            return true;
        }

        return false;
    }
}
