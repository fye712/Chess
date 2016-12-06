package me.franklinye.chess.game;

/**
 * Abstract ChessPiece base class.
 * Created by franklinye on 11/23/16.
 */

public abstract class ChessPiece {

    public enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;
    }

    private Type type;
    private ChessGame.Side side;

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    private boolean hasMoved;

    public ChessPiece() {

    }

    public ChessPiece(Type type, ChessGame.Side side) {
        this.type = type;
        this.side = side;
        this.hasMoved = false;
    }

    /**
     * This abstract method checks to see if the piece can move from one position to the next.
     * @param current starting position
     * @param dest ending position
     * @param board the board
     * @return boolean true if piece can move
     */
    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        return false;
    }

    public Type getType() {
        return this.type;
    }

    public ChessGame.Side getSide() {
        return this.side;
    }
}
