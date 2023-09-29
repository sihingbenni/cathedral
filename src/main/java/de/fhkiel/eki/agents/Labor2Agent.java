package de.fhkiel.eki.agents;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import javax.swing.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Labor2Agent implements Agent {

    @Override
    public String name() {
        return "Labor 2 Bot";
    }

    @Override
    public void initialize(Game game, PrintStream console) {
        Agent.super.initialize(game, console);
    }

    @Override
    public Optional<JComponent> guiElement() {
        return Agent.super.guiElement();
    }

    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {

        int biggestScore = -1;

        for (Building building : game.getPlacableBuildings()) {
            if (building.score() > biggestScore) {
                biggestScore = building.score();
            }
        }


        final int bScore = biggestScore;

        List<Placement> allPossiblePlacementsForBiggestBuilding =
                game.getPlacableBuildings()
                        .stream()
                        .filter(building -> building.score() == bScore)
                        .map(building -> building.getPossiblePlacements(game))
                        .flatMap(Collection::stream)
                        .toList();

        List<Placement> allBiggestBuildingsPlacements = new ArrayList<>();

        for (Building building : game.getPlacableBuildings()) {
            if (biggestScore == building.score()) {
                allBiggestBuildingsPlacements.addAll(building.getPossiblePlacements(game));
            }
        }

        List<Placement> safePositions = new ArrayList<>();

        for (Placement placement : allPossiblePlacementsForBiggestBuilding) {
            List<Position> sil = placement.building().silhouette(placement.direction());
            sil = sil.stream().map(position -> position.plus(placement.position())).toList();
            if (sil.stream()
                    .filter(Position::isViable)
                    .anyMatch(position -> placement.building().getColor() == game.getBoard().getField()[position.x()][position.y()] || Color.Blue == game.getBoard().getField()[position.x()][position.y()])
            ) {
                safePositions.add(placement);
            }
        }

        if (!safePositions.isEmpty()) {
            return safePositions.stream().findFirst();
        }
        if (allPossiblePlacementsForBiggestBuilding.size() > 0) {
            return allPossiblePlacementsForBiggestBuilding.stream().findFirst();
        }

        return Optional.empty();
    }

    @Override
    public String evaluateLastTurn(Game game) {
        return Agent.super.evaluateLastTurn(game);
    }

    @Override
    public void gameFinished(Game game) {
        Agent.super.gameFinished(game);
    }

    @Override
    public void stop() {
        Agent.super.stop();
    }
}
