package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;

import java.util.Arrays;

public class Evaluator {

    private final Game game;
    private final Helper helper;

    Evaluator(Game game) {
        this.game = game;
        this.helper = new Helper(game);
    }

    public int score() {
        return game.score().get(helper.getEnemyColor()) - game.score().get(helper.getMyColor());
    }

    public int potentialAreaInNextTurn() {
        // TODO implement
        return 0;
    }


    public int area() {
        // TODO welche Gebäude passen eigentlich in dieses Gebiet rein.

        Board board = game.getBoard();
        int countBlackOwnedArea = 0;
        int countWhiteOwnedArea = 0;
        for (Color[] colors : board.getField()) {
            for (Color color : colors) {
                if (color == Color.Black_Owned) {
                    countBlackOwnedArea++;
                } else if (color == Color.White_Owned) {
                    countWhiteOwnedArea++;
                }
            }
        }

        if (helper.getMyColor() == Color.White) {
            return countWhiteOwnedArea - countBlackOwnedArea;
        } else {
            return countBlackOwnedArea - countWhiteOwnedArea;
        }
    }
}
