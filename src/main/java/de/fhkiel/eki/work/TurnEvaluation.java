package de.fhkiel.eki.work;

import de.fhkiel.eki.boffin.Boffin;
import de.fhkiel.eki.boffin.Evaluator;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public record TurnEvaluation(int lastTurnNumber, Board board, Placement placement) implements Work {

    public static final AtomicInteger calls = new AtomicInteger(0);

    // Die fertige Arbeit zum Abholen in einer Queue
    public static BlockingQueue<Map<Placement, Integer>> finishedCalculations = new LinkedBlockingQueue<>();

    public TurnEvaluation {
        synchronized (calls) {
            calls.addAndGet(1);
        }
    }

    @Override
    public void work() {
        if (board.placeBuilding(placement)) {
            int eval = Boffin.evaluateGameState(lastTurnNumber, board, new Evaluator(board), false);
            finishedCalculations.add(Map.of(placement, eval));
        }
        synchronized (calls) {
            calls.addAndGet(-1);
        }
    }
}
