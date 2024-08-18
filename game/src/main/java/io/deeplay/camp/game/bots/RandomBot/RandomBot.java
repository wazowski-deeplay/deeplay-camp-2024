package io.deeplay.camp.game.bots.RandomBot;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.bots.TreeBuilder;
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

        if (availableMoves.isEmpty()) {
            return new Move(null, null, Move.MoveType.SKIP, null, 0);
        }

        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    @Override
    public List<Ship.ShipType> buyFleets() {
        List<Ship.ShipType> purchasedShips = new ArrayList<>();
        final Player player = game.getPlayerByName(name);
        Ship.ShipType[] availableShipTypes = Ship.ShipType.values();
        int remainingPoints = player.getTotalGamePoints();
        int maxShipsToBuy = random.nextInt(remainingPoints / 20) + 1;
        int shipsPurchased = 0;

        while (shipsPurchased < maxShipsToBuy) {
            int randomIndex = random.nextInt(availableShipTypes.length);
            Ship.ShipType selectedShipType = availableShipTypes[randomIndex];
            int shipCost = selectedShipType.getShipPower() / 5;

            if (remainingPoints >= shipCost) {
                purchasedShips.add(selectedShipType);
                remainingPoints -= shipCost;
                shipsPurchased++;
            }
            if (remainingPoints < 20 || (shipsPurchased > 0 && random.nextInt(10) < 2)) {
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

        if (FleetDecisionHelper.shouldBuyFleet(startCell, player)) {
            return new Answer(buyFleets());
        } else {
            return new Answer(getMove());
        }
    }
}
/*

package io.deeplay.camp.game.bots.RandomBot;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.entites.*;
        import io.deeplay.camp.game.bots.TreeBuilder;
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

        // Использование TreeBuilder для оценки ходов
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);
        Move bestMove = evaluateBestMove(availableMoves, stats);

        // Случайный выбор с вероятностью 50%, если нужен более случайный подход
        if (random.nextBoolean()) {
            return availableMoves.get(random.nextInt(availableMoves.size()));
        }

        return bestMove;
    }

    @Override
    public List<Ship.ShipType> buyFleets() {
        List<Ship.ShipType> purchasedShips = new ArrayList<>();
        final Player player = game.getPlayerByName(name);
        Ship.ShipType[] availableShipTypes = Ship.ShipType.values();
        int remainingPoints = player.getTotalGamePoints();
        int maxShipsToBuy = random.nextInt(remainingPoints / 10) + 1;
        int shipsPurchased = 0;

        // Использование TreeBuilder для оценки покупки флота
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);
        List<Ship.ShipType> bestFleet = evaluateBestFleet(availableShipTypes, stats);

        while (shipsPurchased < maxShipsToBuy) {
            // Случайный выбор или использование лучшего флота
            if (random.nextBoolean()) {
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
            } else {
                purchasedShips.addAll(bestFleet);
                break;
            }
        }
        return purchasedShips;
    }

    private Move evaluateBestMove(List<Move> availableMoves, TreeBuilder.Stats stats) {
        // Оценка лучших ходов на основе статистики из TreeBuilder
        // Логика может быть основана на maxDepth, numTerminalNodes или других параметрах
        // Например, можно выбрать ход, который ведет к минимальной глубине игры или к максимальному коэффициенту ветвления
        // Пока просто выбираем первый ход как лучший для простоты
        return availableMoves.get(0);
    }

    private List<Ship.ShipType> evaluateBestFleet(Ship.ShipType[] availableShipTypes, TreeBuilder.Stats stats) {
        // Оценка лучшего флота на основе статистики из TreeBuilder
        // Можно выбрать флот, который приведет к максимальному количеству побед
        // Пока просто выбираем первый тип корабля для простоты
        List<Ship.ShipType> fleet = new ArrayList<>();
        fleet.add(availableShipTypes[0]);
        return fleet;
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

        if (FleetDecisionHelper.shouldBuyFleet(startCell, player)) {
            return new Answer(buyFleets());
        } else {
            return new Answer(getMove());
        }
    }
}
*/
