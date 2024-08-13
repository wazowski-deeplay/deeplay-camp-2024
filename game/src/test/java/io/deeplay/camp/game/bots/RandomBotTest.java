package io.deeplay.camp.game.bots;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.deeplay.camp.game.utils.PointsCalculator.DIAGONAL_COST;
import static io.deeplay.camp.game.utils.PointsCalculator.DIRECT_COST;

public class RandomBotTest {
    private Field field;
    private RandomBot randomBot;

    @BeforeEach
    public void setUp() {
        field = new Field(2, new SymmetricalGenerator()); // Создаем поле размером 2x2

        randomBot = new RandomBot.Factory().createBot("Player1", field);
        randomBot.connectingPlayer("Player1");
        randomBot.connectingPlayer("Player2");

        randomBot.startGameSession(randomBot.game.getId());

        // Устанавливаем начальные позиции
        randomBot.gameStarted(field);

        // Создаем флоты
        randomBot.createShips(List.of(Ship.ShipType.BASIC), "Player1");
        randomBot.createShips(List.of(Ship.ShipType.MEDIUM), "Player2");
    }

    @Test
    public void testGetMove() {
        Move move1 = new Move(field.getBoard()[0][0], field.getBoard()[0][1], Move.MoveType.ORDINARY, DIRECT_COST + 1);
        Move move2 = new Move(field.getBoard()[0][0], field.getBoard()[1][0], Move.MoveType.ORDINARY, DIRECT_COST + 1);
        Move move3 = new Move(field.getBoard()[0][0], field.getBoard()[1][1], Move.MoveType.ORDINARY, DIAGONAL_COST + 1);

        List<Move> moves = new ArrayList<>();
        moves.add(move1);
        moves.add(move2);
        moves.add(move3);

        Answer result = randomBot.getAnswer(randomBot.game.getField());
        if (result.getShipList() == null) {
            randomBot.getPlayerAction(result.getMove(), "Player1");
        } else {
            randomBot.createShips(result.getShipList(), "Player1");

        }//todo нормальные тесты на бота после рефакторинга
    }
}
