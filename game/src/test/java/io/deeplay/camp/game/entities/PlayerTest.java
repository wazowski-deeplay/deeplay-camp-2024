package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {


    @Test
    public void testPlayerConstructor() {
        Player player = new Player(1, "Player1");
        assertEquals(1, player.id);
        assertEquals("Player1", player.getName());
    }
    @Test
    public void testPlayerCopyConstructor() {
        // Создаем оригинальный объект Player
        Player original = new Player(1, "Player1");
        Field field = new Field(5, new SymmetricalGenerator());

        // Добавляем флоты и планеты для проверки глубокого копирования
        Fleet fleet1 = new Fleet(field.getBoard()[0][0], original);
        Fleet fleet2 = new Fleet(field.getBoard()[1][1], original);

        // Создаем копию с помощью конструктора копирования
        Player copy = new Player(original);

        // Проверяем, что примитивные и immutable поля скопированы правильно
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getTotalGamePoints(), copy.getTotalGamePoints());

        // Проверяем, что списки флотов и планет скопированы правильно
        assertEquals(original.getFleetList().size(), copy.getFleetList().size());
        assertEquals(original.getControlledPlanet().size(), copy.getControlledPlanet().size());

        // Проверяем, что объекты внутри списков были скопированы, а не просто ссылки на оригиналы
        for (int i = 0; i < original.getFleetList().size(); i++) {
            assertNotSame(original.getFleetList().get(i), copy.getFleetList().get(i));
            assertNotEquals(original.getFleetList().get(i), copy.getFleetList().get(i));
        }

        for (int i = 0; i < original.getControlledPlanet().size(); i++) {
            assertNotSame(original.getControlledPlanet().get(i), copy.getControlledPlanet().get(i));
            assertEquals(original.getControlledPlanet().get(i), copy.getControlledPlanet().get(i));
        }

        // Проверяем, что изменение копии не влияет на оригинал
        Fleet newFleet = new Fleet(field.getBoard()[3][3], copy);
        assertEquals(2, original.getFleetList().size());
        assertEquals(3, copy.getFleetList().size());

        Planet newPlanet = new Planet(10);
        newPlanet.setCell(field.getBoard()[4][4]);

        copy.getControlledPlanet().add(newPlanet);
        assertEquals(0, original.getControlledPlanet().size());
        assertEquals(1, copy.getControlledPlanet().size());
    }
}
