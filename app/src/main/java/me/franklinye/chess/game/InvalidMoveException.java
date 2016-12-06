package me.franklinye.chess.game;

/**
 * This exception is thrown when a user tries to make an invalid move.
 * Created by franklinye on 11/29/16.
 */

public class InvalidMoveException extends Exception {

    public InvalidMoveException(String message) {
        super(message);
    }
}
