package me.franklinye.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franklinye on 11/23/16.
 */

public class ChessBoard {
    public static final int BOARD_DIM = 8;

    private List<List<BoardSpot>> spots = new ArrayList<List<BoardSpot>>();

    public ChessBoard() {

    }

    public void init() {
        for (int i = 0; i < BOARD_DIM; i++) {
            List<BoardSpot> row = new ArrayList<>();
            for (int j = 0; j < BOARD_DIM; j++) {
                row.add(new BoardSpot(i, j));
            }
            spots.add(row);
        }
    }

    public List<List<BoardSpot>> getSpots() {
        return spots;
    }
}
