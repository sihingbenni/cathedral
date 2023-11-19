package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Set;

public class Evaluator {
    private final Helper helper;

    public Evaluator(Board board) {
        helper = new Helper(board);
    }

    public int score(Board board) {
        // White wants to maximize its EvaluationScore.
        // If black is subtracted from white, we get a negative number.
        // To flip the negative number to a positive one and achieve that white maximizes its EvaluationScore,
        // we subtract white from black.
        return board.score().get(Color.Black) - board.score().get(Color.White);
    }

    public int potentialAreaInNextTurn(Board board, Color color) {
        int currentArea = area(board);
        int potAreaEval = currentArea;

        // go through all possible moves, check if the area is bigger than before
        Set<Placement> availableMoves = helper.getAvailableMovesFor(color);
        for (Placement availableMove : availableMoves) {

            // only placements that connect to another building or wall need to be checked
            if (!helper.shouldEvalPotentialAreaForPlacement(availableMove)) {
                continue;
            }

            Board boardCopy = board.copy();
            if (boardCopy.placeBuilding(availableMove)) {
                int potArea = area(boardCopy);
                // if the potential area is bigger than before, save the difference

                // if player white:
                if (color == Color.White) {
                    if (potAreaEval < (potArea - currentArea)) {
                        potAreaEval = (potArea - currentArea);
                    }
                } else {
                    // else player black
                    if (potAreaEval > (potArea - currentArea)) {
                        potAreaEval = (potArea - currentArea);
                    }
                }
            }
        }

        return potAreaEval;
    }


    public int potentialArea(Board board) {
        // as black potential area returns negative number, to keep it negative, we add instead of subtracting
        return potentialAreaInNextTurn(board, Color.White) + potentialAreaInNextTurn(board, Color.Black);
    }

    public int area(Board board) {
        // TODO welche Gebäude passen eigentlich in dieses Gebiet rein.

        int blackOwnedArea = 0;
        int whiteOwnedArea = 0;
        for (Color[] fieldColors : board.getField()) {
            for (Color fieldColor : fieldColors) {
                if (fieldColor == Color.Black_Owned) {
                    blackOwnedArea++;
                } else if (fieldColor == Color.White_Owned) {
                    whiteOwnedArea++;
                }
            }
        }
        return whiteOwnedArea - blackOwnedArea;
    }


}
