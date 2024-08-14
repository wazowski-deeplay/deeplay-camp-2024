package io.deeplay.camp.game.utils;

import io.deeplay.camp.game.entites.Cell;

public final class PointsCalculator {
    public static final int DIRECT_COST = 5;
    public static final int DIAGONAL_COST = 7;

    public static int costMovement(Cell start, Cell end) {
        if (start == null || end == null) {
            return 0;
        }
        // Здесь можно добавить логику для использования totalCost
        return calculateCostMovement(start, end);
    }

    private static int calculateCostMovement(Cell start, Cell end) {
        int deltaX = Math.abs(end.x - start.x);
        int deltaY = Math.abs(end.y - start.y);
        int directMoves = Math.abs(deltaX - deltaY);
        int diagonalMoves = Math.max(deltaX, deltaY) - directMoves;
        return calculateTotalCost(directMoves, diagonalMoves) + costWeightFleet(start.getFleet().getFleetPower()) * (diagonalMoves + directMoves);
    }

    private static int calculateTotalCost(int direct, int diagonal) {
        return direct * DIRECT_COST + diagonal * DIAGONAL_COST;
    }

    private static int costWeightFleet(int fleetPower) {
        return (int) Math.ceil(fleetPower / 100.0);
    }

    public static boolean checkAddCredits(long moveCounter){
        return moveCounter % 6 == 0 && moveCounter > 0;
    }
}
