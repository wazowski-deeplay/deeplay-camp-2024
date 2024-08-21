package io.deeplay.camp.game.entites.boardGenerator;

import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Planet;

public class TreeBuilderGenerator implements BoardGenerator {
    @Override
    public Cell[][] generateBoard(int size) {
        Cell[][] board = new Cell[size][size];
        int planetCount = 0;
        Planet planet1 = new Planet(100);
        board[0][size - 1] = new Cell(0, size - 1, planet1);
        planet1.setCell(board[0][size - 1]);

        Planet planet2 = new Planet(100);
        board[size - 1][0] = new Cell(size - 1, 0, planet2);
        planet2.setCell(board[size - 1][0]);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= i; j++) {
                if ((i == 0 && j == size - 1) || (i == size - 1 && j == 0)) {
                    continue;
                }
                board[i][j] = new Cell(i, j);
                if (i != j) {
                    board[j][i] = new Cell(j, i);
                }
            }

            Planet planet3 = new Planet(100);
            planet3.setCell(board[1][1]);
        }

        return board;
    }

}
