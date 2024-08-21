package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.Game;
import io.deeplay.camp.game.entites.Move;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        int totalPaths;
        int totalLength;
        int winsPlayer1;
        int winsPlayer2;
        int draws;
        int numNodes;  // Счетчик всех узлов
        int totalChildren;  // Общее количество дочерних узлов
        int numParents;  // Количество узлов, имеющих дочерние узлы
        int maxDepth;  // Максимальная глубина
    }

    /**
     * Рекурсивно строит дерево игры, начиная с текущего состояния игры, и обновляет статистику.
     * Избегает повторного посещения состояний, которые уже были исследованы.
     *
     * @param game       текущее состояние игры.
     * @param pathLength длина текущего пути.
     * @param aStats     объект {@code AuxiliaryStats} для накопления статистики.
     * @param visitedStates Множество уже посещенных состояний.
     */
    public static void recursiveTreeBuilder(Game game, int pathLength, AuxiliaryStats aStats, Set<String> visitedStates) {
        String currentState = game.getStateIdentifier(); // Получаем уникальный идентификатор состояния

        // Если текущее состояние уже было посещено, прекращаем обработку
        if (visitedStates.contains(currentState)) {
            return;
        }

        // Добавляем текущее состояние в набор посещенных состояний
        visitedStates.add(currentState);
        aStats.numNodes++;  // Увеличиваем количество узлов

        if (game.isGameOver()) {
            aStats.totalPaths++;
            aStats.totalLength += pathLength;
            aStats.maxDepth = Math.max(aStats.maxDepth, pathLength);  // Обновляем максимальную глубину
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

        String currentPlayer = game.getNextPlayerToAct();
        List<Move> availableMoves = game.availableMoves(currentPlayer);

        if (!availableMoves.isEmpty()) {
            aStats.numParents++;  // Увеличиваем количество узлов-родителей
            aStats.totalChildren += availableMoves.size();  // Добавляем количество детей
        }

        for (Move move : availableMoves) {
            Game gameCopy = game.getCopy();
            gameCopy.getPlayerAction(move, currentPlayer);
            recursiveTreeBuilder(gameCopy, pathLength + 1, aStats, visitedStates);
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
        Set<String> visitedStates = new HashSet<>(); // Множество для хранения посещенных состояний

        long startTime = System.currentTimeMillis();
        recursiveTreeBuilder(root, 0, aStats, visitedStates);
        long endTime = System.currentTimeMillis();

        stats.numNodes = aStats.numNodes;
        stats.numTerminalNodes = aStats.totalPaths;
        stats.maxDepth = aStats.maxDepth;
        stats.branchingFactor = aStats.numParents == 0 ? 0 : (double) aStats.totalChildren / aStats.numParents;
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

        stats.numNodes = aStats.numNodes;
        stats.numTerminalNodes = aStats.totalPaths;
        stats.maxDepth = aStats.maxDepth;
        stats.branchingFactor = aStats.numParents == 0 ? 0 : (double) aStats.totalChildren / aStats.numParents;
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
        stats.numNodes++;  // Увеличиваем количество узлов

        // Если достигнута максимальная глубина или игра окончена
        if (pathLength >= maxDepth || game.isGameOver()) {
            stats.totalPaths++;
            stats.totalLength += pathLength;
            stats.maxDepth = Math.max(stats.maxDepth, pathLength);  // Обновляем максимальную глубину
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

        if (!availableMoves.isEmpty()) {
            stats.numParents++;  // Увеличиваем количество узлов-родителей
            stats.totalChildren += availableMoves.size();  // Добавляем количество детей
        }

        // Рекурсивно обходим все возможные ходы
        for (Move move : availableMoves) {
            Game gameCopy = game.getCopy();
            gameCopy.getPlayerAction(move, currentPlayer);
            recursiveTreeBuilderWithDepth(gameCopy, pathLength + 1, stats, maxDepth);
        }
    }
}
