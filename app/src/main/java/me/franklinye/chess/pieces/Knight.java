package me.franklinye.chess.pieces;

import me.franklinye.chess.ChessBoard;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessPiece;
import me.franklinye.chess.Position;

/**
 * Created by franklinye on 11/29/16.
 */

public class Knight extends ChessPiece {

    public Knight(ChessGame.Side side) {
        super(Type.KNIGHT, side);
    }

    public Knight() {

    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        int rowDif = dest.getRow() - current.getRow();
        int colDif = dest.getCol() - current.getCol();

        if (rowDif == 2 || rowDif == -2) {
            if (colDif == 1 || colDif == -1) {
                return true;
            }
        }

        if (colDif == 2 || colDif == -2) {
            if (rowDif == 1 || rowDif == -1) {
                return true;
            }
        }
        return false;
    }
}
