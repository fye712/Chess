package me.franklinye.chess;

import org.junit.Before;
import org.junit.Test;

import me.franklinye.chess.game.ChessBoard;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessPiece;
import me.franklinye.chess.game.Position;

import static me.franklinye.chess.game.ChessPiece.Type.KING;
import static me.franklinye.chess.game.ChessPiece.Type.ROOK;
import static org.junit.Assert.*;

/**
 * Created by franklinye on 12/1/16.
 */
public class ChessGameTest {

    private ChessGame game;
    private ChessBoard board;

    @Before
    public void setupTest() {
        game = new ChessGame("white", "black");
        board = game.getBoard();
    }

    @Test
    public void moveWhitePawn() {
        game.doCommand(ChessGame.Side.WHITE, new Position(1, 6), new Position(2, 6));
        ChessPiece pieceAtPosition = board.getPieceAt(new Position(2, 6));
        assertEquals(ChessPiece.Type.PAWN, pieceAtPosition.getType());
        assertNull(board.getPieceAt(new Position(1, 6)));
    }

    @Test
    public void invalidMove() {
        game.doCommand(ChessGame.Side.BLACK, new Position(1, 6), new Position(2, 6));
        ChessPiece pieceAtPosition = board.getPieceAt(new Position(2, 6));
        assertNull(pieceAtPosition);
        assertNotNull(board.getPieceAt(new Position(1, 6)));
    }

    @Test
    public void pawnTwoSpaces() {
        game.doCommand(ChessGame.Side.WHITE, "d2", "d4");
        ChessPiece pieceAtPosition = board.getPieceAt(game.parseString("d4"));
        assertNotNull(pieceAtPosition);
        assertNull(board.getPieceAt(game.parseString("d2")));
    }

    @Test
    public void bothPawnTwoSpaces() {
        game.doCommand(ChessGame.Side.WHITE, "d2", "d4");
        game.doCommand(ChessGame.Side.BLACK, "d7", "d5");
        assertNotNull(game.getPieceAt("d4"));
        assertNotNull(game.getPieceAt("d5"));
    }

    @Test
    public void pawnCapturing() {
        game.doCommand(ChessGame.Side.WHITE, "d2", "d4");
        game.doCommand(ChessGame.Side.BLACK, "e7", "e5");
        game.doCommand(ChessGame.Side.WHITE, "d4", "e5");
        assertNotNull(game.getPieceAt("e5"));
        assertEquals(1, game.getCapturedPieces().size());
        assertNull(game.getPieceAt("d4"));
    }

    @Test
    public void castle() {
        board.removePiece(ChessGame.parseString("f1"));
        board.removePiece(ChessGame.parseString("g1"));
        game.doCommand(ChessGame.Side.WHITE, "e1", "g1");
        assertTrue(game.getPieceAt("g1").getType() == KING);
        assertTrue(game.getPieceAt("f1").getType() == ROOK);
    }
}