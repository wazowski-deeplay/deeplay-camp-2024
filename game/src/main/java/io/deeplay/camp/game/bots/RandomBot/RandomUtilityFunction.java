package io.deeplay.camp.game.bots.RandomBot;

import io.deeplay.camp.game.bots.UtilityFunction;
import io.deeplay.camp.game.entites.Fleet;
import io.deeplay.camp.game.entites.Game;
import io.deeplay.camp.game.entites.Player;

public class RandomUtilityFunction implements UtilityFunction {
    @Override
    public double getUtility(Game game) {
        Player currentPlayer = game.getPlayerByName(game.getNextPlayerToAct());
        Player opponent = game.getPlayerByName(game.players[(game.getNextPlayerToActIndex() + 1) % 2].getName());

        // Контроль планет
        int playerPlanets = currentPlayer.getControlledPlanet().size();
        int opponentPlanets = opponent.getControlledPlanet().size();

        // Сила флота
        int playerFleetStrength = 0;
        int opponentFleetStrength = 0;
        for (Fleet fleet: currentPlayer.fleetList){
            playerFleetStrength += fleet.getFleetPower();
        }
        for (Fleet fleet: opponent.fleetList){
            opponentFleetStrength += fleet.getFleetPower();
        }

        // Весовые коэффициенты (их можно подбирать экспериментально)
        double planetControlWeight = 2.0;
        double fleetStrengthWeight = 1.0;

        // Вычисляем полезность для текущего игрока
        return planetControlWeight * (playerPlanets - opponentPlanets)
                + fleetStrengthWeight * (playerFleetStrength - opponentFleetStrength);
    }
}
