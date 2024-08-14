package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.utils.FleetDecisionHelper;
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
        List<Ship.ShipType> purchasedShips = new ArrayList<>();
        final Player player = game.getPlayerByName(name);
        Ship.ShipType[] availableShipTypes = Ship.ShipType.values();
        int remainingPoints = player.getTotalGamePoints();
        int maxShipsToBuy = random.nextInt(remainingPoints / 10) + 1;
        int shipsPurchased = 0;

        while (shipsPurchased < maxShipsToBuy) {
            int randomIndex = random.nextInt(availableShipTypes.length);
            Ship.ShipType selectedShipType = availableShipTypes[randomIndex];
            int shipCost = selectedShipType.getShipPower() / 10;

            if (remainingPoints >= shipCost) {
                purchasedShips.add(selectedShipType);
                remainingPoints -= shipCost;
                shipsPurchased++;
            }
            if (remainingPoints < 10 || (shipsPurchased > 0 && random.nextInt(10) < 2)) {
                break;
            }
        }
        return purchasedShips;
    }

    public static class Factory extends BotFactory {

        @Override
        public RandomBot createBot(final String name, final Field field) {
            return new RandomBot(name, field);  // Передаем копию поля
        }
    }

    @Override
    public Answer getAnswer(Field field) {
        Player player = game.getPlayerByName(name);
        Cell startCell = game.getPlayerStartPosition().get(name);

        if (FleetDecisionHelper.shouldBuyFleet(startCell, player)){
            return new Answer(buyFleets());
    } else {
        return new Answer(getMove());
    }
    }
}
