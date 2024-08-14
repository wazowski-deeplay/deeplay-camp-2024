package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.utils.PointsCalculator;

import java.util.*;

public class RandomBot extends Bot {
    private Random random;
    private List<Move> availableMoves;

    protected RandomBot(final String name, final Field field) {
        super(name, field);  // Передаем копию поля
        this.random = new Random();
    }

    @Override
    public Move getMove() {
        final Field field = game.getField();  // Получаем копию поля
        final Cell[][] board = field.getBoard();
        final Player player = game.getPlayerByName(name);

        availableMoves = game.availableMoves(name);
        availableMoves.removeIf(move -> PointsCalculator.costMovement(move.startPosition(), move.endPosition()) > player.getTotalGamePoints());

        if (availableMoves.isEmpty()) {
            return new Move(null, null, Move.MoveType.SKIP, 0);
        }

        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    @Override
    public List<Ship.ShipType> buyFleets() {
        List<Ship.ShipType> shipList = new ArrayList<>();
        final Player player = game.getPlayerByName(name);
        Ship.ShipType[] shipTypes = Ship.ShipType.values();
        int pointsBeforeBuy = player.getTotalGamePoints();
        int pointsAfterBuy = player.getTotalGamePoints();

        while (true) {
            int randomIndex = random.nextInt(shipTypes.length);
            Ship.ShipType selectedShipType = shipTypes[randomIndex];
            int shipCost = selectedShipType.getShipPower() / 10;

            if (pointsAfterBuy >= shipCost) {
                shipList.add(selectedShipType);
                pointsAfterBuy -= shipCost;
                if (pointsAfterBuy < 10 || pointsAfterBuy <= pointsBeforeBuy / (randomIndex + 1) || shipList.size() >= 6 - randomIndex)
                    break;
            }
        }
        return shipList;
    }

    public static class Factory extends BotFactory {

        @Override
        public RandomBot createBot(final String name, final Field field) {
            return new RandomBot(name, field);  // Передаем копию поля
        }
    }
}
