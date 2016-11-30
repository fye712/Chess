package me.franklinye.chess.pieces;

import me.franklinye.chess.ChessBoard;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessPiece;
import me.franklinye.chess.Position;

/**
 * Created by franklinye on 11/29/16.
 */

public class Rook extends ChessPiece{
    public Rook(ChessGame.Side side) {
        super(Type.ROOK, side);
    }

    public Rook() {

    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        return rookMovement(current, dest, board);
    }

    public static boolean rookMovement(Position current, Position dest, ChessBoard board) {
        if ((dest.getCol() == current.getCol() && dest.getRow() != current.getRow()) ||
                dest.getCol() != current.getCol() && dest.getRow() == current.getRow()) {
                return true;
        }

        return false;
    }

}
