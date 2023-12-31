package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.evaluations.AreaEvaluation;
import de.fhkiel.eki.boffin.evaluations.Evaluation;
import de.fhkiel.eki.boffin.evaluations.Evaluator;
import de.fhkiel.eki.boffin.evaluations.GeneralEvaluation;
import de.fhkiel.eki.boffin.gamestate.GameState;
import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.eki.boffin.work.BoffinsManager;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MidGameTurnCalculator implements TurnCalculator {

    private Map<Placement, GeneralEvaluation> calculatedPlacements;

    @Override
    public Set<Placement> calculateTurn(Game game, Set<Placement> possiblePlacements) {
        // get the right gameStateManager
        GameStateManager gameStateManager = getGameStateManager(game.getCurrentPlayer());

        gameStateManager.startEvaluatingPlacements();
        // Multithreaded evaluation of the placements
        try {
            BoffinsManager manager = new BoffinsManager();
            calculatedPlacements = manager.manageEvaluators(game, possiblePlacements);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        gameStateManager.endEvaluatingPlacements();
        // get my current Area
        int myCurrentArea = Evaluator.area(game.getBoard()).areaForColor(game.getCurrentPlayer());

        Color myColor = game.getCurrentPlayer();

        Collection<GeneralEvaluation> evaluations = calculatedPlacements.values();

        boolean outsideMovesRemain = false;

        // search all evaluations for a move where the area shrunk
        for (GeneralEvaluation evaluation : evaluations) {
            AreaEvaluation areaEval = evaluation.areaEval();
            if (areaEval.areaForColor(myColor) >= myCurrentArea) {
                // a move has been found that is outside owned area.
                outsideMovesRemain = true;
                // break to interrupt the loop
                break;
            }
        }
        // check if a move outside owned area was found
        if (!outsideMovesRemain) {
            // there are only placements inside owned territory
            gameStateManager.switchGameState(GameState.EndGame);
            // return the end-game calculated placements
            return gameStateManager.calculateTurn();
        } else {
            // there are still placements outside own territory
            final int bestEvalScore;

            // get the score of the best-evaluated placement depending on color (white max; black min)
            if (game.getCurrentPlayer() == Color.Black) {
                bestEvalScore = Collections.min(calculatedPlacements.values().stream().map(Evaluation::eval).toList());
            } else {
                bestEvalScore = Collections.max(calculatedPlacements.values().stream().map(Evaluation::eval).toList());
            }


            // filter the calculatedPlacements map so that only the placements with the best score remain
            return calculatedPlacements
                    .keySet()
                    .stream()
                    .filter(placement -> calculatedPlacements.get(placement).eval() == bestEvalScore)
                    .collect(Collectors.toSet());

        }
    }
}
