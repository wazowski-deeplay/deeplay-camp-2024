package io.deeplay.camp.game.entites.boardGenerator;

import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Planet;

import java.util.Random;

public class SymmetricalGenerator implements BoardGenerator {
    @Override
    public Cell[][] generateBoard(final int size) {
        Random random = new Random();
        Cell[][] board = new Cell[size][size];
        int planetCount = 0;

        Planet planet1 = new Planet(900);
        board[0][size - 1] = new Cell(0, size - 1, planet1);
        planet1.setCell(board[0][size - 1]);

        Planet planet2 = new Planet(900);
        board[size - 1][0] = new Cell(size - 1, 0, planet2);
        planet2.setCell(board[size - 1][0]);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= i; j++) {
                if ((i == 0 && j == size - 1) || (i == size - 1 && j == 0)) {
                    continue;
                }

                if (planetCount < size && random.nextInt(2) == 1) {
                    int temp = 100 + random.nextInt(13) * 50;
                    Planet newPlanet = new Planet(temp);
                    board[i][j] = new Cell(i, j, newPlanet);
                    newPlanet.setCell(board[i][j]);
                    planetCount++;

                    if (planetCount < size && i != j) {
                        Planet newPlanet2 = new Planet(temp);
                        board[j][i] = new Cell(j, i, newPlanet2);
                        newPlanet2.setCell(board[j][i]);
                        planetCount++;
                    } else if (i != j) {
                        board[j][i] = new Cell(j, i);
                    }
                } else {
                    board[i][j] = new Cell(i, j);
                    if (i != j) {
                        board[j][i] = new Cell(j, i);
                    }
                }
            }
        }

        return board;
    }


}
