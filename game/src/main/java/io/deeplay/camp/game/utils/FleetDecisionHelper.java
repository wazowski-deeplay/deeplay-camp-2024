package io.deeplay.camp.game.utils;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.entites.Cell;
import io.deeplay.camp.game.entites.Player;

import java.util.Random;

public class FleetDecisionHelper {
    public static boolean hasNoFleet(Player player) {
        return player.getFleetList().isEmpty();
    }

    public static boolean hasEnoughPoints(Player player) {
        return player.getTotalGamePoints() >= 10;
    }

    public static boolean isAvailablePosition(Cell startCell, String playerName) {
        return startCell.planet.getOwner().getName().equals(playerName) && (startCell.getFleet() == null || startCell.getFleet().getOwner().getName().equals(playerName));
    }

    public static boolean isRandomBot(Bot.BotType botType) {
        return botType == Bot.BotType.RandomBot;
    }

    public static boolean shouldBuyFleet(Cell startCell, Player player, Bot.BotType botType) {
        Random random = new Random();
        int randomValue = random.nextInt(10);
        return isRandomBot(botType)
                && isAvailablePosition(startCell, player.getName())
                && hasEnoughPoints(player)
                && (hasNoFleet(player)
                || randomValue < 2);
    }
}

