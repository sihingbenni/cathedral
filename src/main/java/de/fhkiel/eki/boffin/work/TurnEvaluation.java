package de.fhkiel.eki.boffin.work;

import de.fhkiel.eki.boffin.Boffin;
import de.fhkiel.eki.boffin.evaluations.Evaluator;
import de.fhkiel.eki.boffin.evaluations.GeneralEvaluation;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public record TurnEvaluation(Board board, Placement placement) implements Work {

    // Die fertige Arbeit zum Abholen in einer Queue
    public static BlockingQueue<Map<Placement, GeneralEvaluation>> finishedCalculationsWhite = new LinkedBlockingQueue<>();
    public static BlockingQueue<Map<Placement, GeneralEvaluation>> finishedCalculationsBlack = new LinkedBlockingQueue<>();

    @Override
    public void work() {
        if (board.placeBuilding(placement)) {
            GeneralEvaluation eval = Boffin.evaluateGameState(board, new Evaluator(), false);
            if (placement.building().getColor() == Color.Black) {
                finishedCalculationsBlack.add(Map.of(placement, eval));
            } else {
                finishedCalculationsWhite.add(Map.of(placement, eval));
            }
        }
    }
}
