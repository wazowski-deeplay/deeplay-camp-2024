package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.KMeansGenerator;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import io.deeplay.camp.game.entites.boardGenerator.TreeBuilderGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

            @Override
            public String getStateIdentifier() {
                // Здесь вы можете использовать любые данные, которые делают состояние уникальным.
                // Например, положение фигур на доске, текущий игрок и т. д.
                List<String> identifiers = new ArrayList<>();
                for (Move lm : this.availableMoves(this.getNextPlayerToAct())) {
                    identifiers.add(lm.toString());
                }

                // Объединяем элементы списка в одну строку с разделителем ","
                String movesString = String.join(",", identifiers);

                // Возвращаем комбинацию строки ходов и текущего игрока
                return movesString;
            }
        };
    }

    @Test
    void testBuildGameTreeWithSingleNode() {
        Game mockGame = createMockGame("победитель не существует", true);
        mockGame.startGameSession("original-game-id");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

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
                return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Возвращаем один ход
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

            @Override
            public String getStateIdentifier() {
                // Здесь вы можете использовать любые данные, которые делают состояние уникальным.
                // Например, положение фигур на доске, текущий игрок и т. д.
                List<String> identifiers = new ArrayList<>();
                for (Move lm : Objects.requireNonNull(this.availableMoves(this.getNextPlayerToAct()))) {
                    if (lm == null) return "gameEnded";
                    identifiers.add(lm.toString());
                }

                // Объединяем элементы списка в одну строку с разделителем ","
                String movesString = String.join(",", identifiers);

                // Возвращаем комбинацию строки ходов и текущего игрока
                return movesString + moveCount;
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
                return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Возвращаем один ход
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

            @Override
            public String getStateIdentifier() {
                // Здесь вы можете использовать любые данные, которые делают состояние уникальным.
                // Например, положение фигур на доске, текущий игрок и т. д.
                List<String> identifiers = new ArrayList<>();
                for (Move lm : Objects.requireNonNull(this.availableMoves(this.getNextPlayerToAct()))) {
                    if (lm == null) return "gameEnded";
                    identifiers.add(lm.toString());
                }

                // Объединяем элементы списка в одну строку с разделителем ","
                String movesString = String.join(",", identifiers);

                // Возвращаем комбинацию строки ходов и текущего игрока
                return movesString + moveCount;
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

    @Test
    public void testBuildGameTree0() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(3, new SymmetricalGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Игрок 1");
        game.connectingPlayer("Игрок 2");
        game.gameStarted(field);


        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats.toString());
    }

    @Test
    public void testBuildGameTree1() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(3, new SymmetricalGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        game.createShips(startShips, game.getPlayerByName("Player1").getName());
        game.createShips(startShips, game.getPlayerByName("Player2").getName());

        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats.toString());
    }

    @Test
    public void testBuildGameTree2() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(3, new KMeansGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        game.createShips(startShips, game.getPlayerByName("Player1").getName());
        game.createShips(startShips, game.getPlayerByName("Player2").getName());

        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats);
    }

    @Test
    public void testBuildGameTree3() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(5, new SymmetricalGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        game.createShips(startShips, game.getPlayerByName("Player1").getName());
        game.createShips(startShips, game.getPlayerByName("Player2").getName());

        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats);
    }

    @Test
    public void testBuildGameTree4() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(5, new KMeansGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        game.createShips(startShips, game.getPlayerByName("Player1").getName());
        game.createShips(startShips, game.getPlayerByName("Player2").getName());

        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats);
    }
    @Test
    public void testBuildGameTree5() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(3, new TreeBuilderGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        game.createShips(startShips, game.getPlayerByName("Player1").getName());
        game.createShips(startShips, game.getPlayerByName("Player2").getName());

        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats);
    }


    @Test
    void testBuildGameTreeWithLimitedDepth1() {
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;
            boolean moveExecuted = false;

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
                return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Один ход
            }

            @Override
            public void getPlayerAction(Move move, String player) {
                if (!moveExecuted) {
                    moveCount++;
                    moveExecuted = true;
                }
            }

            @Override
            public Game getCopy() {
                moveExecuted = false;
                return this;
            }

            @Override
            public String getStateIdentifier() {
                return "state-" + moveCount;
            }
        };

        // Настройка игры
        mockGame.startGameSession("game-depth-1");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        // Тестируем построение дерева с глубиной 1
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame, 1);

        assertAll("Проверка состояния дерева с глубиной 1",
                () -> assertEquals(2, stats.numNodes, "Должно быть 2 узла"),
                () -> assertEquals(1, stats.maxDepth, "Максимальная глубина должна быть 1"),
                () -> assertEquals(1.0, stats.branchingFactor, "Коэффициент ветвления должен быть 1.0")
        );
    }

    @Test
    void testBuildGameTreeWithLimitedDepth2() {
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;
            boolean moveExecuted = false;

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
                return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Один ход
            }

            @Override
            public void getPlayerAction(Move move, String player) {
                if (!moveExecuted) {
                    moveCount++;
                    moveExecuted = true;
                }
            }

            @Override
            public Game getCopy() {
                moveExecuted = false;
                return this;
            }

            @Override
            public String getStateIdentifier() {
                return "state-" + moveCount;
            }
        };

        // Настройка игры
        mockGame.startGameSession("game-depth-2");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        // Тестируем построение дерева с глубиной 2
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame, 2);

        assertAll("Проверка состояния дерева с глубиной 2",
                () -> assertEquals(3, stats.numNodes, "Должно быть 3 узла"),
                () -> assertEquals(2, stats.maxDepth, "Максимальная глубина должна быть 2"),
                () -> assertEquals(1.0, stats.branchingFactor, "Коэффициент ветвления должен быть 1.0")
        );
    }

    @Test
    void testBuildGameTreeWithLimitedDepth3() {
        Game mockGame = new Game(new Field(5, new SymmetricalGenerator())) {
            int moveCount = 0;
            boolean moveExecuted = false;

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
                return List.of(new Move(null, null, Move.MoveType.SKIP, new ArrayList<>(), 0)); // Один ход
            }

            @Override
            public void getPlayerAction(Move move, String player) {
                if (!moveExecuted) {
                    moveCount++;
                    moveExecuted = true;
                }
            }

            @Override
            public Game getCopy() {
                moveExecuted = false;
                return this;
            }

            @Override
            public String getStateIdentifier() {
                return "state-" + moveCount;
            }
        };

        // Настройка игры
        mockGame.startGameSession("game-depth-3");
        mockGame.connectingPlayer("Player1");
        mockGame.connectingPlayer("Player2");
        mockGame.gameStarted(mockGame.getField());
        new Fleet(mockGame.getField().getBoard()[0][0], mockGame.getPlayerByName("Player2"));
        new Fleet(mockGame.getField().getBoard()[1][1], mockGame.getPlayerByName("Player1"));

        // Тестируем построение дерева с глубиной 3
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(mockGame, 3);

        assertAll("Проверка состояния дерева с глубиной 3",
                () -> assertEquals(4, stats.numNodes, "Должно быть 4 узла"),
                () -> assertEquals(3, stats.maxDepth, "Максимальная глубина должна быть 3"),
                () -> assertEquals(1.0, stats.branchingFactor, "Коэффициент ветвления должен быть 1.0")
        );
    }
    @Test
    public void testBuildGameTreeWithDepth() {
        // Создание игрового поля и начальной конфигурации
        Field field = new Field(3, new TreeBuilderGenerator());
        Game game = new Game(field);

        // Инициализация игроков
        game.startGameSession("0000");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        game.createShips(startShips, game.getPlayerByName("Player1").getName());
        game.createShips(startShips, game.getPlayerByName("Player2").getName());

        // Проверяем, что игра не закончена
        assertFalse(game.isGameOver(), "Игра должна продолжаться");

        // Строим дерево игры и получаем статистику
        TreeBuilder.Stats stats = TreeBuilder.buildGameTree(game, 3);

        // Проверяем статистику
        assertNotNull(stats);
        assertTrue(stats.numNodes > 0, "Количество узлов должно быть больше 0");
        assertTrue(stats.numTerminalNodes >= 0, "Количество терминальных узлов должно быть неотрицательным");
        assertTrue(stats.maxDepth >= 0, "Максимальная глубина должна быть неотрицательной");
        assertTrue(stats.branchingFactor >= 0, "Коэффициент ветвления должен быть неотрицательным");
        assertTrue(stats.workTimeMS >= 0, "Время работы должно быть неотрицательным");

        System.out.println(stats);
    }
}
