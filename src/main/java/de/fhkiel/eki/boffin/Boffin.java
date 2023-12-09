package de.fhkiel.eki.boffin;

import de.fhkiel.eki.boffin.Evaluations.*;
import de.fhkiel.eki.work.BoffinsManager;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class Boffin implements Agent {

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

        long start = System.currentTimeMillis();

        int lastTurnNumber = game.lastTurn().getTurnNumber();
        console.println("=====================================");
        console.println("Calculating turn Nr: " + lastTurnNumber + " for " + game.getCurrentPlayer().name() + "...");


        // check if the game has been reset
        if (lastTurnNumber <= 1) {
            gameState = GameState.EarlyGame;
        }

        // get all possible placements
        Set<Placement> possiblePlacements = game.getPlacableBuildings(game.getCurrentPlayer())
                .stream()
                .map(building -> building.getPossiblePlacements(game))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        long timePossiblePlacements = System.currentTimeMillis();
        console.println("retrieving possible placements took: " + (timePossiblePlacements - start) + "ms");

        console.println(game.getCurrentPlayer().name() + " has " + possiblePlacements.size() + " possible moves.");

        // check if there are any possible placements, if there is only one move left to play, play it
        if (possiblePlacements.isEmpty()) {
            return Optional.empty();
        } else if (possiblePlacements.size() == 1) {
            // if there is only one possible placement, play it
            return possiblePlacements.stream().findFirst();
        }


        Map<Placement, Evaluation> calculatedPlacements = new HashMap<>();

        // Switch between the different game states
        switch (gameState) {
            case EarlyGame:
                gameState = GameState.MidGame;
                console.println("Switching to Mid-Game.");

                // if it's the first turn for white,
                // place the cathedral in the top left corner
                if (lastTurnNumber == 0) {
                    // place the cathedral random
                    return Optional.of(possiblePlacements.stream().toList().get(new Random().nextInt(possiblePlacements.size())));
                } else if (lastTurnNumber == 1) {
                    // place the infirmary on the opposite side of the cathedral
                    Placement cathedral = game.getBoard().getPlacedBuildings().get(0);

                    // calculate place opposite of cathedral
                    int x = cathedral.x() > 4 ? 3 : 6;
                    int y = cathedral.y() > 4 ? 2 : 7;

                    return Optional.of(new Placement(new Position(x, y), Direction._0, Building.Black_Infirmary));
                }

            case MidGame:
                // Multithreaded evaluation of the placements
                try {
                    BoffinsManager manager = new BoffinsManager();
                    calculatedPlacements = manager.manageEvaluators(game, possiblePlacements);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                console.println("calculating and setting placements took: " + (System.currentTimeMillis() - timePossiblePlacements) + "ms");
                int areaSmaller = 0;
                // get my current Area
                int myCurrentArea = Evaluator.area(game.getBoard()).areaForColor(game.getCurrentPlayer());
                System.out.println("My Current Area: " + myCurrentArea);
                for (Map.Entry<Placement, Evaluation> entry : calculatedPlacements.entrySet()) {

                    GeneralEvaluation eval = (GeneralEvaluation) entry.getValue();
//                    System.out.println("My Current Area: " + myCurrentArea + " Evaluated Area: " + eval.areaEval().areaForColor(game.getCurrentPlayer()));
                    if (myCurrentArea > eval.areaEval().areaForColor(game.getCurrentPlayer())) {
                        areaSmaller++;
                    }
                }
                if (areaSmaller < calculatedPlacements.size()) {
                    // there are still placements outside of own territory
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
                    System.out.println("Best placements: " + bestPlacements.size());
                    if (bestPlacements.size() == 1) {
                        console.println("Found only one good move.");
                        return bestPlacements.stream().findFirst();
                    } else {

                        // if there are multiple placements with the same score, evaluate the future ones
                        console.println("Found " + bestPlacements.size() + " good moves.");
                        console.println("Playing random move.");

                        return Optional.of(bestPlacements.stream().toList().get(new Random().nextInt(bestPlacements.size())));

                    }
                } else {
                    // there are only placements inside owned territory, switch to endgame
                    gameState = GameState.EndGame;
                    console.println("Found only placements inside owned territory.");
                    console.println("Switching to End-Game.");
                }

            case EndGame:
                // breadth-first search for the best placement of all possible placements

                Board board = game.getBoard().copy();
                List<Placement> finalTurns = new ArrayList<>();

                List<Placement> bestTurns = breadthFirstSearch(board, finalTurns, game.getCurrentPlayer());
                System.out.println("Best Turns Size: " + bestTurns.size());

            default:
                throw new RuntimeException("Unknown GameState!");
        }
    }

    private List<Placement> breadthFirstSearch(Board board, List<Placement> finalTurns, Color currentPlayer) {

        if (board.score().get(currentPlayer) == 0) {
            return finalTurns;
        }

        // how to only return the one list of turns which leads to a score of 0? Would a depth first search be better?

        // get all possible placements and then place each possible placement on a copy of the board and
        // if it is a successfull turn, add it to the list of final turns and call the method again with the copy
        // of the board
        for (Building building : board.getPlacableBuildings(currentPlayer)) {
            Set<Placement> placements = building.getPossiblePlacements(board);
            System.out.println("Placements Size: " + placements.size());
            for (Placement placement : placements) {
                Board boardCopy = board.copy();
                boolean successfullTurn = boardCopy.placeBuilding(placement, false);
                if (successfullTurn) {
                    System.out.println("placement is added: " + placement);
                    finalTurns.add(placement);

                    breadthFirstSearch(boardCopy, finalTurns, currentPlayer);
                }
            }
        }

        System.out.println("Score: " + board.score().get(currentPlayer));

        System.out.println("Final Turn Liste: " + finalTurns.size());
        return finalTurns;
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
