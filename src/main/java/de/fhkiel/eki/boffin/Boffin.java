package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
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
    public String name() {
        return "Boffin";
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        // TODO: implement
        console.println("Calculating turn Nr: " + game.lastTurn().getTurnNumber() + " for " + game.getCurrentPlayer().name() + "...");

        // get all possible placements
        Set<Building> placeableBuildings = new HashSet<>(game.getPlacableBuildings(game.getCurrentPlayer()));
        Set<Placement> possiblePlacements = new HashSet<>(placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());

        console.println(game.getCurrentPlayer().name() + " has " + possiblePlacements.size() + " possible moves.");

        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        }

        Map<Placement, Integer> calculatedPlacements = new HashMap<>();

        // evaluate all possible placements
        possiblePlacements.forEach(placement -> {
            if (game.takeTurn(placement)) {
                int eval = evaluateGameState(game, new Evaluator(game), false);
                calculatedPlacements.put(placement, eval);
                game.undoLastTurn();
            }
        });

        int bestEvalScore;


        // get the score of the best-evaluated placement depending on color (white max; black min)
        if (game.getCurrentPlayer() == Color.Black) {
            bestEvalScore = Collections.min(calculatedPlacements.values());
        } else {
            bestEvalScore = Collections.max(calculatedPlacements.values());
        }

        Set<Placement> bestPlacements = new HashSet<>(possiblePlacements.stream().filter(placement -> calculatedPlacements.get(placement) == bestEvalScore).toList());

        // return the placement with the best score
        return Optional.of(bestPlacements.stream().toList().get(new Random().nextInt(bestPlacements.size())));
    }

    @Override
    public String evaluateLastTurn(Game game) {

        return "Evaluation score: " + evaluateGameState(game, new Evaluator(game), true);
    }

    private int evaluateGameState(Game game, Evaluator eval, boolean printEval) {

        int scoreEval = eval.score();
        int areaEval = eval.area();
        int potArea = game.lastTurn().getTurnNumber() < 3 ? 0 : eval.potentialArea();
        int sum = scoreEval + areaEval + potArea;
        if (printEval) {

            console.println("==================");
            console.println("----- State Eval -----");
            console.println("ScoreEval:\t" + scoreEval);
            console.println("AreaEval:\t" + areaEval);
            console.println("PotAreaEval:\t" + potArea);
            console.println("------------------------------");
            console.println("Sum:\t" + sum);
            console.println("==================");
        }
        // System.out.println("====State Eval====");
        // System.out.println("ScoreEval + AreaEval + PotAreaEval = Sum");
        // System.out.println(scoreEval + " + " + areaEval + " + " + potArea + " = " + sum);
        // System.out.println("==================");

        return sum;
    }

}
