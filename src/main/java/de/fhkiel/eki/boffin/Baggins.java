package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.Set;
import java.util.Optional;
import java.util.Random;


public class Baggins implements Agent {

    PrintStream console;

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
        this.console = console;
    }

    @Override
    public String name() {
        return "Baggins";
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        // TODO: implement
        console.println("Calculating turn Nr: " + game.lastTurn().getTurnNumber() + " for " + game.getCurrentPlayer().name() + "...");

        int depth = 10;
        System.out.println("=========== Starting Turn ===========");
        EvalPair evalPair = minimax(game, depth, -9999, 9999);
        System.out.println("=========== Turn finished ===========");
        System.out.println(game.getCurrentPlayer().name() + " Playing move: " + evalPair.getPlacement());
        System.out.println("===========   Game Board  ===========");
        Helper.printBoard(game.getBoard());
        System.out.println("=====================================");
        return Optional.of(evalPair.getPlacement());
    }

    class EvalPair {
        private int eval;
        private Placement placement;

        EvalPair(int eval, Placement placement) {
            this.eval = eval;
            this.placement = placement;
        }

        public int getEval() {
            return eval;
        }

        public void setBest(int best) {
            this.eval = best;
        }

        public Placement getPlacement() {
            return placement;
        }

        public void setPlacement(Placement placement) {
            this.placement = placement;
        }


    }

    private EvalPair minimax(Game game, int depth, int alpha, int beta) {
        // termination condition
        if (game.isFinished() || depth == 0) {
            return new EvalPair(evaluateGameState(game, new Evaluator(game)), null);
        }

        // switch for each color
        switch (game.getCurrentPlayer()) {
            case White -> {
                int maxEval = -9999;
                Placement bestPlacement = null;
                for (Placement placement : Helper.getPossiblePlacements(game)) {
                    if (game.takeTurn(placement)) {
                        EvalPair evalPair = minimax(game, depth - 1, alpha, beta);
                        int eval = evalPair.getEval();

                        // alpha
                        alpha = Math.max(alpha, eval);

                        if (beta <= alpha) {
                            game.undoLastTurn();
                            break;
                        }

                        if (eval > maxEval) {
                            System.out.println("WHITE: Found better placement eval: " + eval + " Placement: " + placement);
                            maxEval = eval;
                            bestPlacement = placement;
                        }
                        game.undoLastTurn();
                    }
                }
                System.out.println("WHITE Best Score: " + maxEval + " + " + bestPlacement);
                return new EvalPair(maxEval, bestPlacement);
            }
            case Black -> {
                int minEval = 9999;
                Placement bestPlacement = null;
                for (Placement placement : Helper.getPossiblePlacements(game)) {
                    if (game.takeTurn(placement)) {
                        EvalPair evalPair = minimax(game, depth - 1, alpha, beta);
                        int eval = evalPair.getEval();

                        beta = Math.min(beta, eval);

                        if (beta <= alpha) {
                            game.undoLastTurn();
                            break;
                        }

                        if (eval < minEval) {
                            System.out.println("BLACK: Found better placement eval: " + eval + " Placement Score: " + placement.building().score());
                            minEval = eval;
                            bestPlacement = placement;
                        }
                        game.undoLastTurn();
                    }
                }
                System.out.println("BLACK Best Score: " + minEval + " + " + bestPlacement);
                return new EvalPair(minEval, bestPlacement);
            }
            default -> {
                // it's blue's turn.
                // return random move
                // TODO make predetermined move
                Set<Placement> possiblePlacements = Helper.getPossiblePlacements(game);
                Placement randomPlacement = possiblePlacements.stream().toList().get(new Random().nextInt(possiblePlacements.size()));
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
//        int potArea = game.lastTurn().getTurnNumber() < 3 ? 0 : eval.potentialArea();
//        int sum = scoreEval + areaEval + potArea;
        int sum = scoreEval + areaEval;

//        System.out.println("====State Eval====");
//        System.out.println("ScoreEval + AreaEval + PotAreaEval = Sum");
//        System.out.println(scoreEval + " + " + areaEval + " + " + potArea + " = " + sum);
//        System.out.println("==================");

        return sum;
    }

}
