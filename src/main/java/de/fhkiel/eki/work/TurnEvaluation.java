package de.fhkiel.eki.work;

import de.fhkiel.eki.boffin.Boffin;
import de.fhkiel.eki.boffin.Evaluations.Evaluator;
import de.fhkiel.eki.boffin.Evaluations.GeneralEvaluation;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public record TurnEvaluation(Board board, Placement placement) implements Work {

    // Die fertige Arbeit zum Abholen in einer Queue
    public static BlockingQueue<Map<Placement, GeneralEvaluation>> finishedCalculations = new LinkedBlockingQueue<>();

    @Override
    public void work() {
        if (board.placeBuilding(placement)) {
            GeneralEvaluation eval = Boffin.evaluateGameState(board, new Evaluator(), false);
            finishedCalculations.add(Map.of(placement, eval));
        }
    }
}
