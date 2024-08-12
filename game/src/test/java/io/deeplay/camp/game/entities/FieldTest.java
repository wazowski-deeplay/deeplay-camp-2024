package io.deeplay.camp.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import io.deeplay.camp.game.entites.Field;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Planet;
import io.deeplay.camp.game.entites.Player;

class FieldTest {

    private Field field;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        field = new Field(5, new SymmetricalGenerator());
        player1 = new Player(0, "player1");
        player2 = new Player(1, "player2");
        for (Planet planet : field.getPlanets()) {
            planet.setOwner(player1); // Присваиваем все планеты игроку 1
        }
    }

    @Test
    public void testFieldCopyConstructor() {
        // Создание оригинального поля
        Field originalField = new Field(5, new SymmetricalGenerator());

        // Копирование поля
        Field copiedField = new Field(originalField);

        // Проверка, что размеры полей совпадают
        assertEquals(originalField.getSize(), copiedField.getSize());

        // Проверка, что клетки совпадают по значениям, но являются разными объектами
        for (int i = 0; i < originalField.getSize(); i++) {
            for (int j = 0; j < originalField.getSize(); j++) {
                Cell originalCell = originalField.getBoard()[i][j];
                Cell copiedCell = copiedField.getBoard()[i][j];

                assertNotSame(originalCell, copiedCell);
                assertEquals(originalCell.x, copiedCell.x);
                assertEquals(originalCell.y, copiedCell.y);

                Planet originalPlanet = originalCell.getPlanet();
                Planet copiedPlanet = copiedCell.getPlanet();

                if (originalPlanet != null) {
                    assertNotSame(originalPlanet, copiedPlanet);
                    assertEquals(originalPlanet.getPoints(), copiedPlanet.getPoints());
                    assertEquals(originalPlanet.getOwner(), copiedPlanet.getOwner());
                    assertNotSame(originalPlanet.getCell(), copiedPlanet.getCell());
                } else {
                    assertNull(copiedPlanet);
                }
            }
        }

        // Проверка, что списки планет совпадают по значению, но состоят из разных объектов
        assertEquals(originalField.getPlanets().size(), copiedField.getPlanets().size());
        for (int i = 0; i < originalField.getPlanets().size(); i++) {
            Planet originalPlanet = originalField.getPlanets().get(i);
            Planet copiedPlanet = copiedField.getPlanets().get(i);
            assertNotSame(originalPlanet, copiedPlanet);
        }
    }

    @Test
    void testFieldSize() {
        assertEquals(5, field.getSize(), "Field size = 5");
    }

    @Test
    void testFieldIsGenerated() {
        assertNotNull(field.getBoard(), "Field board not be null");
        assertEquals(5, field.getBoard().length, "Field board size = 5x5");
        assertEquals(5, field.getBoard()[0].length, "Field board size = 5x5");
    }

    @Test
    void testIsGameOver() {
        assertTrue(field.isGameOver(), "All planets should belong to player1, so the game should be over");

        // Change the owner of one planet
        field.getPlanets().get(2).setOwner(player2);

        assertFalse(field.isGameOver(), "Not all planets belong to the same player, so the game should not be over");

        field.getPlanets().get(2).setOwner(null);

        assertFalse(field.isGameOver(), "Not all planets belong to the same player, so the game should not be over");
    }

    @Test
    void testIsWinner() {
        assertEquals(player1.getName(), field.isWinner());

        // Изменение владельца планеты
        field.getPlanets().get(0).setOwner(player2);
    }

    @Test
    void testChangesField() {
        Field originalField = new Field(4, new SymmetricalGenerator());
        Field copiedField = new Field(originalField);


        // Проверка, что размеры полей совпадают
        assertEquals(originalField.getSize(), copiedField.getSize());

        // Проверка, что клетки совпадают по значениям, но являются разными объектами
        for (int i = 0; i < originalField.getSize(); i++) {
            for (int j = 0; j < originalField.getSize(); j++) {
                Cell originalCell = originalField.getBoard()[i][j];
                Cell copiedCell = copiedField.getBoard()[i][j];

                assertNotSame(originalCell, copiedCell);
                assertEquals(originalCell.x, copiedCell.x);
                assertEquals(originalCell.y, copiedCell.y);

                Planet originalPlanet = originalCell.getPlanet();
                Planet copiedPlanet = copiedCell.getPlanet();

                if (originalPlanet != null) {
                    assertNotSame(originalPlanet, copiedPlanet);
                    assertEquals(originalPlanet.getPoints(), copiedPlanet.getPoints());
                    assertEquals(originalPlanet.getOwner(), copiedPlanet.getOwner());
                    assertNotSame(originalPlanet.getCell(), copiedPlanet.getCell());
                } else {
                    assertNull(copiedPlanet);
                }
            }
        }


    }
}
