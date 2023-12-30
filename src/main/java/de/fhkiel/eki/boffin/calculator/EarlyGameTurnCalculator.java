package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.gamestate.GameState;
import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.ki.cathedral.game.*;

import java.util.Collections;
import java.util.Set;

public class EarlyGameTurnCalculator implements TurnCalculator {
    @Override
    public Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements) {
        // get the right gameStateManager
        GameStateManager gameStateManager = getGameStateManager(game.getCurrentPlayer());
        int turnNumber = game.lastTurn().getTurnNumber();

        Board board = game.getBoard();
        gameStateManager.switchGameState(GameState.MidGame);

        switch (turnNumber) {
            case 0 -> {
                // return all possible placements, one will be chosen by random
                return possiblePlacements;
            }
            case 1 -> {
                // get the location of the cathedral
                Placement cathedral = board.getPlacedBuildings().get(0);

                // calculate place opposite of cathedral
                int x = cathedral.x() > 4 ? 3 : 6;
                int y = cathedral.y() > 4 ? 2 : 7;

                // create a new Placement for the infirmary
                Placement infirmaryPlacement = new Placement(
                        new Position(x, y), Direction._0, Building.Black_Infirmary
                );

                // return the placement
                return Collections.singleton(infirmaryPlacement);
            }
            default -> {
                // EarlyGameTurnCalculator should not be called after the first two turns.
                // switch to mid-game and return its turn
                gameStateManager.switchGameState(GameState.MidGame);
                return gameStateManager.calculateTurn();
            }
        }


    }
}
