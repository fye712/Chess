package me.franklinye.chess.pieces;

import me.franklinye.chess.ChessBoard;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessPiece;
import me.franklinye.chess.Position;

/**
 * Created by franklinye on 11/29/16.
 */

public class Bishop extends ChessPiece{
    public Bishop(ChessGame.Side side) {
        super(Type.BISHOP, side);
    }

    public Bishop() {

    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        return bishopMovement(current, dest, board);
    }

    public static boolean bishopMovement(Position current, Position dest, ChessBoard board) {
        int newRow = dest.getRow();
        int newCol = dest.getCol();
        int oldRow = current.getRow();
        int oldCol = current.getCol();

        int rowDif = newRow - oldRow;
        int colDif = newCol - oldCol;

        if (rowDif == 0 || colDif == 0) {
            return false;
        }

        double ratio = (double) rowDif / (double) colDif;
        if (ratio == 1 || ratio == -1) {
            return true;
        }
        return false;
    }
}
