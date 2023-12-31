package de.fhkiel.eki.boffin.work;

import de.fhkiel.eki.boffin.evaluations.GeneralEvaluation;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
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

    public Map<Placement, GeneralEvaluation> manageEvaluators(Game game, Set<Placement> possiblePlacements) throws InterruptedException {


        // Starten der Kalkulation
        possiblePlacements.forEach(placement -> Work.workToDo.add(new TurnEvaluation(game.getBoard().copy(), placement)));

        // Solange weiterarbeiten bis nichts mehr zu tun is
        do {
            //noinspection BusyWait
            Thread.sleep(100);
        } while (!Work.workToDo.isEmpty());

        // work is done stop the workers.
        stop();


        BlockingQueue<Map<Placement, GeneralEvaluation>> finishedCalculations;
        if (game.getCurrentPlayer() == Color.Black) {
            finishedCalculations = TurnEvaluation.finishedCalculationsBlack;
        } else {
            finishedCalculations = TurnEvaluation.finishedCalculationsWhite;
        }
        System.out.println("Ende, Arbeit fertig! Anzahl an evaluated Placements: " + finishedCalculations.size());

        // combine each Map to one big one, if there are duplicate keys, the first one is taken
        Map<Placement, GeneralEvaluation> results = finishedCalculations.stream().flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (eval1, eval2) -> eval1));


        // clear the finishedCalculations lists
        TurnEvaluation.finishedCalculationsBlack.clear();
        TurnEvaluation.finishedCalculationsWhite.clear();

        return results;
    }

    public void stop() {
        // alle feuern
        for (Worker worker : workers) {
            worker.fire();
        }
    }
}