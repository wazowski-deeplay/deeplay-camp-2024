package io.deeplay.camp.game.utils;

import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Ship;

import java.util.List;

public final class PointsCalculator {
    public static final int DIRECT_COST = 5;
    public static final int DIAGONAL_COST = 7;

    public static int costMovement(Cell start, Cell end, List<Ship> shipList) {
        if (start == null || end == null) {
            return 0;
        }
        // Здесь можно добавить логику для использования totalCost
        return calculateCostMovement(start, end, shipList);
    }

    private static int calculateCostMovement(Cell start, Cell end, List<Ship> shipList) {
        int deltaX = Math.abs(end.x - start.x);
        int deltaY = Math.abs(end.y - start.y);
        int directMoves = Math.abs(deltaX - deltaY);
        int diagonalMoves = Math.max(deltaX, deltaY) - directMoves;

        if (shipList == null)
            return calculateTotalCost(directMoves, diagonalMoves);
        else
            return calculateTotalCost(directMoves, diagonalMoves) + costWeightFleet(shipList) * (diagonalMoves + directMoves);
    }

    private static int calculateTotalCost(int direct, int diagonal) {
        return direct * DIRECT_COST + diagonal * DIAGONAL_COST;
    }

    public static int costWeightFleet(List<Ship> shipList) {
        int fleetPower = 0;
        for (Ship ship : shipList) {
            fleetPower += ship.getShipType().getShipPower();
        }
        return (int) Math.floor(fleetPower / 100.0);
    }

    public static boolean checkAddCredits(long moveCounter) {
        return moveCounter % 6 == 0 && moveCounter > 0;
    }
}
