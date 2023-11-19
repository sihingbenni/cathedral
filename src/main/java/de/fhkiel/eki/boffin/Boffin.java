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
        int lastTurnNumber = game.lastTurn().getTurnNumber();
        console.println("Calculating turn Nr: " + lastTurnNumber + " for " + game.getCurrentPlayer().name() + "...");

        // get all possible placements
        Set<Building> placeableBuildings = new HashSet<>(game.getPlacableBuildings(game.getCurrentPlayer()));
        Set<Placement> possiblePlacements = new HashSet<>(placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());

        console.println(game.getCurrentPlayer().name() + " has " + possiblePlacements.size() + " possible moves.");

        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        }

        // if it's the first turn for black, play a big building at a wall far away from the cathedral

        if (lastTurnNumber == 1 && game.getCurrentPlayer() == Color.Black) {
            // TODO implement
            // maybe mirror the board and find the place of the cathedral and then place the building there
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
        // the game does not allow capturing area until the 2nd turn so no need to calculate potential area
        int potArea = lastTurnNumber <= 1 ? 0 : eval.potentialArea();
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
