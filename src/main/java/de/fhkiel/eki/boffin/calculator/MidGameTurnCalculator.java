package de.fhkiel.eki.boffin.calculator;

import de.fhkiel.eki.boffin.evaluations.Evaluation;
import de.fhkiel.eki.boffin.evaluations.Evaluator;
import de.fhkiel.eki.boffin.evaluations.GeneralEvaluation;
import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.eki.boffin.work.BoffinsManager;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.*;
import java.util.stream.Collectors;

public class MidGameTurnCalculator implements TurnCalculator {

    private Map<Placement, Evaluation> calculatedPlacements;

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
        int areaSmaller = 0;
        // get my current Area
        int myCurrentArea = Evaluator.area(game.getBoard()).areaForColor(game.getCurrentPlayer());
        for (Map.Entry<Placement, Evaluation> entry : calculatedPlacements.entrySet()) {

            GeneralEvaluation eval = (GeneralEvaluation) entry.getValue();
//                    System.out.println("My Current Area: " + myCurrentArea + " Evaluated Area: " + eval.areaEval().areaForColor(game.getCurrentPlayer()));
            if (myCurrentArea > eval.areaEval().areaForColor(game.getCurrentPlayer())) {
                areaSmaller++;
            }
        }
        if (areaSmaller >= calculatedPlacements.size()) {
            // there are only placements inside owned territory, switch to endgame
            return gameStateManager.switchToEndgameCalculations();
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
