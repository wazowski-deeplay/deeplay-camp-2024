package io.deeplay.camp.game;

import io.deeplay.camp.game.bots.Bot;
import io.deeplay.camp.game.bots.RandomBot.RandomBot;
import io.deeplay.camp.game.bots.RandomBot.RandomMinimax;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelfPlayTest {
    SelfPlay selfPlay;
    String[] names;

    @Test
    void templateSelfPlayTest() {
        names = new String[]{"TestPlayer0", "TestPlayer1"};
        final Bot.BotFactory[] factories = new Bot.BotFactory[]{
                new RandomBot.Factory(),
                new RandomBot.Factory()
        };
        selfPlay = new SelfPlay(4, names, factories);
        selfPlay.playGames(10);
        //todo нормальные тесты на селфплей после рефакторинга
//        assertThrows(RuntimeException.class, () -> selfPlay.playGame());
    } @Test

    void templateSelfPlayTest1() {
        names = new String[]{"TestPlayer0", "TestPlayer1"};
        final Bot.BotFactory[] factories = new Bot.BotFactory[]{
                new RandomBot.Factory(),
                new RandomMinimax.Factory(2)
        };
        selfPlay = new SelfPlay(4, names, factories);
        selfPlay.playGames(1);
        //todo нормальные тесты на селфплей после рефакторинга
//        assertThrows(RuntimeException.class, () -> selfPlay.playGame());
    }
}
