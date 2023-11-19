package de.fhkiel.eki.boffin;

import de.fhkiel.ki.cathedral.game.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Helper {

    private final Board board;

    Helper(Board board) {
        this.board = board;
    }

    Board getBoard() {
        return board;
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
        Set<Building> placeableBuildings = new HashSet<>(board.getPlacableBuildings(color));
        for (Building placableBuilding : placeableBuildings) {
            availableMoves.addAll(placableBuilding.getAllPossiblePlacements());
        }
        return availableMoves;
    }

    public static Set<Placement> getPossiblePlacements(Game game) {
        Set<Building> placeableBuildings = new HashSet<>(game.getPlacableBuildings());
        int turnNumber = game.lastTurn().getTurnNumber();

        if (turnNumber > 0 && turnNumber <= 3) {
            Set<Building> buildings = new HashSet<>(placeableBuildings.stream().filter(building -> building.score() == 5).toList());
            System.out.println(buildings);
            return new HashSet<>(buildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());
        } else {
            return new HashSet<>(placeableBuildings.stream().map(building -> building.getPossiblePlacements(game)).flatMap(Collection::stream).toList());
        }
    }

    public boolean shouldEvalPotentialAreaForPlacement(Placement placement) {

        // only placements that connect to another building or wall need to be checked
        for (Position position : placement.building().corners(placement.direction())) {
            Position testPosition = new Position(placement.x() + position.x(), placement.y() + position.y());
            // check for placement if it has a connecting wall
            if (testPosition.x() < 0 || testPosition.x() > 9 || testPosition.y() < 0 || testPosition.y() > 9) {
                return true;
            }
            // check if the color next to the brick is the same as the brick
            Color colorAtTestPosition = this.board.getField()[testPosition.x()][testPosition.y()];
            if (colorAtTestPosition == placement.building().getColor()) {
                return true;
            }
        }

        return false;
    }
}
