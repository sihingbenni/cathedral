package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.HashSet;
import java.util.Set;

public interface TurnCalculator {

    default Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements) {
        return new HashSet<>();
    }

    /**
     *
     * @param color {@link Color} the current Player
     * @return {@link GameStateManager} the matching GameStateManager for the color
     */
    default GameStateManager getGameStateManager(Color color) {
        return GameStateManager.getGameStateManagerByColor(color);
    }
}
