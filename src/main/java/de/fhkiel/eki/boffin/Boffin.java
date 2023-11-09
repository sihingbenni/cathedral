package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.ai.Agent;
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

        int depth = 2;

        EvalPair evalPair = minimax(game, depth);

        return Optional.of(evalPair.getPlacement());
    }

    class EvalPair {
        private int best;
        private Placement placement;

        EvalPair(int best, Placement placement) {
            this.best = best;
            this.placement = placement;
        }

        public int getBest() {
            return best;
        }

        public void setBest(int best) {
            this.best = best;
        }

        public Placement getPlacement() {
            return placement;
        }

        public void setPlacement(Placement placement) {
            this.placement = placement;
        }


    }

    private EvalPair minimax(Game game, int depth) {
        // termination condition
        if (game.isFinished() || depth == 0) {
            return new EvalPair(evaluateGameState(game, new Evaluator(game)), null);
        }

        // switch for each color
        switch (game.getCurrentPlayer()) {
            case White -> {
                int best = -9999;
                Placement bestPlacement = null;
                for (Placement placement : Helper.getPossiblePlacements(game)) {
                    if (game.takeTurn(placement)) {
                        EvalPair evalPair = minimax(game, depth - 1);
                        int value = evalPair.getBest();

                        if (value > best) {
                            best = value;
                            bestPlacement = placement;
                        }
                        game.undoLastTurn();
                    }
                }

                return new EvalPair(best, bestPlacement);
            }
            case Black -> {
                int best = 9999;
                Placement bestPlacement = null;
                for (Placement placement : Helper.getPossiblePlacements(game)) {
                    if (game.takeTurn(placement)) {
                        EvalPair evalPair = minimax(game, depth - 1);
                        int value = evalPair.getBest();

                        if (value < best) {
                            best = value;
                            bestPlacement = placement;
                        }
                        game.undoLastTurn();
                    }
                }
                return new EvalPair(best, bestPlacement);
            }
            default -> {
                // it's blue's turn.
                // return random move
                List<Placement> possiblePlacements = Helper.getPossiblePlacements(game);
                Placement randomPlacement = possiblePlacements.get(new Random().nextInt(possiblePlacements.size()));
                return new EvalPair(0, randomPlacement);
            }
        }
    }


    @Override
    public String evaluateLastTurn(Game game) {

        return "Evaluation score: " + evaluateGameState(game, new Evaluator(game));
    }

    private int evaluateGameState(Game game, Evaluator eval) {

        int scoreEval = eval.score();
        int areaEval = eval.area();
        int potArea = game.lastTurn().getTurnNumber() < 3 ? 0 : eval.potentialArea();
        int sum = scoreEval + areaEval + potArea;

//        console.println("==================");
//        console.println("----- State Eval -----");
//        console.println("ScoreEval:\t" + scoreEval);
//        console.println("AreaEval:\t" + areaEval);
//        console.println("PotAreaEval:\t" + potArea);
//        console.println("------------------------------");
//        console.println("Sum:\t" + sum);
//        console.println("==================");
//        System.out.println("==================");
//        System.out.println("----- State Eval -----");
//        System.out.println("ScoreEval:\t" + scoreEval);
//        System.out.println("AreaEval:\t" + areaEval);
//        System.out.println("PotAreaEval:\t" + potArea);
//        System.out.println("------------------------------");
//        System.out.println("Sum:\t" + sum);
//        System.out.println("==================");

        System.out.println("====State Eval====");
        System.out.println("ScoreEval + AreaEval + PotAreaEval = Sum");
        System.out.println(scoreEval + " + " + areaEval + " + " + potArea + " = " + sum);
        System.out.println("==================");

        return sum;
    }

}
