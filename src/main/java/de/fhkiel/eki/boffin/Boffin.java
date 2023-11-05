package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.*;

public class Boffin implements Agent {

    PrintStream console;

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
        this.console = console;
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        // TODO: implement
        console.println("Calculating turn Nr: " + game.lastTurn().getTurnNumber() + " for " + game.getCurrentPlayer().name() + "...");

        // get all possible placements
        List<Building> placeableBuildings = game.getPlacableBuildings();
        List<Placement> possiblePlacements = placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList();

        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        }

        Map<Placement, Integer> calculatedPlacements = new HashMap<>();

        // evaluate all possible placements
        possiblePlacements.forEach(placement -> {
            if (game.takeTurn(placement)) {
                int eval = evaluateGameState(game, getEvaluatorForLastTurn(game));
                calculatedPlacements.put(placement, eval);
                game.undoLastTurn();
            }
        });

        // get the score of the best-evaluated placement
        int bestEvalScore = Collections.max(calculatedPlacements.values());

        List<Placement> bestPlacements = possiblePlacements.stream().filter(placement -> calculatedPlacements.get(placement) == bestEvalScore).toList();

        // return the placement with the best score
        return Optional.of(bestPlacements.get(new Random().nextInt(bestPlacements.size())));
    }

    @Override
    public String evaluateLastTurn(Game game) {

        return "Evaluation score for " + game.getCurrentPlayer().name() + ": " + evaluateGameState(game, getEvaluatorForThisTurn(game));
    }

    private Evaluator getEvaluatorForThisTurn(Game game) {
        return new Evaluator(game, "thisTurn");
    }

    private Evaluator getEvaluatorForLastTurn(Game game) {
        return new Evaluator(game, "lastTurn");
    }

    private int evaluateGameState(Game game, Evaluator eval) {

        int scoreEval = eval.score();
        int areaEval = eval.area();
        int potArea = game.lastTurn().getTurnNumber() < 3 ? 0 : eval.potentialArea();
        int sum = scoreEval + areaEval + potArea;

//        console.println("==================");
//        console.println("----- Eval for " + eval.myColor.name() + " -----");
//        console.println("ScoreEval:\t" + scoreEval);
//        console.println("AreaEval:\t" + areaEval);
//        console.println("PotAreaEval:\t" + potArea);
//        console.println("------------------------------");
//        console.println("Sum:\t" + sum);
//        console.println("==================");

        return sum;
    }

}
