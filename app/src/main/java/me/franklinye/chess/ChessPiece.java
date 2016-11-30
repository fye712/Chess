package me.franklinye.chess;

/**
 * Created by franklinye on 11/23/16.
 */

public abstract class ChessPiece {

    public enum Type {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;
    }

    private Type type;
    private ChessGame.Side side;

    public ChessPiece() {

    }

    public ChessPiece(Type type, ChessGame.Side side) {
        this.type = type;
        this.side = side;
    }

    public abstract boolean canMoveTo(Position current, Position dest, ChessBoard board);

    public boolean isOccupiedByTeam(Position dest, ChessBoard board) {
        if (board.getPieceAt(dest) != null) {
            if (board.getPieceAt(dest).getSide() == this.getSide()) {
                return true;
            }
        }

        return false;
    }

    public Type getType() {
        return this.type;
    }

    public ChessGame.Side getSide() {
        return this.side;
    }
}
