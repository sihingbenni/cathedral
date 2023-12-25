package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.gamestate.GameState;
import de.fhkiel.ki.cathedral.game.*;

import java.util.Collections;
import java.util.Set;

public class EarlyGameTurnCalculator implements TurnCalculator {
    @Override
    public Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements) {
        int turnNumber = game.lastTurn().getTurnNumber();
        gameStateManager.switchGameState(GameState.MidGame);

        // if it's the first turn for white,
        // place the cathedral in the top left corner
        if (turnNumber == 0) {
            // place the cathedral random
            return possiblePlacements;
        } else if (turnNumber == 1) {
            // place the infirmary on the opposite side of the cathedral
            Placement cathedral = game.getBoard().getPlacedBuildings().get(0);

            // calculate place opposite of cathedral
            int x = cathedral.x() > 4 ? 3 : 6;
            int y = cathedral.y() > 4 ? 2 : 7;

            return Collections.singleton(new Placement(new Position(x, y), Direction._0, Building.Black_Infirmary));
        }

        throw new RuntimeException("EarlyGameTurnCalculator should not be called after the first two turns.");
    }
}
