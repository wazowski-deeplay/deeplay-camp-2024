package io.deeplay.camp.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.interfaces.GalaxyListener;
import io.deeplay.camp.game.entites.*;
import io.deeplay.camp.game.entites.boardGenerator.KMeansGenerator;
import io.deeplay.camp.game.interfaces.PlayerInterface;
import io.deeplay.camp.game.utils.GameLogger;
import io.deeplay.camp.game.utils.PointsCalculator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SelfPlay implements GalaxyListener {

    private int sizeField;
    private String[] playerNames;
    private Bot.BotFactory[] factories;
    private PlayerInterface[] players = new PlayerInterface[2];
    private Map<String, PlayerInterface> playerNamesMap;
    private List<GalaxyListener> listeners;
    private int totalGames;
    private Map<String, Integer> wins;
    private String winner = null;

    public SelfPlay(final int sizeField, final String[] playerNames, final Bot.BotFactory[] factories) {
        this.factories = factories;
        this.sizeField = sizeField;
        this.playerNames = playerNames;
        listeners = new ArrayList<>();
        playerNamesMap = new HashMap<>();
        wins = new HashMap<>();
    }

    public void playGames(int numGames) {
        final ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i <= numGames; i++) {
            playGame(executor);
            if (i % 10 == 0) {
                dumpStatisticsToFile();
            }
        }
        executor.shutdown();
    }

    public void playGame(ExecutorService executor) {
        winner = null;
        listeners = new ArrayList<>();
        playerNamesMap = new HashMap<>();
        final Field field = new Field(sizeField, new KMeansGenerator());
        final Game game = new Game(field);
        final GameLogger logger = new GameLogger(field);
        long moveCounter = 0;


        List<Ship.ShipType> startShips = new ArrayList<>();
        startShips.add(Ship.ShipType.BASIC);
        startShips.add(Ship.ShipType.BASIC);
        startShips.add(Ship.ShipType.BASIC);
//        startShips.add(Ship.ShipType.BASIC);
//        startShips.add(Ship.ShipType.BASIC);


        players[0] = factories[0].createBot(playerNames[0], field);
        players[1] = factories[1].createBot(playerNames[1], field);
        playerNamesMap.put(playerNames[0], players[0]);
        playerNamesMap.put(playerNames[1], players[1]);

        listeners.add(game);
        listeners.add(logger);
        listeners.add(players[0]);
        listeners.add(players[1]);

        startGameSession("0000");
        connectingPlayer(playerNames[0]);
        connectingPlayer(playerNames[1]);
        gameStarted(field);

        createShips(startShips, playerNames[0]);
        createShips(startShips, playerNames[1]);


        while (!game.isGameOver()) {
            final String nextPlayerToAct = game.getNextPlayerToAct();

            final PlayerInterface player = playerNamesMap.computeIfAbsent(nextPlayerToAct, (key) -> {
                throw new IllegalStateException("There is no player with name " + key);
            });
            if (PointsCalculator.checkAddCredits(moveCounter)) addCredits();
            Answer answer = null;

            CompletableFuture<Answer> future = CompletableFuture.supplyAsync(() -> player.getAnswer(game.getField()), executor);

            try {
                answer = future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                if (nextPlayerToAct.equals(playerNames[0])) {
                    winner = playerNames[1];
                } else {
                    winner = playerNames[0];
                }
                break;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (answer.getShipList() != null) {
                createShips(answer.getShipList(), game.getNextPlayerToAct());
            } else {
                getPlayerAction(answer.getMove(), nextPlayerToAct);
            }
            moveCounter++;

        }
        if (winner == null) {
            winner = game.isWinner();
        }

        updateStatistics(winner);
        gameEnded(winner);
        endGameSession();
    }

    private void updateStatistics(String winner) {
        totalGames++;
        wins.put(winner, wins.getOrDefault(winner, 0) + 1);
    }

    private void dumpStatisticsToFile() {
        final String FOLDER_PATH = "statistics";
        final String FILE_NAME = "statistics.csv";
        final String FILE_PATH = FOLDER_PATH + File.separator + FILE_NAME;

        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            File file = new File(FILE_PATH);

            if (file.length() == 0) {
                writer.write(String.format("| %-30s | %-10s | %-15s | %-10s |\n", "Player", "Wins", "Win Percentage", "Total Games"));
                writer.write("-".repeat(80) + "\n");
            }

            for (Map.Entry<String, Integer> entry : wins.entrySet()) {
                String player = entry.getKey();
                int playerWins = entry.getValue();
                double winPercentage = totalGames > 0 ? ((double) playerWins / totalGames) * 100 : 0;

                writer.write(String.format("| %-30s | %-10d | %-15.4f | %-10d  |\n", player, playerWins, winPercentage, totalGames));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startGameSession(final String gameId) {
        for (final GalaxyListener listener : listeners) {
            listener.startGameSession(gameId);
        }
    }

    @Override
    public void connectingPlayer(final String waitingPlayerName) {
        for (final GalaxyListener listener : listeners) {
            listener.connectingPlayer(waitingPlayerName);
        }
    }

    @Override
    public void gameStarted(final Field field) {
        for (final GalaxyListener listener : listeners) {

            listener.gameStarted(new Field(field));
        }
    }

    @Override
    public void getPlayerAction(final Move move, final String playerName) {
        for (final GalaxyListener listener : listeners) {
            listener.getPlayerAction(move, playerName);
        }
    }

    @Override
    public void addCredits() {
        for (final GalaxyListener listener : listeners) {
            listener.addCredits();
        }
    }

    @Override
    public void createShips(List<Ship.ShipType> ships, String playerName) {
        for (final GalaxyListener listener : listeners) {
            listener.createShips(ships, playerName);
        }
    }

    @Override
    public void gameEnded(final String winner) {
        for (final GalaxyListener listener : listeners) {
            listener.gameEnded(winner);
        }
    }

    @Override
    public void endGameSession() {
        for (final GalaxyListener listener : listeners) {
            listener.endGameSession();
        }
    }
}
