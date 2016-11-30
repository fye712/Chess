package me.franklinye.chess.pieces;

import me.franklinye.chess.ChessBoard;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessPiece;
import me.franklinye.chess.Position;

import static me.franklinye.chess.ChessGame.Side.WHITE;

/**
 * Created by franklinye on 11/28/16.
 */

public class Pawn extends ChessPiece {
    public Pawn(ChessGame.Side side) {
        super(Type.PAWN, side);
    }

    public Pawn() {

    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        int sideDirection;
        if (getSide() == WHITE) {
            sideDirection = 1;
        } else {
            sideDirection = -1;
        }
        if (dest.getRow() == current.getRow() + sideDirection &&
                (dest.getCol() == current.getCol() + 1 ||
                        dest.getCol() == current.getCol() - 1)) {
            if (board.getPieceAt(dest) != null) {
                return true;
            } else {
                return false;
            }
        }

        if (dest.getRow() == current.getRow() + sideDirection && dest.getCol() == current.getCol()) {
            if (board.getPieceAt(dest) == null) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
