package io.deeplay.camp.game.bots.RandomBot;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.bots.UtilityFunction;
import io.deeplay.camp.game.entites.*;

import java.util.*;

public class RandomMinimax extends Bot {

    private final int maxDepth;

    /**
     * Конструктор для создания бота с использованием алгоритма Minimax.
     *
     * @param name     имя бота
     * @param field    поле игры
     * @param maxDepth максимальная глубина поиска
     */
    public RandomMinimax(final String name, final Field field, int maxDepth) {
        super(name, field);
        this.maxDepth = maxDepth;
    }

    /**
     * Получает ход, который бот считает лучшим на данный момент.
     *
     * @return лучший возможный ход
     */
    @Override
    protected Move getMove() {
        return findBestMove();
    }

    /**
     * Использует алгоритм Minimax для поиска лучшего хода.
     *
     * @return лучший найденный ход
     */
    private Move findBestMove() {
        return minimax(game, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true).move;
    }

    /**
     * Реализация алгоритма Minimax с альфа-бета отсечением для поиска лучшего хода.
     *
     * @param game              текущее состояние игры
     * @param depth             оставшаяся глубина поиска
     * @param alpha             значение альфа для отсечения
     * @param beta              значение бета для отсечения
     * @param maximizingPlayer  если true, бот играет за максимизирующего игрока
     * @return объект MoveScore, содержащий лучший ход и его оценку
     */
    private MoveScore minimax(Game game, int depth, double alpha, double beta, boolean maximizingPlayer) {
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

    /**
     * Вспомогательный класс для хранения хода и его оценки.
     */
    private static class MoveScore {
        final Move move;
        final double score;

        public MoveScore(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }

    /**
     * Покупает флот для игрока, используя стратегию на основе оценки полезности кораблей.
     *
     * @return список типов кораблей, которые были куплены
     */
    @Override
    protected List<Ship.ShipType> buyFleets() {
        List<Ship.ShipType> purchasedShips = new ArrayList<>();
        final Player player = game.getPlayerByName(name);
        Ship.ShipType[] availableShipTypes = Ship.ShipType.values();
        int remainingPoints = player.getTotalGamePoints();

        Map<Ship.ShipType, Double> shipUtilityMap = new HashMap<>();
        for (Ship.ShipType shipType : availableShipTypes) {
            double utility = shipType.getShipPower();
            shipUtilityMap.put(shipType, utility);
        }

        List<Ship.ShipType> sortedShipTypes = new ArrayList<>(Arrays.asList(availableShipTypes));
        sortedShipTypes.sort((a, b) -> Double.compare(shipUtilityMap.get(b), shipUtilityMap.get(a)));

        for (Ship.ShipType shipType : sortedShipTypes) {
            int shipCost = shipType.getShipPower() / 5;
            while (remainingPoints >= shipCost) {
                purchasedShips.add(shipType);
                remainingPoints -= shipCost;
            }
            if (remainingPoints < 20) {
                break;
            }
        }

        return purchasedShips;
    }

    /**
     * Фабрика для создания экземпляров RandomMinimax.
     */
    public static class Factory extends BotFactory {

        private final int maxDepth;

        public Factory(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        @Override
        public RandomMinimax createBot(final String name, final Field field) {
            return new RandomMinimax(name, field, maxDepth);
        }
    }
}
