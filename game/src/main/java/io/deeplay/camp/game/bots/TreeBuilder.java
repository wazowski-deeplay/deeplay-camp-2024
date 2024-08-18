package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.Field;
import io.deeplay.camp.game.entites.Game;
import io.deeplay.camp.game.entites.Move;
import io.deeplay.camp.game.entites.Ship;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;

import java.util.ArrayList;
import java.util.List;


public class TreeBuilder {
    static class Stats {
        int numNodes;
        int numTerminalNodes;
        int maxDepth;
        double branchingFactor;
        long workTimeMS;
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

    static class DFSStats {
        int totalPaths = 0;
        int totalLength = 0;
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;
        int draws = 0;
    }

    public static void dfs(Game game, int pathLength, DFSStats stats) {
        // Проверка завершения игры
        if (game.isGameOver()) {
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

        // Получение списка возможных ходов для текущего игрока
        String currentPlayer = game.getNextPlayerToAct();
        List<Move> availableMoves = game.availableMoves(currentPlayer);

        // Рекурсивный вызов для каждого возможного хода
        for (Move move : availableMoves) {
            Game gameCopy = game.getCopy(game);
            gameCopy.getPlayerAction(move, currentPlayer);
            dfs(gameCopy, pathLength + 1, stats);
        }
    }

    public static Stats buildGameTree(final Game root) {
        Stats stats = new Stats();
        DFSStats dfsStats = new DFSStats();

        long startTime = System.currentTimeMillis();
        dfs(root, 0, dfsStats);
        long endTime = System.currentTimeMillis();

        stats.numNodes = dfsStats.totalPaths;
        stats.numTerminalNodes = dfsStats.winsPlayer1 + dfsStats.winsPlayer2 + dfsStats.draws;
        stats.maxDepth = dfsStats.totalLength / dfsStats.totalPaths; // средняя длина пути
        stats.branchingFactor = (double) dfsStats.totalLength / dfsStats.totalPaths;
        stats.workTimeMS = endTime - startTime;

        return stats;
    }

    public static Stats buildGameTree(final Game root, final int maxDepth) {
        Stats stats = new Stats();
        DFSStats dfsStats = new DFSStats();

        long startTime = System.currentTimeMillis();
        dfsWithDepth(root, 0, dfsStats, maxDepth);
        long endTime = System.currentTimeMillis();

        stats.numNodes = dfsStats.totalPaths;
        stats.numTerminalNodes = dfsStats.winsPlayer1 + dfsStats.winsPlayer2 + dfsStats.draws;
        stats.maxDepth = dfsStats.totalLength / dfsStats.totalPaths; // средняя длина пути
        stats.branchingFactor = (double) dfsStats.totalLength / dfsStats.totalPaths;
        stats.workTimeMS = endTime - startTime;

        return stats;
    }

    private static void dfsWithDepth(Game game, int pathLength, DFSStats stats, int maxDepth) {
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
            dfsWithDepth(gameCopy, pathLength + 1, stats, maxDepth);
        }
    }
}

