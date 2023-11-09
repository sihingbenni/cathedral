package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

public class Helper {

    private final Game game;

    Helper(Game game) {
        this.game = game;
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

    Color flipColor(Color color) {
        if (color == Color.Black) {
            return Color.White;
        } else {
            return Color.Black;
        }
    }

    int getScore(Color color) {
        return game.score().get(color);
    }

    void log(String logString) {
        System.out.println(logString);
    }

    Board getBoard() {
        return game.getBoard();
    }

    void printBoard(Board board) {
        System.out.println("---- Board for " + getMyColor().name() + " ----");
        for (Color[] colors : board.getField()) {
            for (Color color : colors) {
                System.out.print(color);
            }
            System.out.println();
        }
        System.out.println("---------------");
    }

    public Set<Placement> getAvailableMovesFor(Color color) {
        Set<Placement> availableMoves = new HashSet<>();
        List<Building> placeableBuildings = game.getPlacableBuildings(color);
        for (Building placableBuilding : placeableBuildings) {
            availableMoves.addAll(placableBuilding.getAllPossiblePlacements());
        }
        return availableMoves;
    }

    public boolean tryMove(Placement availableMove) {
        game.ignoreRules(true);
        return game.takeTurn(availableMove);
    }

    public void undoLastMove() {
        game.undoLastTurn();
    }

    public Color evalColor(Color color) {
        if (color == Color.Blue) {
            return Color.White;
        } else return color;
    }

    public static List<Placement> getPossiblePlacements(Game game) {
        List<Building> placeableBuildings = game.getPlacableBuildings();
        return placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList();
    }

}
