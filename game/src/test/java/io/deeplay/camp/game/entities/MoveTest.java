package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveTest {
    private Cell startPositionSM;
    private Cell endPositionSM;
    private Move move;

    @BeforeEach
    public void setUp() {
        startPositionSM = new Cell(1, 1);
        endPositionSM = new Cell(5, 5);
        move = new Move(startPositionSM, endPositionSM, Move.MoveType.ORDINARY, new ArrayList<>(), 5);
    }

    @Test
    public void testMoveCreation() {
        assertNotNull(move);
        assertEquals(startPositionSM, move.startPosition());
        assertEquals(endPositionSM, move.endPosition());
        assertEquals(Move.MoveType.ORDINARY, move.moveType());
    }

    @Test
    public void testToString() {
        String expectedString = "start position = [B, 1] end position = [F, 5] cost = 5";
        assertEquals(expectedString, move.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        Move sameMove = new Move(new Cell(1, 1), new Cell(5, 5), Move.MoveType.ORDINARY, new ArrayList<>(), 5);
        Move differentMove = new Move(new Cell(2, 2), new Cell(5, 5), Move.MoveType.ORDINARY, new ArrayList<>(), 5);

        assertEquals(move, sameMove);
        assertNotEquals(move, differentMove);

        assertEquals(move.hashCode(), sameMove.hashCode());
        assertNotEquals(move.hashCode(), differentMove.hashCode());
    }

    Cell startPosition = new Cell(0, 0);
    Cell toPosition = new Cell(1, 1);
    Cell endPosition = new Cell(2, 2);
    private final Move moveOrdinary = new Move(startPosition, toPosition, Move.MoveType.ORDINARY, new ArrayList<>(), 5);

    @Test
    void testToString1() {
        assertEquals("start position = [A, 0] end position = [B, 1] cost = 5", moveOrdinary.toString());
    }

    @Test
    void startPosition() {
        assertEquals(startPosition, moveOrdinary.startPosition());
    }

    @Test
    void endPosition() {
        assertEquals(toPosition, moveOrdinary.endPosition());
    }

    @Test
    void moveType() {
        assertEquals(Move.MoveType.ORDINARY, moveOrdinary.moveType());
    }

    @Test
    void testMoveToEmptyCell() {
        Field field = new Field(10, new SymmetricalGenerator());
        Move move = new Move(field.getBoard()[0][0], field.getBoard()[2][2], Move.MoveType.ORDINARY, new ArrayList<>(), 5);
        Player player = new Player(0, "0");
        Fleet fleet = new Fleet(field.getBoard()[0][0], player);
        field.getBoard()[0][0].setFleet(fleet);
        move.makeMove(player);
        assertNull(field.getBoard()[0][0].getFleet());
        assertNotNull(field.getBoard()[2][2].getFleet());
        assertEquals(field.getBoard()[2][2].getFleet(), fleet);

    }

    @Test
    void testMoveToStrongEnemyFleet() {
        Field field = new Field(10, new SymmetricalGenerator());
        Move move = new Move(field.getBoard()[0][0], field.getBoard()[2][2], Move.MoveType.ORDINARY, new ArrayList<>(), 5);
        Player player1 = new Player(0, "0");
        Player player2 = new Player(1, "1");
        Fleet fleet1 = new Fleet(field.getBoard()[0][0], player1);
        Fleet fleet2 = new Fleet(field.getBoard()[2][2], player2);
        field.getBoard()[0][0].setFleet(fleet1);
        field.getBoard()[2][2].setFleet(fleet2);
        move.makeMove(player1);
        assertNull(field.getBoard()[0][0].getFleet());
        assertNotNull(field.getBoard()[2][2].getFleet());
        assertTrue(player2.getFleetList().contains(fleet2)); // по нашему методу сражения флотов, при одинаковой силе флотов, побеждает враг
        assertFalse(player1.getFleetList().contains(fleet1));
    }

    @Test
    void testMoveToWeakEnemyFleet() {
        Field field = new Field(10, new SymmetricalGenerator());
        Player player1 = new Player(0, "0");
        Player player2 = new Player(1, "1");
        Fleet fleet1 = new Fleet(field.getBoard()[0][0], player1);
        Fleet fleet2 = new Fleet(field.getBoard()[2][2], player2);
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship(Ship.ShipType.MEDIUM, fleet1));
        Move move = new Move(field.getBoard()[0][0], field.getBoard()[2][2], Move.MoveType.ORDINARY, ships, 16);
        move.makeMove(player1);
        assertNull(field.getBoard()[0][0].getFleet());
        assertNotNull(field.getBoard()[2][2].getFleet());
        assertFalse(player2.getFleetList().contains(fleet2)); // у плеер1 сила больше, поэтому у плеера2 флот уничтожается
        assertTrue(player1.getFleetList().contains(fleet1));
    }

    @Test
    void testMoveToJoinFleet() {
        Field field = new Field(10, new SymmetricalGenerator());
        Move move = new Move(field.getBoard()[0][0], field.getBoard()[2][2], Move.MoveType.ORDINARY, new ArrayList<>(), 5);
        Player player = new Player(0, "0");
        Fleet fleet1 = new Fleet(field.getBoard()[0][0], player);
        new Fleet(field.getBoard()[2][2], player);
        move.makeMove(player);
        assertNull(field.getBoard()[0][0].getFleet());
        assertNotNull(field.getBoard()[2][2].getFleet());
        assertFalse(player.getFleetList().contains(fleet1)); // у плеера уже хранится новый флот, состоящий из двух флотов
    }

    @Test
    void testCapturePlanet() {
        Player player = new Player(0, "0");
        Planet planet = new Planet(150);
        move.capturePlanet(player, planet);
        assertTrue(planet.isCaptured());
        assertTrue(player.controlledPlanet.contains(planet));
    }
}
