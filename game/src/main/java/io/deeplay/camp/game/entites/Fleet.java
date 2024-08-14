package io.deeplay.camp.game.entites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.deeplay.camp.game.utils.PointsCalculator;
import io.deeplay.camp.game.utils.ValidationMove;

/**
 * Класс-представление сущности Флот
 */
public class Fleet extends GalaxyEntity {
    /**
     * Флот хранит в себе переменные:
     * 1) shipList - для хранения актуального набора кораблей
     * 2) fleetPower - для хранения суммы сил всех кораблей флота
     * 3) fleetPosition - для клетки - расположения
     */
    private List<Ship> shipList;
    private Cell fleetPosition;
    private int fleetPower;
    private final Player owner;
    private List<Move> fleetMoves;

    public Fleet(final Cell fleetPosition, final Player player) {
        super();
        shipList = new ArrayList<>();
        this.fleetPosition = fleetPosition;
        owner = player;
        owner.addFleet(this);
        this.fleetMoves = new ArrayList<>();
        fleetPosition.setFleet(this);
    }

    public Fleet(final List<Ship> AnotherShipList, final Cell fleetPosition, final Player player) {
        super();
        this.shipList = new ArrayList<>();
        for (Ship ship : AnotherShipList) {
            ship.setFleetAffiliation(this);
        }
        this.fleetPosition = fleetPosition;
        updateFleetPower();
        owner = player;
        owner.addFleet(this);
        fleetPosition.setFleet(this);
    }

    // Конструктор копирования
    public Fleet(final Fleet other) {
        super();
        this.shipList = new ArrayList<>();
        for (Ship ship : other.shipList) {
            Ship newShip = new Ship(ship); // Используем конструктор копирования для Ship
            newShip.setFleetAffiliation(this);
        }
        this.fleetPosition = other.fleetPosition; // Предполагаем, что Cell является неизменяемым объектом или копируем его соответствующим образом
        this.fleetPower = other.fleetPower;
        this.owner = other.owner; // Предполагаем, что Player является неизменяемым объектом или копируем его соответствующим образом
        this.fleetMoves = new ArrayList<>(other.fleetMoves); // Предполагаем, что Move является неизменяемым объектом или копируем его соответствующим образом
        this.fleetPosition.setFleet(this);
    }


    public boolean checkShipExist(final Ship checkedShip) {
        for (Ship ship : shipList) {
            if (checkedShip.getId() == ship.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод для актуализации значения силы Флота для случая,
     * когда у нас пополнение листа или перераспределение кораблей
     */
    private void updateFleetPower() {
        this.fleetPower = shipList.stream().mapToInt(ship -> ship.getShipType().getShipPower()).sum();
    }

    public int getFleetPower() {
        return fleetPower;
    }


    public void addShipsIntoFleet(List<Ship> ships) {

        for (Ship ship : ships) {
            if (checkShipExist(ship)) {
                throw new IllegalArgumentException("корабль уже существует во флоте");
            }
            ship.setFleetAffiliation(this);
        }
        updateFleetPower();
    }

    public void addShipIntoFleet(Ship ship) {
        if (checkShipExist(ship)) {
            throw new IllegalArgumentException("корабль уже существует во флоте");
        }
        this.shipList.add(ship);
        ship.fleetAffiliation();
        updateFleetPower();
    }

    public Map<Ship.ShipType, Integer> getShipsByShipType() {
        Map<Ship.ShipType, Integer> result = new HashMap<>();

        for (Ship ship : shipList) {
            Ship.ShipType shipType = ship.getShipType();
            result.put(shipType, result.getOrDefault(shipType, 0) + 1);
        }

        return result;
    }

    public List<Ship> removeShipsFromFleet(Map<Ship.ShipType, Integer> shipsToRemove) {
        List<Ship> result = new ArrayList<>();
        for (Map.Entry<Ship.ShipType, Integer> entry : shipsToRemove.entrySet()) {
            Ship.ShipType shipType = entry.getKey();
            int countToRemove = entry.getValue();

            Iterator<Ship> iterator = shipList.iterator();
            while (iterator.hasNext() && countToRemove > 0) {
                Ship ship = iterator.next();
                if (ship.getShipType() == shipType) {
                    result.add(ship);
                    iterator.remove();
                    countToRemove--;
                }
            }

            if (countToRemove > 0) {
                throw new IllegalArgumentException("Неверное количество кораблей для удаления");
            }
        }
        updateFleetPower();
        return result;
    }

    public List<Ship> getShipList() {
        return shipList;
    }

    public Cell getFleetPosition() {
        return fleetPosition;
    }

    //не делаю final из-за взаимной зависимости, возможно позже надо будет сетить в клетку
    public void setFleetPosition(Cell position) {
        this.fleetPosition = position;
        position.setFleet(this);
    }

    public void fleetsClashWithPlanet(final Fleet enemyFleet, final Player us, final Player enemy, int planetPoints) {
        if (this.fleetPower > enemyFleet.fleetPower + planetPoints) {
            enemy.removeFleet(enemyFleet);
        } else {
            us.removeFleet(this);
        }
    }

    /**
     * Метод, который обыгрывает столкновение флотов
     *
     * @param enemyFleet флот соперника
     * @param us         игрок
     * @param enemy      игрок
     */
    public void fleetsClash(final Fleet enemyFleet, final Player us, final Player enemy) {
        if (this.fleetPower > enemyFleet.fleetPower) {
            enemy.removeFleet(enemyFleet);
        } else {
            us.removeFleet(this);
        }
    }

    // метод подбора подходящих ходов для флота
    public void addFleetMoves(final Field field) {
        fleetMoves.clear();
        boolean[][] visited = new boolean[field.getSize()][field.getSize()];
        visited[fleetPosition.x][fleetPosition.y] = true;
        if (fleetMoves.isEmpty()) {
            findNeighbors(fleetPosition, visited, field);
        }
        for (int i = 0; i < fleetMoves.size(); i++) {
            Move currentMove = fleetMoves.get(i);
            findNeighbors(currentMove.endPosition(), visited, field);
            if (!ValidationMove.isEnoughPoints(getOwner(), currentMove) || fleetMoves.size() >= field.getSize() * field.getSize() - 1)
                break;
        }
    }

    public void findNeighbors(final Cell currentCell, final boolean[][] map, final Field field) {
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int newX = currentCell.x + direction[0];
            int newY = currentCell.y + direction[1];

            if (ValidationMove.isPositionValid(new Cell(newX, newY), field.getSize()) && !map[newX][newY]) {
                int cost = PointsCalculator.costMovement(fleetPosition, new Cell(newX, newY));
                if (getOwner().getTotalGamePoints() >= cost) {
                    Cell cell = field.getBoard()[newX][newY];
                    map[newX][newY] = true;
                    fleetMoves.add(new Move(fleetPosition, cell, Move.MoveType.ORDINARY, cost));
                }
            }
        }
    }

    public Player getOwner() {
        return owner;
    }

    public List<Move> getFleetMoves() {
        return fleetMoves;
    }
}
