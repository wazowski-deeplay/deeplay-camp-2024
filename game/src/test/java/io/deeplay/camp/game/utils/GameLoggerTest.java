package io.deeplay.camp.game.utils;

import io.deeplay.camp.game.entites.*;

import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GameLoggerTest {

    @Test
    void gameLoggerTest() {
        Field field = new Field(5, new SymmetricalGenerator());
        GameLogger gameLogger = new GameLogger(field);
        String gameId = "TestID";
        String PlayerName = "Test";
        String PlayerName2 = "Test2";
        Player player = new Player(0, PlayerName);

        Cell endPositionSM = new Cell(1, 1);
        Cell startPositionSM = new Cell(0, 4);

        Move move = new Move(startPositionSM, endPositionSM, Move.MoveType.ORDINARY, new ArrayList<>(), 5);
        List<Ship.ShipType> ships = new ArrayList<>();
        ships.add(Ship.ShipType.BASIC);
        gameLogger.startGameSession(gameId);
        gameLogger.connectingPlayer(PlayerName);
        gameLogger.connectingPlayer(PlayerName2);
        gameLogger.gameStarted(field);
        gameLogger.createShips(ships, PlayerName);
        gameLogger.createShips(ships, PlayerName2);
        player.decreaseTotalGamePoints(ships.get(0).getShipPower() / 10);
        gameLogger.getPlayerAction(move, PlayerName);
        gameLogger.gameEnded(PlayerName);
        gameLogger.endGameSession();
    }
}
