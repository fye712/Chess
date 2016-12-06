package me.franklinye.chess.game;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

import me.franklinye.chess.game.pieces.Bishop;
import me.franklinye.chess.game.pieces.King;
import me.franklinye.chess.game.pieces.Knight;
import me.franklinye.chess.game.pieces.Pawn;
import me.franklinye.chess.game.pieces.Queen;
import me.franklinye.chess.game.pieces.Rook;

import static me.franklinye.chess.game.ChessGame.Side.BLACK;
import static me.franklinye.chess.game.ChessGame.Side.WHITE;

/**
 * This class represents a Chess Game
 * Created by franklinye on 11/23/16.
 */

public class ChessGame {

    public enum Side {
        WHITE, BLACK;
    }
    private ChessBoard board;
    private List<GameMove> moves;
    private String whiteUser;
    private String blackUser;
    private String winner;
    private String status;

    private List<ChessPiece> capturedPieces;
    private Side playerToMove;

    private boolean offeredStalemate;
    private String offeredStalemateBy;

    public ChessGame() {
        // empty constructor for Firebase
    }

    /**
     * This method takes a DataSnapshot of a game stored in Firebase and produces a ChessGame object
     * @param dataSnapshot the game stored in Firebase
     */
    public ChessGame(DataSnapshot dataSnapshot) {
        moves = dataSnapshot.child("moves").getValue(new GenericTypeIndicator<List<GameMove>>() {
        });

        if (moves == null) {
            moves = new ArrayList<>();
        }
        if (dataSnapshot.child("offeredStalemate").getValue() == null) {
            offeredStalemate = false;
        } else {
            offeredStalemate = dataSnapshot.child("offeredStalemate").getValue(boolean.class);
        }
        if (dataSnapshot.child("offeredStalemateBy") != null) {
            offeredStalemateBy = dataSnapshot.child("offeredStalemateBy").getValue(String.class);
        }
        status = dataSnapshot.child("status").getValue(String.class);
        whiteUser = dataSnapshot.child("whiteUser").getValue(String.class);
        blackUser = dataSnapshot.child("blackUser").getValue(String.class);
        winner = dataSnapshot.child("winner").getValue(String.class);
        playerToMove = dataSnapshot.child("playerToMove").getValue(ChessGame.Side.class);
        DataSnapshot capturedList = dataSnapshot.child("capturedPieces");
        capturedPieces = new ArrayList<>();
        for (DataSnapshot piece : capturedList.getChildren()) {
            boolean hasMoved = piece.child("hasMoved").getValue(boolean.class);
            ChessGame.Side side = piece.child("side").getValue(ChessGame.Side.class);
            switch (piece.child("type").getValue(ChessPiece.Type.class)) {
                case PAWN:
                    Pawn pawn = new Pawn(side);
                    pawn.setHasMoved(hasMoved);
                    capturedPieces.add(pawn);
                    break;
                case KNIGHT:
                    Knight knight = new Knight(side);
                    knight.setHasMoved(hasMoved);
                    capturedPieces.add(knight);
                    break;
                case BISHOP:
                    Bishop bishop = new Bishop(side);
                    bishop.setHasMoved(hasMoved);
                    capturedPieces.add(bishop);
                    break;
                case ROOK:
                    Rook rook = new Rook(side);
                    rook.setHasMoved(hasMoved);
                    capturedPieces.add(rook);
                    break;
                case QUEEN:
                    Queen queen = new Queen(side);
                    queen.setHasMoved(hasMoved);
                    capturedPieces.add(queen);
                    break;
                case KING:
                    King king = new King(side);
                    king.setHasMoved(hasMoved);
                    capturedPieces.add(king);
                    break;
            }
        }

        board = new ChessBoard(dataSnapshot.child("board"));
    }

    /**
     * Constructor for ChessGame using the strings of the users.
     * @param whiteUser white
     * @param blackUser black
     */
    public ChessGame(String whiteUser, String blackUser) {
        this.moves = new ArrayList<>();
        this.whiteUser = whiteUser;
        this.blackUser = blackUser;
        this.capturedPieces = new ArrayList<>();
        this.playerToMove = WHITE;
        this.status = "WHITE to move.";
        this.offeredStalemate = false;
        init();
    }

    /**
     * This method initializes the board.
     */
    public void init() {
        board = new ChessBoard();
        board.init();
    }

    /**
     * This method does a command taken from a player given as two positions.
     * @param side Side doing the command.
     * @param current Starting position
     * @param dest Ending position
     */
    public void doCommand(Side side, Position current, Position dest) {

        Side otherSide = side == WHITE ? BLACK : WHITE;
        if (side != playerToMove) {
            return;
        }
        ChessPiece piece = board.getPieceAt(current);
        if (piece == null || piece.getSide() != side) {
            if (!status.startsWith("invalid")) {
                status = "invalid move \n" + status;
            }
            return;
        }

        try {
            ChessPiece capturedPiece = board.movePiece(current, dest);
            if (capturedPiece != null) {
                capturedPieces.add(capturedPiece);
                // if you capture a king you win
                if (capturedPiece.getType() == ChessPiece.Type.KING) {
                    endGame(side);
                    return;
                }
            }

            moves.add(new GameMove(side, current.toString(), dest.toString()));

            switchPlayer();
            if (board.checkForKingCheck(otherSide)) {
                status += otherSide.name() + "\n is checked.";
            }
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            if (!status.startsWith("invalid")) {
                status = "invalid move \n" + status;
            }
        }
    }

    /**
     * This method gets the piece at a certain position using the String representation of a
     * position.
     * @param position String representation of a position
     * @return ChessPiece at position in board
     */
    public ChessPiece getPieceAt(String position) {
        return board.getPieceAt(parseString(position));
    }

    /**
     * This method switches the player to move and updates the status of the game.
     */
    public void switchPlayer() {
        if (playerToMove == WHITE) {
            playerToMove = BLACK;
        } else {
            playerToMove = WHITE;
        }

        status = playerToMove.name() + " to move.";
    }

    /**
     * This helper method for doCommand(Side, Position, Position) takes the positions in as strings.
     * @param side Side doing the command.
     * @param currentPosition String representation of starting position
     * @param destination String representation of ending position
     */
    public void doCommand(Side side, String currentPosition, String destination) {
        doCommand(side, parseString(currentPosition), parseString(destination));
    }

    /**
     * This method ends the game when a player wins.
     * @param winnerSide The side that wins.
     */
    public void endGame(Side winnerSide) {
        if (winnerSide == WHITE) {
            winner = getWhiteUser();
        } else {
            winner = getBlackUser();
        }

        playerToMove = null;
        status = winnerSide + " wins.";
    }

    /**
     * This method ends the game in a stalemate.
     */
    public void stalemate() {
        winner = "stalemate";
        playerToMove = null;
        offeredStalemate = false;
        status = "stalemate";
    }

    /**
     * This method parses a position's string representation.
     * @param positionString String
     * @return Position
     */
    public static Position parseString(String positionString) {
        int col = positionString.charAt(0) - 'a';
        int row = Integer.parseInt(Character.toString(positionString.charAt(1))) - 1;
        return new Position(row, col);
    }

    public ChessBoard getBoard() {
        return board;
    }

    public List<GameMove> getMoves() {
        return moves;
    }

    public void addMove(GameMove move) {
        moves.add(move);
    }

    public String getWhiteUser() {
        return whiteUser;
    }

    public String getBlackUser() {
        return blackUser;
    }

    public String getWinner() { return winner; }

    public Side getPlayerToMove() {
        return playerToMove;
    }

    public boolean isOfferedStalemate() {
        return offeredStalemate;
    }

    public void setOfferedStalemate(boolean offeredStalemate) {
        this.offeredStalemate = offeredStalemate;
    }

    public String getOfferedStalemateBy() {
        return offeredStalemateBy;
    }

    public void setOfferedStalemateBy(String offeredStalemateBy) {
        this.offeredStalemateBy = offeredStalemateBy;
    }

    public String getStatus() {
        return status;
    }

    public List<ChessPiece> getCapturedPieces() {
        return capturedPieces;
    }
}
