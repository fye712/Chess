package me.franklinye.chess.game.pieces;

import me.franklinye.chess.game.ChessBoard;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessPiece;
import me.franklinye.chess.game.Position;

/**
 * This class represents a queen piece.
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

        if (Bishop.bishopMovement(current, dest)) {
            return true;
        }

        return false;
    }
}
