package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.*;

public class Boffin implements Agent {

    @Override
    public void initialize(Game game, PrintStream console) {
        new Helper(game);
        Agent.super.initialize(game, console);
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        // TODO: implement

        // get all possible placements
        List<Building> placeableBuildings = game.getPlacableBuildings();
        List<Placement> possiblePlacements = placeableBuildings.stream()
                .map(building -> building.getPossiblePlacements(game))
                .flatMap(Collection::stream)
                .toList();

        Map<Integer, Placement> calculatedPlacements = new HashMap<>();

        // evaluate all possible placements
        possiblePlacements.forEach(placement -> {
            if (game.takeTurn(placement)) {
                int eval = evaluateGameState(getEvaluatorForLastTurn(game));
                calculatedPlacements.put(eval, placement);
                game.undoLastTurn();
            }
        });
        // get the score of the best-evaluated placement
        int bestEvalScore = Collections.max(calculatedPlacements.keySet());

        // return the placement with the best score
        return Optional.of(calculatedPlacements.get(bestEvalScore));
    }

    @Override
    public String evaluateLastTurn(Game game) {

        return "Evaluation score for " + game.getCurrentPlayer().name() + ": " + evaluateGameState(getEvaluatorForThisTurn(game));
    }

    private Evaluator getEvaluatorForThisTurn(Game game) {
        return new Evaluator(game, "thisTurn");
    }

    private Evaluator getEvaluatorForLastTurn(Game game) {
        return new Evaluator(game, "lastTurn");
    }

    private int evaluateGameState(Evaluator eval) {

        int scoreEval = eval.score();
        int areaEval = eval.area();
        int potArea = eval.potentialArea();
        int sum = scoreEval + areaEval + potArea;

        System.out.println("----- Eval for " + eval.myColor.name() + " -----");
        System.out.println("ScoreEval:              " + scoreEval);
        System.out.println("AreaEval:               " + areaEval);
        System.out.println("PotAreaEval:            " + potArea);
        System.out.println("--------------------------");
        System.out.println("Sum:                    " + sum);
        System.out.println("==========================");

        return sum;
    }

}
