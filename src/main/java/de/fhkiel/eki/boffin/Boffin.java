package de.fhkiel.eki.boffin;

import de.fhkiel.eki.boffin.evaluations.*;
import de.fhkiel.eki.boffin.gamestate.GameStateManager;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static de.fhkiel.eki.helper.BoardHelper.getAllPossiblePlacementsFor;

public record Boffin(String name) implements Agent {

    private static GameStateManager gameStateManager;

    private static PrintStream console;

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
        Boffin.console = console;
        gameStateManager = new GameStateManager(game, console);
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {

        // notify the gameManager that a new turn started
        gameStateManager.startTurn(game);

        // get all possible placements for the current Player
        Set<Placement> possiblePlacements = getAllPossiblePlacementsFor(game.getCurrentPlayer(), game.getBoard());

        gameStateManager.nrOfPossiblePlacementsCalculated(possiblePlacements);

        // check if there are any possible placements, if there is only one move left to play, play it
        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        } else if (possiblePlacements.size() == 1) {
            // if there is only one possible placement, play it
            return placeBuilding(possiblePlacements.stream().findFirst().get());
        }

        // calculate the best placements
        Set<Placement> calculatedPlacements = gameStateManager.calculateTurn();

        int calculatedSize = calculatedPlacements.size();
        if (calculatedSize == 0) throw new RuntimeException("I have no moves left. This should not have happened.");
        else if (calculatedSize == 1) {
            console.println("There is only one good move to play...");
        } else {
            console.println("I have " + calculatedSize + " good moves.");
            console.println("Let me choose one at random.");
        }

        // return random move
        int randomNr = new Random().nextInt(calculatedPlacements.size());
        return placeBuilding(calculatedPlacements.stream().toList().get(randomNr));
    }


    private Optional<Placement> placeBuilding(Placement placement) {
        gameStateManager.finishTurn();
        return Optional.of(placement);
    }

    @Override
    public String evaluateLastTurn(Game game) {
        return "Evaluation score: " + evaluateGameState(game.getBoard(), new Evaluator(), true).eval();
    }

    @Override
    public void gameFinished(Game game) {
        gameStateManager.finishGame();
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
