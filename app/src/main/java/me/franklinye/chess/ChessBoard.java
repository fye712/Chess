package me.franklinye.chess;

import java.util.HashMap;
import java.util.Map;

import me.franklinye.chess.pieces.Bishop;
import me.franklinye.chess.pieces.King;
import me.franklinye.chess.pieces.Knight;
import me.franklinye.chess.pieces.Pawn;
import me.franklinye.chess.pieces.Queen;
import me.franklinye.chess.pieces.Rook;

import static me.franklinye.chess.ChessPiece.Type.*;

/**
 * Created by franklinye on 11/23/16.
 */

public class ChessBoard {

    private static final int BOARD_DIM = 8;
    private static final int ROOK_SPOTS = 0;
    private static final int KNIGHT_SPOTS = 1;
    private static final int BISHOP_SPOTS = 2;
    private static final int QUEEN_SPOT = 3;
    private static final int KING_SPOT = 4;

    private Map<String, ChessPiece> spots = new HashMap<>();

    public ChessBoard() {

    }

    public void init() {
        for (int i = 0; i < BOARD_DIM; i++) {
            spots.put(new Position(1, i).toString(), new Pawn(ChessGame.Side.WHITE));
            spots.put(new Position(BOARD_DIM - 2, i).toString(),
                    new Pawn(ChessGame.Side.BLACK));
        }

        spots.put(new Position(0, ROOK_SPOTS).toString(), new Rook(ChessGame.Side.WHITE));
        spots.put(new Position(0, BOARD_DIM - ROOK_SPOTS - 1).toString(),
                new Rook(ChessGame.Side.WHITE));
        spots.put(new Position(BOARD_DIM - 1, ROOK_SPOTS).toString(),
                new Rook(ChessGame.Side.BLACK));
        spots.put(new Position(BOARD_DIM - 1, BOARD_DIM - ROOK_SPOTS - 1).toString(),
                new Rook(ChessGame.Side.BLACK));

        spots.put(new Position(0, KNIGHT_SPOTS).toString(), new Knight(ChessGame.Side.WHITE));
        spots.put(new Position(0, BOARD_DIM - KNIGHT_SPOTS - 1).toString(),
                new Knight(ChessGame.Side.WHITE));
        spots.put(new Position(BOARD_DIM - 1, KNIGHT_SPOTS).toString(),
                new Knight(ChessGame.Side.BLACK));
        spots.put(new Position(BOARD_DIM - 1, BOARD_DIM - KNIGHT_SPOTS - 1).toString(),
                new Knight(ChessGame.Side.BLACK));

        spots.put(new Position(0, BISHOP_SPOTS).toString(), new Bishop(ChessGame.Side.WHITE));
        spots.put(new Position(0, BOARD_DIM - BISHOP_SPOTS - 1).toString(),
                new Bishop(ChessGame.Side.WHITE));
        spots.put(new Position(BOARD_DIM - 1, BISHOP_SPOTS).toString(),
                new Bishop(ChessGame.Side.BLACK));
        spots.put(new Position(BOARD_DIM - 1, BOARD_DIM - BISHOP_SPOTS - 1).toString(),
                new Bishop(ChessGame.Side.BLACK));

        spots.put(new Position(0, QUEEN_SPOT).toString(), new Queen(ChessGame.Side.WHITE));
        spots.put(new Position(BOARD_DIM - 1, QUEEN_SPOT).toString(),
                new Queen(ChessGame.Side.BLACK));

        spots.put(new Position(0, KING_SPOT).toString(), new King(ChessGame.Side.WHITE));
        spots.put(new Position(BOARD_DIM - 1, KING_SPOT).toString(),
                new Knight(ChessGame.Side.BLACK));


    }

    // TODO: write movePiece() function
    public ChessPiece movePiece(Position current, Position dest) throws InvalidMoveException {
        ChessPiece piece = getPieceAt(current);
        if (piece.canMoveTo(current, dest, this) && unobstructedPath(current, dest)) {
            if (getPieceAt(dest) != null) {
                if (getPieceAt(dest).getSide() == piece.getSide()) {
                    throw new InvalidMoveException("Space already occupied by own piece.");
                } else {
                    // remove the piece from its original spot
                    removePiece(current);
                    // capture the piece and return it up
                    return capturePiece(piece, dest);
                }
            } else {
                // move the piece and return null
                removePiece(current);
                addPiece(dest, piece);
            }
        }

        return null;
    }

    private ChessPiece capturePiece(ChessPiece attacker, Position dest) {
        ChessPiece capturedPiece = getPieceAt(dest);
        removePiece(dest);
        addPiece(dest, attacker);
        return capturedPiece;
    }

    private boolean unobstructedPath(Position current, Position dest) {
        if (getPieceAt(current).getType() == KNIGHT) {
            return true;
        }

        int newRow = dest.getRow();
        int newCol = dest.getCol();
        int oldRow = current.getRow();
        int oldCol = current.getCol();

        int steps = Math.max(Math.abs(newRow - oldRow), Math.abs(newCol - oldCol));
        int rowStep = newRow - oldRow / steps;
        int colStep = newCol - oldCol / steps;
        int startRow = oldRow + rowStep;
        int startCol = oldCol + colStep;

        //TODO: refactor this into ChessBoard class, decoupling ChessBoard and ChessPiece
        while (startRow != newRow && startCol != newCol) {
            if (getPieceAt(new Position(startRow, startCol)) != null) {
                return false;
            }
            startRow += rowStep;
            startCol += colStep;
        }
        return true;
    }

    private void removePiece(Position position) {
        spots.remove(position.toString());
    }

    private void addPiece(Position position, ChessPiece piece) {
        spots.put(position.toString(), piece);
    }

    public ChessPiece getPieceAt(Position position) {
        return spots.get(position.toString());
    }

    public Map<String, ChessPiece> getSpots() {
        return spots;
    }
}
