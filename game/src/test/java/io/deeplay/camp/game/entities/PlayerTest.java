package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.entites.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {


    @Test
    public void testPlayerConstructor() {
        Player player = new Player(1, "Player1");
        assertEquals(1, player.id);
        assertEquals("Player1", player.getName());
    }
}
