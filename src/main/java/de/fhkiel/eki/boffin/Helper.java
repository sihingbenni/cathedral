package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.stream.Collectors;

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

    static void printBoard(Board board) {
        for (Color[] colors : board.getField()) {
            for (Color color : colors) {
                System.out.print(color);
            }
            System.out.println();
        }
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
        boolean moveSuccess = game.takeTurn(availableMove);
        game.ignoreRules(false);
        return moveSuccess;
    }

    public void undoLastMove() {
        game.undoLastTurn();
    }

    public Color evalColor(Color color) {
        if (color == Color.Blue) {
            return Color.White;
        } else return color;
    }

    public static Set<Placement> getPossiblePlacements(Game game) {
        Set<Building> placeableBuildings = new HashSet<>(game.getPlacableBuildings());
//        int turnNumber = game.lastTurn().getTurnNumber();
        int turnNumber = 99;
        if (turnNumber > 0 && turnNumber <= 3) {
            Set<Building> buildings = new HashSet<>(placeableBuildings.stream().filter(building -> building.score() == 5).toList());
            System.out.println(buildings);
            return new HashSet<>(buildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());
        } else {
            return new HashSet<>(placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());
        }
    }

}
