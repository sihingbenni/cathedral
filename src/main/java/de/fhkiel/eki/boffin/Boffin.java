package de.fhkiel.eki.boffin;

import de.fhkiel.eki.work.BoffinsManager;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import java.io.PrintStream;
import java.util.*;

public class Boffin implements Agent {

    static PrintStream console;

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
        Boffin.console = console;
    }

    @Override
    public String name() {
        return "Boffin";
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        console.println("Calculating turn Nr: " + game.lastTurn().getTurnNumber() + " for " + game.getCurrentPlayer().name() + "...");

        // get all possible placements
        Set<Building> placeableBuildings = new HashSet<>(game.getPlacableBuildings(game.getCurrentPlayer()));
        Set<Placement> possiblePlacements = new HashSet<>(placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());

        console.println(game.getCurrentPlayer().name() + " has " + possiblePlacements.size() + " possible moves.");

        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        }
        Map<Placement, Integer> calculatedPlacements;

        // Multithreaded evaluation of the placements
        try {
            BoffinsManager manager = new BoffinsManager();
            calculatedPlacements = manager.manageEvaluators(game, possiblePlacements);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final int bestEvalScore;

        // get the score of the best-evaluated placement depending on color (white max; black min)
        if (game.getCurrentPlayer() == Color.Black) {
            bestEvalScore = Collections.min(calculatedPlacements.values());
        } else {
            bestEvalScore = Collections.max(calculatedPlacements.values());
        }

        // filter the calculatedPlacements map so that only the placements with the best score remain
        List<Placement> bestPlacements = calculatedPlacements.keySet().stream().filter(placement -> calculatedPlacements.get(placement) == bestEvalScore).toList();

        System.out.println(bestPlacements.size());

        // return the placement with the best score
        return Optional.of(bestPlacements.get(new Random().nextInt(bestPlacements.size())));
    }

    @Override
    public String evaluateLastTurn(Game game) {

        return "Evaluation score: " + evaluateGameState(game.lastTurn().getTurnNumber(), game.getBoard(), new Evaluator(game.getBoard()), true);
    }

    public static int evaluateGameState(int lastTurnNumber, Board board, Evaluator eval, boolean printEval) {

        int scoreEval = eval.score(board);
        int areaEval = eval.area(board);
        int potArea = lastTurnNumber < 2 ? 0 : eval.potentialArea();
        int sum = scoreEval + areaEval + potArea;

        // print the evaluation if desired
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

        return sum;
    }

}
