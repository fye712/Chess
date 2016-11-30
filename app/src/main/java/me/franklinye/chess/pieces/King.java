package me.franklinye.chess.pieces;

import me.franklinye.chess.ChessBoard;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessPiece;
import me.franklinye.chess.Position;

/**
 * Created by franklinye on 11/29/16.
 */

public class King extends ChessPiece{

    public King(ChessGame.Side side) {
        super(Type.KING, side);
    }

    public King() {

    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        int rowDif = dest.getRow() - current.getRow();
        int colDif = dest.getCol() - current.getCol();
        if ((-1 <= rowDif && rowDif <= 1) && (-1 <= colDif && colDif <= 1)) {
            return true;
        }

        return false;
    }

}
