package io.deeplay.camp.game.entites;

import io.deeplay.camp.game.utils.ValidationMove;

import java.util.List;

/**
 * Класс - ход, как record,
 * чтоб не закладывать, еще не продуманную, логику.
 * Record обеспечивает из коробки все требуемые методы (и даже больше)
 */
public record Move(Cell startPosition, Cell endPosition, MoveType moveType, List<Ship> shipList, int cost) {
    // точка входа для хода
    public void makeMove(final Player player) {
        Fleet fleet = startPosition.getFleet();
        Fleet enemyFleet = endPosition.getFleet();
        List<Ship> removeShips = null;

        if (shipList.size() != fleet.getShipList().size()) {
            removeShips = fleet.updateFleet(shipList);
        }

        if (enemyFleet != null) {
            handleEnemyFleetEncounter(player, fleet, enemyFleet);
        } else {
            moveFleetToPosition(fleet, endPosition);
        }

        checkAndCapturePlanet(player);

        if (removeShips != null)
            startPosition.setFleet(new Fleet(removeShips, startPosition, player));
    }

    // на конечной позиции есть флот
    private void handleEnemyFleetEncounter(final Player player, final Fleet fleet, final Fleet enemyFleet) {
        if (!enemyFleet.getOwner().equals(player)) { // флот оппонента
            handleOpponentFleet(player, fleet, enemyFleet);
        } else { // флот игрока
            mergeFleets(player, fleet, enemyFleet);
        }
    }

    private void handleOpponentFleet(final Player player, final Fleet fleet, final Fleet enemyFleet) {
        if (endPosition.planet != null) {
            if (endPosition.planet.isCaptured()) {// планета есть и она принадлежит сопернику
                fleet.fleetsClashWithPlanet(enemyFleet, player, enemyFleet.getOwner(), endPosition.planet.points);
            } else { // планета есть и она свободна
                fleet.fleetsClash(enemyFleet, player, enemyFleet.getOwner());
            }
        } else fleet.fleetsClash(enemyFleet, player, enemyFleet.getOwner()); //планеты нет

        if (player.getFleetList().contains(fleet)) {
            moveFleetToPosition(fleet, endPosition);
        } else {
            clearFleetFromPosition(startPosition);
        }
    }

    private void mergeFleets(final Player player, final Fleet fleet1, final Fleet fleet2) {
        fleet2.addShipsIntoFleet(fleet1.getShipList());
        player.removeFleet(fleet1);
        clearFleetFromPosition(startPosition);
    }

    private void checkAndCapturePlanet(final Player player) {
        if (endPosition.planet != null && endPosition.getFleet().getOwner().equals(player)) {
            if (ValidationMove.isCapturePlanet(endPosition.getFleet().getFleetPower(), endPosition.planet.points)) {
                capturePlanet(player, endPosition.planet);
            } else if (endPosition.planet.isCaptured() && !endPosition.planet.getOwner().equals(player)) {
                player.removeFleet(endPosition.getFleet());
                clearFleetFromPosition(endPosition);
            }
        }
    }

    public void capturePlanet(final Player player, final Planet planet) {
        if (planet.isCaptured()) {
            planet.getOwner().controlledPlanet.remove(planet);
        }
        player.controlledPlanet.add(planet);
        planet.setOwner(player);
    }

    private void moveFleetToPosition(final Fleet fleet, final Cell targetPosition) {
        setFleetOnPosition(targetPosition, fleet);
        clearFleetFromPosition(startPosition);
    }

    private void setFleetOnPosition(final Cell position, final Fleet fleet) {
        fleet.setFleetPosition(position);
        position.setFleet(fleet);
    }

    private void clearFleetFromPosition(final Cell position) {
        position.setFleet(null);
    }

    /**
     * Два типа хода:
     * ORDINARY - как перемещение по игровому полю
     * CAPTURE - атака на планету, захваченную противником
     */
    public enum MoveType {
        ORDINARY,
        SKIP
    }

    @Override
    public String toString() {
        if (startPosition != null && endPosition != null) {
            return "start position = [" + Character.toString(startPosition.x + 'A') + ", " + startPosition.y + "] end position = [" + Character.toString(endPosition.x + 'A') + ", " + endPosition.y + "] " + "cost = " + cost;
        } else {
            return "Empty Move";
        }
    }
}

