package de.fhkiel.eki.boffin;

import de.fhkiel.eki.boffin.Evaluations.*;
import de.fhkiel.eki.helper.HelperFunction;
import de.fhkiel.eki.work.BoffinsManager;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static de.fhkiel.eki.helper.HelperFunction.getAllPossiblePlacementsFor;

public class Boffin implements Agent {

    static long remainingTime = 120_000;
    long startTime;
    static PrintStream console;
    private GameState gameState;

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
        Boffin.console = console;
        gameState = GameState.EarlyGame;
    }

    @Override
    public String name() {
        return "Boffin";
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {

        startTime = System.currentTimeMillis();

        int lastTurnNumber = game.lastTurn().getTurnNumber();
        console.println("=====================================");
        console.println("Calculating turn Nr: " + lastTurnNumber + " for " + game.getCurrentPlayer().name() + "...");


        // check if the game has been reset
        if (lastTurnNumber <= 1) {
            gameState = GameState.EarlyGame;
        }

        // get all possible placements
        Set<Placement> possiblePlacements = getAllPossiblePlacementsFor(game.getCurrentPlayer(), game.getBoard());

        console.println("I have " + possiblePlacements.size() + " possible moves.");

        // check if there are any possible placements, if there is only one move left to play, play it
        if (possiblePlacements.isEmpty()) {
            console.println("Its your turn");
            return Optional.empty();
        } else if (possiblePlacements.size() == 1) {
            // if there is only one possible placement, play it
            return placeBuilding(possiblePlacements.stream().findFirst());
        }


        Map<Placement, Evaluation> calculatedPlacements;

        // Switch between the different game states
        switch (gameState) {
            case EarlyGame:
                gameState = GameState.MidGame;
                console.println("Switching to Mid-Game.");

                // if it's the first turn for white,
                // place the cathedral in the top left corner
                if (lastTurnNumber == 0) {
                    // place the cathedral random
                    return placeBuilding(Optional.of(possiblePlacements.stream().toList().get(new Random().nextInt(possiblePlacements.size()))));
                } else if (lastTurnNumber == 1) {
                    // place the infirmary on the opposite side of the cathedral
                    Placement cathedral = game.getBoard().getPlacedBuildings().get(0);

                    // calculate place opposite of cathedral
                    int x = cathedral.x() > 4 ? 3 : 6;
                    int y = cathedral.y() > 4 ? 2 : 7;

                    return placeBuilding(Optional.of(new Placement(new Position(x, y), Direction._0, Building.Black_Infirmary)));
                }

            case MidGame:
                // Multithreaded evaluation of the placements
                try {
                    BoffinsManager manager = new BoffinsManager();
                    calculatedPlacements = manager.manageEvaluators(game, possiblePlacements);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                console.println("calculating and setting placements took: " + (System.currentTimeMillis() - startTime) + "ms");
                int areaSmaller = 0;
                // get my current Area
                int myCurrentArea = Evaluator.area(game.getBoard()).areaForColor(game.getCurrentPlayer());
                for (Map.Entry<Placement, Evaluation> entry : calculatedPlacements.entrySet()) {

                    GeneralEvaluation eval = (GeneralEvaluation) entry.getValue();
//                    System.out.println("My Current Area: " + myCurrentArea + " Evaluated Area: " + eval.areaEval().areaForColor(game.getCurrentPlayer()));
                    if (myCurrentArea > eval.areaEval().areaForColor(game.getCurrentPlayer())) {
                        areaSmaller++;
                    }
                }
                if (areaSmaller < calculatedPlacements.size()) {
                    // there are still placements outside own territory
                    final int bestEvalScore;

                    // get the score of the best-evaluated placement depending on color (white max; black min)
                    if (game.getCurrentPlayer() == Color.Black) {
                        bestEvalScore = Collections.min(calculatedPlacements.values().stream().map(Evaluation::eval).toList());
                    } else {
                        bestEvalScore = Collections.max(calculatedPlacements.values().stream().map(Evaluation::eval).toList());
                    }


                    // filter the calculatedPlacements map so that only the placements with the best score remain
                    Map<Placement, Evaluation> finalCalculatedPlacements = calculatedPlacements;
                    Set<Placement> bestPlacements = calculatedPlacements.keySet().stream().filter(placement -> finalCalculatedPlacements.get(placement).eval() == bestEvalScore).collect(Collectors.toSet());

                    if (bestPlacements.size() == 1) {
                        console.println("I found only one good move.");
                        return placeBuilding(bestPlacements.stream().findFirst());
                    } else {

                        // if there are multiple placements with the same score, evaluate the future ones
                        console.println("Found " + bestPlacements.size() + " good moves.");
                        console.println("Playing random move.");

                        return placeBuilding(Optional.of(bestPlacements.stream().toList().get(new Random().nextInt(bestPlacements.size()))));

                    }
                } else {
                    // there are only placements inside owned territory, switch to endgame
                    gameState = GameState.EndGame;
                    console.println("Found only placements inside owned territory.");
                    console.println("Switching to End-Game.");
                }

            case EndGame:

                long maxTime = System.currentTimeMillis() + 9000;
                Board bestBoard = fill(game.getBoard(), game.getPlacableBuildings(game.getCurrentPlayer()), maxTime);

                List<Placement> bestPlacements = bestBoard.getPlacedBuildings();
                List<Placement> currentPlacements = game.getBoard().getPlacedBuildings();
                bestPlacements.removeAll(currentPlacements);


                return placeBuilding(bestPlacements.stream().findFirst());
            default:
                throw new RuntimeException("Unknown GameState!");
        }
    }

    private Optional<Placement> placeBuilding(Optional<Placement> placement) {
        long tookTime = System.currentTimeMillis() - startTime;
        if(tookTime > 10_000) {
            remainingTime -= tookTime - 10_000;
            console.println("I used " + (tookTime - 10_000) / 1000 + "s more than I should have.");
            console.println("I have: " + (remainingTime / 1000) + "s left.");
        }
        console.println("My turn took: " + (tookTime / 1000) + "s");
        console.println("Its your turn!");
        return placement;
    }


    private Board fill(Board board, List<Building> buildings, long maxTime) {

        // if the list of buildings is empty, the score is 0 best possible result
        // return the board
        if (buildings.isEmpty() || maxTime <= System.currentTimeMillis()) {
            return board;
        }

        Board bestBoard = board;

        // get all possible placements for the buildings
        Set<Placement> turnsInOwnArea = HelperFunction.getAllPossiblePlacementsFor(buildings, board);

        if(turnsInOwnArea.isEmpty()) {
            return board;
        }

        for(Placement nextPlacement : turnsInOwnArea) {

            Board nextBoard = board.copy();
            // get the color of the building
            Color color = nextPlacement.building().getColor();

            // place the building on the board
            nextBoard.placeBuilding(nextPlacement);
            List<Building> unplacedBuildings = new ArrayList<>(buildings);
            unplacedBuildings.remove(nextPlacement.building());

            Board returnBoard = fill(nextBoard, unplacedBuildings, maxTime);

            if (returnBoard.score().get(color) <= 0) {
                bestBoard = returnBoard;
            }

            // evaluate the Board
            if (bestBoard.score().get(color) > returnBoard.score().get(color)) {
                bestBoard = returnBoard;
            }


            if(maxTime <= System.currentTimeMillis()) {
                return bestBoard;
            }

        }

        return bestBoard;
    }

    @Override
    public String evaluateLastTurn(Game game) {
        return "Evaluation score: " + evaluateGameState(game.getBoard(), new Evaluator(), true).eval();
    }

    @Override
    public void gameFinished(Game game) {
        // reset the gameState
        gameState = GameState.GameOver;
        Agent.super.gameFinished(game);
        console.println("Game finished!");
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
