package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.Game;
import io.deeplay.camp.game.entites.Move;

import java.util.List;


public class TreeBuilder {
    /**
     * Класс {@code Stats} хранит различные статистические данные,
     * связанные с исследованием структуры дерева.
     */
    static public class Stats {
        /**
         * Общее количество узлов в дереве.
         */
        public int numNodes;
        /**
         * Количество терминальных узлов (листов) в дереве.
         */
        public int numTerminalNodes;
        /**
         * Максимальная глубина дерева.
         */
        public int maxDepth;
        /**
         * Средний коэффициент ветвления дерева, который представляет
         * собой среднее количество дочерних узлов на один узел.
         */
        public double branchingFactor;
        /**
         * Общее время, затраченное на операцию, измеренное в миллисекундах.
         */
        public long workTimeMS;
        /**
         * Возвращает строковое представление объекта {@code Stats},
         * включающее все поля и их значения.
         *
         * @return строковое представление объекта.
         */
        @Override
        public String toString() {
            return "Stats{" +
                    "numNodes=" + numNodes +
                    ", numTerminalNodes=" + numTerminalNodes +
                    ", maxDepth=" + maxDepth +
                    ", branchingFactor=" + branchingFactor +
                    ", workTimeMS=" + workTimeMS +
                    '}';
        }
    }

    /**
     * Класс {@code AuxiliaryStats} хранит дополнительные статистические данные,
     * которые фиксируют разные игровые сценарии.
     */
    static class AuxiliaryStats {
        /**
         * Общее количество пройденных путей.
         */
        int totalPaths = 0;
        /**
         * Общая длина всех путей.
         */
        int totalLength = 0;
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;
        int draws = 0;
    }

    /**
     * Рекурсивно строит дерево игры, начиная с текущего состояния игры, и обновляет статистику.
     *
     * @param game       текущее состояние игры.
     * @param pathLength длина текущего пути.
     * @param aStats     объект {@code AuxiliaryStats} для накопления статистики.
     */
    public static void recursiveTreeBuilder(Game game, int pathLength, AuxiliaryStats aStats) {

        if (game.isGameOver()) {
            aStats.totalPaths++;
            aStats.totalLength += pathLength;
            String winner = game.isWinner();

            if (winner.equals("победитель не существует")) {
                aStats.draws++;
            } else if (winner.equals(game.players[0].getName())) {
                aStats.winsPlayer1++;
            } else if (winner.equals(game.players[1].getName())) {
                aStats.winsPlayer2++;
            }
            return;
        }

        // Получение списка возможных ходов для текущего игрока
        String currentPlayer = game.getNextPlayerToAct();
        List<Move> availableMoves = game.availableMoves(currentPlayer);

        // Рекурсивный вызов для каждого возможного хода
        for (Move move : availableMoves) {
            Game gameCopy = game.getCopy(game);
            gameCopy.getPlayerAction(move, currentPlayer);
            recursiveTreeBuilder(gameCopy, pathLength + 1, aStats);
        }
    }

    /**
     * Строит полное дерево игры, начиная с корня, и возвращает статистику по дереву.
     *
     * @param root начальное состояние игры.
     * @return объект {@code Stats} с собранной статистикой.
     */
    public static Stats buildGameTree(final Game root) {
        Stats stats = new Stats();
        AuxiliaryStats aStats = new AuxiliaryStats();

        long startTime = System.currentTimeMillis();
        recursiveTreeBuilder(root, 0, aStats);
        long endTime = System.currentTimeMillis();

        stats.numNodes = aStats.totalPaths;
        stats.numTerminalNodes = aStats.winsPlayer1 + aStats.winsPlayer2 + aStats.draws;
        stats.maxDepth = aStats.totalLength / aStats.totalPaths; // средняя длина пути
        stats.branchingFactor = (double) aStats.totalLength / aStats.totalPaths;
        stats.workTimeMS = endTime - startTime;

        return stats;
    }

    /**
     * Строит дерево игры с ограничением по глубине и возвращает статистику по дереву.
     *
     * @param root     начальное состояние игры.
     * @param maxDepth максимальная глубина, до которой следует строить дерево.
     * @return объект {@code Stats} с собранной статистикой.
     */
    public static Stats buildGameTree(final Game root, final int maxDepth) {
        Stats stats = new Stats();
        AuxiliaryStats aStats = new AuxiliaryStats();

        long startTime = System.currentTimeMillis();
        recursiveTreeBuilderWithDepth(root, 0, aStats, maxDepth);
        long endTime = System.currentTimeMillis();

        stats.numNodes = aStats.totalPaths;
        stats.numTerminalNodes = aStats.winsPlayer1 + aStats.winsPlayer2 + aStats.draws;
        stats.maxDepth = aStats.totalLength / aStats.totalPaths; // средняя длина пути
        stats.branchingFactor = (double) aStats.totalLength / aStats.totalPaths;
        stats.workTimeMS = endTime - startTime;

        return stats;
    }

    /**
     * Рекурсивно строит дерево игры с ограничением по глубине, начиная с текущего состояния игры,
     * и обновляет статистику.
     *
     * @param game      текущее состояние игры.
     * @param pathLength длина текущего пути.
     * @param stats     объект {@code AuxiliaryStats} для накопления статистики.
     * @param maxDepth  максимальная глубина рекурсии.
     */
    private static void recursiveTreeBuilderWithDepth(Game game, int pathLength, AuxiliaryStats stats, int maxDepth) {
        if (pathLength >= maxDepth || game.isGameOver()) {
            stats.totalPaths++;
            stats.totalLength += pathLength;
            String winner = game.isWinner();

            if (winner.equals("победитель не существует")) {
                stats.draws++;
            } else if (winner.equals(game.players[0].getName())) {
                stats.winsPlayer1++;
            } else if (winner.equals(game.players[1].getName())) {
                stats.winsPlayer2++;
            }
            return;
        }

        String currentPlayer = game.getNextPlayerToAct();
        List<Move> availableMoves = game.availableMoves(currentPlayer);

        for (Move move : availableMoves) {
            Game gameCopy = game.getCopy(game);
            gameCopy.getPlayerAction(move, currentPlayer);
            recursiveTreeBuilderWithDepth(gameCopy, pathLength + 1, stats, maxDepth);
        }
    }
}

