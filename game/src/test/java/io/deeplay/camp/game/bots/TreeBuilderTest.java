package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeBuilderTest {

    // Вспомогательный метод для создания игры с заданным победителем и состоянием завершения
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
                return List.of(); // Возвращаем пустой список ходов для терминального узла
            }

            @Override
            public Game getCopy() {
                return this; // Возвращаем тот же самый объект для простоты
            }
        };
    }

    @Test
    void testBuildGameTreeWithSingleNode() {
        Game mockGame = createMockGame("победитель не существует", true);
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame);

        assertAll("Проверка состояния дерева с одним узлом",
                () -> assertEquals(1, stats.numNodes, "Должен быть 1 узел"),
                () -> assertEquals(1, stats.numTerminalNodes, "Должен быть 1 терминальный узел"),
                () -> assertEquals(0, stats.maxDepth, "Максимальная глубина должна быть 0"),
                () -> assertEquals(0.0, stats.branchingFactor, "Коэффициент ветвления должен быть 0.0"),
                () -> assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным")
        );
    }

    @Test
    void testBuildGameTreeWithMultipleMoves() {
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;

            @Override
            public boolean isGameOver() {
                return moveCount >= 3; // Игра заканчивается после 3-го хода
            }

            @Override
            public String isWinner() {
                return moveCount == 3 ? "Player1" : "победитель не существует";
            }

            @Override
            public List<Move> availableMoves(String player) {
                if (!isGameOver()) {
                    return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Возвращаем один ход
                } else {
                    return null; // Нет доступных ходов после завершения игры
                }
            }

            @Override
            public void getPlayerAction(Move move, String player) {
                if (!isGameOver()) { // Увеличиваем счетчик только если игра не завершена
                    moveCount++;
                }
            }

            @Override
            public Game getCopy() {
                return this; // Возвращаем тот же самый объект для простоты
            }
        };

        // Настройка игры
        mockGame.startGameSession("original-game-id");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        // Тестируем построение дерева
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame);

        assertAll("Проверка состояния дерева с несколькими ходами",
                () -> assertEquals(4, stats.numNodes, "Должно быть 4 узлов"),
                () -> assertEquals(1, stats.numTerminalNodes, "Должен быть 1 терминальный узел"),
                () -> assertEquals(3, stats.maxDepth, "Максимальная глубина должна быть 3"),
                () -> assertEquals(1.0, stats.branchingFactor, "Коэффициент ветвления должен быть 1.0"),
                () -> assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным")
        );
    }

    @Test
    void testBuildGameTreeWithMaxDepth() {
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;
            boolean moveExecuted = false;  // флаг, чтобы контролировать выполнение хода

            @Override
            public boolean isGameOver() {
                return moveCount >= 5; // Игра заканчивается после 5-го хода
            }

            @Override
            public String isWinner() {
                return "победитель не существует";
            }

            @Override
            public List<Move> availableMoves(String player) {
                if (!isGameOver()) {
                    return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Возвращаем один ход
                }
                return List.of();
            }

            @Override
            public void getPlayerAction(Move move, String player) {
                if (!moveExecuted) {  // Выполняем ход только один раз для текущего узла
                    moveCount++;
                    moveExecuted = true;  // Помечаем, что ход выполнен
                }
            }

            @Override
            public Game getCopy() {
                moveExecuted = false;  // Сбрасываем флаг для новой копии игры
                return this;  // Возвращаем тот же объект, но с возможностью повторного выполнения хода
            }
        };

        // Настройка игры
        mockGame.startGameSession("original-game-id");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        // Тестируем построение дерева с ограничением по глубине
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame, 2);

        assertAll("Проверка состояния дерева с ограниченной глубиной",
                () -> assertEquals(3, stats.numNodes, "Должно быть 3 узла"),
                () -> assertEquals(2, stats.maxDepth, "Максимальная глубина должна быть 2"),
                () -> assertEquals(1.0, stats.branchingFactor, "Коэффициент ветвления должен быть 1.0")
        );
    }
}
