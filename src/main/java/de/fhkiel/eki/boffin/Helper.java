package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;

public class Helper {

    private static Helper instance;

    private final Game game;

    Helper(Game game) {
        this.game = game;
        instance = this;
    }

    public static Helper getInstance() {
        return instance;
    }

    Color getEnemyColor() {
        return game.getCurrentPlayer() == Color.White ? Color.Black : Color.White;
    }

    Color getMyColor() {
        Color myColor = game.getCurrentPlayer();
        if (myColor == Color.Blue) {
            return Color.White;
        }
        return myColor;
    }

    void log(String logString) {
        System.out.println(logString);
    }
}
