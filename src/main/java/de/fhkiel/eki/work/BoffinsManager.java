package de.fhkiel.eki.work;

import de.fhkiel.eki.boffin.Evaluations.Evaluation;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Der Manager verteilt und nutzt die Arbeit
public class BoffinsManager {

    private final List<Worker> workers = new ArrayList<>();

    public BoffinsManager() {
        // Arbeiter einstellen
        for (int i = 1; i <= 17; ++i) {
            Worker worker = new Worker("Worker " + i);
            worker.start();
            workers.add(worker);
        }
    }

    public Map<Placement, Evaluation> manageEvaluators(Game game, Set<Placement> possiblePlacements) throws InterruptedException {


        // Starten der Kalkulation
        possiblePlacements.forEach(placement -> Work.workToDo.add(new TurnEvaluation(game.getBoard().copy(), placement)));

        // Solange weiterarbeiten bis nichts mehr zu tun is
        do Thread.sleep(100); while (!Work.workToDo.isEmpty());
        // work is done stop the workers.
        stop();

        System.out.println("Ende, Arbeit fertig! Anzahl an evaluated Placements: " + TurnEvaluation.finishedCalculations.size());

        // combine each Map to one big one, if there are duplicate keys, the first one is taken
        Map<Placement, Evaluation> results = TurnEvaluation.finishedCalculations.stream().flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (eval1, eval2) -> eval1));


        // clear the finishedCalculations list
        TurnEvaluation.finishedCalculations.clear();

        return results;
    }

    public Map<Placement, Evaluation> manageArea(Game game, Set<Placement> possiblePlacements) throws InterruptedException {


        possiblePlacements.forEach(placement -> Work.workToDo.add(new AreaOptimization(game.getBoard().copy(), placement)));

        do Thread.sleep(100); while (!Work.workToDo.isEmpty());
        // work is done stop the workers.
        stop();

        System.out.println("Ende, Arbeit fertig! Anzahl an evaluated Placements: " + AreaOptimization.finishedCalculations.size());

        // combine each Map to one big one, if there are duplicate keys, the first one is taken
        Map<Placement, Evaluation> results = AreaOptimization.finishedCalculations.stream().flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (eval1, eval2) -> eval1));


        // clear the finishedCalculations list
        AreaOptimization.finishedCalculations.clear();

        return results;
    }

    public void stop() {
        // alle feuern
        for (Worker worker : workers) {
            worker.fire();
        }
    }
}