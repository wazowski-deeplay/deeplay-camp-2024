package io.deeplay.camp.game.utils;


import io.deeplay.camp.game.entites.Field;
import io.deeplay.camp.game.entites.Ship;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.deeplay.camp.game.utils.ConvertorFieldToString.convertFieldToString;


public class ConvertorFieldToStringTest {

    Field field;
    Ship ship;
    List<Ship> shipList;

    @BeforeEach
    public void setUp() {
        field = new Field(5, new SymmetricalGenerator());
//        ship = new Ship(Ship.ShipType.BASIC);
//        shipList = new ArrayList<>();
//        shipList.add(ship);
//        field.getBoard()[0][0].setFleet(new Fleet(shipList, field.getBoard()[0][0]));
    }

    @Test
    void print5Cell() {
        String result = convertFieldToString(field);
        System.out.println(result);
    }
}
