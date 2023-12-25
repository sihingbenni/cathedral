package de.fhkiel.eki.boffin.gamestate;

import de.fhkiel.eki.boffin.calculator.EarlyGameTurnCalculator;
import de.fhkiel.eki.boffin.calculator.EndGameTurnCalculator;
import de.fhkiel.eki.boffin.calculator.MidGameTurnCalculator;
import de.fhkiel.eki.boffin.calculator.TurnCalculator;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Set;

public enum GameState {
    EarlyGame(new EarlyGameTurnCalculator()),
    MidGame(new MidGameTurnCalculator()),
    GameOver(null),
    EndGame(new EndGameTurnCalculator());

    private final TurnCalculator turnCalculator;

    GameState(TurnCalculator turnCalculator) {
        this.turnCalculator = turnCalculator;
    }

    public Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements) {
        return turnCalculator.calculateTurn(game, possiblePlacements);
    }
}
