package me.franklinye.chess.game.pieces;

import me.franklinye.chess.game.ChessBoard;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessPiece;
import me.franklinye.chess.game.Position;

/**
 * This class represents a King
 * Created by franklinye on 11/29/16.
 */

public class King extends ChessPiece{

    public King(ChessGame.Side side) {
        super(Type.KING, side);
    }

    public King() {
        // required for Firebase
    }

    public boolean canMoveTo(Position current, Position dest, ChessBoard board) {
        int rowDif = dest.getRow() - current.getRow();
        int colDif = dest.getCol() - current.getCol();
        // standard movement
        if ((-1 <= rowDif && rowDif <= 1) && (-1 <= colDif && colDif <= 1)) {
            return true;
        } else if ((colDif == 2 || colDif == -2) && !isHasMoved() && rowDif == 0) { // castling
            Position rookPosition = colDif == 2 ? new Position(current.getRow(), 7) :
                    new Position(current.getRow(), 0);

            if (!board.getPieceAt(rookPosition).isHasMoved()) {
                int step = colDif == 2 ? 1 : -1;
                for (int i = current.getCol() + colDif; i != rookPosition.getCol(); i += step) {
                    if (board.getPieceAt(new Position(current.getRow(), i)) != null) {
                        return false;
                    }
                }

                // cannot move through check
                for (int j = current.getCol() + colDif; j != dest.getCol(); j += step) {
                    if (board.checkCheck(getSide(), new Position(current.getRow(), j).toString())) {
                        return false;
                    }
                }

                // moving the rook
                ChessPiece rook = board.getPieceAt(rookPosition);
                Position rookDest = new Position(dest.getRow(), dest.getCol() -  step);
                board.removePiece(rookPosition);
                board.addPiece(rookDest, rook);
                return true;
            }

        }

        return false;
    }

}
