package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.interfaces.GalaxyListener;
import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.Move;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//todo переделать и добавить тесты сейчас заглушка для CI
class GameTest {
    private Game originalGame;
    private Game copiedGame;
    private Field field;

    @BeforeEach
    public void setUp() {
        field = new Field(10, new SymmetricalGenerator());

        originalGame = new Game(field);
        originalGame.startGameSession("original-game-id");
        originalGame.connectingPlayer("Player1");
        originalGame.connectingPlayer("Player2");
        originalGame.gameStarted(field);

        copiedGame = new Game(originalGame);  // Создаем копию уже настроенной игры
    }

    @Test
    public void testCopyConstructor() {
        assertEquals(originalGame.getId(), copiedGame.getId());

        for (int i = 0; i < 2; i++) {
            Player originalPlayer = originalGame.players[i];
            Player copiedPlayer = copiedGame.players[i];
            assertNotNull(copiedPlayer);
            assertEquals(originalPlayer.getId(), copiedPlayer.getId());
            assertEquals(originalPlayer.getName(), copiedPlayer.getName());
            assertNotSame(originalPlayer, copiedPlayer);
        }

        for (Map.Entry<String, Cell> entry : originalGame.getPlayerStartPosition().entrySet()) {
            Cell originalCell = entry.getValue();
            Cell copiedCell = copiedGame.getPlayerStartPosition().get(entry.getKey());
            assertNotNull(copiedCell);
            assertEquals(originalCell.x, copiedCell.x);
            assertEquals(originalCell.y, copiedCell.y);
            assertNotSame(originalCell, copiedCell);
        }

        assertEquals(originalGame.getField().getSize(), copiedGame.getField().getSize());
        for (int x = 0; x < originalGame.getField().getSize(); x++) {
            for (int y = 0; y < originalGame.getField().getSize(); y++) {
                Cell originalCell = originalGame.getField().getBoard()[x][y];
                Cell copiedCell = copiedGame.getField().getBoard()[x][y];
                assertNotNull(copiedCell);
                assertEquals(originalCell.x, copiedCell.x);
                assertEquals(originalCell.y, copiedCell.y);
            }
        }

        for (int i = 0; i < 2; i++) {
            Player originalPlayer = originalGame.players[i];
            Player copiedPlayer = copiedGame.players[i];
            assertNotSame(originalPlayer, copiedPlayer);
        }
    }

    @Test
    public void testDrawOnConsecutiveSkips0() {
        Game game = new Game(field);

        game.startGameSession("test-game");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        assertTrue(game.isGameOver());
        assertEquals("победитель не существует", game.isWinner());
    }

    @Test
    public void testDrawOnConsecutiveSkips1() {
        Game game = new Game(field);

        game.startGameSession("test-game");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        new Fleet(field.getBoard()[0][0], game.getPlayerByName("Player2"));

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[0][0], field.getBoard()[1][1], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        assertFalse(game.isGameOver());
    }

    @Test
    public void testDrawOnConsecutiveSkips2() {
        Game game = new Game(field);

        game.startGameSession("test-game");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        new Fleet(field.getBoard()[0][0], game.getPlayerByName("Player2"));

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[0][0], field.getBoard()[1][1], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[1][1], field.getBoard()[2][2], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[2][2], field.getBoard()[3][3], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[3][3], field.getBoard()[4][4], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[4][4], field.getBoard()[5][5], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[5][5], field.getBoard()[6][6], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        assertFalse(game.isGameOver());
    }

    @Test
    public void testDrawOnConsecutiveSkips3() {
        Game game = new Game(field);

        game.startGameSession("test-game");
        game.connectingPlayer("Player1");
        game.connectingPlayer("Player2");
        game.gameStarted(field);

        new Fleet(field.getBoard()[0][0], game.getPlayerByName("Player2"));
        new Fleet(field.getBoard()[9][0], game.getPlayerByName("Player1"));

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[0][0], field.getBoard()[1][1], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][0], field.getBoard()[9][1], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[1][1], field.getBoard()[3][3], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][1], field.getBoard()[9][2], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[3][3], field.getBoard()[4][4], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][2], field.getBoard()[9][3], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[4][4], field.getBoard()[5][5], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][3], field.getBoard()[9][4], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[5][5], field.getBoard()[6][6], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][4], field.getBoard()[9][5], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[6][6], field.getBoard()[7][7], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][5], field.getBoard()[9][6], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[7][7], field.getBoard()[8][8], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][6], field.getBoard()[9][7], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(field.getBoard()[8][8], field.getBoard()[9][9], Move.MoveType.ORDINARY, 7), "Player2");

        game.getPlayerAction(new Move(field.getBoard()[9][7], field.getBoard()[9][8], Move.MoveType.ORDINARY, 5), "Player1");
        game.getPlayerAction(new Move(null, null, Move.MoveType.SKIP, 0), "Player2");

        assertFalse(game.isGameOver());
    }
}
