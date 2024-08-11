package io.deeplay.camp.game.utils;


import io.deeplay.camp.game.domain.GalaxyListener;
import io.deeplay.camp.game.domain.GameTypes;
import io.deeplay.camp.game.entites.*;
import org.slf4j.LoggerFactory;


import org.slf4j.Logger;

import java.util.List;


public class GameLogger implements GalaxyListener {
    static Logger LOGGER;
    private Game game;

    private static final Logger logger = LoggerFactory.getLogger(
            GalaxyListener.class);

    public GameLogger(Field field) {
        Field field1 = new Field(field);
        game = new Game(field1);
    }

    @Override
    public void startGameSession(String gameId, GameTypes gameType) {
        game.startGameSession(gameId, gameType);
        logger.info("Начало игровой сессии {}", gameId);
        logger.info("Игра в формате {}", gameType.toString());
    }

    @Override
    public void connectingPlayer(String waitingPlayerName) {
        game.connectingPlayer(waitingPlayerName);
        logger.info("Подключился игрок {}", waitingPlayerName);
    }

    @Override
    public void gameStarted(Field field) {
        game.gameStarted(field);
//        this.field = field;
        logger.info("Начало игры ");
        String fieldString = ConvertorFieldToString.convertFieldToString(field);
        logger.info("\n{}", fieldString);
    }

    @Override
    public void getPlayerAction(Move move, String playerName) {
        game.getPlayerAction(move, playerName);
        logger.info("Сходил игрок {}", playerName);
        logger.info(move.toString());
        if (game.getField() != null) {
            String fieldString = ConvertorFieldToString.convertFieldToString(game.getField());
            logger.info("\n{}", fieldString);
        } else {
            logger.error("ПУСТОЕ ПОЛЕ");
        }
    }

    @Override
    public void addCredits() {
        game.addCredits();
        logger.info("Начисление очков игры игрокам");
        logger.info("Баланс " + game.players[0].getName() + " - " + game.players[0].getTotalGamePoints());
        logger.info("Баланс " + game.players[1].getName() + " - " + game.players[1].getTotalGamePoints());
    }

    @Override
    public void createShips(List<Ship.ShipType> ships, String playerName) {
        game.createShips(ships, playerName);
        logger.info(playerName + " создал " + ships.size() + " кораблей");
    }

    @Override
    public void gameEnded(String winner) {
        game.gameEnded(winner);
        logger.info("Игра закончилась. Выиграл игрок:{}", winner);
    }


    @Override
    public void endGameSession() {
        game.endGameSession();
        logger.info("Конец игровой сессии");
    }


}
