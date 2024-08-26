package io.deeplay.camp.game.bots.RandomBot;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.bots.UtilityFunction;
import io.deeplay.camp.game.entites.*;

import java.util.*;

public class RandomMinimax extends Bot {

    private final int maxDepth;

    public RandomMinimax(final String name, final Field field, int maxDepth) {
        super(name, field);  // Передаем копию поля
        this.maxDepth = maxDepth;
    }

    @Override
    protected Move getMove() {
        // Ищем лучший ход с помощью Minimax
        return findBestMove();
    }

    private Move findBestMove() {
        return minimax(game, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true).move;
    }

    private MoveScore minimax(Game game, int depth, double alpha, double beta, boolean maximizingPlayer) {
        // Если достигли максимальной глубины или игра завершена
        UtilityFunction ruf = new RandomUtilityFunction();
        if (depth == 0 || game.isGameOver()) {
            double utility = ruf.getUtility(game);
            return new MoveScore(new Move(null, null, Move.MoveType.SKIP, null, 0), utility);
        }

        List<Move> availableMoves = game.availableMoves(name);
        Move bestMove = null;

        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : availableMoves) {
                Game gameCopy = game.getCopy();
                gameCopy.getPlayerAction(move, game.getNextPlayerToAct());
                double eval = minimax(gameCopy, depth - 1, alpha, beta, false).score;
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Альфа-бета отсечение заглушка
                }
            }
            return new MoveScore(bestMove, maxEval);
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : availableMoves) {
                Game gameCopy = game.getCopy();
                gameCopy.getPlayerAction(move, game.getNextPlayerToAct());
                double eval = minimax(gameCopy, depth - 1, alpha, beta, true).score;
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Альфа-бета отсечение заглушка
                }
            }
            return new MoveScore(bestMove, minEval);
        }
    }

    private static class MoveScore {
        final Move move;
        final double score;

        public MoveScore(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }

    @Override
    protected List<Ship.ShipType> buyFleets() {
        List<Ship.ShipType> purchasedShips = new ArrayList<>();
        final Player player = game.getPlayerByName(name);
        Ship.ShipType[] availableShipTypes = Ship.ShipType.values();
        int remainingPoints = player.getTotalGamePoints();

        // Оценка полезности каждого типа корабля
        Map<Ship.ShipType, Double> shipUtilityMap = new HashMap<>();
        for (Ship.ShipType shipType : availableShipTypes) {
            double utility = shipType.getShipPower();
            shipUtilityMap.put(shipType, utility);
        }

        // Сортируем типы кораблей по их полезности (от самого полезного к наименее полезному)
        List<Ship.ShipType> sortedShipTypes = new ArrayList<>(Arrays.asList(availableShipTypes));
        sortedShipTypes.sort((a, b) -> Double.compare(shipUtilityMap.get(b), shipUtilityMap.get(a)));

        for (Ship.ShipType shipType : sortedShipTypes) {
            int shipCost = shipType.getShipPower() / 5;
            while (remainingPoints >= shipCost) {
                purchasedShips.add(shipType);
                remainingPoints -= shipCost;
            }
            if (remainingPoints < 20) {
                break;  // Прекращаем покупки, если очков недостаточно для покупки следующего корабля
            }
        }

        return purchasedShips;
    }

    public static class Factory extends BotFactory {

        private final int maxDepth;

        public Factory(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        @Override
        public RandomMinimax createBot(final String name, final Field field) {
            return new RandomMinimax(name, field, maxDepth);  // Передаем копию поля
        }
    }
}
