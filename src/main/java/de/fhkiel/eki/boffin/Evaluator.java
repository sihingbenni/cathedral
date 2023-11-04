package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Objects;
import java.util.Set;

public class Evaluator {
    private final Helper helper;
    public final Color myColor;
    private final Color enemyColor;

    Evaluator(Game game, String turn) {
        helper = new Helper(game);
        if (Objects.equals(turn, "thisTurn")) {
            this.myColor = helper.getMyColor();
            this.enemyColor = helper.getEnemyColor();
        } else {
            this.myColor = helper.getEnemyColor();
            this.enemyColor = helper.getMyColor();
        }

    }

    public int score() {
        return helper.getScore(enemyColor) - helper.getScore(myColor);
    }

    public int potentialAreaInNextTurn(Color color) {
        int myCurrentArea = areaFor(color);
        int potAreaEval = myCurrentArea;

        // go through all possible moves, check if the area is bigger than before
        Set<Placement> availableMoves = helper.getAvailableMovesFor(color);
        for (Placement availableMove : availableMoves) {

            if (helper.tryMove(availableMove)) {
                int myPotArea = areaFor(color);
                // if the potential area is bigger than before, save the difference
                if (potAreaEval < (myPotArea - myCurrentArea)) {
                    potAreaEval = (myPotArea - myCurrentArea);
                }
                helper.undoLastMove();
            }
        }

        return potAreaEval;
    }


    public int potentialArea() {
        System.out.println("----- Potential Area -----");

        int myPotArea = potentialAreaInNextTurn(myColor);
        System.out.println("My Potential Area:      " + myPotArea);

        int enemyPotArea = potentialAreaInNextTurn(enemyColor);
        System.out.println("Enemy Potential Area:   " + enemyPotArea);

        return myPotArea - enemyPotArea;
    }

    public int area() {
        return areaFor(myColor);
    }

    private int areaFor(Color color) {
        // TODO welche Gebäude passen eigentlich in dieses Gebiet rein.

        Board board = helper.getBoard();
        int countBlackOwnedArea = 0;
        int countWhiteOwnedArea = 0;
        for (Color[] fieldColors : board.getField()) {
            for (Color fieldColor : fieldColors) {
                if (fieldColor == Color.Black_Owned) {
                    countBlackOwnedArea++;
                } else if (fieldColor == Color.White_Owned) {
                    countWhiteOwnedArea++;
                }
            }
        }

        if (color == Color.White) {
            return countWhiteOwnedArea - countBlackOwnedArea;
        } else {
            return countBlackOwnedArea - countWhiteOwnedArea;
        }
    }
}
