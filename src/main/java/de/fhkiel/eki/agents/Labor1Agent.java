package de.fhkiel.eki.agents;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Beispiel aus Labor 1
 */
public class Labor1Agent implements Agent {
  @Override
  public String name() {
    return  "Biggest Building Agent";
  }

  @Override
  public Optional<Placement> calculateTurn(Game game, int i, int i1) {
    // Berechnet mögliche Züge
    List<Placement> possibleTurns = new ArrayList<>();
    for(Building building : game.getPlacableBuildings()){
      for(Direction direction : building.getTurnable().getPossibleDirections()){
        for(int x = 0; x < 10; ++x){
          for(int y = 0; y < 10; ++y){
            Placement placementToTry = new Placement(new Position(x,y), direction, building);
            if(game.takeTurn(placementToTry, true)){
              possibleTurns.add(placementToTry);
              game.undoLastTurn();
            }
          }
        }
      }
    }

    // für alle möglichen Züge finde den Zug mit der größten Größe
    int size = 0;
    for(Placement placement : possibleTurns){
      if(placement.building().score() > size){
        size = placement.building().score();
      }
    }

    // filtere von allen möglichen Zügen die Züge raus mit der größten Größe
    final int rSize = size;
    possibleTurns = possibleTurns.stream().filter(placement -> placement.building().score() >= rSize).toList();


    System.out.println(possibleTurns.size());
    if(possibleTurns.size() > 0) {
      // von allen verbleibenden Zügen führe einen random davon aus
      return Optional.of(possibleTurns.get(new Random().nextInt(possibleTurns.size())));
    }
    // Wenn es keine Züge gibt, return empty
    return Optional.empty();
  }
}
