package me.franklinye.chess.game;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

import me.franklinye.chess.game.pieces.Bishop;
import me.franklinye.chess.game.pieces.King;
import me.franklinye.chess.game.pieces.Knight;
import me.franklinye.chess.game.pieces.Pawn;
import me.franklinye.chess.game.pieces.Queen;
import me.franklinye.chess.game.pieces.Rook;

/**
 * This class represents a chess board
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
        // Empty constructor for Firebase
    }

    /**
     * This copy constructor takes a DataSnapshot of the board and creates a new instance of the
     * board stored in the DataSnapshot
     * @param firebaseBoard the DataSnapshot
     */
    public ChessBoard(DataSnapshot firebaseBoard) {
        spots = new HashMap<>();

        for (DataSnapshot piece : firebaseBoard.child("spots").getChildren()) {
            boolean hasMoved = piece.child("hasMoved").getValue(boolean.class);
            ChessGame.Side side = piece.child("side").getValue(ChessGame.Side.class);
            switch (piece.child("type").getValue(ChessPiece.Type.class)) {
                case PAWN:
                    Pawn pawn = new Pawn(side);
                    pawn.setHasMoved(hasMoved);
                    spots.put(piece.getKey(), pawn);
                    break;
                case KNIGHT:
                    Knight knight = new Knight(side);
                    knight.setHasMoved(hasMoved);
                    spots.put(piece.getKey(), knight);
                    break;
                case BISHOP:
                    Bishop bishop = new Bishop(side);
                    bishop.setHasMoved(hasMoved);
                    spots.put(piece.getKey(), bishop);
                    break;
                case ROOK:
                    Rook rook = new Rook(side);
                    rook.setHasMoved(hasMoved);
                    spots.put(piece.getKey(), rook);
                    break;
                case QUEEN:
                    Queen queen = new Queen(side);
                    queen.setHasMoved(hasMoved);
                    spots.put(piece.getKey(), queen);
                    break;
                case KING:
                    King king = new King(side);
                    king.setHasMoved(hasMoved);
                    spots.put(piece.getKey(), king);
                    break;
            }
        }
    }

    /**
     * This method initializes the board, placing the pieces in their starting positions.
     */
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
                new King(ChessGame.Side.BLACK));


    }

    /**
     * This method attempts to move a piece from one position to the other
     * @param current The starting position
     * @param dest The ending position
     * @return The ChessPiece that gets captured by the move
     * @throws InvalidMoveException throws an exception if the piece cannot perform the move
     */
    public ChessPiece movePiece(Position current, Position dest) throws InvalidMoveException {
        ChessPiece piece = getPieceAt(current);
        if (piece == null) {
            throw new InvalidMoveException("No piece at position.");
        }

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

            piece.setHasMoved(true);
        } else {
            throw new InvalidMoveException("Illegal Move");
        }

        return null;
    }

    /**
     * This method captures a piece and returns it.
     * @param attacker the piece moving onto the square
     * @param dest the piece getting captured
     * @return
     */
    private ChessPiece capturePiece(ChessPiece attacker, Position dest) {
        ChessPiece capturedPiece = getPieceAt(dest);
        removePiece(dest);
        addPiece(dest, attacker);
        return capturedPiece;
    }

    /**
     * This method checks if there is a straight unobstructed path from one position to another. It
     * skips if the piece is a knight or king, since they will never have obstructed paths.
     * @param current starting position
     * @param dest ending position
     * @return
     */
    private boolean unobstructedPath(Position current, Position dest) {
        if (getPieceAt(current).getType() == ChessPiece.Type.KNIGHT ||
                getPieceAt(current).getType() == ChessPiece.Type.KING) {
            return true;
        }

        int newRow = dest.getRow();
        int newCol = dest.getCol();
        int oldRow = current.getRow();
        int oldCol = current.getCol();

        int steps = Math.max(Math.abs(newRow - oldRow), Math.abs(newCol - oldCol));
        int rowStep = (newRow - oldRow) / steps;
        int colStep = (newCol - oldCol) / steps;
        int startRow = oldRow + rowStep;
        int startCol = oldCol + colStep;

        while (startRow != newRow || startCol != newCol) {
            if (getPieceAt(new Position(startRow, startCol)) != null) {
                return false;
            }
            startRow += rowStep;
            startCol += colStep;
        }
        return true;
    }

    /**
     * removes a piece from the board
     * @param position the Position to get removed
     */
    public void removePiece(Position position) {
        spots.remove(position.toString());
    }

    /**
     * adds a piece to the board
     * @param position The position for the piece to be added
     * @param piece The piece to be added
     */
    public void addPiece(Position position, ChessPiece piece) {
        spots.put(position.toString(), piece);
    }

    /**
     * This method return the piece at a position
     * @param position The position
     * @return ChessPiece
     */
    public ChessPiece getPieceAt(Position position) {
        return spots.get(position.toString());
    }

    /**
     * This method checks if the king is in check.
     * @param side Side of the king
     * @return boolean true if king is in check.
     */
    public boolean checkForKingCheck(ChessGame.Side side) {
        String position = getKingPositionString(side);
        return checkCheck(side, position);
    }

    /**
     * This method goes through all of the spots with pieces and finds the position of the King
     * @param side side of King that you are looking for
     * @return String of king position
     */
    private String getKingPositionString(ChessGame.Side side) {
        String position = "";
        for (String key : spots.keySet()) {
            if (spots.get(key).getSide() == side && spots.get(key).getType() == ChessPiece.Type.KING) {
                position = key;
            }
        }
        return position;
    }

    private Map<String, ChessPiece> getPiecesChecking(ChessGame.Side side) {
        String kingPosition = getKingPositionString(side);
        Position kingPositionObject = ChessGame.parseString(kingPosition);
        Map<String, ChessPiece> piecesChecking = new HashMap<>();

        for (String key : spots.keySet()) {
            ChessPiece piece = spots.get(key);
            Position piecePosition = ChessGame.parseString(key);
            if (!key.matches(kingPosition) && piece.getSide() != side) {
                if (piece.canMoveTo(kingPositionObject, piecePosition, this) && unobstructedPath(kingPositionObject, piecePosition)) {
                    piecesChecking.put(key, piece);
                }
            }
        }

        return piecesChecking;
    }

    // check mate happens when you can't get out of check. That is, for every possible move, you will
    // still be in check.
    public boolean checkForMate(ChessGame.Side side) {
        String kingPosition = getKingPositionString(side);
        Map<String, ChessPiece> piecesChecking = getPiecesChecking(side);

        // check that the king cannot move to any space

        return false;
    }


    /**
     * This method checks if any of the opponents pieces can move onto a certain square.
     * @param side Side checking
     * @param position Position checking
     * @return True if the king would be in check here.
     */
    public boolean checkCheck(ChessGame.Side side, String position) {
        Position checkPosition = ChessGame.parseString(position);
        for (String key : spots.keySet()) {
            ChessPiece piece = spots.get(key);
            Position piecePosition = ChessGame.parseString(key);
            if (key != position && piece.getSide() != side) {
                if (piece.canMoveTo(checkPosition, piecePosition, this) &&
                        unobstructedPath(checkPosition, piecePosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    // getter for Firebase
    public Map<String, ChessPiece> getSpots() {
        return spots;
    }
}
