package me.franklinye.chess.game.pieces;

import me.franklinye.chess.game.ChessBoard;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessPiece;
import me.franklinye.chess.game.Position;

import static me.franklinye.chess.game.ChessGame.Side.WHITE;

/**
 * This class represents a pawn piece
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

        // capturing
        if (dest.getRow() == current.getRow() + sideDirection &&
                (dest.getCol() == current.getCol() + 1 ||
                        dest.getCol() == current.getCol() - 1)) {
            if (board.getPieceAt(dest) != null) {
                return true;
            } else {
                return false;
            }
        }

        // not capturing
        if (dest.getRow() == current.getRow() + sideDirection && dest.getCol() == current.getCol()) {
            if (board.getPieceAt(dest) == null) {
                return true;
            } else {
                return false;
            }
        }

        // two move
        if (dest.getCol() == current.getCol() && dest.getRow() == current.getRow() + 2 * sideDirection) {
            return !isHasMoved();
        }
        return false;
    }
}
