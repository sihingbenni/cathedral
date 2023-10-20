package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.Optional;

public class Boffin implements Agent {

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {

        return Optional.empty();
    }

    @Override
    public String evaluateLastTurn(Game game) {
        return "Evaluation for " + game.getCurrentPlayer().name() + ": " + evaluateGameState(game);
    }

    int evaluateGameState(Game game) {
        System.out.println("---- Eval ----");
        System.out.println("My Color is: " + game.getCurrentPlayer().name());
        Evaluator eval = new Evaluator(game);
        int scoreEval = eval.score();
        int areaEval = eval.area();


        System.out.println("ScoreEval: " + scoreEval);
        System.out.println("AreaEval: " + areaEval);

        return scoreEval + areaEval;
    }

}
