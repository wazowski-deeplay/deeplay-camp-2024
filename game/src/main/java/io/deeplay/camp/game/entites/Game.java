package io.deeplay.camp.game.entites;

import io.deeplay.camp.game.domain.GalaxyListener;
import io.deeplay.camp.game.domain.GameTypes;
import io.deeplay.camp.game.utils.ValidationMove;

import java.util.*;

/**
 * Контроллер
 */
public class Game implements GalaxyListener {
    private final static int NUM_PLAYERS = 2;

    private final Field field;
    private GameTypes gameType;
    private List<Move> allGameMoves;
    public final Player[] players = new Player[NUM_PLAYERS];
    private final Map<String, Player> playerNames;
    private final Map<String, Cell> playerStartPosition;
    private int nextPlayerToAct;
    private String id;
    private boolean skipException = false;
    private int[] consecutiveSkipCounts = new int[NUM_PLAYERS];

    public Game(final Field field) {
        this.field = field;
        this.allGameMoves = new ArrayList<>();
        this.playerNames = new HashMap<>();
        this.playerStartPosition = new HashMap<>();
    }

    // Конструктор копирования
    public Game(Game other) {
        // Глубокое копирование поля
        this.field = new Field(other.field);

        // GameTypes — это enum
        this.gameType = other.gameType;
        this.id = other.id;

        // Копируем заранее определенные ходы (если они допустимы в новой сессии)
        this.allGameMoves = new ArrayList<>(other.allGameMoves.size());

        // Копируем информацию об именах игроков и начальных позициях
        this.playerNames = new HashMap<>(other.playerNames.size());
        this.playerStartPosition = new HashMap<>(other.playerStartPosition.size());

        // Устанавливаем, какой игрок ходит следующим (начальное состояние)
        this.nextPlayerToAct = other.nextPlayerToAct;
    }

    public GameTypes getGameType() {
        return gameType;
    }


    public String getId() {
        return id;
    }

    public String getNextPlayerToAct() {
        return players[nextPlayerToAct].getName();
    }


    public boolean isGameOver() {
        // Если игра завершена из-за 3 подряд пропусков у обоих игроков, завершаем игру
        if (skipException) {
            return true;
        }
        // Проверяем исходную логику завершения игры (например, контроль всех планет одним игроком)
        return field.isGameOver();
    }

    public String isWinner() {
        // Если игра завершена из-за 3 подряд пропусков у обоих игроков, возвращаем "ничья"
        if (skipException) {
            return "победитель не существует";
        }
        // В противном случае возвращаем победителя по исходной логике (контроль всех планет)
        return field.isWinner();
    }


    @Override
    public void startGameSession(String gameId, GameTypes gameType) {
        this.gameType = gameType;
        id = gameId;
    }

    @Override
    public void connectingPlayer(final String waitingPlayerName) {
        if (players[0] == null) {
            players[0] = new Player(0, waitingPlayerName);
            playerNames.put(waitingPlayerName, players[0]);
        } else if (players[1] == null) {
            players[1] = new Player(1, waitingPlayerName);
            playerNames.put(waitingPlayerName, players[1]);
        } else throw new IllegalArgumentException("Игроки уже существуют");
    }

    @Override
    public void gameStarted(Field field) {
        //this.field.setBoard(field.getBoard());
        playerStartPosition.put(players[0].getName(), this.field.getBoard()[0][this.field.getSize() - 1]);
        playerStartPosition.put(players[1].getName(), this.field.getBoard()[this.field.getSize() - 1][0]);

        playerStartPosition.get(players[0].getName()).planet.setOwner(players[0]);
        playerStartPosition.get(players[1].getName()).planet.setOwner(players[1]);

    }


    /**
     * Обрабатывает действие игрока в игре.
     *
     * @param move_      объект {@link Move}, представляющий ход игрока.
     * @param playerName имя игрока, совершающего ход.
     * @throws IllegalArgumentException если игрок с указанным именем не существует.
     * @throws IllegalStateException    если ход не валиден или тип хода не поддерживается.
     *                                  <p>
     *                                  Метод проверяет валидность игрока, переключает ход на следующего игрока и обрабатывает ход в зависимости от его типа (ORDINARY, CAPTURE, SKIP).
     *                                  Если ход валиден, он добавляется в список всех ходов игры и применяется к текущему игроку.
     *                                  В конце, из очков текущего игрока вычитается стоимость хода.
     */
    @Override
    public void getPlayerAction(Move move_, String playerName) {
        final Move move;
        if (move_.moveType() != Move.MoveType.SKIP) {
            Cell[][] b = field.getBoard();
            move = new Move(b[move_.startPosition().x][move_.startPosition().y], b[move_.endPosition().x][move_.endPosition().y], move_.moveType(), move_.cost());
        } else {
            move = move_;
        }

        if (!playerNames.containsKey(playerName)) {
            throw new IllegalArgumentException("Отсутствует игрок:" + playerName);
        }

        if (move.moveType() == Move.MoveType.ORDINARY) {
            if (ValidationMove.isValidOrdinaryMove(move, field, players[nextPlayerToAct])) {
                consecutiveSkipCounts[nextPlayerToAct] = 0; // Если игрок сделал обычный ход, сбрасываем счётчик
                allGameMoves.add(move);
                move.makeMove(players[nextPlayerToAct]);
            } else throw new IllegalStateException("Такой ORDINARY move не валиден!");
        } else if (move.moveType() == Move.MoveType.SKIP) {
            consecutiveSkipCounts[nextPlayerToAct]++;

            if (consecutiveSkipCounts[nextPlayerToAct] >= 3) {
                // Проверяем, сделал ли другой игрок также 3 последовательных хода SKIP
                if (consecutiveSkipCounts[(nextPlayerToAct + 1) % NUM_PLAYERS] >= 3) {
                    skipException = true;
                    return;
                }

            }
            getAllGameMoves().add(move);
        } else throw new IllegalStateException("Не существует такого типа хода!");

        players[nextPlayerToAct].decreaseTotalGamePoints(move.cost());
        switchPlayerToAct();
    }

    @Override
    public void addCredits() {
        players[0].addTotalGamePoints();
        players[1].addTotalGamePoints();
    }

    @Override
    public void createShips(List<Ship.ShipType> ships, String playerName) {
        Fleet fleet;
        int totalPower = 0;
        if (playerStartPosition.get(playerName).getFleet() != null) {
            fleet = playerStartPosition.get(playerName).getFleet();
        } else {
            fleet = new Fleet(playerStartPosition.get(playerName), playerNames.get(playerName));
        }
        for (Ship.ShipType shipType : ships) {
            new Ship(shipType, fleet);
            totalPower += shipType.getShipPower();
        }
        players[nextPlayerToAct].decreaseTotalGamePoints(totalPower / 10);
        switchPlayerToAct();
    }


    @Override
    public void gameEnded(String winner) {
    }

    @Override
    public void endGameSession() {

    }

    public Field getField() {
        return field;
    }

    public List<Move> getAllGameMoves() {
        return allGameMoves;
    }

    public Player getPlayerByName(final String name) {
        return playerNames.computeIfAbsent(name, (key) -> {
            throw new IllegalStateException("There is no player with name " + key);
        });
    }

    public Map<String, Player> getPlayerNames() {
        return playerNames;
    }

    public void switchPlayerToAct() {
        nextPlayerToAct = (nextPlayerToAct + 1) % NUM_PLAYERS;
    }


    public Map<String, Cell> getPlayerStartPosition() {
        return playerStartPosition;
    }
}
