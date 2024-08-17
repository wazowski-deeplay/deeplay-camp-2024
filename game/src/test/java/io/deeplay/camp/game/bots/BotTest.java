package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    private static class TestBot extends Bot {
        protected TestBot(final String name, final Field field) {
            super(name, field);
        }

        public static class Factory extends BotFactory {

            public Factory() {
            }

            @Override
            public TestBot createBot(final String name, final Field field) {
                return new TestBot(name, field);
            }
        }

        @Override
        protected Move getMove() {
            Random random = new Random();
            List<Move> availableMoves;
            // Возвращаем тестовый ход
            final Field field = game.getField();  // Получаем копию поля
            final Cell[][] board = field.getBoard();
            final Player player = game.getPlayerByName(name);

            long fleetCount = Arrays.stream(board)
                    .flatMap(Arrays::stream)
                    .filter(cell -> cell.getFleet() != null && Objects.equals(cell.getFleet().getOwner().getName(), player.getName())).count();
            if (fleetCount == 0) {
                return new Move(null, null, Move.MoveType.SKIP, null, 0);
            }

            Cell startCell = Arrays.stream(board)
                    .flatMap(Arrays::stream)
                    .filter(cell -> cell.getFleet() != null && Objects.equals(cell.getFleet().getOwner().getName(), player.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Нет клеток с флотом"));
            startCell.getFleet().addFleetMoves(field);

            availableMoves = startCell.getFleet().getFleetMoves();

            if (availableMoves.isEmpty()) {
                return new Move(null, null, Move.MoveType.SKIP, null, 0);
            }

            return availableMoves.get(random.nextInt(availableMoves.size()));
        }

        @Override
        protected List<Ship.ShipType> buyFleets() {
            return List.of(Ship.ShipType.BASIC);
        }
    }

    private Field field;
    private Bot bot;
    private Player player;
    private Game game;

    @BeforeEach
    void setUp() {
        field = new Field(10, new SymmetricalGenerator());
        bot = new TestBot.Factory().createBot("player1", field);
        player = new Player(0, "player1");
        game = bot.game;
    }

    @Test
    void testStartGameSession() {
        assertDoesNotThrow(() -> bot.startGameSession("testGameId"));
        assertDoesNotThrow(() -> bot.connectingPlayer("player1"));
        assertDoesNotThrow(() -> bot.connectingPlayer("player0"));
        assertDoesNotThrow(() -> bot.gameStarted(field));
    }

    @Test
    void testGetPlayerAction_ValidOrdinaryMove() {
        player = new Player(0, "player1");
        Player player2 = new Player(1, "player2");

        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);

        game.connectingPlayer(player.getName());
        game.connectingPlayer(player2.getName());
        game.gameStarted(field);
        game.getPlayerNames().put(player.getName(), player);
        game.getPlayerNames().put(player2.getName(), player2);

        game.createShips(startShips, player.getName());
        game.createShips(startShips, player2.getName());
        player.decreaseTotalGamePoints(startShips.get(0).getShipPower() / 10);
        player2.decreaseTotalGamePoints(startShips.get(0).getShipPower() / 10);

        final Answer answer = bot.getAnswer(game.getField());

        if (answer.getShipList() != null) {
            game.createShips(answer.getShipList(), game.getNextPlayerToAct());
        } else {
            assertDoesNotThrow(() -> bot.getPlayerAction(answer.getMove(), player.getName()));
        }
    }

    @Test
    void testGetPlayerAction_InvalidPlayer() {
        Move move = new Move(new Cell(0, 0), new Cell(1, 1), Move.MoveType.ORDINARY, new ArrayList<>(), 7);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bot.getPlayerAction(move, "invalidPlayer"));
        assertEquals("Отсутствует игрок: invalidPlayer", exception.getMessage());
    }

    @Test
    void testGetPlayerAction_InvalidTurn() {
        Player player2 = new Player(1, "player2");
        Move move = new Move(new Cell(0, 0), new Cell(1, 1), Move.MoveType.ORDINARY, new ArrayList<>(), 7);

        game.connectingPlayer(player.getName());
        game.connectingPlayer(player2.getName());
        game.gameStarted(field);
        game.getPlayerNames().put(player.getName(), player);
        game.getPlayerNames().put(player2.getName(), player2);
        game.switchPlayerToAct();

        /*IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bot.getPlayerAction(move, player.getName()));
        assertEquals("Сейчас не ход игрока: player1", exception.getMessage());*/
    }

    @Test
    void testGameEnded() {
        assertDoesNotThrow(() -> bot.gameEnded("player1"));
    }

    @Test
    void testEndGameSession() {
        assertDoesNotThrow(() -> bot.endGameSession());
    }
}
