package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.*;

import java.util.Set;

public class Evaluator {
    private final Helper helper;

    Evaluator(Game game) {
        helper = new Helper(game);
    }

    public int score() {
        // White wants to maximize its EvaluationScore.
        // If black is subtracted from white, we get a negative number.
        // To flip the negative number to a positive one and achieve that white maximizes its EvaluationScore,
        // we subtract white from black.
        return helper.getScore(Color.Black) - helper.getScore(Color.White);
    }

    public int potentialAreaInNextTurn(Color color) {
        int currentArea = area();
        int potAreaEval = currentArea;

        // go through all possible moves, check if the area is bigger than before
        Set<Placement> availableMoves = helper.getAvailableMovesFor(color);
        for (Placement availableMove : availableMoves) {

            // only placements, that connect to another building or wall, need to be checked
            boolean doNotCheck = true;

            for (Position position : availableMove.building().corners(availableMove.direction())) {
                Position testPosition = new Position(availableMove.x() + position.x(), availableMove.y() + position.y());
                // check for placement if it has connecting wall
                if (testPosition.x() < 0 || testPosition.x() > 9 || testPosition.y() < 0 || testPosition.y() > 9) {
                    doNotCheck = false;
                    break;
                }
                // check if the color next to the brick is the same as the brick
                Color colorAtTestPosition = helper.getBoard().getField()[testPosition.x()][testPosition.y()];
                if (colorAtTestPosition == availableMove.building().getColor()) {
                    doNotCheck = false;
                    break;
                }
            }

            if (doNotCheck) {
                continue;
            }

            if (helper.tryMove(availableMove)) {
                int potArea = area();
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
                helper.undoLastMove();
            }
        }

        return potAreaEval;
    }


    public int potentialArea() {
        // as black potential area returns negative number, to keep it negative, we add instead of subtracting
        return potentialAreaInNextTurn(Color.White) + potentialAreaInNextTurn(Color.Black);
    }

    public int area() {
        // TODO welche Gebäude passen eigentlich in dieses Gebiet rein.

        Board board = helper.getBoard();
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
