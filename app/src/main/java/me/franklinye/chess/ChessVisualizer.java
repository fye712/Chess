package me.franklinye.chess;

import android.content.res.Resources;

import java.util.Map;

/**
 * Created by franklinye on 12/4/16.
 */

public class ChessVisualizer {

    public static final String B_PAWN = "♟";
    public static final String W_PAWN = "♙";
    public static final String B_KNIGHT = "♞";
    public static final String W_KNIGHT = "♘";
    public static final String B_BISHOP = "♝";
    public static final String W_BISHOP = "♗";
    public static final String B_ROOK = "♜";
    public static final String W_ROOK = "♖";
    public static final String B_QUEEN = "♛";
    public static final String W_QUEEN = "♕";
    public static final String B_KING = "♚";
    public static final String W_KING = "♔";


    private ChessVisualizer() {}

    public static String visualize(ChessBoard board) {
        String result = "";
        for (int i = 8; i > 0; i--) {
            String row = "";
            for (char j = 'a'; j < 'i'; j++) {
                ChessPiece piece = board.getPieceAt(ChessGame.parseString(Character.toString(j) + Integer.toString(i)));
                if (piece == null) {
                    row += squareDecider(i, j);
                } else {
                    switch(piece.getType()) {
                        case PAWN:
                            row += choose(W_PAWN, B_PAWN, piece);
                            break;
                        case KNIGHT:
                            row += choose(W_KNIGHT, B_KNIGHT, piece);
                            break;
                        case BISHOP:
                            row += choose(W_BISHOP, B_BISHOP, piece);
                            break;
                        case ROOK:
                            row += choose(W_ROOK, B_ROOK, piece);
                            break;
                        case QUEEN:
                            row += choose(W_QUEEN, B_QUEEN, piece);
                            break;
                        case KING:
                            row += choose(W_KING, B_KING, piece);
                            break;
                    }
                }
            }
            result += row;
            result += "\n";
        }

        return result;
    }

    private static String choose(String white, String black, ChessPiece piece) {
        if (piece.getSide() == ChessGame.Side.WHITE) {
            return white;
        } else {
            return black;
        }
    }

    private static String squareDecider(int row, char col) {
        if ((row + col) % 2 == 1) {
            return "⚝";
        } else {
            return "⚝";
        }

    }
}
