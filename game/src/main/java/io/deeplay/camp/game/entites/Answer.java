package io.deeplay.camp.game.entites;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static io.deeplay.camp.game.entites.Move.MoveType.SKIP;

/**
 * Представление сущности ответ игрока (response)
 */
public class Answer {
    private final Move move;
    private final String responseTime;
    private final List<Ship.ShipType> shipList;

    /**
     * Конструктор, который инициализирует ответ с текущим временем в формате ISO_LOCAL_DATE_TIME.
     *
     * @param move действие игрока
     * @throws IllegalArgumentException если move является null
     */
    public Answer(final Move move) {
        this(move, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    /**
     * Конструктор, который инициализирует ответ с заданным временем.
     *
     * @param move         действие игрока
     * @param responseTime время ответа в формате строки
     * @throws IllegalArgumentException если move или responseTime являются null
     */
    public Answer(final Move move, final String responseTime) {
        this(move, responseTime, null);
    }

    public Answer(Move move, String responseTime, List<Ship.ShipType> shipList) {
//        if (move == null) {
//            throw new IllegalArgumentException("Move cannot be null.");
//        }
        if (responseTime == null) {
            throw new IllegalArgumentException("Response time cannot be null.");
        }
        this.move = move;
        this.responseTime = responseTime;
        this.shipList = shipList;
    }
    public Answer(final List<Ship.ShipType> shipList) {
        this(null, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), shipList);
    }

    public Move getMove() {
        return move;
    }

    public List<Ship.ShipType> getShipList() {
        return shipList;
    }

    public String getResponseTime() {
        return responseTime;
    }

    /**
     * Метод toString() обрабатывает разные типы ход
     *
     * @return строку нормированного формата
     */
    @Override
    public String toString() {
        if (this.move.moveType() == SKIP) {
            return "Answer{response='" + " SKIP " + " : " + responseTime + "'}";
        }
        else return "Answer{response='" + move + " : " + responseTime + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return Objects.equals(move, answer.move) && Objects.equals(responseTime, answer.responseTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, responseTime);
    }
}
