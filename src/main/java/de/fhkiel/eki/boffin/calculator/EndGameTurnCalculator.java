package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.eki.helper.BoardHelper;
import de.fhkiel.ki.cathedral.game.*;

import java.util.*;

public class EndGameTurnCalculator implements TurnCalculator {

    /**
     * save the best placements for each player, so that both Players have their own when playing against himself
     */
    private static final Map<Color, List<Placement>> finalMoves = new HashMap<>();

    @Override
    public Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements) {
        // get the right gameStateManager
        GameStateManager gameStateManager = getGameStateManager(game.getCurrentPlayer());

        Color currentPlayer = game.getCurrentPlayer();
        // get from the gameStateManager the maximum amount of time
        long maxTime = System.currentTimeMillis() + gameStateManager.getRemainingTurnTime();

        // only calculate the best solution once
        if (finalMoves.get(currentPlayer) == null || finalMoves.get(currentPlayer).isEmpty()) {
            // notify the gameState Manager
            gameStateManager.startCalculatingFinalMoves();
            // calculate the best solution with a maximum amount of time
            Board bestBoard = fill(game.getBoard(), game.getPlacableBuildings(currentPlayer), maxTime);
            // compare the current board with the best board and get the difference
            List<Placement> bestPlacements = bestBoard.getPlacedBuildings();
            List<Placement> currentPlacements = game.getBoard().getPlacedBuildings();
            bestPlacements.removeAll(currentPlacements);

            // save the best placements for the current player
            finalMoves.put(currentPlayer, bestPlacements);
            // notify the gameStateManager
            gameStateManager.endCalculatingFinalMoves();
        }

        // get and remove the best move to play from the list
        Placement moveToPlay = finalMoves.get(currentPlayer).get(0);
        finalMoves.get(currentPlayer).remove(0);

        // return the best placements for the current player
        return Collections.singleton(moveToPlay);
    }

    private Board fill(Board board, List<Building> buildings, long maxTime) {

        // if the list of buildings is empty, the score is 0 best possible result
        // return the board
        if (buildings.isEmpty() || maxTime <= System.currentTimeMillis()) {
            return board;
        }

        Board bestBoard = board;

        // get all possible placements for the buildings
        Set<Placement> turnsInOwnArea = BoardHelper.getAllPossiblePlacementsFor(buildings, board);

        if (turnsInOwnArea.isEmpty()) {
            return board;
        }

        for (Placement nextPlacement : turnsInOwnArea) {

            Board nextBoard = board.copy();
            // get the color of the building
            Color color = nextPlacement.building().getColor();

            // place the building on the board
            if (!nextBoard.placeBuilding(nextPlacement, true)) {
                continue;
            }

            List<Building> unplacedBuildings = new ArrayList<>(buildings);
            unplacedBuildings.remove(nextPlacement.building());

            Board returnBoard = fill(nextBoard, unplacedBuildings, maxTime);

            if (returnBoard.score().get(color) <= 0) {
                bestBoard = returnBoard;
            }

            // evaluate the Board
            if (bestBoard.score().get(color) > returnBoard.score().get(color)) {
                bestBoard = returnBoard;
            }

            // if the maxTime has been reached or the score is 0 return the best Board
            if ((maxTime <= System.currentTimeMillis()) || bestBoard.score().get(color) == 0) {
                return bestBoard;
            }

        }

        return bestBoard;
    }

    public static void resetFinalResults() {
        finalMoves.clear();
    }
}
