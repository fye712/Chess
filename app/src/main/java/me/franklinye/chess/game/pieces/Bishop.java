package me.franklinye.chess.game.pieces;

import me.franklinye.chess.game.ChessBoard;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessPiece;
import me.franklinye.chess.game.Position;

/**
 * This class represents a Bishop
 * Created by franklinye on 11/29/16.
 */
public class Bishop extends ChessPiece{
    public Bishop(ChessGame.Side side) {
        super(Type.BISHOP, side);
    }

    public Bishop() {
        // required for Firebase
    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        return bishopMovement(current, dest);
    }

    /**
     * This method checks if you can move to a position. Bishops can move to positions that are
     * on the same diagonal as them.
     * @param current The starting position
     * @param dest The destination
     * @return true if the piece can move there, false if it cannot
     */
    public static boolean bishopMovement(Position current, Position dest) {
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
        if (ratio == 1 || ratio == -1) { // diagonal if the ratio of the row and col differences = 1
            return true;
        }
        return false;
    }
}
