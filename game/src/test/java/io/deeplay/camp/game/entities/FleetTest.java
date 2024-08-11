package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FleetTest {
    private Player player = new Player(0, "test");
    private Player enemy = new Player(1, "enemy");

    private Fleet fleet;
    private Fleet fleetEnemy;


    private Ship ship;
    Cell position = new Cell(0, 0);
    Cell positionEnemy = new Cell(1, 0);


    @BeforeEach
    void setUp() {
        fleetEnemy = new Fleet(positionEnemy, enemy);
        fleet = new Fleet(position, player);
        ship = new Ship(Ship.ShipType.BASIC, fleet);
    }

    @Test
    void getFleetPower() {
        assertEquals(100, fleet.getFleetPower());
    }

    @Test
    void actualFleetPower0() {
        Ship basicShip = new Ship(Ship.ShipType.BASIC, fleet);
        assertEquals(200, fleet.getFleetPower());
    }


    @Test
    void updateShipList() {
        ArrayList<Ship> tryShip = new ArrayList<>();
        Ship basicShip = new Ship(Ship.ShipType.BASIC, fleet);
        tryShip.add(ship);
        tryShip.add(basicShip);
        assertEquals(tryShip, fleet.getShipList());
    }

    @Test
    void testFleetCreation() {
        assertNotNull(fleet);
        assertEquals(position, fleet.getFleetPosition());
        assertEquals(1, fleet.getShipList().size());
        assertEquals(100, fleet.getFleetPower());
    }

    @Test
    void testUpdateShipListWithFleet() {
        Cell secondCell = new Cell(1, 1);
        Fleet secondFleet = new Fleet(secondCell, player);
        ArrayList<Ship> newShipList = new ArrayList<>();
        newShipList.add(new Ship(Ship.ShipType.BASIC, secondFleet));
        newShipList.add(new Ship(Ship.ShipType.BASIC, secondFleet));
        Map<Ship.ShipType, Integer> shipTypeIntegerMap = new HashMap<>();
        shipTypeIntegerMap.put(Ship.ShipType.BASIC, 2);
        List<Ship> shipListTemp = secondFleet.removeShipsFromFleet(shipTypeIntegerMap);


        fleet.addShipsIntoFleet(shipListTemp);

        assertEquals(3, fleet.getShipList().size());
        assertEquals(300, fleet.getFleetPower());
    }

    @Test
    void testUpdateShipWithSameShip() {
        ArrayList<Ship> newShipList = new ArrayList<>();
        newShipList.add(new Ship(Ship.ShipType.BASIC, fleet));
        newShipList.add(new Ship(Ship.ShipType.BASIC, fleet));
        assertThrows(IllegalArgumentException.class, () -> fleet.addShipsIntoFleet(newShipList));
    }

    @Test
    public void testUpdateShipListWithNewShips() {
        ArrayList<Ship> newShipList = new ArrayList<>();
        newShipList.add(new Ship(Ship.ShipType.BASIC, fleet));
        newShipList.add(new Ship(Ship.ShipType.BASIC, fleet));

        assertEquals(3, fleet.getShipList().size());
        assertEquals(300, fleet.getFleetPower());
    }

    @Test
    void getShipList() {
        ArrayList<Ship> tryShip = new ArrayList<>();
        Ship basicShip = new Ship(Ship.ShipType.BASIC, fleet);
        tryShip.add(ship);
        tryShip.add(basicShip);
        assertEquals(tryShip, fleet.getShipList());
    }

    @Test
    void getFleetPosition() {
        assertEquals(position, fleet.getFleetPosition());
    }

    @Test
    void setFleetPosition() {
        fleet.setFleetPosition(new Cell(2, 2));
        assertNotEquals(position, fleet.getFleetPosition());
    }

    @Test
    public void testUpdateShipListWithSingleShip() {
        Ship newShip = new Ship(Ship.ShipType.BASIC, fleet);

        assertEquals(2, fleet.getShipList().size());
        assertEquals(200, fleet.getFleetPower());

    }

    @Test
    public void testSetFleetPosition() {
        Cell newPosition = new Cell(10, 10);
        fleet.setFleetPosition(newPosition);
        assertEquals(newPosition, fleet.getFleetPosition());
    }

    @Test
    void getShipsByShipTypeTest() {
        Map<Ship.ShipType, Integer> shipTypeIntegerMap = fleet.getShipsByShipType();
        assertEquals(shipTypeIntegerMap.get(Ship.ShipType.BASIC), 1);
    }

    @Test
    void removeShipsFromFleetTest() {
        Map<Ship.ShipType, Integer> shipTypeIntegerMap = fleet.getShipsByShipType();
        Ship ship1 = new Ship(Ship.ShipType.BASIC, fleet);
        Ship ship2 = new Ship(Ship.ShipType.BASIC, fleet);

        List<Ship> result = fleet.removeShipsFromFleet(shipTypeIntegerMap);

        assertEquals(2, fleet.getShipList().size());
        assertEquals(200, fleet.getFleetPower());

        assertEquals(result.size(), 1);

    }

    //todo посмотреть что с переопределением хэшкода сейчас StackOverflow хотя Objects.hash должен исключать такое(?)
    @Test
    public void testEqualsAndHashCode() {
//        ArrayList<Ship> otherShipList = new ArrayList<>(List.of(
//                new Ship(Ship.ShipType.BASIC, fleet)
//        ));
//        Fleet sameFleet = new Fleet(otherShipList, new Cell(0, 0), player);
//        Fleet differentFleet = new Fleet(new ArrayList<>(), new Cell(6, 6), enemy);
//
//        assertEquals(fleet, sameFleet);
//        assertNotEquals(fleet, differentFleet);
//
//        assertEquals(fleet.hashCode(), sameFleet.hashCode());
//        assertNotEquals(fleet.hashCode(), differentFleet.hashCode());
    }

    @Test
    public void testFleetsClash() {
        // Создаем корабли
        Ship ship1 = new Ship(Ship.ShipType.BASIC, fleet);
        Ship ship2 = new Ship(Ship.ShipType.MEDIUM, fleet);
        Ship ship3 = new Ship(Ship.ShipType.POWERFUL, fleetEnemy);


        // Выполняем столкновение флотов
        fleet.fleetsClash(fleetEnemy, player, enemy);

        // Проверяем, что проигравший флот удален из списка флотов проигравшего игрока
        assertFalse(enemy.getFleetList().contains(fleetEnemy));
        // Проверяем, что победивший флот остался в списке флотов победившего игрока
        assertTrue(player.getFleetList().contains(fleet));
    }

    @Test
    void testAddFleetMovesValidMoves() {
        Field field = new Field(5, new SymmetricalGenerator());
        Fleet currentFleet = new Fleet(field.getBoard()[2][2], player);
        currentFleet.addFleetMoves(field);
        Move move1 = new Move(field.getBoard()[2][2], field.getBoard()[1][1], Move.MoveType.ORDINARY, 7);
        Move move2 = new Move(field.getBoard()[2][2], field.getBoard()[2][1], Move.MoveType.ORDINARY, 5);
        Move move3 = new Move(field.getBoard()[2][2], field.getBoard()[0][0], Move.MoveType.ORDINARY, 14);
        assertTrue(currentFleet.getFleetMoves().contains(move1));
        assertTrue(currentFleet.getFleetMoves().contains(move2));
        assertTrue(currentFleet.getFleetMoves().contains(move3));
    }

    @Test
    void testAddFleetMovesInvalidMoves() {
        Field field = new Field(20, new SymmetricalGenerator());
        Fleet currentFleet = new Fleet(field.getBoard()[2][2], player);
        currentFleet.addFleetMoves(field);
        Move move1 = new Move(field.getBoard()[2][2], field.getBoard()[9][10], Move.MoveType.ORDINARY, 64); // уже не хватает очков для хода
        Move move2 = new Move(field.getBoard()[2][2], field.getBoard()[9][9], Move.MoveType.ORDINARY, 49);
        assertFalse(currentFleet.getFleetMoves().contains(move1));
        assertTrue(currentFleet.getFleetMoves().contains(move2));
    }

    @Test
    void testCopyConstructor() {
        // Создаем оригинальные объекты для теста
        Player player = new Player(1, "TestPlayer");
        Cell cell = new Cell(2, 3);
        Fleet originalFleet = new Fleet(cell, player);

        // Добавляем корабли в оригинальный флот
        Ship ship1 = new Ship(Ship.ShipType.BASIC, originalFleet);
        Ship ship2 = new Ship(Ship.ShipType.MEDIUM, originalFleet);

        // Создаем копию флота
        Fleet copiedFleet = new Fleet(originalFleet);

        // Проверяем, что размеры флота совпадают
        assertEquals(originalFleet.getFleetPower(), copiedFleet.getFleetPower());

        // Проверяем, что список кораблей в копии и оригинале не совпадает по ссылкам, но имеет одинаковые данные
        List<Ship> originalShips = originalFleet.getShipList();
        List<Ship> copiedShips = copiedFleet.getShipList();
        assertEquals(originalShips.size(), copiedShips.size());
        for (int i = 0; i < originalShips.size(); i++) {
            Ship originalShip = originalShips.get(i);
            Ship copiedShip = copiedShips.get(i);
            assertNotSame(originalShip, copiedShip);
            assertEquals(originalShip.getShipType(), copiedShip.getShipType());
        }

        // Проверяем, что позиция флота и игрок (владелец) тоже корректны
        assertEquals(originalFleet.getFleetPosition(), copiedFleet.getFleetPosition());
        assertSame(originalFleet.getOwner(), copiedFleet.getOwner());

        // Проверяем, что список движений флота корректно скопирован
        List<Move> originalMoves = originalFleet.getFleetMoves();
        List<Move> copiedMoves = copiedFleet.getFleetMoves();
        assertEquals(originalMoves.size(), copiedMoves.size());

        // Проверяем, что изменения в копии не влияют на оригинал
        Ship ship3 = new Ship(Ship.ShipType.BASIC, copiedFleet);
        assertNotEquals(originalFleet.getShipList().size(), copiedFleet.getShipList().size());
    }

}
