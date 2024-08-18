package io.deeplay.camp.game.entites;

import io.deeplay.camp.game.interfaces.GalaxyListener;
import io.deeplay.camp.game.utils.ValidationMove;

import java.util.*;

/**
 * Контроллер
 */
public class Game implements GalaxyListener {
    private final static int NUM_PLAYERS = 2;

    private final Field field;
    public final Player[] players = new Player[NUM_PLAYERS];
    private final Map<String, Player> playerNames;
    private final Map<String, Cell> playerStartPosition;
    private int nextPlayerToAct;
    private String id;
    private boolean skipException = false;
    private int[] consecutiveSkipCounts = new int[NUM_PLAYERS];

    public Game(final Field field) {
        this.field = field;
        this.playerNames = new HashMap<>();
        this.playerStartPosition = new HashMap<>();
    }

    // Конструктор копирования
    public Game(Game other) {
        // Глубокое копирование поля (предполагается, что Field имеет конструктор копирования)
        this.field = new Field(other.field);

        // Копирование идентификатора игры
        this.id = other.id;

        // Глубокое копирование игроков
        for (int i = 0; i < NUM_PLAYERS; i++) {
            this.players[i] = new Player(other.players[i]);
        }

        // Глубокое копирование имен игроков
        this.playerNames = new HashMap<>();
        for (Map.Entry<String, Player> entry : other.playerNames.entrySet()) {
            this.playerNames.put(entry.getKey(), new Player(entry.getValue()));
        }

        for (Planet planet : other.field.getPlanets()) {
            if (planet.getOwner() != null) {
                this.field.getBoard()[planet.getCell().x][planet.getCell().y].planet.setOwner(playerNames.get(planet.getOwner().getName()));
            }
        }

        // Глубокое копирование начальных позиций игроков
        this.playerStartPosition = new HashMap<>();
        for (Map.Entry<String, Cell> entry : other.playerStartPosition.entrySet()) {
            this.playerStartPosition.put(entry.getKey(), new Cell(entry.getValue()));
        }

        for (int x = 0; x < other.field.getSize(); x++) {
            for (int y = 0; y < other.field.getSize(); y++) {
                Cell originalCell = other.field.getBoard()[x][y];
                Cell copiedCell = this.field.getBoard()[x][y];
                if (originalCell.getFleet() != null) {
                    copiedCell.setFleet(new Fleet(originalCell.getFleet()));
                }
            }
        }

        // Копирование состояния следующего игрока
        this.nextPlayerToAct = other.nextPlayerToAct;

        // Копирование флагов и счетчиков
        this.skipException = other.skipException;
        this.consecutiveSkipCounts = Arrays.copyOf(other.consecutiveSkipCounts, other.consecutiveSkipCounts.length);
    }

    public Game getCopy(Game originalGame) {
        return new Game(originalGame);
    }

    public String getId() {
        return id;
    }

    public String getNextPlayerToAct() {
        return players[nextPlayerToAct].getName();
    }


    public boolean isGameOver() {
        return checkConsecutiveSkips() || isCheckPlayer0Fail() || isCheckPlayer1Fail() || field.isGameOver();
    }

    private boolean checkConsecutiveSkips() {
        return skipException;
    }

    public String isWinner() {
        // Если игра завершена из-за 3 подряд пропусков у обоих игроков, возвращаем "ничья"
        if (skipException) {
            return "победитель не существует";
        }
        if (isCheckPlayer0Fail()) {
            return players[1].getName();
        }
        if (isCheckPlayer1Fail()) {
            return players[0].getName();
        }
        // В противном случае возвращаем победителя по исходной логике (контроль всех планет)
        return field.isWinner();
    }

    //todo разделение флота
    public List<Move> availableMoves(String player) {
        List<Move> moves = new ArrayList<>();
        for (Fleet fleet : playerNames.get(player).fleetList) {
            fleet.addFleetMoves(field);
            moves.addAll(fleet.getFleetMoves());
        }
        return moves;
    }

    @Override
    public void startGameSession(String gameId) {
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

        players[0].controlledPlanet.add(this.field.getBoard()[0][this.field.getSize() - 1].planet);
        players[1].controlledPlanet.add(this.field.getBoard()[this.field.getSize() - 1][0].planet);
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

    private boolean isCheckPlayer0Fail() {
        return !players[0].controlledPlanet.contains(this.field.getBoard()[0][this.field.getSize() - 1].planet) && players[0].fleetList.isEmpty();
    }

    private boolean isCheckPlayer1Fail() {
        return !players[1].controlledPlanet.contains(this.field.getBoard()[this.field.getSize() - 1][0].planet) && players[1].fleetList.isEmpty();
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
