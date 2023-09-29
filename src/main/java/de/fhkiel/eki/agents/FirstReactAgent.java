package de.fhkiel.eki.agents;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import java.util.*;
import java.util.stream.Collectors;

public class FirstReactAgent implements Agent {

    private Game game;

    @Override
    public String name() {
        return "Our first reactive Agent";
    }

    /**
     * @return {@link List<Building>} of the biggest Buildings available to play
     */
    private List<Building> getBiggestBuildings() {
        // get the score of the biggest building
        final int biggestScore = game.getPlacableBuildings()
                .stream()
                .filter(building -> !building.getPossiblePlacements(game).isEmpty())
                .mapToInt(Building::score)
                .max()
                .orElse(-1);
        return game.getPlacableBuildings()
                .stream()
                .filter(building -> building.score() == biggestScore)
                .toList();
    }

    /**
     * @param buildings {@link List<Placement>} of buildings.
     * @return {@link List<Placement>} of allowed Placements for the provided buildings.
     */
    private Optional<List<Placement>> getPlacementsForBuildings(List<Building> buildings) {
        return Optional.of(
                buildings
                .stream()
                .map(building -> building.getPossiblePlacements(game))
                .flatMap(Collection::stream)
                .toList()
        );
    }

    /**
     * @param buildings {@link List<Placement>} of building placements.
     * @return all safe Placements on corners and edges for buildings.
     */
    private Optional<List<Placement>> getAllSafePlacements(List<Building> buildings) {
        // calculate possible placements of buildings
        Optional<List<Placement>> placementList = getPlacementsForBuildings(buildings);
        if (placementList.isPresent()) {
            List<Placement> safePlacements = new ArrayList<>();
            for (Placement placement : placementList.get()) {
                // calculate silhouettes for each possible placement
                List<Position> sil = placement
                        .building()
                        .corners(placement.direction())
                        .stream()
                        .map(position -> position.plus(placement.position()))
                        .toList();
                if (sil.stream()
                        .filter(Position::isViable)
                        // position is safe, if building is next to other owned building or cathedral
                        .anyMatch(position ->
                                placement.building().getColor() == game.getBoard().getField()[position.y()][position.x()]
                                || Color.Blue == game.getBoard().getField()[position.x()][position.y()])
                ) {
                    safePlacements.add(placement);
                }
            }

            if (!safePlacements.isEmpty()) {
                return Optional.of(safePlacements);
            }
        }

        return Optional.empty();
    }


    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        this.game = game;

        // Rule 1: Always play the biggest block that can be played
        // Rule 2: Try to place block in near proximity to your other blocks


        // get the List of biggest buildings that can be placed
        List<Building> biggestBuildings = getBiggestBuildings();

        // Calculate possible Placements for the biggest Buildings
        Optional<List<Placement>> placementsOfBiggestBuildings = getPlacementsForBuildings(biggestBuildings);

        // Calculate safe Placements for the biggest Buildings
        Optional<List<Placement>> safePlacementsOfBiggestBuildings = getAllSafePlacements(biggestBuildings);


        if (safePlacementsOfBiggestBuildings.isPresent() && placementsOfBiggestBuildings.isPresent()) {
            Set<Placement> placementsThatFollowBothRules = placementsOfBiggestBuildings
                    .get()
                    .stream()
                    .filter(safePlacementsOfBiggestBuildings.get()::contains)
                    .collect(Collectors.toSet());
            //noinspection OptionalGetWithoutIsPresent
            return Optional.of(placementsThatFollowBothRules.stream().skip(new Random().nextInt(placementsThatFollowBothRules.size())).findFirst().get());
        } else if (safePlacementsOfBiggestBuildings.isPresent()) {
            List<Placement> list = safePlacementsOfBiggestBuildings.get();
            //noinspection OptionalGetWithoutIsPresent
            return Optional.of(list.stream().skip(new Random().nextInt(list.size())).findFirst().get());
        } else if (placementsOfBiggestBuildings.isPresent()){
            List<Placement> list = placementsOfBiggestBuildings.get();
            //noinspection OptionalGetWithoutIsPresent
            return Optional.of(list.stream().skip(new Random().nextInt(list.size())).findFirst().get());
        }


        return Optional.empty();

    }
}
