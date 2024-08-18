package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.bots.TreeBuilder;
import io.deeplay.camp.game.entites.Field;
import io.deeplay.camp.game.entites.Fleet;
import io.deeplay.camp.game.entites.Game;
import io.deeplay.camp.game.entites.Move;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeBuilderTest {

    // Вспомогательный метод для создания простой игры с заданным исходом.
    private Game createMockGame(String winner, boolean gameOver) {
        return new Game(new Field(5, new SymmetricalGenerator())) {
            @Override
            public boolean isGameOver() {
                return gameOver;
            }

            @Override
            public String isWinner() {
                return winner;
            }

            @Override
            public List<Move> availableMoves(String player) {
                return List.of(); // Возвращаем пустой список ходов
            }

            @Override
            public Game getCopy(Game originalGame) {
                return this; // Возвращаем тот же самый объект для простоты
            }
        };
    }

    @Test
    void testBuildGameTreeWithSingleNode() {
        Game mockGame = createMockGame("победитель не существует", true);
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame);

        assertEquals(1, stats.numNodes, "Должен быть 1 узел");
        assertEquals(1, stats.numTerminalNodes, "Должен быть 1 терминальный узел");
        assertEquals(0, stats.maxDepth, "Максимальная глубина должна быть 0");
        assertEquals(0.0, stats.branchingFactor, "Коэффициент ветвления должен быть 0.0");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");
    }

    @Test
    void testBuildGameTreeWithMultipleMoves() {
        // В игре будет 3 хода, последний из которых приводит к завершению игры
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;
            @Override
            public boolean isGameOver() {
                return moveCount >= 3;
            }

            @Override
            public String isWinner() {
                return moveCount == 3 ? "Player1" : "победитель не существует";
            }

            @Override
            public List<Move> availableMoves(String player) {
                moveCount++;
                return moveCount < 3 ? List.of(new Move(null, null, Move.MoveType.SKIP, 0)) : List.of();
            }

            @Override
            public Game getCopy(Game originalGame) {
                return this;
            }
        };

        mockGame.startGameSession("original-game-id");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame);

        assertEquals(3, stats.numNodes, "Должно быть 3 узла");
        assertEquals(1, stats.numTerminalNodes, "Должен быть 1 терминальный узел");
        assertEquals(1, stats.maxDepth, "Максимальная глубина должна быть 1");
        assertEquals(1.0, stats.branchingFactor, "Коэффициент ветвления должен быть 1.0");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");
    }

    @Test
    void testBuildGameTreeWithMaxDepth() {
        // Глубина дерева ограничена 2
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;

            @Override
            public boolean isGameOver() {
                return moveCount >= 5;
            }

            @Override
            public String isWinner() {
                return "победитель не существует";
            }

            @Override
            public List<Move> availableMoves(String player) {
                moveCount++;
                return List.of(new Move(null, null, Move.MoveType.SKIP, 0));
            }

            @Override
            public Game getCopy(Game originalGame) {
                return this;
            }
        };

        mockGame.startGameSession("original-game-id");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame, 2);

        assertEquals(2, stats.numNodes, "Должно быть 2 узла");
        assertEquals(0, stats.maxDepth, "Максимальная глубина должна быть 0");
        assertEquals(0.0, stats.branchingFactor, "Коэффициент ветвления должен быть 0.0");
    }
}
