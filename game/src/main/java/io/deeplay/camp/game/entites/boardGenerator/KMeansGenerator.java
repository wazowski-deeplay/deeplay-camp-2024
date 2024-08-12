package io.deeplay.camp.game.entites.boardGenerator;

import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Planet;

import java.util.Random;

public class KMeansGenerator implements BoardGenerator {
    @Override
    public Cell[][] generateBoard(int size) {
        Cell[][] board = new Cell[size][size];

        int[][] matrix = generateRandomMatrix(size);
        int[] center1 = {0, size - 1};
        int[] center2 = {size - 1, 0};
        balanceMatrix(matrix, center1, center2);
        matrix[0][size - 1] = 300;
        matrix[size - 1][0] = 300;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] != 0) {
                    Planet newPlanet = new Planet(matrix[i][j]);
                    board[i][j] = new Cell(i, j, newPlanet);
                    newPlanet.setCell(board[i][j]);
                } else {
                    board[i][j] = new Cell(i, j);
                }
            }
        }
        return board;
    }

    private int[][] generateRandomMatrix(int size) {
        Random random = new Random();
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            matrix[random.nextInt(size - 1)][random.nextInt(size - 1)] = 100 + random.nextInt(13) * 50;
        }
        return matrix;
    }

    private void balanceMatrix(int[][] matrix, int[] center1, int[] center2) {
        int size = matrix.length;
        int count = 0;
        boolean balanced = (Math.abs(sumDistToCenter(matrix, center1) - sumDistToCenter(matrix, center2)) < 1000);
        while (!balanced && count < 10) {
            for (int k = 0; k < size * size; k++) {
                int i = k / size; // строка
                int j = k % size; // столбец
                if (i == j) {
                    continue;
                }
                if (matrix[i][j] != 0) {
                    if (distance(i, j, center1[0], center1[1]) < distance(i, j, center2[0], center2[1])) {
                        if (matrix[(i + 1) % size][Math.abs(j - 1)] == 0) {
                            swap(matrix, i, j, (i + 1) % size, Math.abs(j - 1));// вниз влево
                        } else if (matrix[(i + 1) % size][j] == 0) {
                            swap(matrix, i, j, (i + 1) % size, j);// вниз
                        } else if (matrix[i][Math.abs(j - 1)] == 0) {
                            swap(matrix, i, j, i, Math.abs(j - 1));//  влево
                        }
                    } else {
                        if (matrix[Math.abs(i - 1)][(j + 1) % size] == 0) {
                            swap(matrix, i, j, Math.abs(i - 1), (j + 1) % size);//вверх вправо
                        } else if (matrix[Math.abs(i - 1)][j] == 0) {
                            swap(matrix, i, j, Math.abs(i - 1), j);//вверх
                        } else if (matrix[i][(j + 1) % size] == 0) {
                            swap(matrix, i, j, i, (j + 1) % size);// вправо
                        }
                    }
                    balanced = (Math.abs(sumDistToCenter(matrix, center1) - sumDistToCenter(matrix, center2)) < 1000);
                    if (balanced) {
                        break;
                    }
                }
            }
            count++;
        }
    }

    public void swap(int[][] matrix, int row1, int col1, int row2, int col2) {
        int temp = matrix[row1][col1];
        matrix[row1][col1] = matrix[row2][col2];
        matrix[row2][col2] = temp;
    }


    private int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private int sumDistToCenter(int[][] matrix, int[] center) {
        int size = matrix.length;
        int distToCenter = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distToCenter += distance(i, j, center[0], center[1]) * matrix[i][j];
            }
        }
        return distToCenter;
    }

}
