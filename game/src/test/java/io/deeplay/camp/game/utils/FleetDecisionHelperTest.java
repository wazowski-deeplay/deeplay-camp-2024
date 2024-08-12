package io.deeplay.camp.game.utils;

import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Field;
import io.deeplay.camp.game.entites.Fleet;
import io.deeplay.camp.game.entites.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FleetDecisionHelperTest {

    @Test
    void testHasEnoughPoints() {
        Player player1 = new Player(0, "Player0");
        Player player2 = new Player(1, "Player1");
        player2.decreaseTotalGamePoints(55);

        assertTrue(FleetDecisionHelper.hasEnoughPoints(player1));
        assertFalse(FleetDecisionHelper.hasEnoughPoints(player2));
    }
    @Test
    void testIsAvailablePosition() {
        Field field = new Field(4);
        Player player1 = new Player(0, "Player0");
        Player player2 = new Player(1, "Player1");
        new Fleet(field.getBoard()[0][3], player2);
        assertFalse(FleetDecisionHelper.shouldBuyFleet(field.getBoard()[0][3], player1));
        assertTrue(FleetDecisionHelper.shouldBuyFleet(field.getBoard()[3][0], player1));
    }
}