package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.SymmetricalGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {
    private Player player;
    private Field field;


    @BeforeEach
    public void setUp() {
        player = new Player(1L, "Player1");

    }

    @Test
    public void testPlayerConstructor() {
        Player player = new Player(1, "Player1");
        assertEquals(1, player.id);
        assertEquals("Player1", player.getName());
    }

    //
//    @Test
//    public void testGetControlledPlanet() {
//        assertEquals(Arrays.asList(planet1, planet2), player.getControlledPlanet());
//    }
//
//    @Test
//    public void testGetLegalMoves() {
//        assertEquals(Arrays.asList(move1, move2), player.getLegalMoves());
//    }
//
//    @Test
//    public void testGetTotalGamePoints() {
//        assertEquals(0, player.getTotalGamePoints());
//    }
//
//    @Test
//    public void testGetCurrentGamePoints() {
//        assertEquals(30, player.getCurrentGamePoints());
//    }
//
//    @Test
//    public void testCalculateAttacksOnTile() {
//        Collection<Move> moves = player.calculateAttacksOnTile(cell, Arrays.asList(move1, move2));
//        assertEquals(Arrays.asList(move1, move2), moves);
//        assertThrows(UnsupportedOperationException.class, () -> moves.add(new Move(cell, cell, Move.MoveType.ORDINARY)));
//
//    }
//
//    @Test
//    public void testCalculateAttacksOnTile2() {
//        Cell targetCell = new Cell(5, 5);
//        Cell anotherCell = new Cell(3, 3);
//
//        Move moveToTarget = new Move(new Cell(1, 1), targetCell, Move.MoveType.ORDINARY);
//        Move moveToAnother = new Move(new Cell(2, 2), anotherCell, Move.MoveType.CAPTURE);
//        Move anotherMoveToTarget = new Move(new Cell(4, 4), targetCell, Move.MoveType.ORDINARY);
//
//        Collection<Move> haveMoves = new ArrayList<>();
//        haveMoves.add(moveToTarget);
//        haveMoves.add(moveToAnother);
//        haveMoves.add(anotherMoveToTarget);
//
//        player.legalMoves = haveMoves;
//
//        Collection<Move> moves = player.calculateAttacksOnTile(targetCell, player.getLegalMoves());
//
//        assertEquals(Arrays.asList(moveToTarget, anotherMoveToTarget), new ArrayList<>(moves));
//
//        assertThrows(UnsupportedOperationException.class, () -> moves.add(new Move(targetCell, targetCell, Move.MoveType.ORDINARY)));
//    }
    @Test
    void testAddLegalMoves() {
        Field field = new Field(5, new SymmetricalGenerator());
        Fleet fleet1 = new Fleet(field.getBoard()[0][2], player);
        Fleet fleet2 = new Fleet(field.getBoard()[3][2], player);
        fleet1.addFleetMoves(field);
        fleet2.addFleetMoves(field);
        player.addLegalMoves();
        assertEquals(fleet1.getFleetMoves().size() + fleet2.getFleetMoves().size(), player.legalMoves.size());
    }
}
