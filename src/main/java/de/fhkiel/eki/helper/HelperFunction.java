package de.fhkiel.eki.helper;

import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HelperFunction {

    public static Set<Placement> getAllPossiblePlacementsFor(Color currentPlayer, Board board) {
        return board.getPlacableBuildings(currentPlayer)
                .stream().flatMap(building -> building.getPossiblePlacements(board).stream())
                .collect(Collectors.toSet());
    }

    public static Set<Placement> getAllPossiblePlacementsFor(List<Building> buildings, Board board) {
        return buildings.stream().flatMap(building -> building.getPossiblePlacements(board).stream())
                .collect(Collectors.toSet());
    }
}
