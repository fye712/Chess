package me.franklinye.chess;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.franklinye.chess.pieces.Bishop;
import me.franklinye.chess.pieces.King;
import me.franklinye.chess.pieces.Knight;
import me.franklinye.chess.pieces.Pawn;
import me.franklinye.chess.pieces.Queen;
import me.franklinye.chess.pieces.Rook;

import static me.franklinye.chess.ChessGame.Side.BLACK;
import static me.franklinye.chess.ChessGame.Side.WHITE;
import static me.franklinye.chess.ChessPiece.Type.KING;

/**
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

    }

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

    public void init() {
        board = new ChessBoard();
        board.init();
    }

    public void doCommand(Side side, Position current, Position dest) {

        Side otherSide = side == WHITE ? BLACK : WHITE;
        if (side != playerToMove) {
            return;
        }
        ChessPiece piece = board.getPieceAt(current);
        if (piece == null || piece.getSide() != side) {
            return;
        }

        try {
            ChessPiece capturedPiece = board.movePiece(current, dest);
            if (capturedPiece != null) {
                capturedPieces.add(capturedPiece);
                // if you capture a king you win
                if (capturedPiece.getType() == KING) {
                    endGame(side);
                }
            }

            moves.add(new GameMove(side, current.toString(), dest.toString()));

            switchPlayer();
            if (board.checkForKingCheck(otherSide)) {
                status += otherSide.name() + " is checked.";
            }
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            return;
        }
    }

    public ChessPiece getPieceAt(String position) {
        return board.getPieceAt(parseString(position));
    }

    public void switchPlayer() {
        if (playerToMove == WHITE) {
            playerToMove = BLACK;
        } else {
            playerToMove = WHITE;
        }

        status = playerToMove.name() + " to move.";
    }

    public void doCommand(Side side, String currentPosition, String destination) {
        doCommand(side, parseString(currentPosition), parseString(destination));
    }

    public void endGame(Side winnerSide) {
        if (winnerSide == WHITE) {
            winner = getWhiteUser();
        } else {
            winner = getBlackUser();
        }

        playerToMove = null;
    }

    public void stalemate() {
        winner = "stalemate";
        playerToMove = null;
        offeredStalemate = false;
    }

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
