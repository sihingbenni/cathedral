package de.fhkiel.eki.work;

import de.fhkiel.eki.boffin.Evaluations.AreaOptimizationEvaluation;
import de.fhkiel.eki.boffin.Evaluations.Evaluation;
import de.fhkiel.eki.boffin.Evaluations.ScoreEvaluation;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public record AreaOptimization(Board board, Placement placement) implements Work {

    // Die fertige Arbeit zum Abholen in einer Queue
    public static BlockingQueue<Map<Placement, Evaluation>> finishedCalculations = new LinkedBlockingQueue<>();

    @Override
    public void work() {
        // TODO implement
        if (board.placeBuilding(placement)) {
            AreaOptimizationEvaluation eval = new AreaOptimizationEvaluation(new ScoreEvaluation(0, 0));
            finishedCalculations.add(Map.of(placement, eval));
        }
    }
}
