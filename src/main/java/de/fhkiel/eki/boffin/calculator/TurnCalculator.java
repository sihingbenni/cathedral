package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Set;

public interface TurnCalculator {

    GameStateManager gameStateManager = GameStateManager.getInstance();

    Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements);
}
