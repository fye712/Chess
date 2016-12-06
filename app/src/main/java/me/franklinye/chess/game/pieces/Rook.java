package me.franklinye.chess.game.pieces;

import me.franklinye.chess.game.ChessBoard;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessPiece;
import me.franklinye.chess.game.Position;

/**
 * This class represents a rook piece
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

    /**
     * This method checks to see if a rook can move to a certain spot. Rooks movement is straight
     * lines.
     * @param current Starting spot
     * @param dest Ending spot
     * @param board the board
     * @return boolean true if rook can move to there'
     */
    public static boolean rookMovement(Position current, Position dest, ChessBoard board) {
        return ((dest.getCol() == current.getCol() && dest.getRow() != current.getRow()) ||
                dest.getCol() != current.getCol() && dest.getRow() == current.getRow());
    }

}
