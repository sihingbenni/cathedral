package de.fhkiel.eki.boffin.evaluations;

import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.HashSet;
import java.util.Set;

public class Evaluator {

    public ScoreEvaluation score(Board board) {
        // White wants to maximize its EvaluationScore.
        // If black is subtracted from white, we get a negative number.
        // To flip the negative number to a positive one and achieve that white maximizes its EvaluationScore,
        // we subtract white from black.
        return new ScoreEvaluation(board.score().get(Color.Black), board.score().get(Color.White));
    }

    public int potentialInNextTurn(Board board, Color color) {
        int currentArea = area(board).eval();
        int currentScore = score(board).eval();
        int potEval = currentArea + currentScore;

        // go through all possible moves, check if the area + score is bigger than before
        Set<Placement> availableMoves = new HashSet<>();
        Set<Building> placeableBuildings = new HashSet<>(board.getPlacableBuildings(color));
        for (Building placableBuilding : placeableBuildings) {
            availableMoves.addAll(placableBuilding.getAllPossiblePlacements());
        }

        for (Placement availableMove : availableMoves) {

            Board boardCopy = board.copy();
            if (boardCopy.placeBuilding(availableMove)) {
                int potArea = area(boardCopy).eval();
                int potScore = score(boardCopy).eval();
                // if the potential area + score is bigger than before, save the difference

                // if player white:
                if (color == Color.White) {
                    if (potEval < (potArea + potScore - potEval)) {
                        potEval = (potArea + potScore);
                    }
                } else {
                    // else player black
                    if (potEval > (potArea + potScore - potEval)) {
                        potEval = (potArea + potScore);
                    }
                }
            }
        }

        return potEval;
    }

    public NextTurnEvaluation potentialNextTurn(Board board) {
        return new NextTurnEvaluation(potentialInNextTurn(board, Color.White), potentialInNextTurn(board, Color.Black));
    }

    public static AreaEvaluation area(Board board) {
        // TO-DO welche Gebäude passen eigentlich in dieses Gebiet rein.

        int areaBlack = 0;
        int areaWhite = 0;
        for (Color[] fieldColors : board.getField()) {
            for (Color fieldColor : fieldColors) {
                if (fieldColor == Color.Black_Owned) {
                    areaBlack++;
                } else if (fieldColor == Color.White_Owned) {
                    areaWhite++;
                }
            }
        }
        return new AreaEvaluation(areaBlack, areaWhite);
    }
}

