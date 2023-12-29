package de.fhkiel.eki.boffin;

import de.fhkiel.eki.boffin.evaluations.*;
import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static de.fhkiel.eki.helper.BoardHelper.getAllPossiblePlacementsFor;

public class Boffin implements Agent {

    private static final String name = "Boffin";

    private static GameStateManager blackGameStateManager;
    private static GameStateManager whiteGameStateManager;

    private GameStateManager currentGameStateManager;

    private static PrintStream console;


    @Override
    public String name() {
        return name;
    }

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
        Boffin.console = console;
        blackGameStateManager = new GameStateManager(game, console, Color.Black);
        whiteGameStateManager = new GameStateManager(game, console, Color.White);
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {

        if (game.getCurrentPlayer() == Color.Black) {
            currentGameStateManager = blackGameStateManager;
        } else {
            currentGameStateManager = whiteGameStateManager;
        }

        // notify the gameManager that a new turn started
        currentGameStateManager.startTurn(game);

        // get all possible placements for the current Player
        Set<Placement> possiblePlacements = getAllPossiblePlacementsFor(game.getCurrentPlayer(), game.getBoard());

        currentGameStateManager.nrOfPossiblePlacementsCalculated(possiblePlacements);

        // check if there are any possible placements, if there is only one move left to play, play it
        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        } else if (possiblePlacements.size() == 1) {
            // if there is only one possible placement, play it
            return placeBuilding(possiblePlacements.stream().findFirst().get());
        }

        // calculate the best placements
        Set<Placement> calculatedPlacements = currentGameStateManager.calculateTurn();

        int calculatedSize = calculatedPlacements.size();
        if (calculatedSize == 0) throw new RuntimeException("I have no moves left. This should not have happened.");
        else if (calculatedSize == 1) {
            console.println("There is only one really good move to play...");
        } else {
            console.println("I have " + calculatedSize + " good moves.");
            console.println("Let me choose one at random.");
        }

        // return random move
        int randomNr = new Random().nextInt(calculatedPlacements.size());
        return placeBuilding(calculatedPlacements.stream().toList().get(randomNr));
    }


    private Optional<Placement> placeBuilding(Placement placement) {
        currentGameStateManager.finishTurn();
        return Optional.of(placement);
    }

    @Override
    public String evaluateLastTurn(Game game) {
        return "Evaluation score: " + evaluateGameState(game.getBoard(), new Evaluator(), true).eval();
    }

    @Override
    public void gameFinished(Game game) {
        currentGameStateManager.finishGame();
        Agent.super.gameFinished(game);
    }

    public static GeneralEvaluation evaluateGameState(Board board, Evaluator eval, boolean printEval) {

        ScoreEvaluation scoreEval = eval.score(board);
        AreaEvaluation areaEval = Evaluator.area(board);
        NextTurnEvaluation nextTurnEval = eval.potentialNextTurn(board);

        GeneralEvaluation sum = new GeneralEvaluation(scoreEval, areaEval, nextTurnEval);

        if (printEval) {
            console.println(sum);
        }

        return sum;
    }
}
