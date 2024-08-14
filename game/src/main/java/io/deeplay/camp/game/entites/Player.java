package io.deeplay.camp.game.entites;

import java.util.*;


/**
 * Класс-представление сущности Игрок
 */
public class Player {
    /**
     * Хранит в себе переменные:
     * name - имя игрока
     * id - идентификатор
     * fleetList - список флотов в распоряжении
     * controlledPlanet - список захваченных планет
     * legalMoves - коллекция ходов для всех флотов в распоряжении
     */
    public final long id;
    private final String name;
    private int totalGamePoints;
    public List<Fleet> fleetList;
    public List<Planet> controlledPlanet;

    public Player(final long id, final String name) {
        this.id = id;
        this.name = name;
        this.totalGamePoints = 60;
        this.fleetList = new ArrayList<>();
        this.controlledPlanet = new ArrayList<>();
    }

    public Player(Player other) {
        this.id = other.id;
        this.name = other.name;
        this.totalGamePoints = other.totalGamePoints;

        // Глубокое копирование fleetList
        this.fleetList = new ArrayList<>();
        for (Fleet fleet : other.fleetList) {
            this.fleetList.add(new Fleet(fleet));
        }

        // Глубокое копирование controlledPlanet
        this.controlledPlanet = new ArrayList<>();
        for (Planet planet : other.controlledPlanet) {
            this.controlledPlanet.add(new Planet(planet));
        }
    }

    public String getName() {
        return name;
    }

    public List<Fleet> getFleetList() {
        return fleetList;
    }

    public List<Planet> getControlledPlanet() {
        return controlledPlanet;
    }

    public int getTotalGamePoints() {
        return totalGamePoints;
    }

    public void decreaseTotalGamePoints(int totalGamePoints) {
        this.totalGamePoints = this.totalGamePoints - totalGamePoints;
    }

    public void addTotalGamePoints() {
        totalGamePoints += 30 + getCurrentGamePoints();
    }

    /**
     * Метод актуализирует количество очков игрока, пересчитывая их за захваченные планеты
     *
     * @return totalPoints
     */
    public int getCurrentGamePoints() {
        return controlledPlanet.stream()
                .mapToInt(planet -> (int) Math.ceil(planet.getPoints() / 100.0) + 5)
                .sum();
    }

    /**
     * Метод для удаления флота из общего списка
     *
     * @param fleet флот, который мы удаляем/проиграл сражение другому флоту
     */
    public void removeFleet(Fleet fleet) {
        this.fleetList.remove(fleet);
    }

    public void addFleet(Fleet fleet) {
        this.fleetList.add(fleet);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id && totalGamePoints == player.totalGamePoints && Objects.equals(name, player.name) && Objects.equals(fleetList, player.fleetList) && Objects.equals(controlledPlanet, player.controlledPlanet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, totalGamePoints, fleetList, controlledPlanet);
    }

    public long getId() {
        return id;
    }
}
